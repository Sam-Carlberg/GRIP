package edu.wpi.grip.core.events;

import edu.wpi.grip.core.settings.ProjectSettings;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * This event is posted after the {@link ProjectSettings} are changed so anything that relies on
 * them can immediately update without restarting the application.
 */
@LoggableEvent
public class ProjectSettingsChangedEvent implements DirtiesSaveEvent {
  private final ProjectSettings projectSettings;

  public ProjectSettingsChangedEvent(ProjectSettings projectSettings) {
    this.projectSettings = checkNotNull(projectSettings, "Project settings cannot be null");
  }

  public ProjectSettings getProjectSettings() {
    return projectSettings;
  }
}
