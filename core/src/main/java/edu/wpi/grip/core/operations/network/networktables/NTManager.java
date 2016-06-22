package edu.wpi.grip.core.operations.network.networktables;

import edu.wpi.grip.core.PipelineRunner;
import edu.wpi.grip.core.events.ProjectSettingsChangedEvent;
import edu.wpi.grip.core.operations.network.Manager;
import edu.wpi.grip.core.operations.network.MapNetworkPublisher;
import edu.wpi.grip.core.operations.network.MapNetworkPublisherFactory;
import edu.wpi.grip.core.settings.ProjectSettings;
import edu.wpi.grip.core.util.GRIPMode;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import com.google.common.eventbus.Subscribe;
import com.google.inject.Singleton;

import edu.wpi.first.wpilibj.networktables.NetworkTable;
import edu.wpi.first.wpilibj.networktables.NetworkTablesJNI;
import edu.wpi.first.wpilibj.tables.ITable;

import java.io.File;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.inject.Inject;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * This class encapsulates the way we map various settings to the global NetworkTables state.
 */
@Singleton
public class NTManager implements Manager, MapNetworkPublisherFactory {
    /*
     * Nasty hack that is unavoidable because of how NetworkTables works.
     */
    private static final AtomicInteger publisherCount = new AtomicInteger(0);

    /**
     * Information from:
     * https://github.com/PeterJohnson/ntcore/blob/master/src/Log.h
     * and
     * https://github.com/PeterJohnson/ntcore/blob/e6054f543a6ab10aa27af6cace855da66d67ee44/include/ntcore_c.h#L39
     */
    private final static Map<Integer, Level> ntLogLevels = new HashMap<Integer, Level>() {{
        put(40, Level.SEVERE);
        put(30, Level.WARNING);
        put(20, Level.INFO);
        put(10, Level.FINE);
        put(9, Level.FINE);
        put(8, Level.FINE);
        put(7, Level.FINER);
        put(6, Level.FINEST);
    }};
    private final Logger logger = Logger.getLogger(getClass().getName());

    @Inject private PipelineRunner pipelineRunner;
    @Inject private GRIPMode gripMode;

    @Inject
    NTManager() {
        // We may have another instance of this method lying around
        NetworkTable.shutdown();

        // Redirect NetworkTables log messages to our own log files.  This gets rid of console spam, and it also lets
        // us grep through NetworkTables messages just like any other messages.
        NetworkTablesJNI.setLogger((level, file, line, msg) -> {
            String filename = new File(file).getName();
            logger.log(ntLogLevels.get(level), String.format("NetworkTables: %s:%d %s", filename, line, msg));
        }, 0);

        NetworkTable.setClientMode();

        // When in headless mode, start and stop the pipeline based on the "GRIP/run" key.  This allows robot programs
        // to control GRIP without actually restarting the process.
        NetworkTable.getTable("GRIP").addTableListener("run", (source, key, value, isNew) -> {
            if (gripMode == GRIPMode.HEADLESS) {
                if (!(value instanceof Boolean)) {
                    logger.warning("NetworkTables value GRIP/run should be a boolean!");
                    return;
                }

                if ((Boolean) value) {
                    if (!pipelineRunner.isRunning()) {
                        logger.info("Starting GRIP from NetworkTables");
                        pipelineRunner.startAsync();
                    }
                } else if (pipelineRunner.isRunning()) {
                    logger.info("Stopping GRIP from NetworkTables");
                    pipelineRunner.stopAsync();

                }
            }
        }, true);

        NetworkTable.shutdown();
    }

    /**
     * Change the server address according to the project setting.
     */
    @Subscribe
    public void updateSettings(ProjectSettingsChangedEvent event) {
        final ProjectSettings projectSettings = event.getProjectSettings();

        synchronized (NetworkTable.class) {
            NetworkTable.shutdown();
            NetworkTable.setIPAddress(projectSettings.getPublishAddress());
        }
    }

    private static final class NTPublisher<P> extends MapNetworkPublisher<P> {
        private final Set<String> keys;
        private Optional<String> name = Optional.empty();

        protected NTPublisher(Set<String> keys) {
            super(keys);
            this.keys = new LinkedHashSet<>(keys);
        }

        @Override
        protected void publishNameChanged(Optional<String> oldName, String newName) {
            if (oldName.isPresent()) {
                deleteOldTable(oldName.get());
            }
            this.name = Optional.of(newName);
        }

        /**
         * Publishes a nested map of values. This is called recursively on any values
         * that are themselves maps.
         *
         * @param table    the table to publish the nested map to
         * @param valueMap the nested map to publish
         */
        private void publishNested(ITable table, Map<String, ?> valueMap) {
            valueMap.forEach((k, v) -> {
                if (v instanceof Map) {
                    publishNested(table.getSubTable(k), (Map<String, ?>) v);
                } else if (v instanceof Collection) {
                    table.putValue(k, ((Collection) v).toArray());
                } else if (v instanceof Number) {
                    table.putNumber(k, ((Number) v).doubleValue());
                } else {
                    table.putValue(k, v);
                }
            });
        }

        @Override
        @SuppressWarnings("unchecked")
        protected void doPublish(Map<String, P> publishValueMap) {
            keys.clear();
            keys.addAll(publishValueMap.keySet());
            deleteOldTable(name.get()); // delete the table before repopulating
            NetworkTable.flush(); // force network tables to clear
            publishValueMap.forEach((k, v) -> {
                if (v instanceof Map) {
                    publishNested(getTable().getSubTable(k), (Map<String, ?>) v);
                } else if (v instanceof Collection) {
                    getTable().putValue(k, ((Collection) v).toArray());
                } else {
                    if (v instanceof Number) {
                        // putValue only supports doubles, so convert number to double and use the explicit putNumber for efficiency
                        getTable().putNumber(k, ((Number) v).doubleValue());
                    } else {
                        getTable().putValue(k, v);
                    }
                }
            });
            NetworkTable.flush(); // push updates
        }

        @Override
        protected void doPublishSingle(P value) {
            checkNotNull(value, "value cannot be null");
            getRootTable().putValue(name.get(), value);
        }

        @Override
        public void doPublish() {
            deleteOldTable(name.get());
        }


        private void deleteOldTable(String tableName) {
            final ITable root, subTable;
            synchronized (NetworkTable.class) {
                root = getRootTable();
                subTable = root.getSubTable(tableName);
            }
            deleteNestedTable(subTable);
            root.delete(tableName);
        }

        private void deleteNestedTable(ITable table) {
            table.getKeys().forEach(table::delete); // delete all entries in this table
            table.getSubTables().forEach(t -> deleteNestedTable(table.getSubTable(t))); // delete all entries in nested tables
            table.getSubTables().forEach(table::delete); // delete all nested tables
        }

        @Override
        public void close() {
            if(name.isPresent()) {
                deleteOldTable(name.get());
            }
            synchronized (NetworkTable.class) {
                // This publisher is no longer used.
                if (NTManager.publisherCount.addAndGet(-1) == 0) {
                    // We are the last publisher so shut it down
                    NetworkTable.shutdown();
                }
            }
        }

        ITable getTable() {
            synchronized (NetworkTable.class) {
                return getRootTable().getSubTable(name.get());
            }
        }

        private static ITable getRootTable() {
            NetworkTable.flush();
            synchronized (NetworkTable.class) {
                return NetworkTable.getTable("GRIP");
            }
        }
    }

    @Override
    public <P>MapNetworkPublisher<P> create(Set<String> keys) {
        // Keep track of ever publisher created.
        publisherCount.getAndAdd(1);
        return new NTPublisher<>(keys);
    }
}
