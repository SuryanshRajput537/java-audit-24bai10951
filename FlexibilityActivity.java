import java.time.LocalDate;

public class FlexibilityActivity extends Activity {
    private String style;

    public FlexibilityActivity(int id, String name, int durationMinutes, LocalDate logDate, String notes, String style) {
        super(id, name, durationMinutes, logDate, notes);
        this.style = style;
    }

    @Override
    public double calculateCaloriesBurned(double weightKg) {
        return 2.5 * weightKg * (durationMinutes / 60.0);
    }

    @Override
    public String getActivityType() { return "FLEXIBILITY"; }

    public String getStyle() { return style; }
}
