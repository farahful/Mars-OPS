package mission;

import facility.Equipment;
import facility.Laboratory;
import core.Resource;
import core.Environment;
import agent.Capability;

import java.util.List;

public class ResearchMission extends Mission {
    private final Laboratory laboratory;
    private final Equipment equipment;

    public ResearchMission(String name, int duration, Laboratory laboratory, Equipment equipment, List<Resource> resources) {
        super(name, duration, resources);
        this.laboratory = laboratory;
        this.equipment = equipment;
        this.requiredCapabilities.add(Capability.SCIENTIFIC_RESEARCH);
    }

    @Override
    public String getTargetLocation() {
        return laboratory != null ? laboratory.getName() : null;
    }

    @Override
    public String checkConditions(Environment env) {
        if(laboratory == null  || !laboratory.isOperational()) {
            return "Laboratory is unavailable or damaged";
        }
        if(equipment == null || equipment.isDamaged()) {
            return "Scientific equipment is unavailable or damaged";
        }
        return null;
    }
}
