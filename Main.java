import java.time.LocalDate;
import java.util.*;

public class Main {

    static Scanner sc = new Scanner(System.in);
    static FitnessService service = new FitnessService();

    public static void main(String[] args) {
        printBanner();
        System.out.print("Enter your weight in kg (for calorie calc, default 70): ");
        try {
            String w = sc.nextLine().trim();
            if (!w.isEmpty()) service.setUserWeight(Double.parseDouble(w));
        } catch (Exception ignored) {}

        int choice;
        do {
            printMenu();
            choice = readInt("Choice: ");
            sc.nextLine();
            try {
                switch (choice) {
                    case 1: logWorkout();        break;
                    case 2: viewAllActivities(); break;
                    case 3: logDailyStats();     break;
                    case 4: viewWeeklySummary(); break;
                    case 5: viewStreak();        break;
                    case 6: searchByDate();      break;
                    case 7: deleteActivity();    break;
                    case 8: exportReports();     break;
                    case 0: System.out.println("\n  Goodbye! Stay consistent. \uD83D\uDCAA\n"); break;
                    default: System.out.println("  Invalid option.");
                }
            } catch (Exception e) {
                System.err.println("  Error: " + e.getMessage());
            }
        } while (choice != 0);

        DatabaseManager.getInstance().close();
    }

    static void printBanner() {
        System.out.println();
        System.out.println("  \u2554\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2557");
        System.out.println("  \u2551       FITTRACK  \u2014 Health Logger  v1.0        \u2551");
        System.out.println("  \u2551   Log Workouts \u2022 Sleep \u2022 Water \u2022 Weight      \u2551");
        System.out.println("  \u255A\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u255D");
        System.out.println();
    }

    static void printMenu() {
        System.out.println();
        System.out.println("  \u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550 MAIN MENU \u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550");
        System.out.println("    1.  Log Workout");
        System.out.println("    2.  View All Activities");
        System.out.println("    3.  Log Daily Stats");
        System.out.println("    4.  Weekly Summary");
        System.out.println("    5.  View Streak");
        System.out.println("    6.  Search by Date Range");
        System.out.println("    7.  Delete Activity");
        System.out.println("    8.  Export Reports (CSV + TXT)");
        System.out.println("    0.  Exit");
        System.out.println("  \u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550");
    }

    static void logWorkout() throws Exception {
        System.out.println("\n  Type: 1=Cardio  2=Strength  3=Flexibility");
        int type = readInt("  Select type: "); sc.nextLine();

        System.out.print("  Name (e.g. Morning Run): "); String name = sc.nextLine();
        int dur = readInt("  Duration (minutes): "); sc.nextLine();
        System.out.print("  Date (YYYY-MM-DD, blank=today): "); String ds = sc.nextLine().trim();
        LocalDate date = ds.isEmpty() ? LocalDate.now() : LocalDate.parse(ds);
        System.out.print("  Notes (optional): "); String notes = sc.nextLine();

        Activity a;
        if (type == 1) {
            double dist = readDouble("  Distance (km): "); sc.nextLine();
            a = new CardioActivity(0, name, dur, date, notes, dist);
        } else if (type == 2) {
            int sets = readInt("  Sets: "); sc.nextLine();
            int reps = readInt("  Reps: "); sc.nextLine();
            a = new StrengthActivity(0, name, dur, date, notes, sets, reps);
        } else {
            System.out.print("  Style (e.g. Hatha/Vinyasa): "); String style = sc.nextLine();
            a = new FlexibilityActivity(0, name, dur, date, notes, style);
        }

        service.addActivity(a);
        System.out.printf("  Logged! Estimated calories: %.1f%n",
            a.calculateCaloriesBurned(service.getUserWeight()));
    }

    static void viewAllActivities() throws Exception {
        List<Activity> list = service.getAllActivities();
        if (list.isEmpty()) { System.out.println("  No activities logged yet."); return; }
        System.out.printf("%n  %-4s %-12s %-22s %-8s %-10s %-8s%n",
            "ID", "Type", "Name", "Mins", "Date", "Cal");
        System.out.println("  " + "-".repeat(70));
        for (Activity a : list)
            System.out.printf("  %-4d %-12s %-22s %-8d %-10s %-8.0f%n",
                a.getId(), a.getActivityType(), a.getName(),
                a.getDurationMinutes(), a.getLogDate(),
                a.calculateCaloriesBurned(service.getUserWeight()));
    }

    static void logDailyStats() throws Exception {
        System.out.print("  Date (YYYY-MM-DD, blank=today): ");
        String ds = sc.nextLine().trim();
        LocalDate date = ds.isEmpty() ? LocalDate.now() : LocalDate.parse(ds);
        double water = readDouble("  Water intake (ml): "); sc.nextLine();
        double sleep = readDouble("  Sleep (hours): ");    sc.nextLine();
        double weight = readDouble("  Weight (kg): ");     sc.nextLine();
        service.logDaily(new DailyLog(0, date, water, sleep, weight));
        System.out.println("  Daily stats saved.");
    }

    static void viewWeeklySummary() throws Exception {
        Map<String, Object> s = service.getWeeklySummary();
        System.out.println("\n  == WEEKLY SUMMARY ==");
        s.forEach((k, v) -> System.out.println("  " + k + ": " + v));
    }

    static void viewStreak() throws Exception {
        int streak = service.getCurrentStreak();
        System.out.println("\n  Current Streak: " + streak + " day(s)");
        if (streak == 0) System.out.println("  Log a workout today to start your streak!");
        else System.out.println("  Keep it going!");
    }

    static void searchByDate() throws Exception {
        System.out.print("  From (YYYY-MM-DD): "); String f = sc.nextLine();
        System.out.print("  To   (YYYY-MM-DD): "); String t = sc.nextLine();
        List<Activity> list = service.searchByDateRange(LocalDate.parse(f), LocalDate.parse(t));
        if (list.isEmpty()) System.out.println("  No activities found in range.");
        else list.forEach(a -> System.out.printf("  [%d] %s - %s (%d min) on %s%n",
            a.getId(), a.getActivityType(), a.getName(),
            a.getDurationMinutes(), a.getLogDate()));
    }

    static void deleteActivity() throws Exception {
        int id = readInt("  Enter Activity ID to delete: "); sc.nextLine();
        System.out.print("  Confirm delete? (y/n): ");
        if ("y".equalsIgnoreCase(sc.nextLine().trim())) {
            boolean ok = service.deleteActivity(id);
            System.out.println(ok ? "  Deleted." : "  ID not found.");
        }
    }

    static void exportReports() throws Exception {
        List<Activity> list = service.getAllActivities();
        Map<String, Object> summary = service.getWeeklySummary();
        String csv = ReportWriter.exportCSV(list, service.getUserWeight());
        String txt = ReportWriter.exportTXT(list, summary);
        System.out.println("  CSV saved: " + csv);
        System.out.println("  TXT saved: " + txt);
    }

    static int readInt(String prompt) {
        while (true) {
            System.out.print(prompt);
            try { return Integer.parseInt(sc.nextLine().trim()); }
            catch (Exception e) { System.out.println("  Please enter a valid number."); }
        }
    }

    static double readDouble(String prompt) {
        System.out.print(prompt);
        try { return Double.parseDouble(sc.nextLine().trim()); }
        catch (Exception e) { return 0; }
    }
}
