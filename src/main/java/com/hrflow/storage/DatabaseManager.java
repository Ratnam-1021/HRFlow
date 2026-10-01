package com.hrflow.storage;

import com.hrflow.model.Candidate;

import java.sql.*;
import java.util.*;

/**
 * SQLite Database Manager for HRFlow Recruitment & Hiring Subsystem.
 */
public class DatabaseManager {
    private static final String DB_URL = "jdbc:sqlite:hrflow.db";
    private static DatabaseManager instance;

    private DatabaseManager() {
        initSchema();
    }

    public static synchronized DatabaseManager getInstance() {
        if (instance == null) {
            instance = new DatabaseManager();
        }
        return instance;
    }

    public Connection getConnection() throws SQLException {
        return DriverManager.getConnection(DB_URL);
    }

    private void initSchema() {
        String sqlCandidates = """
            CREATE TABLE IF NOT EXISTS candidates (
                id TEXT PRIMARY KEY,
                name TEXT NOT NULL,
                email TEXT,
                applied_role TEXT,
                skills TEXT,
                experience_years REAL,
                education_score REAL,
                projects_score REAL,
                certifications_score REAL,
                overall_score REAL,
                status TEXT
            );
        """;

        try (Connection conn = getConnection(); Statement stmt = conn.createStatement()) {
            stmt.execute(sqlCandidates);
        } catch (SQLException e) {
            System.err.println("Database initialization error: " + e.getMessage());
        }
    }

    /**
     * Seeds initial applicant pool if candidates table is empty.
     */
    public void seedInitialDataIfEmpty() {
        try (Connection conn = getConnection(); Statement stmt = conn.createStatement()) {
            ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM candidates");
            if (rs.next() && rs.getInt(1) > 0) {
                return; // Data already exists
            }
        } catch (SQLException e) {
            return;
        }

        seedCandidates();
    }

    private void seedCandidates() {
        List<Candidate> candidates = new ArrayList<>();
        String[] firstNames = {"Aman", "Priya", "Rahul", "Ankit", "Neha", "Vikram", "Kavita", "Rohan", "Deepika", "Aditi", "Arjun", "Sanya", "Tarun", "Varun", "Zoya"};
        String[] lastNames = {"Sharma", "Singh", "Verma", "Kumar", "Patel", "Malhotra", "Rao", "Joshi", "Kapoor", "Bansal", "Mehta", "Chawla", "Gupta", "Nair", "Reddy"};
        String[][] skillSets = {
                {"Java", "Spring Boot", "SQL", "Microservices", "Docker"},
                {"Java", "JavaFX", "DSA", "Algorithms", "Git"},
                {"Python", "FastAPI", "PostgreSQL", "Docker", "AWS"},
                {"React", "TypeScript", "Node.js", "CSS", "GraphQL"},
                {"Java", "AWS", "Kubernetes", "Kafka", "Redis"},
                {"Machine Learning", "Python", "PyTorch", "SQL", "Data Science"},
                {"QA", "Selenium", "Java", "TestNG", "JIRA"}
        };
        String[] roles = {"Senior Java Developer", "Java Software Engineer", "Full Stack Developer", "Backend Engineer", "Cloud Architect"};

        Random rand = new Random(42); // deterministic seed
        for (int i = 1; i <= 250; i++) {
            String id = String.format("CAN-%04d", i);
            String fName = firstNames[rand.nextInt(firstNames.length)];
            String lName = lastNames[rand.nextInt(lastNames.length)];
            String name = fName + " " + lName;
            String email = fName.toLowerCase() + "." + lName.toLowerCase() + i + "@applicant.net";
            String role = roles[rand.nextInt(roles.length)];
            String[] chosenSkills = skillSets[rand.nextInt(skillSets.length)];
            Set<String> skills = new HashSet<>(Arrays.asList(chosenSkills));

            double exp = 1.0 + rand.nextDouble() * 9.0;
            double edu = 60.0 + rand.nextDouble() * 38.0;
            double proj = 65.0 + rand.nextDouble() * 33.0;
            double cert = 50.0 + rand.nextDouble() * 48.0;

            Candidate c = new Candidate(id, name, email, role, skills, exp, edu, proj, cert);
            candidates.add(c);
        }

        insertCandidates(candidates);
    }

    public void insertCandidates(List<Candidate> list) {
        String sql = "INSERT OR REPLACE INTO candidates VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            for (Candidate c : list) {
                ps.setString(1, c.getId());
                ps.setString(2, c.getName());
                ps.setString(3, c.getEmail());
                ps.setString(4, c.getAppliedRole());
                ps.setString(5, String.join(",", c.getSkills()));
                ps.setDouble(6, c.getExperienceYears());
                ps.setDouble(7, c.getEducationScore());
                ps.setDouble(8, c.getProjectsScore());
                ps.setDouble(9, c.getCertificationsScore());
                ps.setDouble(10, c.getOverallScore());
                ps.setString(11, c.getStatus().name());
                ps.addBatch();
            }
            ps.executeBatch();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<Candidate> loadAllCandidates() {
        List<Candidate> list = new ArrayList<>();
        String sql = "SELECT * FROM candidates ORDER BY overall_score DESC";
        try (Connection conn = getConnection(); Statement st = conn.createStatement(); ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                String skillsStr = rs.getString("skills");
                Set<String> skills = (skillsStr != null && !skillsStr.isEmpty()) ?
                        new HashSet<>(Arrays.asList(skillsStr.split(","))) : Collections.emptySet();
                Candidate c = new Candidate(
                        rs.getString("id"),
                        rs.getString("name"),
                        rs.getString("email"),
                        rs.getString("applied_role"),
                        skills,
                        rs.getDouble("experience_years"),
                        rs.getDouble("education_score"),
                        rs.getDouble("projects_score"),
                        rs.getDouble("certifications_score")
                );
                c.setStatus(Candidate.CandidateStatus.valueOf(rs.getString("status")));
                list.add(c);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }
}
