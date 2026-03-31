import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class ActivityDAO {

    private Connection conn() {
        return DatabaseManager.getInstance().getConnection();
    }

    public void insert(Activity a, double weightKg) throws SQLException {
        String sql = "INSERT INTO activities (activity_type,name,duration_min,calories,log_date,notes,extra1,extra2) VALUES (?,?,?,?,?,?,?,?)";
        PreparedStatement ps = conn().prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
        ps.setString(1, a.getActivityType());
        ps.setString(2, a.getName());
        ps.setInt(3, a.getDurationMinutes());
        ps.setDouble(4, a.calculateCaloriesBurned(weightKg));
        ps.setString(5, a.getLogDate().toString());
        ps.setString(6, a.getNotes());

        if (a instanceof CardioActivity) {
            ps.setString(7, String.valueOf(((CardioActivity) a).getDistanceKm()));
            ps.setNull(8, Types.VARCHAR);
        } else if (a instanceof StrengthActivity) {
            ps.setString(7, String.valueOf(((StrengthActivity) a).getSets()));
            ps.setString(8, String.valueOf(((StrengthActivity) a).getReps()));
        } else if (a instanceof FlexibilityActivity) {
            ps.setString(7, ((FlexibilityActivity) a).getStyle());
            ps.setNull(8, Types.VARCHAR);
        } else {
            ps.setNull(7, Types.VARCHAR);
            ps.setNull(8, Types.VARCHAR);
        }

        ps.executeUpdate();
        ResultSet keys = ps.getGeneratedKeys();
        if (keys.next()) a.setId(keys.getInt(1));
        ps.close();
    }

    public List<Activity> findAll() throws SQLException {
        List<Activity> list = new ArrayList<>();
        ResultSet rs = conn().createStatement().executeQuery("SELECT * FROM activities ORDER BY log_date DESC");
        while (rs.next()) list.add(map(rs));
        return list;
    }

    public List<Activity> findByDateRange(LocalDate from, LocalDate to) throws SQLException {
        List<Activity> list = new ArrayList<>();
        PreparedStatement ps = conn().prepareStatement(
            "SELECT * FROM activities WHERE log_date BETWEEN ? AND ? ORDER BY log_date");
        ps.setString(1, from.toString());
        ps.setString(2, to.toString());
        ResultSet rs = ps.executeQuery();
        while (rs.next()) list.add(map(rs));
        ps.close();
        return list;
    }

    public boolean delete(int id) throws SQLException {
        PreparedStatement ps = conn().prepareStatement("DELETE FROM activities WHERE id=?");
        ps.setInt(1, id);
        int rows = ps.executeUpdate();
        ps.close();
        return rows > 0;
    }

    private Activity map(ResultSet rs) throws SQLException {
        int id = rs.getInt("id");
        String type = rs.getString("activity_type");
        String name = rs.getString("name");
        int dur = rs.getInt("duration_min");
        LocalDate date = LocalDate.parse(rs.getString("log_date"));
        String notes = rs.getString("notes");
        String extra1 = rs.getString("extra1");
        String extra2 = rs.getString("extra2");

        switch (type) {
            case "CARDIO":
                return new CardioActivity(id, name, dur, date, notes,
                    extra1 != null ? Double.parseDouble(extra1) : 0);
            case "STRENGTH":
                return new StrengthActivity(id, name, dur, date, notes,
                    extra1 != null ? Integer.parseInt(extra1) : 0,
                    extra2 != null ? Integer.parseInt(extra2) : 0);
            default:
                return new FlexibilityActivity(id, name, dur, date, notes,
                    extra1 != null ? extra1 : "");
        }
    }
}
