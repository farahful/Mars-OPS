package mission;

import agent.Capability;
import facility.Equipment;
import java.util.List;

import core.Resource;
import core.Environment;

public class ExplorationMission extends Mission {
    private final String region;
    private final Equipment equipment;

    public ExplorationMission(String name, int duration, String region, Equipment equipment, List<Resource> resources) {
        super(name, duration, resources);
        this.region = region;
        this.equipment = equipment;
        this.requiredCapabilities.add(Capability.SCIENTIFIC_RESEARCH);
        this.requiredCapabilities.add(Capability.SURFACE_NAVIGATION);
    }

    @Override
    public boolean isSurfaceMission() {
        return true;
    }

    @Override
    public String getTargetLocation() {
        return region;
    }

    @Override
    public String checkConditions(Environment env) {
        if(env == Environment.DUST_STORM) {
            return "Surface operations can't happen in dust storm";
        }
        if(equipment == null || equipment.isDamaged()) {
            return "Equipment is damaged";
        }
        return null;
    }
}
