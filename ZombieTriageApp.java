import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Scanner;

public class ZombieTriageApp {

    enum ImmuneStrength {
        STRONG, NORMAL, WEAK, IMMUNOCOMPROMISED
    }

    enum ExposureLevel {
        LOW, MODERATE, HIGH, EXTREME
    }

    enum MutationLevel {
        NONE, LOW, MODERATE, HIGH
    }

    enum HumidityLevel {
        LOW, MODERATE, HIGH
    }

    enum PopulationDensity {
        RURAL, SUBURBAN, URBAN
    }

    enum InfectionCertainty {
        CONFIRMED, SUSPECTED, UNKNOWN
    }

    enum PriorityGroup {
        CHILD, ADULT, SENIOR, MEDICAL_PERSONNEL
    }
    
    enum ZombieType {
        NOT_A_ZOMBIE, FRESH, OLD
    }

    static class Patient {
        private static int nextId = 1;

        int id;
        String name;
        int complaintChoice;
        String complaint;
        int age;
        double weight;
        int stage;
        long intakeTime;

        boolean throatTightness;
        boolean hotEnvironmentExposure;
        boolean cleanEnvironment;
        boolean recentlyTurnedUnder10Min;
        boolean highRiskMedicalHistory;
        boolean isOverweight;

        ImmuneStrength immuneStrength;
        boolean hasRespiratoryDisease;
        boolean hasDiabetes;
        boolean hasNeurologicalDisorder;
        ExposureLevel exposureLevel;
        MutationLevel mutationLevel;

        boolean delayedSymptoms;
        boolean highStressSuppression;
        boolean partialTransformationAwareness;

        HumidityLevel humidityLevel;
        boolean highUVSunlightExposure;
        PopulationDensity populationDensity;
        boolean nighttimeExposure;

        int dosesRequired;
        boolean vaccineSideEffectRisk;
        int vaccineDaysSincePrepared;
        boolean storageCompromised;
        boolean powerOutageOrTransportDelay;

        boolean falsePositiveRisk;
        boolean falseNegativeRisk;
        int testingTimeMinutes;

        ZombieType zombieType;
        boolean sensoryAttractionRisk;
        boolean swarmRisk;

        InfectionCertainty infectionCertainty;
        PriorityGroup priorityGroup;
        boolean vaccineTradeoffConcern;

        boolean panicOrDishonesty;
        boolean carrierImmunity;
        boolean reinfectionCase;

        int priorityScore;
        String priorityLabel;
        String disposition;
        double vaccineEffectiveness;
        String notes;

        public Patient(
                String name,
                int complaintChoice,
                int age,
                double weight,
                int stage,

                boolean throatTightness,
                boolean hotEnvironmentExposure,
                boolean cleanEnvironment,
                boolean recentlyTurnedUnder10Min,
                boolean highRiskMedicalHistory,
                boolean isOverweight,

                ImmuneStrength immuneStrength,
                boolean hasRespiratoryDisease,
                boolean hasDiabetes,
                boolean hasNeurologicalDisorder,
                ExposureLevel exposureLevel,
                MutationLevel mutationLevel,

                boolean delayedSymptoms,
                boolean highStressSuppression,
                boolean partialTransformationAwareness,

                HumidityLevel humidityLevel,
                boolean highUVSunlightExposure,
                PopulationDensity populationDensity,
                boolean nighttimeExposure,

                int dosesRequired,
                boolean vaccineSideEffectRisk,
                int vaccineDaysSincePrepared,
                boolean storageCompromised,
                boolean powerOutageOrTransportDelay,

                boolean falsePositiveRisk,
                boolean falseNegativeRisk,
                int testingTimeMinutes,

                ZombieType zombieType,
                boolean sensoryAttractionRisk,
                boolean swarmRisk,

                InfectionCertainty infectionCertainty,
                PriorityGroup priorityGroup,
                boolean vaccineTradeoffConcern,

                boolean panicOrDishonesty,
                boolean carrierImmunity,
                boolean reinfectionCase
        ) {
            this.id = nextId++;
            this.name = name;
            this.complaintChoice = complaintChoice;
            this.complaint = complaintFromChoice(complaintChoice);
            this.age = age;
            this.weight = weight;
            this.stage = stage;
            this.intakeTime = System.currentTimeMillis();

            this.throatTightness = throatTightness;
            this.hotEnvironmentExposure = hotEnvironmentExposure;
            this.cleanEnvironment = cleanEnvironment;
            this.recentlyTurnedUnder10Min = recentlyTurnedUnder10Min;
            this.highRiskMedicalHistory = highRiskMedicalHistory;
            this.isOverweight = isOverweight;

            this.immuneStrength = immuneStrength;
            this.hasRespiratoryDisease = hasRespiratoryDisease;
            this.hasDiabetes = hasDiabetes;
            this.hasNeurologicalDisorder = hasNeurologicalDisorder;
            this.exposureLevel = exposureLevel;
            this.mutationLevel = mutationLevel;

            this.delayedSymptoms = delayedSymptoms;
            this.highStressSuppression = highStressSuppression;
            this.partialTransformationAwareness = partialTransformationAwareness;

            this.humidityLevel = humidityLevel;
            this.highUVSunlightExposure = highUVSunlightExposure;
            this.populationDensity = populationDensity;
            this.nighttimeExposure = nighttimeExposure;

            this.dosesRequired = dosesRequired;
            this.vaccineSideEffectRisk = vaccineSideEffectRisk;
            this.vaccineDaysSincePrepared = vaccineDaysSincePrepared;
            this.storageCompromised = storageCompromised;
            this.powerOutageOrTransportDelay = powerOutageOrTransportDelay;

            this.falsePositiveRisk = falsePositiveRisk;
            this.falseNegativeRisk = falseNegativeRisk;
            this.testingTimeMinutes = testingTimeMinutes;

            this.zombieType = zombieType;
            this.sensoryAttractionRisk = sensoryAttractionRisk;
            this.swarmRisk = swarmRisk;

            this.infectionCertainty = infectionCertainty;
            this.priorityGroup = priorityGroup;
            this.vaccineTradeoffConcern = vaccineTradeoffConcern;

            this.panicOrDishonesty = panicOrDishonesty;
            this.carrierImmunity = carrierImmunity;
            this.reinfectionCase = reinfectionCase;

            evaluatePatient();
        }

        private String complaintFromChoice(int choice) {
            switch (choice) {
                case 1: return "Bite or direct infection";
                case 2: return "Fever or flu-like symptoms";
                case 3: return "Aggression or confusion";
                case 4: return "Breathing problem";
                case 5: return "Recently turned";
                case 6: return "Exposure with unclear symptoms";
                case 7: return "Medical staff exposure";
                case 8: return "Possible symptom-free carrier";
                default: return "Unknown complaint";
            }
        }

        private void evaluatePatient() {
            int score = 0;
            StringBuilder detailNotes = new StringBuilder();

            vaccineEffectiveness = calculateVaccineEffectiveness();

            switch (stage) {
                case 0:
                    score += 25;
                    detailNotes.append("Unknown stage. ");
                    break;
                case 1:
                case 2:
                case 3:
                    score += 100;
                    detailNotes.append("Early stage; higher chance of cure. ");
                    break;
                case 4:
                    score += 65;
                    detailNotes.append("Stage 4; quarantine required. ");
                    break;
                case 5:
                    score += 50;
                    detailNotes.append("Stage 5; severe and contagious. ");
                    break;
                case 6:
                    score += 15;
                    detailNotes.append("Stage 6; advanced transformation. ");
                    break;
                default:
                    score += 10;
                    detailNotes.append("Unclear stage. ");
                    break;
            }

            if (age < 16 && stage >= 1 && stage <= 3) {
                score += 100;
                detailNotes.append("Child in curable stage; highest priority. ");
            }

            switch (priorityGroup) {
                case MEDICAL_PERSONNEL:
                    score += 20;
                    detailNotes.append("Medical staff priority. ");
                    break;
                case CHILD:
                    score += 15;
                    detailNotes.append("Child priority group. ");
                    break;
                case SENIOR:
                    score += 8;
                    detailNotes.append("Senior priority group. ");
                    break;
                case ADULT:
                    break;
            }

            switch (infectionCertainty) {
                case CONFIRMED:
                    score += 20;
                    detailNotes.append("Confirmed infection. ");
                    break;
                case SUSPECTED:
                    score += 10;
                    detailNotes.append("Suspected infection. ");
                    break;
                case UNKNOWN:
                    score += 5;
                    detailNotes.append("Infection not yet confirmed. ");
                    break;
            }

            switch (immuneStrength) {
                case STRONG:
                    score -= 5;
                    detailNotes.append("Strong immune system. ");
                    break;
                case NORMAL:
                    break;
                case WEAK:
                    score += 10;
                    detailNotes.append("Weak immune system. ");
                    break;
                case IMMUNOCOMPROMISED:
                    score += 20;
                    detailNotes.append("Immunocompromised. ");
                    break;
            }

            if (hasRespiratoryDisease) {
                score += 12;
                detailNotes.append("Respiratory illness present. ");
            }
            if (hasDiabetes) {
                score += 10;
                detailNotes.append("Diabetes risk. ");
            }
            if (hasNeurologicalDisorder) {
                score += 8;
                detailNotes.append("Neurological condition present. ");
            }
            if (highRiskMedicalHistory) {
                score += 12;
                detailNotes.append("High-risk medical history. ");
            }

            switch (exposureLevel) {
                case LOW:
                    score += 3;
                    break;
                case MODERATE:
                    score += 8;
                    detailNotes.append("Moderate exposure. ");
                    break;
                case HIGH:
                    score += 15;
                    detailNotes.append("High exposure. ");
                    break;
                case EXTREME:
                    score += 25;
                    detailNotes.append("Extreme exposure. ");
                    break;
            }

            switch (mutationLevel) {
                case NONE:
                    break;
                case LOW:
                    score += 5;
                    detailNotes.append("Low mutation risk. ");
                    break;
                case MODERATE:
                    score += 12;
                    detailNotes.append("Moderate mutation risk. ");
                    break;
                case HIGH:
                    score += 20;
                    detailNotes.append("High mutation risk. ");
                    break;
            }

            if (delayedSymptoms) {
                score += 8;
                detailNotes.append("Symptoms may be delayed. ");
            }
            if (highStressSuppression) {
                score += 10;
                detailNotes.append("Stress may be hiding severity. ");
            }
            if (partialTransformationAwareness && stage == 5) {
                score += 5;
                detailNotes.append("Partial awareness present. ");
            }

            if (hotEnvironmentExposure) {
                score += 10;
                detailNotes.append("Recent heat exposure. ");
            }
            if (cleanEnvironment) {
                score -= 4;
                detailNotes.append("Lower spread risk from clean setting. ");
            }

            switch (humidityLevel) {
                case LOW:
                    score -= 2;
                    break;
                case MODERATE:
                    break;
                case HIGH:
                    score += 8;
                    detailNotes.append("High humidity risk. ");
                    break;
            }

            if (!highUVSunlightExposure) {
                score += 5;
                detailNotes.append("Limited sunlight exposure. ");
            } else {
                score -= 2;
            }

            if (nighttimeExposure) {
                score += 5;
                detailNotes.append("Night exposure. ");
            }

            switch (populationDensity) {
                case RURAL:
                    score += 2;
                    detailNotes.append("Rural area; lower spread but less access to care. ");
                    break;
                case SUBURBAN:
                    score += 5;
                    break;
                case URBAN:
                    score += 12;
                    detailNotes.append("Urban crowding risk. ");
                    break;
            }

            if (isOverweight) {
                score += 10;
                detailNotes.append("Higher risk from weight-related progression. ");
            }

            if (age <= 2) {
                score += 15;
                detailNotes.append("Very young patient. ");
            } else if (age < 16) {
                score += 8;
            } else if (age >= 65) {
                score += 12;
                detailNotes.append("Older age risk. ");
            }

            if (throatTightness) {
                score += 12;
                detailNotes.append("Breathing or throat symptoms present. ");
            }
            if (falsePositiveRisk) {
                score -= 3;
                detailNotes.append("Possible false positive. ");
            }
            if (falseNegativeRisk) {
                score += 6;
                detailNotes.append("Possible false negative. ");
            }
            if (testingTimeMinutes >= 30) {
                score += 8;
                detailNotes.append("Testing delay may worsen outcome. ");
            }
            if (hasRespiratoryDisease && throatTightness) {
                score += 8;
                detailNotes.append("Respiratory history may complicate diagnosis. ");
            }

            switch (zombieType) {
                case NOT_A_ZOMBIE:
                    break;
                case FRESH:
                    score += 10;
                    detailNotes.append("Fresh zombie behavior observed. ");
                    break;
                case OLD:
                    score += 6;
                    detailNotes.append("Older zombie behavior observed. ");
                    break;
            }

            if (sensoryAttractionRisk) {
                score += 5;
                detailNotes.append("Sound or body heat may attract zombies. ");
            }
            if (swarmRisk) {
                score += 15;
                detailNotes.append("Swarm risk nearby. ");
            }

            if (dosesRequired >= 2) {
                score -= 8;
                detailNotes.append("Multiple doses required. ");
            }
            if (vaccineSideEffectRisk) {
                score -= 5;
                detailNotes.append("Higher vaccine reaction risk. ");
            }
            if (storageCompromised || powerOutageOrTransportDelay) {
                detailNotes.append("Vaccine storage may be unreliable. ");
            }
            if (vaccineTradeoffConcern) {
                score -= 10;
                detailNotes.append("Using vaccine here may prevent saving others. ");
            }

            if (panicOrDishonesty) {
                score += 6;
                detailNotes.append("Symptoms may be hidden or inaccurately reported. ");
            }
            if (carrierImmunity) {
                score += 8;
                detailNotes.append("Possible immune carrier. ");
            }
            if (reinfectionCase) {
                score += 8;
                detailNotes.append("Possible reinfection. ");
            }

            disposition = determineDisposition(vaccineEffectiveness, detailNotes);

            priorityScore = Math.max(score, 0);

            if (priorityScore >= 180) {
                priorityLabel = "TOP PRIORITY";
            } else if (priorityScore >= 130) {
                priorityLabel = "CRITICAL";
            } else if (priorityScore >= 90) {
                priorityLabel = "HIGH";
            } else if (priorityScore >= 50) {
                priorityLabel = "MEDIUM";
            } else {
                priorityLabel = "LOW";
            }

            notes = detailNotes.toString().trim();
        }

        private double calculateVaccineEffectiveness() {
            double effectiveness;

            switch (stage) {
                case 1:
                case 2:
                case 3:
                    effectiveness = 1.00;
                    break;
                case 4:
                    effectiveness = 0.60;
                    break;
                case 5:
                    effectiveness = 0.30;
                    break;
                case 6:
                    effectiveness = recentlyTurnedUnder10Min ? 0.10 : 0.00;
                    break;
                default:
                    effectiveness = 0.40;
                    break;
            }

            if (immuneStrength == ImmuneStrength.IMMUNOCOMPROMISED) {
                effectiveness -= 0.20;
            } else if (immuneStrength == ImmuneStrength.WEAK) {
                effectiveness -= 0.10;
            }

            if (hasDiabetes) {
                effectiveness -= 0.08;
            }

            switch (mutationLevel) {
                case LOW:
                    effectiveness -= 0.05;
                    break;
                case MODERATE:
                    effectiveness -= 0.15;
                    break;
                case HIGH:
                    effectiveness -= 0.30;
                    break;
                default:
                    break;
            }

            if (storageCompromised || powerOutageOrTransportDelay) {
                effectiveness -= 0.20;
            }

            if (vaccineDaysSincePrepared > 14) {
                effectiveness = Math.min(effectiveness, 0.35);
            } else if (vaccineDaysSincePrepared >= 12) {
                effectiveness -= 0.25;
            } else if (vaccineDaysSincePrepared >= 8) {
                effectiveness -= 0.12;
            }

            if (dosesRequired >= 2) {
                effectiveness -= 0.05;
            }
            if (vaccineSideEffectRisk) {
                effectiveness -= 0.05;
            }

            return clamp(effectiveness, 0.0, 1.0);
        }

        private String determineDisposition(double vaccineEffectiveness, StringBuilder detailNotes) {
            if (stage == 4) {
                detailNotes.append("Action: quarantine. ");
                return "QUARANTINE";
            }

            if (stage == 6) {
                if (age < 16) {
                    detailNotes.append("Action: quarantine because patient is young. ");
                    return "QUARANTINE";
                }

                if (highRiskMedicalHistory || age >= 65 || isOverweight || immuneStrength == ImmuneStrength.IMMUNOCOMPROMISED) {
                    detailNotes.append("Action: euthanize and incinerate. ");
                    return "EUTHANIZE AND INCINERATE";
                }

                detailNotes.append("Action: quarantine. ");
                return "QUARANTINE";
            }

            if (stage == 5) {
                if (partialTransformationAwareness && vaccineEffectiveness >= 0.35 && !vaccineTradeoffConcern) {
                    detailNotes.append("Action: strict quarantine with ethics review. ");
                    return "STRICT QUARANTINE / ETHICS REVIEW";
                }
                detailNotes.append("Action: strict quarantine. ");
                return "STRICT QUARANTINE";
            }

            if (stage >= 1 && stage <= 3) {
                if (vaccineEffectiveness >= 0.70) {
                    detailNotes.append("Action: treat immediately. ");
                    return "TREAT IMMEDIATELY";
                } else if (vaccineEffectiveness >= 0.40) {
                    detailNotes.append("Action: treat and monitor. ");
                    return "TREAT / MONITOR";
                } else {
                    detailNotes.append("Action: quarantine and observe. ");
                    return "QUARANTINE / OBSERVE";
                }
            }

            if (infectionCertainty == InfectionCertainty.UNKNOWN || stage == 0) {
                detailNotes.append("Action: isolate and test. ");
                return "ISOLATE / TEST";
            }

            detailNotes.append("Action: evaluate. ");
            return "EVALUATE";
        }

        private double clamp(double value, double min, double max) {
            return Math.max(min, Math.min(max, value));
        }

        public long getWaitMinutes() {
            return (System.currentTimeMillis() - intakeTime) / 60000;
        }

        @Override
        public String toString() {
            return String.format(
                    "ID: %d | Name: %s | Age: %d | Weight: %.1f lbs | Complaint: %s | Stage: %d | Certainty: %s | Priority: %s (%d) | Vaccine: %.0f%% | Action: %s | Wait: %d min",
                    id, name, age, weight, complaint, stage, infectionCertainty, priorityLabel,
                    priorityScore, vaccineEffectiveness * 100, disposition, getWaitMinutes()
            );
        }

        public String detailedSummary() {
            return toString() + "\nNotes: " + notes;
        }
    }

    private static final Comparator<Patient> TRIAGE_ORDER =
            Comparator.comparingInt((Patient p) -> p.priorityScore).reversed()
                    .thenComparingLong(p -> p.intakeTime);

    private final PriorityQueue<Patient> waitingList;

    public ZombieTriageApp() {
        waitingList = new PriorityQueue<>(TRIAGE_ORDER);
    }

    public void intakePatient(Scanner scanner) {
        System.out.println("\n--- RAPID TRIAGE INTAKE ---");
        System.out.println("Answer each question as clearly and quickly as possible.\n");

        System.out.print("Patient name: ");
        String name = readNonEmptyString(scanner);

        int complaintChoice = readComplaintChoice(scanner);
        int age = readIntInRange(scanner, "Patient age: ", 0, 130);
        double weight = readDoubleInRange(scanner, "Patient weight (lbs): ", 1, 1400);
        int stage = readIntInRange(scanner, "Suspected infection stage (0-6): ", 0, 6);

        boolean throatTightness = readYesNo(scanner, "Is the patient having throat tightness or trouble breathing? (yes/no): ");
        boolean hotEnvironmentExposure = readYesNo(scanner, "Was the patient recently exposed to extreme heat? (yes/no): ");
        boolean cleanEnvironment = readYesNo(scanner, "Was the patient in a clean or sterile place before arrival? (yes/no): ");
        boolean recentlyTurnedUnder10Min = readYesNo(scanner, "Did the patient fully turn within the last 10 minutes? (yes/no): ");

        boolean highRiskMedicalHistory = readYesNo(scanner, "Does the patient have any serious medical history? (yes/no): ");
        boolean isOverweight = readYesNo(scanner, "Is the patient considered overweight by health standards? (yes/no): ");

        ImmuneStrength immuneStrength = readImmuneStrength(scanner);

        boolean hasRespiratoryDisease = readYesNo(scanner, "Does the patient have a lung or breathing condition, such as asthma or COPD? (yes/no): ");
        boolean hasDiabetes = readYesNo(scanner, "Does the patient have diabetes? (yes/no): ");
        boolean hasNeurologicalDisorder = readYesNo(scanner, "Does the patient have a neurological condition? (yes/no): ");

        ExposureLevel exposureLevel = readExposureLevel(scanner);
        MutationLevel mutationLevel = readMutationLevel(scanner);

        boolean delayedSymptoms = readYesNo(scanner, "Do the symptoms seem delayed, mild, or unusual for the stage? (yes/no): ");
        boolean highStressSuppression = readYesNo(scanner, "Could stress or adrenaline be hiding the true severity of symptoms? (yes/no): ");
        boolean partialTransformationAwareness = readYesNo(scanner, "Does the patient still show awareness, memory, or recognition? (yes/no): ");

        HumidityLevel humidityLevel = readHumidityLevel(scanner);
        boolean highUVSunlightExposure = readYesNo(scanner, "Was the patient recently exposed to strong sunlight or UV? (yes/no): ");
        PopulationDensity populationDensity = readPopulationDensity(scanner);
        boolean nighttimeExposure = readYesNo(scanner, "Did the exposure happen mostly at night? (yes/no): ");

        int dosesRequired = readIntInRange(scanner, "Estimated vaccine doses needed (1-3): ", 1, 3);
        boolean vaccineSideEffectRisk = readYesNo(scanner, "Is the patient at high risk for a dangerous vaccine reaction? (yes/no): ");
        int vaccineDaysSincePrepared = readIntInRange(scanner, "How old is the vaccine batch in days? ", 0, 30);
        boolean storageCompromised = readYesNo(scanner, "Was the vaccine storage compromised at any point? (yes/no): ");
        boolean powerOutageOrTransportDelay = readYesNo(scanner, "Were there power outages or transport delays affecting storage? (yes/no): ");

        boolean falsePositiveRisk = readYesNo(scanner, "Is there a real chance the patient was incorrectly marked infected? (yes/no): ");
        boolean falseNegativeRisk = readYesNo(scanner, "Is there a real chance the patient is infected even if signs are unclear? (yes/no): ");
        int testingTimeMinutes = readIntInRange(scanner, "Estimated testing time in minutes: ", 0, 180);

        ZombieType zombieType = readZombieType(scanner);
        boolean sensoryAttractionRisk = readYesNo(scanner, "Could sound or body heat easily attract nearby zombies? (yes/no): ");
        boolean swarmRisk = readYesNo(scanner, "Is there an active swarm risk in the area? (yes/no): ");

        InfectionCertainty infectionCertainty = readInfectionCertainty(scanner);
        PriorityGroup priorityGroup = readPriorityGroup(scanner);
        boolean vaccineTradeoffConcern = readYesNo(scanner, "Would using the vaccine here likely prevent saving multiple other people? (yes/no): ");

        boolean panicOrDishonesty = readYesNo(scanner, "Is the patient hiding symptoms or giving unreliable answers? (yes/no): ");
        boolean carrierImmunity = readYesNo(scanner, "Could the patient be immune but still able to spread the virus? (yes/no): ");
        boolean reinfectionCase = readYesNo(scanner, "Is this a possible reinfection case? (yes/no): ");

        Patient patient = new Patient(
                name,
                complaintChoice,
                age,
                weight,
                stage,

                throatTightness,
                hotEnvironmentExposure,
                cleanEnvironment,
                recentlyTurnedUnder10Min,
                highRiskMedicalHistory,
                isOverweight,

                immuneStrength,
                hasRespiratoryDisease,
                hasDiabetes,
                hasNeurologicalDisorder,
                exposureLevel,
                mutationLevel,

                delayedSymptoms,
                highStressSuppression,
                partialTransformationAwareness,

                humidityLevel,
                highUVSunlightExposure,
                populationDensity,
                nighttimeExposure,

                dosesRequired,
                vaccineSideEffectRisk,
                vaccineDaysSincePrepared,
                storageCompromised,
                powerOutageOrTransportDelay,

                falsePositiveRisk,
                falseNegativeRisk,
                testingTimeMinutes,

                zombieType,
                sensoryAttractionRisk,
                swarmRisk,

                infectionCertainty,
                priorityGroup,
                vaccineTradeoffConcern,

                panicOrDishonesty,
                carrierImmunity,
                reinfectionCase
        );

        waitingList.offer(patient);
        System.out.println("\nPatient registered.");
        System.out.println(patient);
        System.out.println("Notes: " + patient.notes);
    }

    public void seeNextPatient() {
        if (waitingList.isEmpty()) {
            System.out.println("No patients waiting.");
            return;
        }

        Patient next = waitingList.poll();
        System.out.println("\n--- NEXT PATIENT ---");
        System.out.println(next.detailedSummary());
    }

    public void displayWaitingList() {
        if (waitingList.isEmpty()) {
            System.out.println("No patients waiting.");
            return;
        }

        List<Patient> temp = new ArrayList<>(waitingList);
        temp.sort(TRIAGE_ORDER);

        System.out.println("\n--- WAITING LIST ---");
        for (Patient p : temp) {
            System.out.println(p);
        }
    }

    public void displayScenarioRules() {
        System.out.println("\n--- RULES ---");
        System.out.println("1. Virus is a leaked bioweapon and spreads rapidly.");
        System.out.println("2. Vaccine exists, but supply and storage are limited.");
        System.out.println("3. Vaccine lasts 14 days when cooled and 3 days if cooling fails.");
        System.out.println("4. Stage 1-3 patients are usually the most curable.");
        System.out.println("5. Patients under 16 in stages 1-3 are top priority.");
        System.out.println("6. Stage 4 patients are quarantined.");
        System.out.println("7. Stage 5 patients go to strict quarantine.");
        System.out.println("8. Stage 6 youth are quarantined.");
        System.out.println("9. Many stage 6 adults are euthanized and incinerated.");
        System.out.println("10. Mutation, weak immunity, humidity, and urban density increase urgency.");
        System.out.println("11. Old vaccine may only suppress symptoms temporarily.");
        System.out.println("12. Resource trade-offs matter when supply is limited.");
    }

    private static int readMainMenuChoice(Scanner scanner) {
        while (true) {
            System.out.println("\n=== ZOMBIE TRIAGE ===");
            System.out.println("1. Register patient");
            System.out.println("2. Call next");
            System.out.println("3. View waiting list");
            System.out.println("4. View rules");
            System.out.println("5. Exit");
            System.out.print("Choice (1-5): ");

            String input = scanner.nextLine().trim();
            try {
                int choice = Integer.parseInt(input);
                if (choice >= 1 && choice <= 5) {
                    return choice;
                }
            } catch (NumberFormatException e) {
                // retry
            }

            System.out.println("Enter 1-5.");
        }
    }

    private int readComplaintChoice(Scanner scanner) {
        while (true) {
            System.out.println("\nMain concern:");
            System.out.println("1. Bite or direct infection");
            System.out.println("2. Fever or flu-like signs");
            System.out.println("3. Aggression or confusion");
            System.out.println("4. Breathing problem");
            System.out.println("5. Recently turned");
            System.out.println("6. Exposure with unclear signs");
            System.out.println("7. Medical staff exposure");
            System.out.println("8. Possible symptom-free carrier");
            System.out.print("Choice (1-8): ");

            String input = scanner.nextLine().trim();
            try {
                int choice = Integer.parseInt(input);
                if (choice >= 1 && choice <= 8) {
                    return choice;
                }
            } catch (NumberFormatException e) {
                // retry
            }

            System.out.println("Enter 1-8.");
        }
    }

    private ImmuneStrength readImmuneStrength(Scanner scanner) {
        while (true) {
            System.out.println("\nImmune status:");
            System.out.println("1. Strong");
            System.out.println("2. Normal");
            System.out.println("3. Weak");
            System.out.println("4. Immunocompromised");
            System.out.print("Choice (1-4): ");
            int choice = readRawInt(scanner);
            switch (choice) {
                case 1: return ImmuneStrength.STRONG;
                case 2: return ImmuneStrength.NORMAL;
                case 3: return ImmuneStrength.WEAK;
                case 4: return ImmuneStrength.IMMUNOCOMPROMISED;
                default: System.out.println("Enter 1-4.");
            }
        }
    }

    private ExposureLevel readExposureLevel(Scanner scanner) {
        while (true) {
            System.out.println("\nExposure level:");
            System.out.println("1. Low");
            System.out.println("2. Moderate");
            System.out.println("3. High");
            System.out.println("4. Extreme");
            System.out.print("Choice (1-4): ");
            int choice = readRawInt(scanner);
            switch (choice) {
                case 1: return ExposureLevel.LOW;
                case 2: return ExposureLevel.MODERATE;
                case 3: return ExposureLevel.HIGH;
                case 4: return ExposureLevel.EXTREME;
                default: System.out.println("Enter 1-4.");
            }
        }
    }

    private MutationLevel readMutationLevel(Scanner scanner) {
        while (true) {
            System.out.println("\nVirus mutation level:");
            System.out.println("1. None");
            System.out.println("2. Low");
            System.out.println("3. Moderate");
            System.out.println("4. High");
            System.out.print("Choice (1-4): ");
            int choice = readRawInt(scanner);
            switch (choice) {
                case 1: return MutationLevel.NONE;
                case 2: return MutationLevel.LOW;
                case 3: return MutationLevel.MODERATE;
                case 4: return MutationLevel.HIGH;
                default: System.out.println("Enter 1-4.");
            }
        }
    }

    private HumidityLevel readHumidityLevel(Scanner scanner) {
        while (true) {
            System.out.println("\nHumidity level:");
            System.out.println("1. Low");
            System.out.println("2. Moderate");
            System.out.println("3. High");
            System.out.print("Choice (1-3): ");
            int choice = readRawInt(scanner);
            switch (choice) {
                case 1: return HumidityLevel.LOW;
                case 2: return HumidityLevel.MODERATE;
                case 3: return HumidityLevel.HIGH;
                default: System.out.println("Enter 1-3.");
            }
        }
    }

    private PopulationDensity readPopulationDensity(Scanner scanner) {
        while (true) {
            System.out.println("\nExposure area:");
            System.out.println("1. Rural");
            System.out.println("2. Suburban");
            System.out.println("3. Urban");
            System.out.print("Choice (1-3): ");
            int choice = readRawInt(scanner);
            switch (choice) {
                case 1: return PopulationDensity.RURAL;
                case 2: return PopulationDensity.SUBURBAN;
                case 3: return PopulationDensity.URBAN;
                default: System.out.println("Enter 1-3.");
            }
        }
    }

    private ZombieType readZombieType(Scanner scanner) {
        while (true) {
            System.out.println("\nObserved behavior:");
            System.out.println("1. None / not applicable");
            System.out.println("2. Fresh zombie behavior");
            System.out.println("3. Older zombie behavior");
            System.out.print("Choice (1-3): ");
            int choice = readRawInt(scanner);
            switch (choice) {
                case 1: return ZombieType.NOT_A_ZOMBIE;
                case 2: return ZombieType.FRESH;
                case 3: return ZombieType.OLD;
                default: System.out.println("Enter 1-3.");
            }
        }
    }

    private InfectionCertainty readInfectionCertainty(Scanner scanner) {
        while (true) {
            System.out.println("\nInfection status:");
            System.out.println("1. Confirmed");
            System.out.println("2. Suspected");
            System.out.println("3. Unknown");
            System.out.print("Choice (1-3): ");
            int choice = readRawInt(scanner);
            switch (choice) {
                case 1: return InfectionCertainty.CONFIRMED;
                case 2: return InfectionCertainty.SUSPECTED;
                case 3: return InfectionCertainty.UNKNOWN;
                default: System.out.println("Enter 1-3.");
            }
        }
    }

    private PriorityGroup readPriorityGroup(Scanner scanner) {
        while (true) {
            System.out.println("\nPriority category:");
            System.out.println("1. Child");
            System.out.println("2. Adult");
            System.out.println("3. Senior");
            System.out.println("4. Medical staff");
            System.out.print("Choice (1-4): ");
            int choice = readRawInt(scanner);
            switch (choice) {
                case 1: return PriorityGroup.CHILD;
                case 2: return PriorityGroup.ADULT;
                case 3: return PriorityGroup.SENIOR;
                case 4: return PriorityGroup.MEDICAL_PERSONNEL;
                default: System.out.println("Enter 1-4.");
            }
        }
    }

    private int readIntInRange(Scanner scanner, String prompt, int min, int max) {
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine().trim();
            try {
                int value = Integer.parseInt(input);
                if (value >= min && value <= max) {
                    return value;
                }
                System.out.println("Enter " + min + "-" + max + ".");
            } catch (NumberFormatException e) {
                System.out.println("Enter a whole number.");
            }
        }
    }

    private double readDoubleInRange(Scanner scanner, String prompt, double min, double max) {
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine().trim();
            try {
                double value = Double.parseDouble(input);
                if (value >= min && value <= max) {
                    return value;
                }
                System.out.println("Enter " + min + "-" + max + ".");
            } catch (NumberFormatException e) {
                System.out.println("Enter a valid number.");
            }
        }
    }

    private boolean readYesNo(Scanner scanner, String prompt) {
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine().trim().toLowerCase();
            if (input.equals("yes") || input.equals("y")) {
                return true;
            }
            if (input.equals("no") || input.equals("n")) {
                return false;
            }
            System.out.println("Enter yes or no.");
        }
    }

    private String readNonEmptyString(Scanner scanner) {
        while (true) {
            String input = scanner.nextLine().trim();
            if (!input.isEmpty()) {
                return input;
            }
            System.out.print("Cannot be blank. Enter value: ");
        }
    }

    private int readRawInt(Scanner scanner) {
        String input = scanner.nextLine().trim();
        try {
            return Integer.parseInt(input);
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    public static void main(String[] args) {
        ZombieTriageApp app = new ZombieTriageApp();
        Scanner scanner = new Scanner(System.in);
        boolean running = true;

        while (running) {
            int choice = readMainMenuChoice(scanner);

            switch (choice) {
                case 1:
                    app.intakePatient(scanner);
                    break;
                case 2:
                    app.seeNextPatient();
                    break;
                case 3:
                    app.displayWaitingList();
                    break;
                case 4:
                    app.displayScenarioRules();
                    break;
                case 5:
                    running = false;
                    System.out.println("Exiting system.");
                    break;
                default:
                    System.out.println("Unexpected error.");
            }
        }

        scanner.close();
    }
}