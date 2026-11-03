package agent;

public abstract class CrewMember extends Agent {
    protected int health;
    protected int fatigue;

    public CrewMember(String id, String name, String location, Capability... initialCapabilities) {
        super(id, name, location, initialCapabilities);
        this.health = 100;
        this.fatigue = 0;
    }

    @Override
    public boolean isUsable() {
        return health > 20 && fatigue < 100;
    }

    @Override
    public void performDuty() {
        fatigue = Math.min(100, fatigue + 10);
    }

    public void rest(int amount) {
        if (amount > 0) {
            fatigue = Math.max(0, fatigue - amount);
        }
    }

    public void heal(int amount) {
        if (amount > 0) {
            health = Math.min(100, health + amount);
        }
    }

    public void injure(int amount) {
        if (amount > 0) {
            health = Math.max(0, health - amount);
        }
    }

    public int getHealth() { return health; }
    public int getFatigue() { return fatigue; }

    @Override
    public String toString() {
        return String.format("%s [%s] | Loc: %s | Health: %d | Fatigue: %d | Available: %s",
                name, id, location, health, fatigue, available);
    }
}
