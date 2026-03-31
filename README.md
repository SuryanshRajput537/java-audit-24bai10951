# 🏋️ FitTrack CLI — Personal Health & Fitness Logger

> A command-line Java application to log workouts, track daily water/sleep/weight, view weekly summaries, and export reports — with SQLite persistence and full OOP design.

---

## 📁 Project Architecture

```
FitTrackCLI/
├── src/main/java/com/fittrack/
│   ├── Main.java                         ← CLI entry point
│   ├── model/
│   │   ├── Activity.java                 ← Abstract base class (OOP)
│   │   ├── CardioActivity.java           ← Inherits Activity
│   │   ├── StrengthActivity.java         ← Inherits Activity
│   │   ├── FlexibilityActivity.java      ← Inherits Activity
│   │   └── DailyLog.java                ← Water / Sleep / Weight model
│   ├── dao/
│   │   ├── ActivityDAO.java              ← JDBC CRUD for workouts
│   │   └── DailyLogDAO.java             ← JDBC CRUD for daily stats
│   ├── service/
│   │   └── FitnessService.java          ← Business logic, streak, summary
│   ├── util/
│   │   ├── DatabaseManager.java         ← SQLite Singleton
│   │   └── ReportWriter.java           ← CSV + TXT file export
│   └── exception/
│       ├── ActivityNotFoundException.java
│       └── InvalidInputException.java
├── lib/                                  ← JAR dependencies (auto-downloaded)
├── reports/                              ← Exported reports saved here
├── run.sh                                ← Linux/macOS build & run
├── run.bat                               ← Windows build & run
└── README.md
```

---

## ✨ Features

- **Log Workouts** — Cardio, Strength, Flexibility (each with unique fields)
- **Calorie Estimation** — MET-based formula per activity type (polymorphism)
- **Daily Stats** — Track water intake, sleep hours, and body weight
- **Weekly Summary** — Totals, averages, and breakdown by workout type
- **Streak Counter** — Tracks consecutive days with logged activity
- **Search by Date Range** — Filter activities between two dates
- **Delete Activity** — Remove entries with confirmation
- **Export Reports** — CSV and formatted TXT saved to `reports/`
- **Persistent Storage** — SQLite `fittrack.db` auto-created on first run

---

## 🛠️ Setup & Run

### Prerequisites

| Requirement | Version |
|-------------|---------|
| Java JDK | 17 or higher |

No Maven, Gradle, or IDE required. Scripts auto-download all dependencies.

---

### Windows

```cmd
git clone https://github.com/YOUR_USERNAME/FitTrackCLI.git
cd FitTrackCLI
run.bat
```

### Linux / macOS

```bash
git clone https://github.com/YOUR_USERNAME/FitTrackCLI.git
cd FitTrackCLI
chmod +x run.sh
./run.sh
```

---

## 🚀 Usage

```
╔══════════════════════════════════════════════╗
║       FITTRACK  — Health Logger  v1.0        ║
║   Log Workouts • Sleep • Water • Weight      ║
╚══════════════════════════════════════════════╝

════════════ MAIN MENU ════════════
  1. ➕  Log Workout
  2. 📋  View All Activities
  3. 💧  Log Daily Stats
  4. 📊  Weekly Summary
  5. 🏆  View Streak
  6. 🔍  Search by Date Range
  7. 🗑️   Delete Activity
  8. 💾  Export Reports (CSV + TXT)
  0. 🚪  Exit
═══════════════════════════════════
```

### Sample Data to Test

| Type | Name | Duration | Extra |
|------|------|----------|-------|
| Cardio | Morning Run | 30 min | 5 km |
| Strength | Bench Press | 45 min | 4 sets × 10 reps |
| Flexibility | Yoga | 20 min | Hatha |

After logging, use option **4** for weekly summary and **8** to export.

---

## 📖 Java Concepts Demonstrated

### OOP — Inheritance & Polymorphism
```java
public abstract class Activity {
    public abstract double calculateCaloriesBurned(double weightKg); // Polymorphism
    public abstract String getActivityType();
}

public class CardioActivity extends Activity {
    @Override
    public double calculateCaloriesBurned(double weightKg) {
        return 7.0 * weightKg * (durationMinutes / 60.0); // MET formula
    }
}
```

### JDBC — SQLite Operations
```java
PreparedStatement ps = conn.prepareStatement(
    "INSERT INTO activities (activity_type, name, ...) VALUES (?, ?, ...)"
);
ps.setString(1, a.getActivityType());
ps.executeUpdate();
```

### Collections Framework
```java
List<Activity> activities = new ArrayList<>();
Map<String, Long> byType = activities.stream()
    .collect(Collectors.groupingBy(Activity::getActivityType, Collectors.counting()));
```

### File I/O
```java
// CSV — character stream
BufferedWriter bw = new BufferedWriter(new FileWriter("report.csv"));

// TXT — byte stream with encoding
PrintWriter pw = new PrintWriter(
    new OutputStreamWriter(new FileOutputStream("report.txt"), StandardCharsets.UTF_8)
);
```

---

## 📁 Database Schema

```sql
CREATE TABLE activities (
    id            INTEGER PRIMARY KEY AUTOINCREMENT,
    activity_type TEXT NOT NULL,   -- 'CARDIO', 'STRENGTH', 'FLEXIBILITY'
    name          TEXT NOT NULL,
    duration_min  INTEGER NOT NULL,
    calories      REAL,
    log_date      TEXT NOT NULL,
    notes         TEXT,
    extra1        TEXT,            -- distance / sets / style
    extra2        TEXT             -- reps (strength only)
);

CREATE TABLE daily_logs (
    id          INTEGER PRIMARY KEY AUTOINCREMENT,
    log_date    TEXT NOT NULL UNIQUE,
    water_ml    REAL DEFAULT 0,
    sleep_hours REAL DEFAULT 0,
    weight_kg   REAL DEFAULT 0
);
```

---

## 📝 References

1. Herbert Schildt, *Java The Complete Reference*, 11th Edition
2. Deitel & Deitel, *Java How to Program*, 10th Edition
3. SQLite JDBC Driver — https://github.com/xerial/sqlite-jdbc
4. MET (Metabolic Equivalent of Task) values — Compendium of Physical Activities
