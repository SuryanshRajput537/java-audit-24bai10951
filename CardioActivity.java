import java.time.LocalDate;

public class CardioActivity extends Activity {
    private double distanceKm;

    public CardioActivity(int id, String name, int durationMinutes, LocalDate logDate, String notes, double distanceKm) {
        super(id, name, durationMinutes, logDate, notes);
        this.distanceKm = distanceKm;
    }

    @Override
    public double calculateCaloriesBurned(double weightKg) {
        return 7.0 * weightKg * (durationMinutes / 60.0);
    }

    @Override
    public String getActivityType() { return "CARDIO"; }

    public double getDistanceKm() { return distanceKm; }
}
