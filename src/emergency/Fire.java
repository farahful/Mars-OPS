package emergency;

import agent.Capability;
import core.MarsColony;
import core.Resource;
import core.ResourceType;
import facility.Laboratory;

public class Fire extends Emergency {
    private final Laboratory affectedLaboratory;

    public Fire(Laboratory affectedLaboratory) {
        super(affectedLaboratory.getName());
        this.affectedLaboratory = affectedLaboratory;

        this.requiredCapabilities.add(Capability.FIRE_RESPONSE);
        this.requiredCapabilities.add(Capability.FIRE_SUPPRESSION);

        this.requiredResources.add(new Resource(ResourceType.WATER, 20));
        this.requiredResources.add(new Resource(ResourceType.SPARE_PARTS, 2));
    }

    @Override
    protected void applyImpact(MarsColony colony) {
        affectedLaboratory.setOperational(false);
        affectedLaboratory.damageAllEquipment();
        affectedLaboratory.deductResource(ResourceType.OXYGEN, 25);
    }

    @Override
    protected void onResolved(MarsColony colony) {
        affectedLaboratory.setOperational(true);
    }
}
