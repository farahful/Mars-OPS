package agent;

public class MedicalOfficer extends CrewMember {
    public MedicalOfficer(String id, String name, String location) {
        super(id, name, location, Capability.MEDICAL_TREATMENT, Capability.RESCUE_OPERATION);
    }

    @Override
    public void performDuty() {
        super.performDuty();
        System.out.println("  -> " + name + " (Medical Officer) is administering medical care.");
    }
}
