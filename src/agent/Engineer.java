package agent;

import mission.Mission;

public class Engineer extends CrewMember {
    public Engineer(String id, String name, String location) {
        super(id, name, location,
                Capability.ENGINEERING_REPAIR,
                Capability.FIRE_RESPONSE,
                Capability.RESCUE_OPERATION);
    }

    @Override
    public void performDuty() {
        super.performDuty();
        System.out.println("  -> " + name + " (Engineer) is repairing equipment.");
    }

    public void repair(Mission mission) {
        performDuty();
    }
}
