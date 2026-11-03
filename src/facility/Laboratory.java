package facility;

public class Laboratory extends Facility {
    public Laboratory(String name, String location, int capacity) {
        super(name, location, capacity);
    }

    public void damageAllEquipment() {
        for (Equipment eq : equipmentList) {
            eq.setDamaged(true);
        }
    }
}
