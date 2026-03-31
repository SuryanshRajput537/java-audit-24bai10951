import java.time.LocalDate;

public abstract class Activity {
    protected int id;
    protected String name;
    protected int durationMinutes;
    protected LocalDate logDate;
    protected String notes;

    public Activity(int id, String name, int durationMinutes, LocalDate logDate, String notes) {
        this.id = id;
        this.name = name;
        this.durationMinutes = durationMinutes;
        this.logDate = logDate;
        this.notes = notes;
    }

    public abstract double calculateCaloriesBurned(double weightKg);
    public abstract String getActivityType();

    public int getId() { return id; }
    public String getName() { return name; }
    public int getDurationMinutes() { return durationMinutes; }
    public LocalDate getLogDate() { return logDate; }
    public String getNotes() { return notes; }
    public void setId(int id) { this.id = id; }
}
