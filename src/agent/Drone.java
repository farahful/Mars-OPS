package agent;

import core.Environment;
import mission.Mission;

public class Drone extends Robot {
    public Drone(String id, String name, String location, int batteryLevel) {
        super(id, name, location, batteryLevel, Capability.AERIAL_RECON);
    }

    @Override
    public boolean canOperateIn(Mission mission, Environment env) {
        return env != Environment.COMMUNICATION_DISRUPTION;
    }

    @Override
    public void performDuty() {
        super.performDuty();
        System.out.println("  -> " + name + " (Drone) is performing aerial reconnaissance.");
    }
}
