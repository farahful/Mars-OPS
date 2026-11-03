package facility;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import core.Resource;
import core.ResourceType;

public abstract class Facility {
    protected final String name;
    protected final String location;
    protected final int capacity;
    protected boolean operational = true;
    // Composition
    protected final List<Resource> resources = new ArrayList<>();
    // Aggregation
    protected final List<Equipment> equipmentList = new ArrayList<>();

    public Facility(String name, String location, int capacity) {
        this.name = name;
        this.location = location;
        this.capacity = capacity;
    }

    public void storeResource(ResourceType type, int amount) {
        if (amount <= 0) return;
        for (Resource res : resources) {
            if (res.getType() == type) {
                res.add(amount);
                return;
            }
        }
        resources.add(new Resource(type, amount));
    }

    public int getStoredQuantity(ResourceType type) {
        for (Resource res : resources) {
            if (res.getType() == type) {
                return res.getQuantity();
            }
        }
        return 0;
    }
    public int getAvailableQuantity(ResourceType type) {
        return operational ? getStoredQuantity(type) : 0;
    }

    public int deductResource(ResourceType type, int amount) {
        for (Resource res : resources) {
            if (res.getType() == type) {
                return res.deduct(amount);
            }
        }
        return 0;
    }

    public void addEquipment(Equipment equipment) {
        if (equipment != null && !equipmentList.contains(equipment)) {
            equipmentList.add(equipment);
        }
    }

    public boolean hasEquipment(Equipment equipment) {
        return equipmentList.contains(equipment);
    }

    public String getName() { return name; }
    public String getLocation() { return location; }
    public int getCapacity() { return capacity; }
    public boolean isOperational() { return operational; }
    public void setOperational(boolean operational) { this.operational = operational; }
    public List<Resource> getResources() { return Collections.unmodifiableList(resources); }
    public List<Equipment> getEquipmentList() { return Collections.unmodifiableList(equipmentList); }

    @Override
    public String toString() {
        return String.format("%s,  operational %s,  resources %s, equipment %s",
                name, operational, resources, equipmentList);
    }
}
