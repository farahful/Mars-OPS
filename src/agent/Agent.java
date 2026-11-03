package agent;

import core.Environment;
import emergency.Emergency;
import mission.Mission;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public abstract class Agent {
    protected final String id;
    protected final String name;
    protected String location;
    protected boolean available = true;
    protected final List<Capability> capabilities = new ArrayList<>();

    public Agent(String id, String name, String location, Capability... initialCapabilities) {
        this.id = id;
        this.name = name;
        this.location = location;
        for (Capability cap : initialCapabilities) {
            if (cap != null && !this.capabilities.contains(cap)) {
                this.capabilities.add(cap);
            }
        }
    }

    public final void addCapability(Capability capability) {
        if (!capabilities.contains(capability)) {
            capabilities.add(capability);
        }
    }

    public final boolean hasCapability(Capability capability) {
        return capabilities.contains(capability);
    }

    public boolean canPerform(Mission mission) {
        return canPerform(mission, Environment.NORMAL);
    }

    public boolean canPerform(Mission mission, Environment env) {
        if (!available || !isUsable() || !canOperateIn(mission, env)) {
            return false;
        }
        for (Capability cap : mission.getRequiredCapabilities()) {
            if (capabilities.contains(cap)) {
                return true;
            }
        }
        return false;
    }

    public boolean canRespondTo(Emergency emergency) {
        if (!available || !isUsable()) {
            return false;
        }
        for (Capability cap : emergency.getRequiredCapabilities()) {
            if (capabilities.contains(cap)) {
                return true;
            }
        }
        return false;
    }

    public boolean canOperateIn(Mission mission, Environment env) {
        return true;
    }

    public abstract boolean isUsable();
    public abstract void performDuty();

    public String getId() { return id; }
    public String getName() { return name; }
    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }
    public boolean isAvailable() { return available; }
    public void setAvailable(boolean available) { this.available = available; }
    public List<Capability> getCapabilities() { return Collections.unmodifiableList(capabilities); }
}
