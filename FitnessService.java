import java.sql.SQLException;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

public class FitnessService {

    private final ActivityDAO activityDAO = new ActivityDAO();
    private final DailyLogDAO dailyLogDAO = new DailyLogDAO();
    private double userWeightKg = 70.0;

    public void setUserWeight(double kg) { this.userWeightKg = kg; }
    public double getUserWeight() { return userWeightKg; }

    public void addActivity(Activity a) throws SQLException {
        activityDAO.insert(a, userWeightKg);
    }

    public List<Activity> getAllActivities() throws SQLException {
        return activityDAO.findAll();
    }

    public List<Activity> searchByDateRange(LocalDate from, LocalDate to) throws SQLException {
        return activityDAO.findByDateRange(from, to);
    }

    public boolean deleteActivity(int id) throws SQLException {
        return activityDAO.delete(id);
    }

    public void logDaily(DailyLog log) throws SQLException {
        dailyLogDAO.upsert(log);
    }

    public List<DailyLog> getAllDailyLogs() throws SQLException {
        return dailyLogDAO.findAll();
    }

    public Map<String, Object> getWeeklySummary() throws SQLException {
        LocalDate to = LocalDate.now();
        LocalDate from = to.minusDays(6);
        List<Activity> acts = activityDAO.findByDateRange(from, to);
        List<DailyLog> logs = dailyLogDAO.findByDateRange(from, to);

        Map<String, Object> summary = new LinkedHashMap<>();
        summary.put("period", from + " to " + to);
        summary.put("totalWorkouts", acts.size());
        summary.put("totalMinutes", acts.stream().mapToInt(Activity::getDurationMinutes).sum());
        summary.put("totalCalories", acts.stream()
            .mapToDouble(a -> a.calculateCaloriesBurned(userWeightKg)).sum());

        Map<String, Long> byType = acts.stream()
            .collect(Collectors.groupingBy(Activity::getActivityType, Collectors.counting()));
        summary.put("byType", byType);

        double avgSleep = logs.stream().mapToDouble(DailyLog::getSleepHours).average().orElse(0);
        double avgWater = logs.stream().mapToDouble(DailyLog::getWaterMl).average().orElse(0);
        double latestWeight = logs.stream()
            .max(Comparator.comparing(DailyLog::getLogDate))
            .map(DailyLog::getWeightKg).orElse(0.0);

        summary.put("avgSleepHours", String.format("%.1f", avgSleep));
        summary.put("avgWaterMl", String.format("%.0f", avgWater));
        summary.put("latestWeightKg", latestWeight);
        return summary;
    }

    public int getCurrentStreak() throws SQLException {
        List<Activity> all = activityDAO.findAll();
        if (all.isEmpty()) return 0;
        Set<LocalDate> activeDays = new HashSet<>();
        all.forEach(a -> activeDays.add(a.getLogDate()));
        int streak = 0;
        LocalDate day = LocalDate.now();
        while (activeDays.contains(day)) {
            streak++;
            day = day.minusDays(1);
        }
        return streak;
    }
}
