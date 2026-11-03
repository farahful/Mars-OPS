package mission;

import agent.Agent;
import agent.Capability;
import core.Environment;
import core.Resource;
import core.Resource;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public abstract class Mission {

    protected final String name;
    protected final int duration;
    protected int elapsedTime;
    protected MissionState state = null;
    protected String failureReason;
    protected final List<Agent> assignedAgents = new ArrayList<>();
    protected final List<Resource> requiredResources;
    protected final Set<Capability> requiredCapabilities = new HashSet<>();

    public Mission(String name, int duration, List<Resource> requiredResources) {
        if (duration < 0 ) {
            throw new IllegalArgumentException("Mission duration can not be negative");
        }
        this.name = name;
        this.duration = duration;
        this.requiredResources = requiredResources;
    }

    public void assign(Agent agent) {
        assignedAgents.add(agent);
        agent.setAvailable(false);
        if(getTargetLocation() != null){
            agent.setLocation(getTargetLocation());
        }
    }

    public void markAssigned() {
        this.state = MissionState.ASSIGNED;
    }

    public void fail(String reason) {
        if (this.state == MissionState.COMPLETED){
            throw new IllegalArgumentException("Mission already is completed");
        }
        if(this.state == MissionState.FAILED) {
            throw new IllegalArgumentException("Mission has already failed");
        }
        this.state = MissionState.FAILED;
        this.failureReason = reason;
        releaseAgents();
    }

    public void advanceOneStep(Environment env) {
        if (state != MissionState.ASSIGNED && state != MissionState.IN_PROGRESS) {
            return;
        }
        if(state == MissionState.ASSIGNED) {
            state = MissionState.IN_PROGRESS;
        }

        String error  = checkConditions(env);
        if(error != null) {
            fail(error);
            return;
        }

        for(Agent agent: assignedAgents) {
            agent.performDuty();
            if(!agent.isUsable()) {
                fail("Agent" + agent.getName() + " failed");
                return;
            }
        }

        this.elapsedTime++;
        if (this.elapsedTime >= this.duration) {
            this.state = MissionState.COMPLETED;
            onComplete();
            releaseAgents();
        }
    }

    private void releaseAgents() {
        for(Agent agent: assignedAgents) {
            if(agent.isUsable()) {
                agent.setAvailable(true);
            }
        }
    }

    public boolean isSurfaceMission() { return false; }
    public abstract String getTargetLocation();
    public abstract String checkConditions(Environment env);
    protected void onComplete() {}

    public String getName() {
        return name;
    }
    public int getDuration() {
        return duration;
    }
    public int getElapsedTime() {
        return elapsedTime;
    }
    public MissionState getState() {
        return state;
    }
    public String getFailureReason() {
        return failureReason;
    }
    public List<Agent> getAssignedAgents() {
        return assignedAgents;
    }
    public List<Resource> getRequiredResources() {return requiredResources;}
    public Set<Capability> getRequiredCapabilities() { return requiredCapabilities;}


}
