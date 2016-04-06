package edu.wpi.grip.core;

import com.google.common.base.MoreObjects;
import com.google.common.collect.ImmutableSet;

import java.io.InputStream;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * An interface describing how an operation should be displayed in the {@link Palette} to the user.
 */
public final class OperationDescription {

    private final Operation.Constructor operationConstructor;
    private final String name;
    private final String description;
    private final Category category;
    private final InputStream icon;
    private final ImmutableSet<String> aliases;

    /**
     * Private constructor - use {@link #builder} to instantiate this class.
     */
    private OperationDescription(Operation.Constructor operationConstructor,
                                 String name,
                                 String description,
                                 Category category,
                                 InputStream iconStream,
                                 Set<String> aliases) {
        this.operationConstructor = checkNotNull(operationConstructor);
        this.name = checkNotNull(name);
        this.description = checkNotNull(description);
        this.category = checkNotNull(category);
        this.icon = iconStream; // This is allowed to be null
        this.aliases = ImmutableSet.copyOf(checkNotNull(aliases));
    }

    /**
     * The categories that entries can be in.
     */
    public enum Category {
        IMAGE_PROCESSING,
        FEATURE_DETECTION,
        NETWORK,
        LOGICAL,
        OPENCV,
        MISCELLANEOUS,
    }

    public Operation.Constructor getConstructor() {
        return operationConstructor;
    }

    /**
     * @return The unique user-facing name of the operation, such as "Gaussian Blur"
     */
    public String getName() {
        return name;
    }

    /**
     * @return A description of the operation.
     */
    public String getDescription() {
        return description;
    }

    /**
     * @return What category the operation falls under.  This is used to organize them in the GUI
     */
    public Category getCategory() {
        return category;
    }

    /**
     * @return An {@link InputStream} of a 128x128 image to show the user as a representation of the operation.
     */
    public Optional<? extends InputStream> getIcon() {
        return Optional.ofNullable(icon);
    }

    /**
     * @return Any old unique user-facing names of the operation. This is used to preserve compatibility with
     * old versions of GRIP if the operation name changes.
     */
    public ImmutableSet<String> getAliases() {
        return aliases;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("name", getName())
                .add("description", getDescription())
                .add("aliases", getAliases())
                .add("category", getCategory())
                .toString();
    }

    /**
     * Creates a new {@link Builder} instance to create a new {@code OperationDescription} object.
     * <p>
     * The created descriptor has a default category of {@link Category#MISCELLANEOUS MISCELLANEOUS} and no icon; use
     * the {@link Builder#category(Category) .category()} and {@link Builder#icon(InputStream) .icon()} methods to
     * override the default values.
     */
    public static  Builder builder() {
        return new Builder()
                .category(Category.MISCELLANEOUS)
                .icon(null);
    }

    /**
     * Builder class for {@code OperationDescription}
     *
     * @param  the type of operation that the built descriptor will be describing. Only one descriptor may exist
     *            for any {@code Operation} subclass.
     */
    public static final class Builder {
        private Operation.Constructor operationConstructor;
        private String name;
        private String description;
        private Category category;
        private InputStream icon;
        private ImmutableSet<String> aliases = ImmutableSet.of(); // default to empty Set to avoid NPE if not assigned

        /**
         * Private constructor; use {@link OperationDescription#builder()} to create a builder.
         */
        private Builder() { }

        /**
         * Sets the Operation constructor.
         */
        public Builder constructor(Operation.Constructor constructor) {
            this.operationConstructor = checkNotNull(constructor);
            return this;
        }

        /**
         * Sets the name
         */
        public Builder name(String name) {
            this.name = checkNotNull(name);
            return this;
        }

        /**
         * Sets the description
         */
        public Builder description(String description) {
            this.description = checkNotNull(description);
            return this;
        }

        /**
         * Sets the category
         */
        public Builder category(Category category) {
            this.category = checkNotNull(category);
            return this;
        }

        /**
         * Sets the icon
         */
        public Builder icon(InputStream icon) {
            this.icon = icon;
            return this;
        }

        /**
         * Sets the aliases
         */
        public Builder aliases(String... aliases) {
            this.aliases = ImmutableSet.copyOf(checkNotNull(aliases));
            return this;
        }

        /**
         * Builds a new {@code OperationDescription}
         */
        public OperationDescription build() {
            return new OperationDescription(
                    operationConstructor,
                    name,
                    description,
                    category,
                    icon,
                    aliases);
        }
    }

}
