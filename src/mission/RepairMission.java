package mission;

import facility.Equipment;
import facility.Facility;
import core.Resource;
import core.Environment;
import agent.Capability;

import java.util.List;


public class RepairMission extends Mission {
    private final Facility facility;
    private final Equipment equipment;

    public RepairMission(String name, int duration, Facility facility, Equipment equipment, List<Resource> resources){
        super(name, duration, resources);
        this.facility = facility;
        this.equipment = equipment;
        this.requiredCapabilities.add(Capability.ENGINEERING_REPAIR);
        this.requiredCapabilities.add(Capability.ROBOTIC_REPAIR);
    }

    @Override
    public String getTargetLocation() {
        return facility != null ? facility.getName() : null;
    }

    @Override
    public String checkConditions(Environment env) {
        if(facility == null && equipment == null) {
            return "Repair mission requires a facility or equipment";
        }
        return null;
    }

    @Override
    protected void onComplete() {
        if(facility != null) {
            facility.setOperational(true);
        }
        if(equipment != null) {
            equipment.setDamaged(false);
        }
    }

}
