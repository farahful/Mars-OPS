package core;

import agent.Agent;
import agent.Capability;
import emergency.Emergency;
import facility.Facility;
import mission.Mission;
import mission.MissionState;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MarsColony {
    private final String name;
    private int currentTime = 0;
    private Environment environment = Environment.NORMAL;
    private final List<Agent> agents = new ArrayList<>();
    private final List<Facility> facilities = new ArrayList<>();
    private Mission activeMission;

    public MarsColony(String name) {
        this.name = name;
    }

    public void addAgent(Agent agent) {
        if (agent != null && !agents.contains(agent)) {
            agents.add(agent);
        }
    }

    public void addFacility(Facility facility) {
        if (facility != null && !facilities.contains(facility)) {
            facilities.add(facility);
        }
    }

    public boolean submitMission(Mission mission, List<Agent> candidateAgents) {
        if (activeMission != null) {
            mission.fail("Another mission (" + activeMission.getName() + ") is already active.");
            return false;
        }

        String conditionError = mission.checkConditions(environment);
        if (conditionError != null) {
            mission.fail(conditionError);
            return false;
        }

        if (candidateAgents == null || candidateAgents.isEmpty()) {
            mission.fail("No agents submitted for mission.");
            return false;
        }

        for (Agent agent : candidateAgents) {
            if (!agent.canPerform(mission, environment)) {
                mission.fail("Agent does not possess the required capability or availability: " + agent.getName());
                return false;
            }
        }

        for (Capability reqCap : mission.getRequiredCapabilities()) {
            boolean covered = false;
            for (Agent agent : candidateAgents) {
                if (agent.hasCapability(reqCap)) {
                    covered = true;
                    break;
                }
            }
            if (!covered) {
                mission.fail("Agent does not possess the required capability: missing " + reqCap);
                return false;
            }
        }

        if (!consumeResources(mission.getRequiredResources())) {
            mission.fail("Insufficient colony-wide resources.");
            return false;
        }

        for (Agent agent : candidateAgents) {
            mission.assign(agent);
        }
        mission.markAssigned();
        this.activeMission = mission;
        return true;
    }

    public void triggerEmergency(Emergency emergency) {
        emergency.handle(this);
    }

    public void advanceTime(int units) {
        for (int step = 0; step < units; step++) {
            currentTime++;
            if (activeMission != null) {
                activeMission.advanceOneStep(environment);
                if (activeMission.getState() == MissionState.COMPLETED
                        || activeMission.getState() == MissionState.FAILED) {
                    activeMission = null;
                }
            }
        }
    }

    public void setEnvironment(Environment newEnvironment) {
        this.environment = newEnvironment;
        if (activeMission != null) {
            String error = activeMission.checkConditions(newEnvironment);
            if (error != null) {
                failActiveMission(error);
            }
        }
    }

    public void failActiveMission(String reason) {
        if (activeMission != null) {
            activeMission.fail(reason);
            activeMission = null;
        }
    }

    public int getTotalResource(ResourceType type) {
        int total = 0;
        for (Facility facility : facilities) {
            total += facility.getAvailableQuantity(type);
        }
        return total;
    }

    public boolean consumeResources(List<Resource> requiredList) {
        if (requiredList == null || requiredList.isEmpty()) {
            return true;
        }

        for (Resource req : requiredList) {
            if (getTotalResource(req.getType()) < req.getQuantity()) {
                return false;
            }
        }

        for (Resource req : requiredList) {
            int remaining = req.getQuantity();
            for (Facility facility : facilities) {
                if (facility.isOperational() && remaining > 0) {
                    remaining -= facility.deductResource(req.getType(), remaining);
                }
            }
        }
        return true;
    }

    public String printStatusReport() {
        StringBuilder sb = new StringBuilder();
        sb.append("============================================================\n");
        sb.append("COLONY STATUS REPORT: ").append(name)
          .append(" | Time: ").append(currentTime)
          .append(" | Env: ").append(environment).append("\n");
        sb.append("------------------------------------------------------------\n");

        sb.append("Colony-Wide Operational Resource Totals:\n  ");
        for (ResourceType type : ResourceType.values()) {
            sb.append(type).append("=").append(getTotalResource(type)).append("  ");
        }
        sb.append("\n------------------------------------------------------------\n");

        sb.append("Facilities:\n");
        for (Facility f : facilities) {
            sb.append("  - ").append(f).append("\n");
        }

        sb.append("Personnel & Robots:\n");
        for (Agent a : agents) {
            sb.append("  - ").append(a).append("\n");
        }

        sb.append("Active Mission: ");
        if (activeMission == null) {
            sb.append("NONE\n");
        } else {
            sb.append(activeMission.getName())
              .append(" [").append(activeMission.getState()).append("] ")
              .append(activeMission.getElapsedTime()).append("/").append(activeMission.getDuration()).append("\n");
        }
        sb.append("============================================================");
        return sb.toString();
    }

    public String getName() { return name; }
    public int getCurrentTime() { return currentTime; }
    public Environment getEnvironment() { return environment; }
    public Mission getActiveMission() { return activeMission; }
    public List<Agent> getAgents() { return Collections.unmodifiableList(agents); }
    public List<Facility> getFacilities() { return Collections.unmodifiableList(facilities); }
}
