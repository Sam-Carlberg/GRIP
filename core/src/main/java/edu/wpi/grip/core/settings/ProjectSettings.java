package edu.wpi.grip.core.settings;

import com.google.common.base.MoreObjects;
import com.google.common.base.Throwables;

import javax.annotation.Nonnegative;
import javax.annotation.Nullable;

import static com.google.common.base.Preconditions.checkArgument;

/**
 * This object holds settings that are saved in project files.  This includes things like team
 * numbers, which need to be preserved when deploying the project.
 */
@SuppressWarnings("JavadocMethod")
public class ProjectSettings implements Settings, Cloneable {

  @Setting(label = "FRC team number", description = "The team number, if used for FRC")
  private int teamNumber = 0;

  @Setting(label = "NetworkTables server address", description = "The host that runs the "
      + "NetworkTables server. If not specified and NetworkTables is used, the hostname is derived "
      + "from the team number.")
  private String publishAddress = computeFRCAddress(teamNumber);

  // Getters and setters

  public int getTeamNumber() {
    return teamNumber;
  }

  /**
   * Set the FRC team number.  If the deploy address and NetworkTables server address haven't been
   * manually overridden, this also changes them to the mDNS hostname of the team's roboRIO.
   */
  public void setTeamNumber(@Nonnegative int teamNumber) {
    checkArgument(teamNumber >= 0, "Team number cannot be negative");

    final String oldFrcAddress = computeFRCAddress(this.teamNumber);
    final String newFrcAddress = computeFRCAddress(teamNumber);

    this.teamNumber = teamNumber;

    // If the NetworkTables server address was previously the default for
    // the old team number (ie: it was roborio-xxx-frc.local), update it with the new team number
    if (oldFrcAddress.equals(getPublishAddress())) {
      setPublishAddress(newFrcAddress);
    }
  }

  public String getPublishAddress() {
    return publishAddress;
  }

  public void setPublishAddress(@Nullable String publishAddress) {
    if (publishAddress != null) {
      this.publishAddress = publishAddress;
    }
  }

  private String computeFRCAddress(int teamNumber) {
    return "roboRIO-" + teamNumber + "-FRC.local";
  }

  @Override
  public String toString() {
    return MoreObjects.toStringHelper(this)
        .add("publishAddress", publishAddress)
        .add("teamNumber", teamNumber)
        .toString();
  }

  @Override
  @SuppressWarnings("PMD.CloneThrowsCloneNotSupportedException")
  public ProjectSettings clone() {
    try {
      return (ProjectSettings) super.clone();
    } catch (CloneNotSupportedException impossible) {
      Throwables.propagate(impossible);
    }

    return null;
  }
}
