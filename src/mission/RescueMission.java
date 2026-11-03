package mission;

import agent.Agent;
import facility.CommunicationStation;
import core.Resource;
import core.Environment;
import agent.Capability;

import java.util.List;

public class RescueMission extends Mission{
    private final Agent agent;
    private final CommunicationStation station;

    public RescueMission(String name, int duration, Agent agent, CommunicationStation station, List<Resource> resources){
        super(name, duration, resources);
        this.agent = agent;
        this.station = station;
        this.requiredCapabilities.add(Capability.SURFACE_NAVIGATION);
        this.requiredCapabilities.add(Capability.RESCUE_OPERATION);
    }

    @Override
    public boolean isSurfaceMission() {
        return true;
    }

    @Override
    public String getTargetLocation(){
        return agent != null ? agent.getLocation() : "Surface";
    }

    @Override
    public String checkConditions(Environment env) {
        if(env == Environment.COMMUNICATION_DISRUPTION || station == null || !station.isOperational()) {
            return "Rescue mission requires active communication";
        }
        if(env == Environment.DUST_STORM) {
            return "Rescue mission blocked by storm";
        }
        if(agent == null) {
            return "No agent assigned";
        }
        return null;
    }

    @Override
    protected void onComplete() {
        agent.setLocation("Habitat");
        agent.setAvailable(true);
    }


}
