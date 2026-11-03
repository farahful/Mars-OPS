import java.util.List;
import agent.Drone;
import agent.Engineer;
import agent.ExplorationRover;
import agent.MedicalOfficer;
import agent.RepairRobot;
import agent.Scientist;
import core.Environment;
import core.MarsColony;
import core.Resource;
import core.ResourceType;
import emergency.Fire;
import emergency.MedicalEmergency;
import emergency.OxygenLeak;
import emergency.RoverBreakdown;
import facility.CommunicationStation;
import facility.Equipment;
import facility.Habitat;
import facility.Laboratory;
import facility.PowerStation;
import facility.StorageFacility;
import mission.ExplorationMission;
import mission.RepairMission;
import mission.ResearchMission;

public class Main {
    public static void main(String[] args) {
        System.out.println("=== MARS-OPS: Autonomous Mars Colony Management & Emergency Simulator ===\n");

        // Setup initial Mars Colony
        MarsColony colony = new MarsColony("Ares Base Alpha");

        // Facilities
        Habitat habitatA = new Habitat("Habitat A", "Sector 1", 10);
        habitatA.storeResource(ResourceType.OXYGEN, 300);
        habitatA.storeResource(ResourceType.WATER, 200);
        habitatA.storeResource(ResourceType.FOOD, 150);
        habitatA.storeResource(ResourceType.MEDICAL_SUPPLIES, 40);

        StorageFacility storage = new StorageFacility("Storage Depot", "Sector 2", 500);
        storage.storeResource(ResourceType.OXYGEN, 200);
        storage.storeResource(ResourceType.FUEL, 100);
        storage.storeResource(ResourceType.BATTERY_POWER, 80);
        storage.storeResource(ResourceType.SPARE_PARTS, 20);

        Laboratory bioLab = new Laboratory("BioLab-1", "Sector 3", 6);
        Equipment microscope = new Equipment("Electron Microscope");
        bioLab.addEquipment(microscope);
        bioLab.storeResource(ResourceType.OXYGEN, 50);

        PowerStation solarPlant = new PowerStation("Solar Array", "Sector 4", 4);
        CommunicationStation commStation = new CommunicationStation("Comms Tower", "Sector 5", 4);

        // Register StorageFacility first so colony draws from Storage before Habitat reserves
        colony.addFacility(storage);
        colony.addFacility(habitatA);
        colony.addFacility(bioLab);
        colony.addFacility(solarPlant);
        colony.addFacility(commStation);

        // Crew Members & Robots
        Scientist scientist = new Scientist("C-01", "Dr. Nadia", "Habitat A");
        Engineer engineer = new Engineer("C-02", "Eng. Omar", "Habitat A");
        MedicalOfficer medic = new MedicalOfficer("C-03", "Dr. Layla", "Habitat A");

        Equipment coreDrill = new Equipment("Spectrometer Drill");
        ExplorationRover rover = new ExplorationRover("R-01", "Pathfinder-1", "Storage Depot", 100);
        rover.loadEquipment(coreDrill);

        RepairRobot repairBot = new RepairRobot("R-02", "FixBot-7", "Storage Depot", 100);
        Drone drone = new Drone("R-03", "SkyEye-1", "Comms Tower", 100);

        colony.addAgent(scientist);
        colony.addAgent(engineer);
        colony.addAgent(medic);
        colony.addAgent(rover);
        colony.addAgent(repairBot);
        colony.addAgent(drone);

        // =========================================================================
        // SCENARIO 1: Successful Exploration (Section 12)
        // =========================================================================
        System.out.println(">>> SCENARIO 1: Successful Exploration");
        System.out.println("[State BEFORE Exploration Mission]");
        System.out.println(colony.printStatusReport());

        ExplorationMission expMission1 = new ExplorationMission(
                "MSN-EXP-01",
                3,
                "Jezero Crater",
                coreDrill,
                List.of(
                        new Resource(ResourceType.OXYGEN, 50),
                        new Resource(ResourceType.FUEL, 20)
                )
        );

        boolean accepted1 = colony.submitMission(expMission1, List.of(scientist, rover));
        System.out.println("Mission " + expMission1.getName() + " submitted -> Accepted: " + accepted1
                + " | State: " + expMission1.getState());

        colony.advanceTime(3);
        System.out.println("Mission " + expMission1.getName() + " after 3 time units -> State: " + expMission1.getState());
        System.out.println("[State AFTER Exploration Mission]");
        System.out.println(colony.printStatusReport());
        System.out.println();

        // =========================================================================
        // SCENARIO 2: Mission Rejection — Unsuitable Agent (Section 12)
        // =========================================================================
        System.out.println(">>> SCENARIO 2: Mission Rejection (Unsuitable Agent)");
        ResearchMission badAgentMission = new ResearchMission(
                "MSN-RES-02",
                2,
                bioLab,
                microscope,
                List.of(new Resource(ResourceType.BATTERY_POWER, 10))
        );

        // Submitting a MedicalOfficer instead of a Scientist
        boolean accepted2 = colony.submitMission(badAgentMission, List.of(medic));
        if (!accepted2) {
            System.out.println("Mission cannot be assigned.");
            System.out.println("State: " + badAgentMission.getState()
                    + " | Active Mission: " + colony.getActiveMission());
            System.out.println("Reason: " + badAgentMission.getFailureReason());
        }
        System.out.println();

        // =========================================================================
        // SCENARIO 3: Resource Shortage — All-or-Nothing Preservation (Section 12)
        // =========================================================================
        System.out.println(">>> SCENARIO 3: Resource Shortage (All-or-Nothing)");
        int oxygenBefore = colony.getTotalResource(ResourceType.OXYGEN);
        int fuelBefore = colony.getTotalResource(ResourceType.FUEL);
        System.out.println("Available before attempt -> OXYGEN: " + oxygenBefore + ", FUEL: " + fuelBefore);

        ExplorationMission shortageMission = new ExplorationMission(
                "MSN-EXP-03",
                2,
                "Olympus Mons Foothills",
                coreDrill,
                List.of(
                        new Resource(ResourceType.FUEL, 10),     // Available
                        new Resource(ResourceType.OXYGEN, 9999)  // Exceeds colony-wide total
                )
        );

        boolean accepted3 = colony.submitMission(shortageMission, List.of(scientist, rover));
        System.out.println("Mission accepted: " + accepted3 + " | State: " + shortageMission.getState());
        System.out.println("Reason: " + shortageMission.getFailureReason());
        System.out.println("Available after rejected attempt -> OXYGEN: "
                + colony.getTotalResource(ResourceType.OXYGEN)
                + ", FUEL: " + colony.getTotalResource(ResourceType.FUEL) + " (Zero consumed!)");
        System.out.println();

        // =========================================================================
        // SCENARIO 4: Emergency Handling — Oxygen Leak (Section 12)
        // =========================================================================
        System.out.println(">>> SCENARIO 4: Emergency Handling (Oxygen Leak in Habitat A)");
        System.out.println("Habitat A Oxygen BEFORE leak: " + habitatA.getStoredQuantity(ResourceType.OXYGEN)
                + " | Spare Parts: " + colony.getTotalResource(ResourceType.SPARE_PARTS));

        OxygenLeak leak = new OxygenLeak(habitatA, 40);
        colony.triggerEmergency(leak);

        System.out.println("Emergency at " + leak.getLocation() + " -> Status: " + leak.getStatusLabel());
        System.out.println("Habitat A Oxygen AFTER resolved leak (1x loss): "
                + habitatA.getStoredQuantity(ResourceType.OXYGEN)
                + " | Habitat Operational: " + habitatA.isOperational()
                + " | Spare Parts remaining: " + colony.getTotalResource(ResourceType.SPARE_PARTS));
        System.out.println();

        // =========================================================================
        // SCENARIO 5: Environmental Effect — Dust Storm (Section 12)
        // =========================================================================
        System.out.println(">>> SCENARIO 5: Environmental Effect (Dust Storm)");
        colony.setEnvironment(Environment.DUST_STORM);
        System.out.println("Environment changed to: " + colony.getEnvironment());

        ExplorationMission stormMission = new ExplorationMission(
                "MSN-EXP-05",
                2,
                "Valles Marineris",
                coreDrill,
                List.of(new Resource(ResourceType.FUEL, 10))
        );

        boolean accepted5 = colony.submitMission(stormMission, List.of(scientist, rover));
        if (!accepted5) {
            System.out.println("Exploration Mission cannot start.");
            System.out.println("State: " + stormMission.getState());
            System.out.println("Reason: " + stormMission.getFailureReason());
        }

        // Restore normal environment and demonstrate additional emergencies & repair mission
        colony.setEnvironment(Environment.NORMAL);
        System.out.println("\n>>> BONUS VERIFICATION: Lab Fire, Medical Treatment & Equipment Repair Mission");
        scientist.setLocation(bioLab.getName());
        Fire labFire = new Fire(bioLab);
        colony.triggerEmergency(labFire);
        System.out.println("Lab Fire Status: " + labFire.getStatusLabel()
                + " | Microscope Damaged: " + microscope.isDamaged()
                + " | Dr. Nadia Health: " + scientist.getHealth());

        MedicalEmergency medEmerg = new MedicalEmergency(scientist, habitatA);
        colony.triggerEmergency(medEmerg);
        System.out.println("Medical Emergency Status: " + medEmerg.getStatusLabel()
                + " | Dr. Nadia Health after treatment: " + scientist.getHealth());

        RepairMission repairMicroscope = new RepairMission(
                "MSN-REP-06",
                2,
                bioLab,
                microscope,
                List.of(new Resource(ResourceType.SPARE_PARTS, 2), new Resource(ResourceType.BATTERY_POWER, 5))
        );
        colony.submitMission(repairMicroscope, List.of(engineer, repairBot));
        colony.advanceTime(2);
        System.out.println("Repair Mission State: " + repairMicroscope.getState()
                + " | Microscope Damaged: " + microscope.isDamaged());

        // Demonstrate Rover Breakdown during an active mission
        ExplorationMission breakdownRun = new ExplorationMission(
                "MSN-EXP-07", 3, "Hellas Basin", coreDrill, List.of(new Resource(ResourceType.FUEL, 10)));
        colony.submitMission(breakdownRun, List.of(scientist, rover));
        colony.advanceTime(1);
        RoverBreakdown breakdown = new RoverBreakdown(rover);
        colony.triggerEmergency(breakdown);
        System.out.println("Rover Breakdown Status: " + breakdown.getStatusLabel()
                + " | Aborted Mission State: " + breakdownRun.getState()
                + " (" + breakdownRun.getFailureReason() + ")");

        System.out.println("\n[FINAL COLONY STATUS REPORT]");
        System.out.println(colony.printStatusReport());
    }
}
