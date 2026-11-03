package facility;

public class LifeSupportSystem {
    private boolean functional = true;

    public boolean isFunctional() {
        return functional;
    }

    public void damage() {
        this.functional = false;
    }

    public void repair() {
        this.functional = true;
    }
}
