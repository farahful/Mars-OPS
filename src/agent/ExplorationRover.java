package agent;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import core.Environment;
import facility.Equipment;
import mission.Mission;

public class ExplorationRover extends Robot {
    private final List<Equipment> equipmentList = new ArrayList<>();

    public ExplorationRover(String id, String name, String location, int batteryLevel) {
        super(id, name, location, batteryLevel, Capability.SURFACE_NAVIGATION);
    }

    public void loadEquipment(Equipment equipment) {
        if (equipment != null && !equipmentList.contains(equipment)) {
            equipmentList.add(equipment);
        }
    }

    public List<Equipment> getEquipmentList() {
        return Collections.unmodifiableList(equipmentList);
    }

    @Override
    public boolean canOperateIn(Mission mission, Environment env) {
        return env != Environment.DUST_STORM;
    }

    @Override
    public void performDuty() {
        super.performDuty();
        System.out.println("  -> " + name + " (Exploration Rover) is traversing the Martian surface.");
    }
}
