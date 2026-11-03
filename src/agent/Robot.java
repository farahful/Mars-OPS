package agent;

public abstract class Robot extends Agent {
    protected int batteryLevel;
    protected boolean damaged;

    public Robot(String id, String name, String location, int batteryLevel, Capability... initialCapabilities) {
        super(id, name, location, initialCapabilities);
        this.batteryLevel = Math.max(0, Math.min(100, batteryLevel));
        this.damaged = false;
    }

    @Override
    public boolean isUsable() {
        return !damaged && batteryLevel > 0;
    }

    @Override
    public void performDuty() {
        batteryLevel = Math.max(0, batteryLevel - 15);
    }

    public void recharge(int amount) {
        if (amount > 0) {
            batteryLevel = Math.min(100, batteryLevel + amount);
        }
    }

    public int getBatteryLevel() { return batteryLevel; }
    public boolean isDamaged() { return damaged; }
    public void setDamaged(boolean damaged) { this.damaged = damaged; }

    @Override
    public String toString() {
        return String.format("%s [%s] | Loc: %s | Battery: %d%% | Damaged: %s | Available: %s",
                name, id, location, batteryLevel, damaged, available);
    }
}
