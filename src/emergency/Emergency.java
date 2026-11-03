package emergency;

import agent.Agent;
import agent.Capability;
import core.MarsColony;
import core.Resource;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public abstract class Emergency {
    protected final String location;
    protected boolean resolved = false;
    protected String failureReason;

    protected final List<Capability> requiredCapabilities = new ArrayList<>();
    protected final List<Resource> requiredResources = new ArrayList<>();
    protected final List<Agent> responders = new ArrayList<>();

    public Emergency(String location) {
        this.location = location;
    }

    public final void handle(MarsColony colony) {
        applyImpact(colony);

        String error = checkConditions(colony);
        if (error != null) {
            markUnresolved(colony, error);
            return;
        }

        // Check for agents
        List<Agent> selectedResponders = new ArrayList<>();
        for (Capability reqCap : requiredCapabilities) {
            Agent matched = null;
            for (Agent agent : colony.getAgents()) {
                if (!selectedResponders.contains(agent)
                        && agent.canRespondTo(this)
                        && agent.hasCapability(reqCap)) {
                    matched = agent;
                    break;
                }
            }
            if (matched == null) {
                markUnresolved(colony, "Required responder unavailable for capability: " + reqCap);
                return;
            }
            selectedResponders.add(matched);
        }

        // Check for resources.
        if (!colony.consumeResources(requiredResources)) {
            markUnresolved(colony, "Insufficient colony-wide resources to respond to emergency.");
            return;
        }

        this.responders.addAll(selectedResponders);
        for (Agent responder : responders) {
            responder.setAvailable(false);
            responder.setLocation(location);
            responder.performDuty();
        }

        onResolved(colony);
        this.resolved = true;

        for (Agent responder : responders) {
            if (responder.isUsable()) {
                responder.setAvailable(true);
            }
        }
    }

    private void markUnresolved(MarsColony colony, String reason) {
        this.resolved = false;
        this.failureReason = reason;
        onUnresolved(colony);
    }

    protected abstract void applyImpact(MarsColony colony);
    protected String checkConditions(MarsColony colony) { return null; }
    protected abstract void onResolved(MarsColony colony);
    protected void onUnresolved(MarsColony colony) {}

    public String getLocation() { return location; }
    public boolean isResolved() { return resolved; }
    public String getStatusLabel() { return resolved ? "RESOLVED" : "UNRESOLVED"; }
    public String getFailureReason() { return failureReason; }
    public List<Capability> getRequiredCapabilities() { return Collections.unmodifiableList(requiredCapabilities); }
    public List<Resource> getRequiredResources() { return Collections.unmodifiableList(requiredResources); }
    public List<Agent> getResponders() { return Collections.unmodifiableList(responders); }
}
