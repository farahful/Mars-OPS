package emergency;

import agent.Capability;
import agent.CrewMember;
import core.MarsColony;
import core.Resource;
import core.ResourceType;
import facility.Habitat;

public class MedicalEmergency extends Emergency {
    private final CrewMember patient;
    private final Habitat medicalFacility;

    public MedicalEmergency(CrewMember patient, Habitat medicalFacility) {
        super(medicalFacility.getName());
        this.patient = patient;
        this.medicalFacility = medicalFacility;

        this.requiredCapabilities.add(Capability.MEDICAL_TREATMENT);
        this.requiredResources.add(new Resource(ResourceType.MEDICAL_SUPPLIES, 10));
        this.requiredResources.add(new Resource(ResourceType.OXYGEN, 10));
    }

    @Override
    protected void applyImpact(MarsColony colony) {
        patient.injure(25);
    }

    @Override
    protected String checkConditions(MarsColony colony) {
        if (medicalFacility == null || !medicalFacility.isOperational()) {
            return "Medical facility is not operational.";
        }
        return null;
    }

    @Override
    protected void onResolved(MarsColony colony) {
        patient.setLocation(medicalFacility.getName());
        patient.heal(40);
    }
}
