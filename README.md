# MARS-OPS: Mars Colony Management & Emergency Simulator

**Course:** Computer and Communications Programming II — Lab 5  
**Faculty of Engineering, Alexandria University (SSP)**

---

## 1. Project Overview

MARS-OPS is a Java simulation built to manage day-to-day operations and emergencies for a small Mars research colony (`Ares Base Alpha`). It keeps track of human crew members, robots, colony facilities, shared resources, missions, weather changes, and emergency responses.

The code is split into 5 packages under `src/`, along with `Main.java` which runs the test scenarios:

1. **`core` (`MarsColony`, `Resource`, `ResourceType`, `Environment`)**  
   Contains the main `MarsColony` class that holds the colony's facilities, agents, current time, weather condition, and active mission. It also defines the `Resource` class and the `ResourceType` and `Environment` enums.
2. **`agent` (`Agent`, `Capability`, `CrewMember`, `Scientist`, `Engineer`, `MedicalOfficer`, `Robot`, `ExplorationRover`, `RepairRobot`, `Drone`)**  
   Covers all human crew members and robots. Every agent has an ID, name, location, availability flag, and a list of capabilities (`Capability`).
3. **`facility` (`Facility`, `Habitat`, `LifeSupportSystem`, `Laboratory`, `StorageFacility`, `PowerStation`, `CommunicationStation`, `Equipment`)**  
   Represents the physical buildings in the colony, their stored resources, operational state, and any equipment stored inside them.
4. **`mission` (`Mission`, `MissionState`, `ExplorationMission`, `RepairMission`, `ResearchMission`, `RescueMission`)**  
   Handles the different mission types, their required capabilities and resources, and how missions progress as time advances.
5. **`emergency` (`Emergency`, `OxygenLeak`, `Fire`, `RoverBreakdown`, `MedicalEmergency`)**  
   Handles colony emergencies, their initial damage, finding suitable responders, deducting response resources, and resolving or failing the emergency.

---

## 2. OOP Design & UML Relationships

### Polymorphic Capability Checks
Instead of checking concrete classes with `instanceof`, missions and emergencies rely on capabilities and polymorphic methods:
- Each `Agent` stores a `List<Capability>` (such as `SCIENTIFIC_RESEARCH`, `ENGINEERING_REPAIR`, `ROBOTIC_REPAIR`, `MEDICAL_TREATMENT`, `SURFACE_NAVIGATION`, `AERIAL_RECON`, `RESCUE_OPERATION`, `FIRE_RESPONSE`, and `FIRE_SUPPRESSION`).
- During mission submission, `MarsColony` calls `agent.canPerform(mission, environment)` on each candidate and checks that all required capabilities in `mission.getRequiredCapabilities()` are covered.
- During an emergency, `Emergency.handle()` searches `colony.getAgents()` using `agent.canRespondTo(this)` and `agent.hasCapability(reqCap)`.

### UML Relationships Used
- **Inheritance:**
  - `CrewMember` and `Robot` extend the abstract `Agent` class.
  - `Scientist`, `Engineer`, and `MedicalOfficer` extend `CrewMember`.
  - `ExplorationRover`, `RepairRobot`, and `Drone` extend `Robot`.
  - `Habitat`, `Laboratory`, `StorageFacility`, `PowerStation`, and `CommunicationStation` extend the abstract `Facility` class.
  - All mission types extend `Mission`, and all emergency types extend `Emergency`.
- **Composition:**
  - `Habitat` creates its own `LifeSupportSystem` inside its constructor (`new LifeSupportSystem()`). If the habitat becomes non-operational, its life support system is marked damaged, and repairing the habitat repairs the life support system.
  - `Facility` manages its own internal `List<Resource>` entries.
- **Aggregation:**
  - `MarsColony` holds `List<Agent>` and `List<Facility>`. Both agents and facilities are instantiated outside `MarsColony` and passed in through `addAgent()` and `addFacility()`.
  - `Facility` and `ExplorationRover` hold `List<Equipment> equipmentList`, since equipment objects are created independently and loaded into facilities or rovers.
- **Association:**
  - `MarsColony` keeps a reference to the current `activeMission`.
  - `Mission` subclasses reference their assigned agents, target facilities, and required `Equipment`.
  - `Emergency` subclasses reference the affected `Habitat`, `Laboratory`, `CrewMember`, or `ExplorationRover`.

---

## 3. Resource Storage & Allocation Policy

Resources (`OXYGEN`, `WATER`, `FOOD`, `FUEL`, `BATTERY_POWER`, `MEDICAL_SUPPLIES`, `SPARE_PARTS`) are stored inside individual facilities, while `MarsColony` calculates and deducts them across the whole colony:

1. **Operational Facilities Only:**  
   `MarsColony.getTotalResource(type)` sums `facility.getAvailableQuantity(type)` across all registered facilities. If a facility has `operational == false`, `getAvailableQuantity()` returns `0`, so resources inside damaged or offline facilities cannot be used.
2. **All-or-Nothing Deduction:**  
   In `MarsColony.consumeResources(requiredList)`, the colony first checks every required resource against `getTotalResource(type)`. If even one resource is short, the method returns `false` right away and deducts **nothing** from any facility.
3. **Facility Order Policy:**  
   When enough resources exist across the colony, `consumeResources()` deducts them from operational facilities in the order they were registered. In `Main.java`, `StorageFacility` (`Storage Depot`) is registered before `Habitat A` and `BioLab-1` so that central storage is drawn from first before touching habitat supplies.

---

## 4. Mission States & Lifecycle

Missions use the `MissionState` enum (`ASSIGNED`, `IN_PROGRESS`, `FAILED`, `COMPLETED`). Only one mission can be active in the colony at a time.

- **Submission (`submitMission`):**  
  Checks that no other mission is active, `mission.checkConditions(environment)` passes, all submitted agents are available/usable and cover the mission's `requiredCapabilities`, and `consumeResources()` succeeds.
  - If accepted: agents are assigned (`available = false` and moved to the target location), the mission state becomes `ASSIGNED`, and `activeMission` is set.
  - If rejected: `mission.fail(reason)` sets the state to `FAILED`, stores the `failureReason`, and leaves `activeMission` as `null`.
- **Execution (`advanceTime` / `advanceOneStep`):**  
  Each time step moves an `ASSIGNED` mission to `IN_PROGRESS`, re-checks environmental conditions, calls `performDuty()` on each assigned agent (`CrewMember` fatigue increases by +10, `Robot` battery decreases by -15), and increments `elapsedTime`.
- **Completion (`COMPLETED`):**  
  When `elapsedTime >= duration`, the state becomes `COMPLETED`, `onComplete()` runs (for example, `RepairMission` sets its target facility back to operational and repairs damaged equipment, and `RescueMission` moves the rescued agent back to the habitat), usable agents are released (`available = true`), and `activeMission` is cleared.
- **Failure (`FAILED`):**  
  If a mission is rejected on submission, or if a dust storm / communication disruption / rover breakdown / agent exhaustion happens while it is running, `fail(reason)` marks it `FAILED`, releases usable agents, and clears `activeMission`.

---

## 5. Environmental Conditions

The environment is represented by the `Environment` enum:
- **`NORMAL`:** All missions and agents operate normally.
- **`DUST_STORM`:** Blocks `ExplorationRover` (`canOperateIn` returns `false`), `ExplorationMission`, and `RescueMission`. If `colony.setEnvironment(Environment.DUST_STORM)` is called while a surface mission is active, `MarsColony` immediately fails the active mission.
- **`COMMUNICATION_DISRUPTION`:** Blocks `Drone` operations and `RescueMission` (which requires active communication and an operational `CommunicationStation`).

---

## 6. Emergency Handling Rules

When `colony.triggerEmergency(emergency)` is called, `Emergency.handle(colony)` applies the initial impact, checks conditions, searches for available agents matching `requiredCapabilities`, and attempts to consume `requiredResources`.

| Emergency | Target | Required Capabilities (Responders) | Required Resources | Impact & Resolution Behavior |
| :--- | :--- | :--- | :--- | :--- |
| **`OxygenLeak`** | `Habitat` | `ENGINEERING_REPAIR` + `ROBOTIC_REPAIR` (`Engineer` + `RepairRobot`) | `2 SPARE_PARTS`, `5 BATTERY_POWER` | Immediately deducts `leakAmount` oxygen from the habitat. If resolved, the habitat stays operational. If unresolved, `onUnresolved()` deducts `leakAmount` a second time (2x total oxygen loss) and sets the habitat non-operational. |
| **`Fire`** | `Laboratory` | `FIRE_RESPONSE` + `FIRE_SUPPRESSION` (`Engineer` + `RepairRobot`) | `20 WATER`, `2 SPARE_PARTS` | Immediately sets the laboratory non-operational, damages all equipment inside it (`damageAllEquipment()`), and deducts `25 OXYGEN`. When resolved, the laboratory is set back to operational. |
| **`RoverBreakdown`** | `ExplorationRover` | `ENGINEERING_REPAIR` + `ROBOTIC_REPAIR` (`Engineer` + `RepairRobot`) | `3 SPARE_PARTS`, `10 BATTERY_POWER` | Marks the rover damaged and unavailable. If the rover is part of the `activeMission`, that mission immediately fails. When resolved, the rover is repaired, recharged by `+20%`, and marked available. |
| **`MedicalEmergency`** | `CrewMember` & `Habitat` | `MEDICAL_TREATMENT` (`MedicalOfficer`) | `10 MEDICAL_SUPPLIES`, `10 OXYGEN` | Injures the patient by `-25` health. Requires an operational `Habitat`. When resolved, moves the patient to the habitat and heals `+40` health (capped at `100`). |

If required responders or resources are missing, the emergency is marked `UNRESOLVED` with a `failureReason`, and no response resources are deducted.

---

## 7. Project Structure

```text
Mars OPS/
├── README.md
├── docs/
│   └── UML/
│       ├── MARS_OPS_UML.drawio
│       ├── 1_Class_Diagram.drawio
│       ├── 2_Object_Diagram.drawio
│       ├── 3_State_Machine_Diagram.drawio
│       ├── 4A_Sequence_Mission_Execution.drawio
│       ├── 4B_Sequence_Oxygen_Leak.drawio
│       ├── 4C_Sequence_Environmental_Restriction.drawio
│       ├── 5_Use_Case_Diagram.drawio
│       └── png/
│           ├── 1_Class_Diagram.png
│           ├── 2_Object_Diagram.png
│           ├── 3_State_Machine_Diagram.png
│           ├── 4A_Sequence_Mission_Execution.png
│           ├── 4B_Sequence_Oxygen_Leak.png
│           ├── 4C_Sequence_Environmental_Restriction.png
│           └── 5_Use_Case_Diagram.png
└── src/
    ├── Main.java
    ├── agent/
    │   ├── Agent.java
    │   ├── Capability.java
    │   ├── CrewMember.java
    │   ├── Drone.java
    │   ├── Engineer.java
    │   ├── ExplorationRover.java
    │   ├── MedicalOfficer.java
    │   ├── RepairRobot.java
    │   ├── Robot.java
    │   └── Scientist.java
    ├── core/
    │   ├── Environment.java
    │   ├── MarsColony.java
    │   ├── Resource.java
    │   └── ResourceType.java
    ├── emergency/
    │   ├── Emergency.java
    │   ├── Fire.java
    │   ├── MedicalEmergency.java
    │   ├── OxygenLeak.java
    │   └── RoverBreakdown.java
    ├── facility/
    │   ├── CommunicationStation.java
    │   ├── Equipment.java
    │   ├── Facility.java
    │   ├── Habitat.java
    │   ├── Laboratory.java
    │   ├── LifeSupportSystem.java
    │   ├── PowerStation.java
    │   └── StorageFacility.java
    └── mission/
        ├── ExplorationMission.java
        ├── Mission.java
        ├── MissionState.java
        ├── RepairMission.java
        ├── RescueMission.java
        └── ResearchMission.java
```

---

## 8. How to Compile and Run

From the project root directory (`Mars OPS/`):

```bash
javac -sourcepath src -d out src/Main.java
java -cp out Main
```

---

## 9. Demonstration Scenarios in `Main.java`

Running `Main` executes the 5 required scenarios followed by a bonus verification pass:

1. **Scenario 1 — Successful Exploration:**  
   Prints the initial colony status report, submits `MSN-EXP-01` (`Jezero Crater`, duration `3`, costing `50 OXYGEN` and `20 FUEL`) with `Dr. Nadia` (`Scientist`) and `Pathfinder-1` (`ExplorationRover`), advances time by 3 units until the mission reaches `COMPLETED`, and prints the updated colony status report.
2. **Scenario 2 — Mission Rejection (Unsuitable Agent):**  
   Submits a `ResearchMission` (`MSN-RES-02`) with `Dr. Layla` (`MedicalOfficer`) instead of a `Scientist`. The mission is immediately rejected (`FAILED`) and `activeMission` stays `null`.
3. **Scenario 3 — Resource Shortage (All-or-Nothing):**  
   Attempts to submit `MSN-EXP-03` requiring `10 FUEL` (available) and `9999 OXYGEN` (more than the colony has). The mission is rejected with `"Insufficient colony-wide resources."` and both `OXYGEN` (`500`) and `FUEL` (`80`) remain untouched.
4. **Scenario 4 — Emergency Handling (Oxygen Leak):**  
   Triggers an `OxygenLeak` (`40` units) in `Habitat A`. `Eng. Omar` and `FixBot-7` respond, `2 SPARE_PARTS` and `5 BATTERY_POWER` are consumed, `Habitat A` loses `40 OXYGEN` once (`300 -> 260`), and the emergency is marked `RESOLVED`.
5. **Scenario 5 — Environmental Effect (Dust Storm):**  
   Changes the environment to `DUST_STORM` and tries to submit `MSN-EXP-05`. The mission is rejected immediately with `"Surface operations can't happen in dust storm"`.
6. **Bonus Verification:**  
   Restores `NORMAL` weather and tests a `Fire` in `BioLab-1` (which damages the `Electron Microscope`), a `MedicalEmergency` for `Dr. Nadia`, a `RepairMission` (`MSN-REP-06`) that fixes the microscope, and a `RoverBreakdown` during an active exploration mission at `Hellas Basin`.
