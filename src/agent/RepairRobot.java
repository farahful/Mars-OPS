package agent;

import core.Environment;
import mission.Mission;

public class RepairRobot extends Robot {
    public RepairRobot(String id, String name, String location, int batteryLevel) {
        super(id, name, location, batteryLevel, Capability.ROBOTIC_REPAIR, Capability.FIRE_SUPPRESSION);
    }

    @Override
    public boolean canOperateIn(Mission mission, Environment env) {
        return !mission.isSurfaceMission();
    }

    @Override
    public void performDuty() {
        super.performDuty();
        System.out.println("  -> " + name + " (Repair Robot) is performing automated structural repairs.");
    }
}
