package emergency;

import agent.Capability;
import core.MarsColony;
import core.Resource;
import core.ResourceType;
import facility.Habitat;

public class OxygenLeak extends Emergency {
    private final Habitat habitat;
    private final int leakAmount;

    public OxygenLeak(Habitat habitat, int leakAmount) {
        super(habitat.getName());
        this.habitat = habitat;
        this.leakAmount = Math.max(0, leakAmount);

        this.requiredCapabilities.add(Capability.ENGINEERING_REPAIR);
        this.requiredCapabilities.add(Capability.ROBOTIC_REPAIR);

        this.requiredResources.add(new Resource(ResourceType.SPARE_PARTS, 2));
        this.requiredResources.add(new Resource(ResourceType.BATTERY_POWER, 5));
    }

    @Override
    protected void applyImpact(MarsColony colony) {
        habitat.deductOxygenFromLeak(leakAmount);
    }

    @Override
    protected void onResolved(MarsColony colony) {
        habitat.setOperational(true);
    }

    @Override
    protected void onUnresolved(MarsColony colony) {
        habitat.deductOxygenFromLeak(leakAmount);
        habitat.setOperational(false);
    }

    public Habitat getHabitat() {
        return habitat;
    }

    public int getLeakAmount() {
        return leakAmount;
    }
}
