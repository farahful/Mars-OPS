package core;

public class Resource {
    private final ResourceType type;
    private int quantity;

    public Resource(ResourceType type, int quantity) {
        this.type = type;
        this.quantity = Math.max(0, quantity);
    }

    public ResourceType getType() {
        return type;
    }

    public int getQuantity() {
        return quantity;
    }

    public void add(int amount) {
        if (amount > 0) {
            this.quantity += amount;
        }
    }

    public int deduct(int amount) {
        if (amount <= 0) {
            return 0;
        }
        int taken = Math.min(this.quantity, amount);
        this.quantity -= taken;
        return taken;
    }

    @Override
    public String toString() {
        return type + ": " + quantity;
    }
}
