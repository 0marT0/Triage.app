import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.PriorityQueue;
import java.util.Scanner;

public class TriageQueue {

    // ─────────────────────────────────────────────
    //  Mental state — affects virus spread speed
    //  More negative = faster zombification
    // ─────────────────────────────────────────────
    enum MentalState {
        STABLE    ("Stable",     0),      // no effect
        ANXIOUS   ("Anxious",    1),      // mild acceleration
        DEPRESSED ("Depressed",  2),      // moderate acceleration
        SEVERE    ("Severe",     3),      // strong acceleration
        CRISIS    ("Crisis",     4);      // maximum acceleration

        final String label;
        final int    level;   // 0–4 severity

        MentalState(String label, int level) {
            this.label = label;
            this.level = level;
        }

        // Urgency multiplier applied to elapsed-time score
        double spreadMultiplier() {
            return 1.0 + (level * 0.25);  // 1.0 → 2.0
        }

        // Flat score penalty — negative mindset accelerates progression
        int scorePenalty() {
            return level * 400;           // 0 → 1600
        }
    }

    // ─────────────────────────────────────────────
    //  Patient record
    // ─────────────────────────────────────────────
    static class Patient implements Comparable<Patient> {
        String      id;
        int         age;
        int         stage;
        boolean     essential;
        boolean     hotEnvironment;
        boolean     throatTightness;
        boolean     sterileEnvironment;
        MentalState mentalState;
        String      admittedAt;
        int         priorityScore;

        // -1 = unknown/not applicable; 0–10 = within last-resort window; >10 = past window
        int minutesToTransformation;

        Patient(String id, int age, int stage,
                boolean essential, boolean hotEnvironment,
                boolean throatTightness, boolean sterileEnvironment,
                MentalState mentalState, int minutesToTransformation) {

            if (stage < 1 || stage > 6) throw new IllegalArgumentException("Stage must be 1–6");
            if (age  < 1 || age  > 120) throw new IllegalArgumentException("Age must be 1–120");

            this.id                      = id;
            this.age                     = age;
            this.essential               = essential;
            this.hotEnvironment          = hotEnvironment;
            this.throatTightness         = throatTightness;
            this.sterileEnvironment      = sterileEnvironment;
            this.mentalState             = mentalState;
            this.minutesToTransformation = minutesToTransformation;
            this.admittedAt              = LocalTime.now()
                                                    .format(DateTimeFormatter.ofPattern("HH:mm:ss"));

            // Throat tightness at stage 2 → treat as stage 4
            if (throatTightness && stage == 2) {
                System.out.println("  [WARNING] " + id +
                    ": throat tightness at Stage 2 — possible Stage 4 misread. Treating as Stage 4.");
                stage = 4;
            }
            this.stage = stage;
            this.priorityScore = calcScore();
        }

        // ── Priority scoring ──────────────────────
        private int calcScore() {
            int score = 0;
            boolean isChild  = age < 16;
            boolean isSenior = age >= 65;
            boolean isVulnerable = isChild || isSenior;

            double timeLeft      = isVulnerable ? 15.0 : 24.0;
            double[] stageHours  = {0, 0, 4, 8, 12, 18, 23};
            double hoursElapsed  = stageHours[Math.min(stage, 6)];

            // Mental state accelerates spread — scale elapsed time upward
            double adjustedElapsed = hoursElapsed * mentalState.spreadMultiplier();
            double urgency         = adjustedElapsed / timeLeft;

            // Top priority: children under 16, stages 1–3
            if (isChild && stage <= 3) score += 10000;

            // Curable window
            if (stage <= 3) score += 5000;
            if (stage <= 2) score += 1000;

            // Essential personnel
            if (essential) score += 3000;

            // Vulnerable groups
            if (isChild)  score += 2000;
            if (isSenior) score += 1500;

            // Hot environment / high body mass → faster zombification
            if (hotEnvironment) score += (int)(urgency * 1000);

            // Negative mental state → faster spread → higher urgency penalty
            // More negative = more points added (they need treatment sooner)
            score += (int)(urgency * mentalState.level * 500);
            score -= mentalState.scorePenalty();  // prognosis worsens with severity

            // Sterile environment → slightly lower urgency
            if (sterileEnvironment) score -= 300;

            // Higher stage = worse prognosis
            score -= stage * 800;

            // Stage 5 last-resort floor
            if (stage == 5) score = Math.max(score, 50);

            // Stage 6 = no treatment
            if (stage == 6) score = -9999;

            return score;
        }

        // ── Tier classification ───────────────────
        String tier() {
            boolean isChild      = age < 16;
            boolean isSenior     = age >= 65;
            boolean isVulnerable = isChild || isSenior;
            boolean mentalRisk   = mentalState.level >= 3; // SEVERE or CRISIS

            if (stage == 6)                                      return "NO VACCINE";
            if (isChild && stage <= 3)                           return "TIER 1 — IMMEDIATE";
            if (stage <= 3 && essential)                         return "TIER 1 — IMMEDIATE";
            if (stage <= 3 && isVulnerable)                      return "TIER 1 — IMMEDIATE";
            // Mental crisis at curable stage bumps to Tier 2 — faster spread risk
            if (stage <= 3 && mentalRisk)                        return "TIER 2 — SECONDARY";
            if (stage <= 3)                                       return "TIER 4 — STANDARD";
            if (stage == 4)                                       return "TIER 2 — SECONDARY";
            if (stage == 5)                                       return "TIER 3 — CONDITIONAL";
            return "TIER 5 — MONITOR";
        }

        // ── Vaccination recommendation ────────────
        String vaccinationAdvice(int vaccinesRemaining) {
            boolean isChild      = age < 16;
            boolean isSenior     = age >= 65;
            boolean isVulnerable = isChild || isSenior;
            boolean mentalRisk   = mentalState.level >= 3;
            boolean withinWindow = minutesToTransformation >= 0
                                   && minutesToTransformation <= 10;
            boolean pastWindow   = minutesToTransformation > 10;

            // ── Stage 6 handling ──────────────────────────────────────────────
            if (stage == 6) {
                if (pastWindow)
                    return "*** EUTHANIZE *** — Stage 6, past the 10-minute transformation window "
                         + "(" + minutesToTransformation + " min recorded). Full zombification "
                         + "is irreversible. Neutralise immediately to protect other patients and staff.";
                if (withinWindow)
                    return "VACCINATE NOW (emergency last resort) — Stage 6 but within the "
                         + "10-minute window (" + minutesToTransformation + " min remaining). "
                         + "Administer immediately. If vaccine fails to halt transformation, "
                         + "proceed to euthanasia protocol.";
                // minutesToTransformation unknown for stage 6
                return "*** EUTHANIZE *** — Stage 6, transformation window status unknown. "
                     + "Assume irreversible. Neutralise and contain.";
            }

            // ── No supply ─────────────────────────────────────────────────────
            if (vaccinesRemaining <= 0)
                return "DO NOT VACCINATE — no doses remaining. Place on hold and monitor.";

            // ── Stage 5 ───────────────────────────────────────────────────────
            if (stage == 5) {
                // Check triage list: only vaccinate stage 5 if no stage 1–4 patients are ahead
                return "VACCINATE (last resort, conditional) — Stage 5. Administer ONLY if no "
                     + "Stage 1–4 patients remain in queue. If transformation completes before "
                     + "vaccine takes effect, proceed to euthanasia protocol immediately.";
            }

            // ── Stage 4 ───────────────────────────────────────────────────────
            if (stage == 4) {
                StringBuilder sb = new StringBuilder(
                    "VACCINATE — Stage 4 per triage list (Tier 2 / Secondary). "
                    + "Cure probability is reduced but viable.");
                if (throatTightness)
                    sb.append(" Throat tightness confirmed — Stage 2 misread ruled out.");
                if (mentalRisk)
                    sb.append(" URGENT: ").append(mentalState.label)
                      .append(" mental state is accelerating progression — administer without delay.");
                if (vaccinesRemaining <= 3)
                    sb.append(" [SUPPLY CRITICAL: ").append(vaccinesRemaining)
                      .append(" dose(s) left]");
                return sb.toString();
            }

            // ── Stages 1–3 (curable window) ───────────────────────────────────
            StringBuilder sb = new StringBuilder();

            // Determine triage tier and instruction
            if (isChild && stage <= 3) {
                sb.append("VACCINATE IMMEDIATELY — ");
                sb.append("Triage Tier 1 (highest priority): child under 16, Stage ")
                  .append(stage).append(", 15hr accelerated timeline. No delay permitted.");
            } else if (isVulnerable) {
                sb.append("VACCINATE URGENTLY — ");
                sb.append("Triage Tier 1: ").append(isChild ? "child" : "senior")
                  .append(", Stage ").append(stage)
                  .append(", accelerated 15hr timeline.");
            } else if (essential) {
                sb.append("VACCINATE — ");
                sb.append("Triage Tier 1: essential personnel (medic/security), Stage ")
                  .append(stage).append(". Loss of this individual multiplies overall casualties.");
            } else if (mentalRisk) {
                sb.append("VACCINATE — ");
                sb.append("Triage Tier 2 (bumped from Tier 4 due to mental state): Stage ")
                  .append(stage).append(", ").append(mentalState.label)
                  .append(" — progression accelerated x")
                  .append(String.format("%.2f", mentalState.spreadMultiplier())).append(".");
            } else {
                sb.append("VACCINATE — ");
                sb.append("Triage Tier 4 (Standard): Stage ").append(stage)
                  .append(", within curable window.");
            }

            // Append risk modifiers
            if (mentalRisk && !(stage <= 3 && (isVulnerable || essential)))
                sb.append(" Mental state: ").append(mentalState.label)
                  .append(" (spread x").append(String.format("%.2f", mentalState.spreadMultiplier()))
                  .append(") — treat before state deteriorates further.");
            if (hotEnvironment)
                sb.append(" Hot environment / high body mass: progression faster than baseline.");
            if (vaccinesRemaining <= 3)
                sb.append(" [SUPPLY CRITICAL: ").append(vaccinesRemaining)
                  .append(" dose(s) remaining — confirm no higher-priority patient exists first]");

            return sb.toString();
        }

        // ── Comparable: higher score = higher priority ──
        @Override
        public int compareTo(Patient other) {
            return Integer.compare(other.priorityScore, this.priorityScore);
        }

        // toString does not have access to vaccine count — use toStringWithAdvice() for full output
        @Override
        public String toString() {
            return toStringWithAdvice(-1);  // -1 = suppress advice
        }

        String toStringWithAdvice(int vaccinesRemaining) {
            String flags = ""
                + (age < 16            ? " [CHILD<16]"    : "")
                + (age >= 65           ? " [SENIOR]"      : "")
                + (essential           ? " [ESSENTIAL]"   : "")
                + (hotEnvironment      ? " [HOT-ENV]"     : "")
                + (throatTightness     ? " [THROAT-RISK]" : "")
                + (sterileEnvironment  ? " [STERILE]"     : "")
                + (mentalState != MentalState.STABLE
                                       ? " [MENTAL:" + mentalState.label.toUpperCase() + "]" : "");

            String timeline = (age < 16 || age >= 65)
                ? "│ ! 15hr ACCELERATED TIMELINE\n" : "";

            String mentalWarn = mentalState.level >= 2
                ? "│ ! MENTAL STATE: " + mentalState.label.toUpperCase()
                  + " — spread x" + String.format("%.2f", mentalState.spreadMultiplier()) + "\n"
                : "";

            String transformWarn = "";
            if (stage == 6) {
                if (minutesToTransformation >= 0 && minutesToTransformation <= 10)
                    transformWarn = "│ ! WITHIN 10-MIN WINDOW: " + minutesToTransformation + " min to full transformation\n";
                else if (minutesToTransformation > 10)
                    transformWarn = "│ !! PAST 10-MIN WINDOW: " + minutesToTransformation + " min recorded — IRREVERSIBLE\n";
                else
                    transformWarn = "│ !! STAGE 6 — transformation window unknown\n";
            }

            String advice = (vaccinesRemaining >= 0)
                ? "│\n│ >> " + vaccinationAdvice(vaccinesRemaining) + "\n"
                : "";

            return String.format(
                "┌─────────────────────────────────────────\n" +
                "│ %-10s  Age: %-3d  Stage: %d%s\n"           +
                "│ %s\n"                                        +
                "│ Score: %-6d  Admitted: %s\n"                +
                "%s%s%s%s"                                      +
                "└─────────────────────────────────────────",
                id, age, stage, flags, tier(), priorityScore, admittedAt,
                timeline, mentalWarn, transformWarn, advice
            );
        }
    }

    // ─────────────────────────────────────────────
    //  Triage queue wrapper
    // ─────────────────────────────────────────────
    static class ZombieTriageQueue {
        private final PriorityQueue<Patient> queue = new PriorityQueue<>();
        private int vaccines;

        ZombieTriageQueue(int initialVaccines) {
            this.vaccines = initialVaccines;
        }

        void admit(Patient p) {
            queue.add(p);
            System.out.println("\n[ADMITTED] " + p.id +
                " → " + p.tier() + " (score: " + p.priorityScore + ")");
            System.out.println("  >> " + p.vaccinationAdvice(vaccines));
        }

        Patient next() {
            return queue.peek();
        }

        // Administer vaccine to highest-priority patient
        void vaccinate() {
            if (queue.isEmpty()) {
                System.out.println("[INFO] Queue is empty.");
                return;
            }
            if (vaccines <= 0) {
                System.out.println("[CRITICAL] No vaccines remaining!");
                return;
            }
            Patient p = queue.poll();
            if (p.stage == 6) {
                System.out.println("[DENIED] " + p.id + " is Stage 6 — no treatment possible.");
                return;
            }
            vaccines--;
            System.out.println("\n[VACCINATED] " + p.id +
                " (Stage " + p.stage + ", Age " + p.age + ")" +
                " — " + vaccines + " dose(s) remaining.");
        }

        void printQueue() {
            if (queue.isEmpty()) {
                System.out.println("\n  Queue is empty.\n");
                return;
            }
            System.out.println("\n══════════════════════════════════════════════");
            System.out.println("  PRIORITY QUEUE  (" + queue.size() +
                " patients | " + vaccines + " vaccine(s) remaining)");
            System.out.println("══════════════════════════════════════════════");

            PriorityQueue<Patient> temp = new PriorityQueue<>(queue);
            int rank = 1;
            while (!temp.isEmpty()) {
                System.out.println("\nRANK #" + rank++);
                System.out.println(temp.poll().toStringWithAdvice(vaccines));
            }
            System.out.println();
        }

        int size()     { return queue.size(); }
        int vaccines() { return vaccines; }
    }

    // ─────────────────────────────────────────────
    //  Interactive CLI
    // ─────────────────────────────────────────────
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        ZombieTriageQueue triage = new ZombieTriageQueue(20);

        printBanner();

        boolean running = true;
        while (running) {
            System.out.print("\n> ");
            String line = sc.nextLine().trim();
            if (line.isEmpty()) continue;

            String[] parts = line.split("\\s+");
            String cmd = parts[0].toLowerCase();

            switch (cmd) {
                case "admit", "a" -> admitFast(parts, triage);
                case "vax",   "v" -> triage.vaccinate();
                case "queue", "q" -> triage.printQueue();
                case "next",  "n" -> {
                    Patient next = triage.next();
                    if (next == null) System.out.println("  Queue is empty.");
                    else System.out.println(next.toStringWithAdvice(triage.vaccines()));
                }
                case "help",  "h" -> printHelp();
                case "quit", "exit", "x" -> running = false;
                default -> System.out.println("  Unknown command. Type 'help' for usage.");
            }
        }

        sc.close();
        System.out.println("System shutdown.");
    }

    static void printBanner() {
        System.out.println();
        System.out.println("  ╔══════════════════════════════════════════════════════════╗");
        System.out.println("  ║      ZOMBIOTRIAGE v1.0 — FIELD HOSPITAL TRIAGE CLI      ║");
        System.out.println("  ║              ** TIME-CRITICAL SYSTEM **                 ║");
        System.out.println("  ╚══════════════════════════════════════════════════════════╝");
        System.out.println();
        System.out.println("  COMMANDS");
        System.out.println("  ─────────────────────────────────────────────────────────");
        System.out.println("  admit <data>   Admit a new patient  (shortcut: a)");
        System.out.println("  vax            Vaccinate highest-priority patient  (v)");
        System.out.println("  next           Show next patient + recommendation  (n)");
        System.out.println("  queue          Show full priority queue  (q)");
        System.out.println("  help           Show admit format guide  (h)");
        System.out.println("  quit           Exit  (x)");
        System.out.println();
        System.out.println("  QUICK ADMIT FORMAT");
        System.out.println("  ─────────────────────────────────────────────────────────");
        System.out.println("  admit  <ID>  <age>  <stage 1-6>  [flags]  [mental]  [mins]");
        System.out.println();
        System.out.println("  EXAMPLE   admit PT-01 34 2 E H 1");
        System.out.println("  EXAMPLE   admit PT-02 8 3              (child, no flags)");
        System.out.println("  EXAMPLE   admit PT-03 45 6 T 4 8       (stage 6, 8 mins)");
        System.out.println();
        System.out.println("  Type 'help' to see the full field guide at any time.");
        System.out.println();
    }

    static void printHelp() {
        System.out.println();
        System.out.println("  ╔══════════════════════════════════════════════════════════╗");
        System.out.println("  ║                  ADMIT — FIELD GUIDE                    ║");
        System.out.println("  ╚══════════════════════════════════════════════════════════╝");
        System.out.println();
        System.out.println("  FORMAT:  admit <ID> <age> <stage> [flags] [mental] [mins]");
        System.out.println();
        System.out.println("  ── REQUIRED FIELDS ─────────────────────────────────────");
        System.out.println();
        System.out.println("  ID      Patient identifier. Any text, no spaces.");
        System.out.println("          e.g.  PT-042  or  JOHN  or  BED-7");
        System.out.println();
        System.out.println("  age     Patient age in years (1–120).");
        System.out.println("          Children under 16 and seniors 65+ are flagged");
        System.out.println("          automatically — they have a 15hr timeline vs 24hr.");
        System.out.println();
        System.out.println("  stage   Infection stage 1–6.");
        System.out.println("          1  Early onset — fully curable");
        System.out.println("          2  Mild symptoms — fully curable");
        System.out.println("          3  Moderate — fully curable");
        System.out.println("          4  Advanced — reduced cure chance");
        System.out.println("             NOTE: Stage 2 with throat flag (T) is reclassified");
        System.out.println("             as Stage 4 automatically.");
        System.out.println("          5  Critical — last resort vaccine only");
        System.out.println("          6  Terminal — euthanasia if past 10-min window");
        System.out.println();
        System.out.println("  ── OPTIONAL FLAGS (add in any order after stage) ────────");
        System.out.println();
        System.out.println("  E       Essential personnel.");
        System.out.println("          Doctors, nurses, soldiers, security.");
        System.out.println("          Loss of essential staff multiplies casualties.");
        System.out.println();
        System.out.println("  H       Hot environment / high body mass.");
        System.out.println("          Accelerates zombification — increases urgency.");
        System.out.println();
        System.out.println("  T       Throat tightness / breathing difficulty.");
        System.out.println("          Indicates possible Stage 2 → Stage 4 misread.");
        System.out.println("          System will reclassify automatically if Stage 2.");
        System.out.println();
        System.out.println("  S       Sterile environment (hospital / clean home).");
        System.out.println("          Reduces spread risk — slightly lowers urgency.");
        System.out.println();
        System.out.println("  ── MENTAL HEALTH STATE (0–4, default = 0) ───────────────");
        System.out.println();
        System.out.println("  More negative mental state = faster virus spread.");
        System.out.println("  Enter a single digit after any flags.");
        System.out.println();
        System.out.println("  0   Stable     — no effect on spread");
        System.out.println("  1   Anxious    — mild acceleration (x1.25)");
        System.out.println("  2   Depressed  — moderate acceleration (x1.50)");
        System.out.println("  3   Severe     — strong acceleration (x1.75)");
        System.out.println("                   bumps curable patients to Tier 2");
        System.out.println("  4   Crisis     — maximum acceleration (x2.00)");
        System.out.println("                   treat immediately before state worsens");
        System.out.println();
        System.out.println("  ── MINUTES TO TRANSFORMATION (stage 5/6 only) ───────────");
        System.out.println();
        System.out.println("  Enter estimated minutes remaining before full zombification.");
        System.out.println("  Only relevant for Stage 5 and Stage 6 patients.");
        System.out.println();
        System.out.println("  Stage 6 decision logic:");
        System.out.println("    <= 10 min  →  VACCINATE (emergency last resort)");
        System.out.println("    >  10 min  →  EUTHANIZE (irreversible, contain immediately)");
        System.out.println("    unknown    →  EUTHANIZE (assume irreversible)");
        System.out.println();
        System.out.println("  ── EXAMPLES ─────────────────────────────────────────────");
        System.out.println();
        System.out.println("  admit PT-01 34 2 E H 1");
        System.out.println("    ID=PT-01  age=34  stage=2  essential  hot-env  anxious");
        System.out.println();
        System.out.println("  admit PT-02 8 3");
        System.out.println("    ID=PT-02  age=8  stage=3  (child — auto Tier 1)");
        System.out.println();
        System.out.println("  admit PT-03 45 6 T 4 8");
        System.out.println("    ID=PT-03  age=45  stage=6  throat-risk  crisis  8-mins");
        System.out.println("    → within window: VACCINATE NOW or proceed to euthanasia");
        System.out.println();
        System.out.println("  admit PT-04 72 5 S 3 5");
        System.out.println("    ID=PT-04  age=72  stage=5  sterile  severe-mental  5-mins");
        System.out.println();
    }

    // ─────────────────────────────────────────────
    //  Fast single-line admit parser
    //  Usage: admit <ID> <age> <stage> [E] [H] [T] [S] [mental 0-4] [mins]
    //  Flags and mental level can appear in any order after stage.
    //  mins is only used for stage 5/6 and must be a plain integer.
    // ─────────────────────────────────────────────
    static void admitFast(String[] parts, ZombieTriageQueue triage) {
        // Need at least: admit ID age stage
        if (parts.length < 4) {
            System.out.println("  Usage: admit <ID> <age> <stage> [E] [H] [T] [S] [0-4] [mins]");
            return;
        }
        try {
            String id    = parts[1];
            int    age   = Integer.parseInt(parts[2]);
            int    stage = Integer.parseInt(parts[3]);

            boolean essential   = false;
            boolean hot         = false;
            boolean throat      = false;
            boolean sterile     = false;
            int     mentalLevel = 0;
            int     mins        = -1;

            // Parse remaining tokens — flags, mental level, and optional mins
            for (int i = 4; i < parts.length; i++) {
                String tok = parts[i].toUpperCase();
                switch (tok) {
                    case "E"  -> essential = true;
                    case "H"  -> hot       = true;
                    case "T"  -> throat    = true;
                    case "S"  -> sterile   = true;
                    default   -> {
                        int val = Integer.parseInt(tok); // throws if not a number
                        if (val >= 0 && val <= 4 && mentalLevel == 0 && mins == -1) {
                            mentalLevel = val;           // first small int = mental level
                        } else {
                            mins = val;                  // second int (or >4) = mins to transform
                        }
                    }
                }
            }

            MentalState mental = switch (mentalLevel) {
                case 1  -> MentalState.ANXIOUS;
                case 2  -> MentalState.DEPRESSED;
                case 3  -> MentalState.SEVERE;
                case 4  -> MentalState.CRISIS;
                default -> MentalState.STABLE;
            };

            Patient p = new Patient(id, age, stage, essential, hot, throat, sterile, mental, mins);
            triage.admit(p);

        } catch (NumberFormatException e) {
            System.out.println("  [ERROR] Bad number in entry. Format: admit ID age stage [E H T S] [0-4] [mins]");
        } catch (IllegalArgumentException e) {
            System.out.println("  [ERROR] " + e.getMessage());
        }
    }
}
