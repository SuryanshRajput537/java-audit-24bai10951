# 🏋️ FitTrack CLI — Personal Health & Fitness Logger

> A command-line Java application to log workouts, track daily water/sleep/weight, view weekly summaries, and export reports — with SQLite persistence and full OOP design.

---

## 📁 Project Structure

All source files are at the repository root — no folders required.

```
java-audit-24bai10951/
├── Main.java                     ← CLI entry point
├── Activity.java                 ← Abstract base class (OOP)
├── CardioActivity.java           ← Inherits Activity (running, cycling)
├── StrengthActivity.java         ← Inherits Activity (weights, calisthenics)
├── FlexibilityActivity.java      ← Inherits Activity (yoga, stretching)
├── DailyLog.java                 ← Water / Sleep / Weight model
├── ActivityDAO.java              ← JDBC CRUD for workouts
├── DailyLogDAO.java              ← JDBC CRUD for daily stats
├── FitnessService.java           ← Business logic, streak, weekly summary
├── DatabaseManager.java          ← SQLite Singleton connection
├── ReportWriter.java             ← CSV + TXT file export (File I/O)
├── ActivityNotFoundException.java
├── InvalidInputException.java
├── run.sh                        ← Linux/macOS build & run script
├── run.bat                       ← Windows build & run script
└── README.md
```

> **lib/** and **reports/** folders are created automatically on first run.

---

## ✨ Features

- **Log Workouts** — Cardio, Strength, Flexibility with type-specific fields
- **Calorie Estimation** — MET-based formula, calculated per activity type via polymorphism
- **Daily Stats** — Track water intake (ml), sleep hours, and body weight
- **Weekly Summary** — Totals and averages for the last 7 days
- **Streak Counter** — Consecutive days with at least one logged activity
- **Search by Date Range** — Filter workouts between two dates
- **Delete Activity** — Remove entries with confirmation prompt
- **Export Reports** — CSV and formatted TXT saved to `reports/`
- **Persistent Storage** — SQLite `fittrack.db` auto-created on first run

---

## 🛠️ Setup & Run

### Prerequisites

| Requirement | Version | Download |
|-------------|---------|----------|
| Java JDK | 17 or higher | https://adoptium.net |

> **No Maven, no Gradle, no IDE required.** Scripts handle everything automatically — including downloading dependencies.

---

### Windows

```cmd
git clone https://github.com/SuryanshRajput537/java-audit-24bai10951.git
cd java-audit-24bai10951
run.bat
```

### Linux / macOS

```bash
git clone https://github.com/SuryanshRajput537/java-audit-24bai10951.git
cd java-audit-24bai10951
chmod +x run.sh
./run.sh
```

### Manual Build (any OS)

**Step 1 — Download JARs into `lib/`:**
- https://repo1.maven.org/maven2/org/xerial/sqlite-jdbc/3.45.1.0/sqlite-jdbc-3.45.1.0.jar → `lib/sqlite-jdbc.jar`
- https://repo1.maven.org/maven2/org/slf4j/slf4j-api/2.0.9/slf4j-api-2.0.9.jar → `lib/slf4j-api.jar`
- https://repo1.maven.org/maven2/org/slf4j/slf4j-simple/2.0.9/slf4j-simple-2.0.9.jar → `lib/slf4j-simple.jar`

**Step 2 — Compile:**
```bash
# Linux/macOS
mkdir -p out
javac -cp "lib/sqlite-jdbc.jar" -d out *.java

# Windows
mkdir out
javac -cp "lib\sqlite-jdbc.jar" -d out *.java
```

**Step 3 — Run:**
```bash
# Linux/macOS
java -cp "out:lib/sqlite-jdbc.jar:lib/slf4j-api.jar:lib/slf4j-simple.jar" Main

# Windows
java -cp "out;lib\sqlite-jdbc.jar;lib\slf4j-api.jar;lib\slf4j-simple.jar" Main
```

---

## 🚀 Usage

On launch you will see:

```
╔══════════════════════════════════════════════╗
║       FITTRACK  — Health Logger  v1.0        ║
║   Log Workouts • Sleep • Water • Weight      ║
╚══════════════════════════════════════════════╝

════════════ MAIN MENU ════════════
  1.  Log Workout
  2.  View All Activities
  3.  Log Daily Stats
  4.  Weekly Summary
  5.  View Streak
  6.  Search by Date Range
  7.  Delete Activity
  8.  Export Reports (CSV + TXT)
  0.  Exit
══════════════════════════════════
```

### Sample Data to Test

| Type | Name | Details | Duration |
|------|------|---------|----------|
| Cardio | Morning Run | 5 km | 30 min |
| Strength | Bench Press | 4 sets × 10 reps | 45 min |
| Flexibility | Yoga Session | Hatha style | 20 min |

After logging, use option **4** for weekly summary, option **5** to check your streak, and option **8** to export reports.

---

## 📖 Java Concepts Demonstrated

### OOP — Inheritance & Polymorphism
```java
// Abstract base class
public abstract class Activity {
    public abstract double calculateCaloriesBurned(double weightKg);
    public abstract String getActivityType();
}

// Each subclass uses a different MET value
public class CardioActivity extends Activity {
    @Override
    public double calculateCaloriesBurned(double weightKg) {
        return 7.0 * weightKg * (durationMinutes / 60.0); // MET = 7.0
    }
}

public class StrengthActivity extends Activity {
    @Override
    public double calculateCaloriesBurned(double weightKg) {
        return 5.0 * weightKg * (durationMinutes / 60.0); // MET = 5.0
    }
}
```

### JDBC — Database Operations
```java
PreparedStatement ps = connection.prepareStatement(
    "INSERT INTO activities (activity_type, name, duration_min, ...) VALUES (?, ?, ?, ...)"
);
ps.setString(1, a.getActivityType());
ps.executeUpdate();
```

### Collections Framework
```java
List<Activity> activities = new ArrayList<>();

// Stream API for summary
Map<String, Long> byType = activities.stream()
    .collect(Collectors.groupingBy(Activity::getActivityType, Collectors.counting()));

// Set for streak calculation
Set<LocalDate> activeDays = new HashSet<>();
```

### File I/O
```java
// CSV — character stream
BufferedWriter bw = new BufferedWriter(new FileWriter("report.csv"));

// TXT — byte stream with UTF-8 encoding
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

1. Herbert Schildt, *Java The Complete Reference*, 11th Edition, Oracle Press, 2018
2. Deitel & Deitel, *Java How to Program*, 10th Edition, Pearson, 2015
3. SQLite JDBC Driver — https://github.com/xerial/sqlite-jdbc
4. MET Values — Compendium of Physical Activities
5. Oracle Java Documentation — https://docs.oracle.com/en/java/

---

*Developed by Suryansh Singh Rajput — 24BAI10951 | B.Tech AI | Java Programming Audit 2024–25*
