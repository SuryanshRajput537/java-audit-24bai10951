import java.time.LocalDate;

public class StrengthActivity extends Activity {
    private int sets;
    private int reps;

    public StrengthActivity(int id, String name, int durationMinutes, LocalDate logDate, String notes, int sets, int reps) {
        super(id, name, durationMinutes, logDate, notes);
        this.sets = sets;
        this.reps = reps;
    }

    @Override
    public double calculateCaloriesBurned(double weightKg) {
        return 5.0 * weightKg * (durationMinutes / 60.0);
    }

    @Override
    public String getActivityType() { return "STRENGTH"; }

    public int getSets() { return sets; }
    public int getReps() { return reps; }
}
