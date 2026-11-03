package facility;

import core.ResourceType;

public class Habitat extends Facility {
    private final LifeSupportSystem lifeSupport;

    public Habitat(String name, String location, int capacity) {
        super(name, location, capacity);
        this.lifeSupport = new LifeSupportSystem();
    }

    public void deductOxygenFromLeak(int amount) {
        deductResource(ResourceType.OXYGEN, amount);
    }

    @Override
    public void setOperational(boolean operational) {
        super.setOperational(operational);
        if (operational) {
            lifeSupport.repair();
        } else {
            lifeSupport.damage();
        }
    }

    public LifeSupportSystem getLifeSupport() {
        return lifeSupport;
    }
}
