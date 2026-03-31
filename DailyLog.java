import java.time.LocalDate;

public class DailyLog {
    private int id;
    private LocalDate logDate;
    private double waterMl;
    private double sleepHours;
    private double weightKg;

    public DailyLog(int id, LocalDate logDate, double waterMl, double sleepHours, double weightKg) {
        this.id = id;
        this.logDate = logDate;
        this.waterMl = waterMl;
        this.sleepHours = sleepHours;
        this.weightKg = weightKg;
    }

    public int getId() { return id; }
    public LocalDate getLogDate() { return logDate; }
    public double getWaterMl() { return waterMl; }
    public double getSleepHours() { return sleepHours; }
    public double getWeightKg() { return weightKg; }
}
