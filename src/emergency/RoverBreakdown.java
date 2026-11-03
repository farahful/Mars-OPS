package emergency;

import agent.Capability;
import agent.ExplorationRover;
import core.MarsColony;
import core.Resource;
import core.ResourceType;
import mission.Mission;

public class RoverBreakdown extends Emergency {
    private final ExplorationRover damagedRover;

    public RoverBreakdown(ExplorationRover damagedRover) {
        super(damagedRover.getLocation());
        this.damagedRover = damagedRover;

        this.requiredCapabilities.add(Capability.ENGINEERING_REPAIR);
        this.requiredCapabilities.add(Capability.ROBOTIC_REPAIR);

        this.requiredResources.add(new Resource(ResourceType.SPARE_PARTS, 3));
        this.requiredResources.add(new Resource(ResourceType.BATTERY_POWER, 10));
    }

    @Override
    protected void applyImpact(MarsColony colony) {
        damagedRover.setDamaged(true);
        damagedRover.setAvailable(false);

        Mission active = colony.getActiveMission();
        if (active != null && active.getAssignedAgents().contains(damagedRover)) {
            colony.failActiveMission("Rover " + damagedRover.getName() + " broke down at " + damagedRover.getLocation());
        }
    }

    @Override
    protected void onResolved(MarsColony colony) {
        damagedRover.setDamaged(false);
        damagedRover.recharge(20);
        damagedRover.setAvailable(true);
    }

    public ExplorationRover getDamagedRover() {
        return damagedRover;
    }
}
