import java.io.*;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

public class ReportWriter {

    private static final String REPORTS_DIR = "reports/";

    public static String exportCSV(List<Activity> activities, double weightKg) throws IOException {
        new File(REPORTS_DIR).mkdirs();
        String filename = REPORTS_DIR + "activities_" +
            LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss")) + ".csv";
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(filename))) {
            bw.write("ID,Type,Name,Duration(min),Calories,Date,Notes");
            bw.newLine();
            for (Activity a : activities) {
                bw.write(String.format("%d,%s,%s,%d,%.1f,%s,%s",
                    a.getId(), a.getActivityType(), a.getName(),
                    a.getDurationMinutes(), a.calculateCaloriesBurned(weightKg),
                    a.getLogDate(), a.getNotes() != null ? a.getNotes() : ""));
                bw.newLine();
            }
        }
        return filename;
    }

    public static String exportTXT(List<Activity> activities, Map<String, Object> summary) throws IOException {
        new File(REPORTS_DIR).mkdirs();
        String filename = REPORTS_DIR + "weekly_report_" +
            LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss")) + ".txt";
        try (PrintWriter pw = new PrintWriter(
                new OutputStreamWriter(new FileOutputStream(filename), StandardCharsets.UTF_8))) {
            pw.println("╔══════════════════════════════════════════════╗");
            pw.println("║       FITTRACK — WEEKLY REPORT               ║");
            pw.println("╚══════════════════════════════════════════════╝");
            pw.println();
            if (summary != null) {
                pw.println("  Period        : " + summary.get("period"));
                pw.println("  Workouts      : " + summary.get("totalWorkouts"));
                pw.println("  Total Minutes : " + summary.get("totalMinutes"));
                pw.printf ("  Total Calories: %.1f%n", (double) summary.get("totalCalories"));
                pw.println("  Avg Sleep     : " + summary.get("avgSleepHours") + " hrs");
                pw.println("  Avg Water     : " + summary.get("avgWaterMl") + " ml");
                pw.println("  Latest Weight : " + summary.get("latestWeightKg") + " kg");
                pw.println();
                pw.println("  Breakdown by Type:");
                @SuppressWarnings("unchecked")
                Map<String, Long> byType = (Map<String, Long>) summary.get("byType");
                if (byType != null) byType.forEach((k, v) -> pw.println("    " + k + ": " + v));
                pw.println();
            }
            pw.println("  ── Activity Log ──────────────────────────────");
            pw.printf("  %-4s %-12s %-20s %-8s %-10s%n", "ID", "Type", "Name", "Mins", "Date");
            pw.println("  " + "─".repeat(58));
            for (Activity a : activities) {
                pw.printf("  %-4d %-12s %-20s %-8d %-10s%n",
                    a.getId(), a.getActivityType(), a.getName(),
                    a.getDurationMinutes(), a.getLogDate());
            }
        }
        return filename;
    }
}
