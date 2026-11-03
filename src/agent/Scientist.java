package agent;

public class Scientist extends CrewMember {
    public Scientist(String id, String name, String location) {
        super(id, name, location, Capability.SCIENTIFIC_RESEARCH);
    }

    @Override
    public void performDuty() {
        super.performDuty();
        System.out.println("  -> " + name + " (Scientist) is conducting scientific research.");
    }
}
