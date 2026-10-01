package com.hrflow.storage;

import com.hrflow.model.*;

import java.sql.*;
import java.time.LocalDate;
import java.util.*;

/**
 * SQLite Database Manager for HRFlow.
 * Handles schema migration, connection pooling, and realistic enterprise seed data generation.
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

        String sqlEmployees = """
            CREATE TABLE IF NOT EXISTS employees (
                id TEXT PRIMARY KEY,
                name TEXT NOT NULL,
                email TEXT,
                department TEXT,
                role TEXT,
                manager_id TEXT,
                salary REAL,
                joining_date TEXT,
                attendance_rate REAL,
                performance_score REAL
            );
        """;

        String sqlLeaves = """
            CREATE TABLE IF NOT EXISTS leave_requests (
                id TEXT PRIMARY KEY,
                employee_id TEXT,
                employee_name TEXT,
                department TEXT,
                start_date TEXT,
                end_date TEXT,
                leave_type TEXT,
                status TEXT,
                reason TEXT
            );
        """;

        String sqlTasks = """
            CREATE TABLE IF NOT EXISTS hr_tasks (
                id TEXT PRIMARY KEY,
                title TEXT NOT NULL,
                description TEXT,
                priority TEXT,
                deadline TEXT,
                department TEXT,
                status TEXT,
                created_at TEXT
            );
        """;

        try (Connection conn = getConnection(); Statement stmt = conn.createStatement()) {
            stmt.execute(sqlCandidates);
            stmt.execute(sqlEmployees);
            stmt.execute(sqlLeaves);
            stmt.execute(sqlTasks);
        } catch (SQLException e) {
            System.err.println("Database initialization error: " + e.getMessage());
        }
    }

    /**
     * Seeds initial mock data if the database is currently empty.
     */
    public void seedInitialDataIfEmpty() {
        try (Connection conn = getConnection(); Statement stmt = conn.createStatement()) {
            ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM employees");
            if (rs.next() && rs.getInt(1) > 0) {
                return; // Data already exists
            }
        } catch (SQLException e) {
            return;
        }

        seedEmployees();
        seedCandidates();
        seedLeaveRequests();
        seedHRTasks();
    }

    private void seedEmployees() {
        List<Employee> list = new ArrayList<>();
        // Executive
        list.add(new Employee("EMP-0001", "Satya Nadella", "satya@hrflow.corp", Department.EXECUTIVE, "Chief Executive Officer", null, 450000, LocalDate.of(2021, 1, 15), 99.2, 99.0));

        // Department Heads reporting to CEO
        list.add(new Employee("EMP-0002", "Sundar Pichai", "sundar@hrflow.corp", Department.ENGINEERING, "VP of Engineering", "EMP-0001", 280000, LocalDate.of(2021, 3, 1), 98.5, 96.0));
        list.add(new Employee("EMP-0003", "Mira Murati", "mira@hrflow.corp", Department.PRODUCT, "Head of Product", "EMP-0001", 260000, LocalDate.of(2021, 4, 10), 97.8, 95.0));
        list.add(new Employee("EMP-0004", "Leena Nair", "leena@hrflow.corp", Department.HUMAN_RESOURCES, "Chief People Officer", "EMP-0001", 240000, LocalDate.of(2021, 2, 20), 99.0, 97.5));
        list.add(new Employee("EMP-0005", "Ruth Porat", "ruth@hrflow.corp", Department.FINANCE, "Chief Financial Officer", "EMP-0001", 270000, LocalDate.of(2021, 1, 15), 98.9, 96.5));

        // Engineering Team
        list.add(new Employee("EMP-0006", "Linus Torvalds", "linus@hrflow.corp", Department.ENGINEERING, "Principal Architect", "EMP-0002", 210000, LocalDate.of(2022, 1, 10), 96.0, 98.0));
        list.add(new Employee("EMP-0007", "James Gosling", "james@hrflow.corp", Department.ENGINEERING, "Senior Java Specialist", "EMP-0006", 185000, LocalDate.of(2022, 2, 15), 97.4, 94.0));
        list.add(new Employee("EMP-0008", "Martin Odersky", "martin@hrflow.corp", Department.ENGINEERING, "Backend Tech Lead", "EMP-0006", 175000, LocalDate.of(2022, 5, 20), 95.8, 92.5));
        list.add(new Employee("EMP-0009", "Aman Sharma", "aman.s@hrflow.corp", Department.ENGINEERING, "Senior Software Engineer", "EMP-0008", 135000, LocalDate.of(2023, 1, 5), 98.2, 91.0));
        list.add(new Employee("EMP-0010", "Priya Singh", "priya.s@hrflow.corp", Department.ENGINEERING, "Full Stack Engineer", "EMP-0008", 125000, LocalDate.of(2023, 3, 12), 97.0, 90.5));
        list.add(new Employee("EMP-0011", "Rahul Verma", "rahul.v@hrflow.corp", Department.ENGINEERING, "DevOps Engineer", "EMP-0008", 120000, LocalDate.of(2023, 6, 18), 94.5, 88.0));
        list.add(new Employee("EMP-0012", "Ankit Kumar", "ankit.k@hrflow.corp", Department.ENGINEERING, "Junior Software Engineer", "EMP-0009", 85000, LocalDate.of(2024, 2, 1), 99.1, 87.0));

        // HR & Recruitment Team
        list.add(new Employee("EMP-0013", "Neha Patel", "neha.p@hrflow.corp", Department.HUMAN_RESOURCES, "Talent Acquisition Manager", "EMP-0004", 130000, LocalDate.of(2022, 4, 1), 98.0, 93.0));
        list.add(new Employee("EMP-0014", "Vikram Malhotra", "vikram.m@hrflow.corp", Department.HUMAN_RESOURCES, "Technical Recruiter", "EMP-0013", 95000, LocalDate.of(2023, 2, 10), 96.5, 89.0));
        list.add(new Employee("EMP-0015", "Kavita Rao", "kavita.r@hrflow.corp", Department.HUMAN_RESOURCES, "HR Operations Specialist", "EMP-0013", 90000, LocalDate.of(2023, 7, 22), 99.0, 92.0));

        // Product & Design Team
        list.add(new Employee("EMP-0016", "Jony Ive", "jony@hrflow.corp", Department.DESIGN, "Head of Design", "EMP-0003", 220000, LocalDate.of(2022, 3, 1), 96.2, 95.0));
        list.add(new Employee("EMP-0017", "Dieter Rams", "dieter@hrflow.corp", Department.DESIGN, "Senior UX Designer", "EMP-0016", 140000, LocalDate.of(2023, 1, 15), 97.5, 93.5));

        insertEmployees(list);
    }

    private void seedCandidates() {
        List<Candidate> candidates = new ArrayList<>();
        String[] firstNames = {"Aarav", "Aditi", "Arjun", "Deepika", "Gaurav", "Isha", "Kunal", "Meera", "Nikhil", "Pooja", "Rohan", "Sanya", "Tarun", "Varun", "Zoya"};
        String[] lastNames = {"Kapoor", "Joshi", "Bansal", "Mehta", "Chawla", "Gupta", "Deshmukh", "Nair", "Reddy", "Iyer", "Saxena", "Chopra"};
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

            double exp = 1.0 + rand.nextDouble() * 9.0; // 1 to 10 years
            double edu = 60.0 + rand.nextDouble() * 38.0;
            double proj = 65.0 + rand.nextDouble() * 33.0;
            double cert = 50.0 + rand.nextDouble() * 48.0;

            Candidate c = new Candidate(id, name, email, role, skills, exp, edu, proj, cert);
            candidates.add(c);
        }

        insertCandidates(candidates);
    }

    private void seedLeaveRequests() {
        List<LeaveRequest> leaves = new ArrayList<>();
        LocalDate base = LocalDate.now().plusDays(5);

        // Intentionally create a departmental bottleneck in Software Engineering!
        leaves.add(new LeaveRequest("LR-001", "EMP-0009", "Aman Sharma", Department.ENGINEERING, base, base.plusDays(6), LeaveRequest.LeaveType.VACATION, "Family function"));
        leaves.add(new LeaveRequest("LR-002", "EMP-0010", "Priya Singh", Department.ENGINEERING, base.plusDays(2), base.plusDays(7), LeaveRequest.LeaveType.VACATION, "Annual vacation"));
        leaves.add(new LeaveRequest("LR-003", "EMP-0011", "Rahul Verma", Department.ENGINEERING, base.plusDays(3), base.plusDays(8), LeaveRequest.LeaveType.SICK_LEAVE, "Medical procedure"));

        // Product leaves (no bottleneck)
        leaves.add(new LeaveRequest("LR-004", "EMP-0003", "Mira Murati", Department.PRODUCT, base.plusDays(10), base.plusDays(14), LeaveRequest.LeaveType.PERSONAL, "Conference"));

        insertLeaveRequests(leaves);
    }

    private void seedHRTasks() {
        List<HRTask> tasks = new ArrayList<>();
        LocalDate today = LocalDate.now();

        tasks.add(new HRTask("TSK-101", "Approve Candidate Offer for Priya Singh", "Final salary revision approved by CFO. Roll out offer letter.", HRTask.Priority.CRITICAL, today.plusDays(1), Department.HUMAN_RESOURCES));
        tasks.add(new HRTask("TSK-102", "Schedule Technical Panel for 8 Shortlisted Candidates", "Allocate senior developers and meeting rooms for tomorrow's interviews.", HRTask.Priority.HIGH, today.plusDays(1), Department.HUMAN_RESOURCES));
        tasks.add(new HRTask("TSK-103", "Resolve Engineering Overlapping Leave Bottleneck", "3 core backend engineers applied for overlapping leaves next week.", HRTask.Priority.HIGH, today.plusDays(2), Department.ENGINEERING));
        tasks.add(new HRTask("TSK-104", "Screen 250+ New Resumes for Java Engineer Roles", "Run Trie-based skill indexer and prioritize top-20 MaxHeap candidates.", HRTask.Priority.MEDIUM, today.plusDays(3), Department.HUMAN_RESOURCES));
        tasks.add(new HRTask("TSK-105", "Quarterly Attendance & Performance Audit", "Review attendance patterns and generate departmental reports.", HRTask.Priority.LOW, today.plusDays(5), Department.HUMAN_RESOURCES));

        insertHRTasks(tasks);
    }

    public void insertEmployees(List<Employee> list) {
        String sql = "INSERT OR REPLACE INTO employees VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            for (Employee e : list) {
                ps.setString(1, e.getId());
                ps.setString(2, e.getName());
                ps.setString(3, e.getEmail());
                ps.setString(4, e.getDepartment().name());
                ps.setString(5, e.getRole());
                ps.setString(6, e.getManagerId());
                ps.setDouble(7, e.getSalary());
                ps.setString(8, e.getJoiningDate().toString());
                ps.setDouble(9, e.getAttendanceRate());
                ps.setDouble(10, e.getPerformanceScore());
                ps.addBatch();
            }
            ps.executeBatch();
        } catch (SQLException e) {
            e.printStackTrace();
        }
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

    public void insertLeaveRequests(List<LeaveRequest> list) {
        String sql = "INSERT OR REPLACE INTO leave_requests VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            for (LeaveRequest r : list) {
                ps.setString(1, r.getId());
                ps.setString(2, r.getEmployeeId());
                ps.setString(3, r.getEmployeeName());
                ps.setString(4, r.getDepartment().name());
                ps.setString(5, r.getStartDate().toString());
                ps.setString(6, r.getEndDate().toString());
                ps.setString(7, r.getType().name());
                ps.setString(8, r.getStatus().name());
                ps.setString(9, r.getReason());
                ps.addBatch();
            }
            ps.executeBatch();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void insertHRTasks(List<HRTask> list) {
        String sql = "INSERT OR REPLACE INTO hr_tasks VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            for (HRTask t : list) {
                ps.setString(1, t.getId());
                ps.setString(2, t.getTitle());
                ps.setString(3, t.getDescription());
                ps.setString(4, t.getPriority().name());
                ps.setString(5, t.getDeadline().toString());
                ps.setString(6, t.getDepartment() != null ? t.getDepartment().name() : null);
                ps.setString(7, t.getStatus().name());
                ps.setString(8, t.getCreatedAt().toString());
                ps.addBatch();
            }
            ps.executeBatch();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<Employee> loadAllEmployees() {
        List<Employee> list = new ArrayList<>();
        String sql = "SELECT * FROM employees ORDER BY id ASC";
        try (Connection conn = getConnection(); Statement st = conn.createStatement(); ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                list.add(new Employee(
                        rs.getString("id"),
                        rs.getString("name"),
                        rs.getString("email"),
                        Department.valueOf(rs.getString("department")),
                        rs.getString("role"),
                        rs.getString("manager_id"),
                        rs.getDouble("salary"),
                        LocalDate.parse(rs.getString("joining_date")),
                        rs.getDouble("attendance_rate"),
                        rs.getDouble("performance_score")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
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

    public List<LeaveRequest> loadAllLeaveRequests() {
        List<LeaveRequest> list = new ArrayList<>();
        String sql = "SELECT * FROM leave_requests ORDER BY start_date ASC";
        try (Connection conn = getConnection(); Statement st = conn.createStatement(); ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                LeaveRequest lr = new LeaveRequest(
                        rs.getString("id"),
                        rs.getString("employee_id"),
                        rs.getString("employee_name"),
                        Department.valueOf(rs.getString("department")),
                        LocalDate.parse(rs.getString("start_date")),
                        LocalDate.parse(rs.getString("end_date")),
                        LeaveRequest.LeaveType.valueOf(rs.getString("leave_type")),
                        rs.getString("reason")
                );
                lr.setStatus(LeaveRequest.LeaveStatus.valueOf(rs.getString("status")));
                list.add(lr);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public List<HRTask> loadAllHRTasks() {
        List<HRTask> list = new ArrayList<>();
        String sql = "SELECT * FROM hr_tasks ORDER BY priority DESC";
        try (Connection conn = getConnection(); Statement st = conn.createStatement(); ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                String deptStr = rs.getString("department");
                Department dept = (deptStr != null) ? Department.valueOf(deptStr) : null;
                HRTask t = new HRTask(
                        rs.getString("id"),
                        rs.getString("title"),
                        rs.getString("description"),
                        HRTask.Priority.valueOf(rs.getString("priority")),
                        LocalDate.parse(rs.getString("deadline")),
                        dept
                );
                t.setStatus(HRTask.TaskStatus.valueOf(rs.getString("status")));
                list.add(t);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }
}
