package mission;

public enum MissionState {
    // Reserved resources / requirements for the mission to start.
    ASSIGNED,
    // Currently executing / time advancing
    IN_PROGRESS,
    // Aborted or rejected, ... etc

    FAILED,
    COMPLETED
}
