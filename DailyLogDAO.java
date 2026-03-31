import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class DailyLogDAO {

    private Connection conn() {
        return DatabaseManager.getInstance().getConnection();
    }

    public void upsert(DailyLog log) throws SQLException {
        String sql =
            "INSERT INTO daily_logs (log_date, water_ml, sleep_hours, weight_kg) VALUES (?,?,?,?) " +
            "ON CONFLICT(log_date) DO UPDATE SET " +
            "water_ml=excluded.water_ml, sleep_hours=excluded.sleep_hours, weight_kg=excluded.weight_kg";
        PreparedStatement ps = conn().prepareStatement(sql);
        ps.setString(1, log.getLogDate().toString());
        ps.setDouble(2, log.getWaterMl());
        ps.setDouble(3, log.getSleepHours());
        ps.setDouble(4, log.getWeightKg());
        ps.executeUpdate();
        ps.close();
    }

    public List<DailyLog> findAll() throws SQLException {
        List<DailyLog> list = new ArrayList<>();
        ResultSet rs = conn().createStatement().executeQuery("SELECT * FROM daily_logs ORDER BY log_date DESC");
        while (rs.next()) list.add(map(rs));
        return list;
    }

    public List<DailyLog> findByDateRange(LocalDate from, LocalDate to) throws SQLException {
        List<DailyLog> list = new ArrayList<>();
        PreparedStatement ps = conn().prepareStatement(
            "SELECT * FROM daily_logs WHERE log_date BETWEEN ? AND ? ORDER BY log_date");
        ps.setString(1, from.toString());
        ps.setString(2, to.toString());
        ResultSet rs = ps.executeQuery();
        while (rs.next()) list.add(map(rs));
        ps.close();
        return list;
    }

    private DailyLog map(ResultSet rs) throws SQLException {
        return new DailyLog(
            rs.getInt("id"),
            LocalDate.parse(rs.getString("log_date")),
            rs.getDouble("water_ml"),
            rs.getDouble("sleep_hours"),
            rs.getDouble("weight_kg")
        );
    }
}
