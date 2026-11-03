package facility;

public class Equipment {
    private final String name;
    private boolean damaged;

    public Equipment(String name) {
        this.name = name;
        this.damaged = false;
    }

    public String getName() {
        return name;
    }

    public boolean isDamaged() {
        return damaged;
    }

    public void setDamaged(boolean damaged) {
        this.damaged = damaged;
    }

    public void repair() {
        this.damaged = false;
    }

    @Override
    public String toString() {
        return String.format("%s, status %s", name, (damaged ? "damaged": "working"));
    }
}
