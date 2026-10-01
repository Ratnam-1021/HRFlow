package com.hrflow.model;

import java.util.Collections;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * Represents a job applicant evaluated by the recruitment engine.
 */
public class Candidate implements Comparable<Candidate> {
    private final String id;
    private String name;
    private String email;
    private String appliedRole;
    private Set<String> skills;
    private double experienceYears;
    private double educationScore;      // 0 - 100
    private double projectsScore;       // 0 - 100
    private double certificationsScore; // 0 - 100
    private double skillMatchScore;     // 0 - 100 (computed based on target role)
    private double overallScore;        // Weighted composite score (0 - 100)
    private CandidateStatus status;

    public enum CandidateStatus {
        APPLIED,
        SCREENED,
        SHORTLISTED,
        INTERVIEW_SCHEDULED,
        OFFERED,
        REJECTED
    }

    public Candidate(String id, String name, String email, String appliedRole,
                     Set<String> skills, double experienceYears,
                     double educationScore, double projectsScore, double certificationsScore) {
        this.id = Objects.requireNonNull(id, "ID cannot be null");
        this.name = Objects.requireNonNull(name, "Name cannot be null");
        this.email = email;
        this.appliedRole = appliedRole;
        this.skills = (skills == null) ? new HashSet<>() : new HashSet<>(skills);
        this.experienceYears = experienceYears;
        this.educationScore = educationScore;
        this.projectsScore = projectsScore;
        this.certificationsScore = certificationsScore;
        this.status = CandidateStatus.APPLIED;
        this.skillMatchScore = 75.0; // default initial baseline
        recalculateOverallScore();
    }

    /**
     * Recalculates candidate ranking score using the HRFlow formula:
     * Skills: 40%, Experience: 20%, Education: 15%, Projects: 15%, Certifications: 10%
     */
    public void recalculateOverallScore() {
        // Experience is normalized: 5+ years = 100 points
        double normalizedExp = Math.min(100.0, (experienceYears / 5.0) * 100.0);

        this.overallScore = (this.skillMatchScore * 0.40)
                          + (normalizedExp * 0.20)
                          + (this.educationScore * 0.15)
                          + (this.projectsScore * 0.15)
                          + (this.certificationsScore * 0.10);
    }

    public void updateSkillMatch(Set<String> requiredSkills) {
        if (requiredSkills == null || requiredSkills.isEmpty()) {
            this.skillMatchScore = 70.0;
        } else {
            long matchCount = requiredSkills.stream()
                    .filter(req -> skills.stream().anyMatch(s -> s.equalsIgnoreCase(req.trim())))
                    .count();
            this.skillMatchScore = Math.min(100.0, ((double) matchCount / requiredSkills.size()) * 100.0);
        }
        recalculateOverallScore();
    }

    public String getId() { return id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getAppliedRole() { return appliedRole; }
    public void setAppliedRole(String appliedRole) { this.appliedRole = appliedRole; }
    public Set<String> getSkills() { return Collections.unmodifiableSet(skills); }
    public void addSkill(String skill) { this.skills.add(skill); }
    public double getExperienceYears() { return experienceYears; }
    public void setExperienceYears(double experienceYears) { 
        this.experienceYears = experienceYears; 
        recalculateOverallScore();
    }
    public double getEducationScore() { return educationScore; }
    public void setEducationScore(double educationScore) { 
        this.educationScore = educationScore; 
        recalculateOverallScore();
    }
    public double getProjectsScore() { return projectsScore; }
    public void setProjectsScore(double projectsScore) { 
        this.projectsScore = projectsScore; 
        recalculateOverallScore();
    }
    public double getCertificationsScore() { return certificationsScore; }
    public void setCertificationsScore(double certificationsScore) { 
        this.certificationsScore = certificationsScore; 
        recalculateOverallScore();
    }
    public double getOverallScore() { return overallScore; }
    public CandidateStatus getStatus() { return status; }
    public void setStatus(CandidateStatus status) { this.status = status; }

    @Override
    public int compareTo(Candidate other) {
        // Standard natural ordering: higher score is greater
        int cmp = Double.compare(this.overallScore, other.overallScore);
        if (cmp != 0) return cmp;
        return this.id.compareTo(other.id);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Candidate that)) return false;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return String.format("%s (Score: %.1f, %s, Exp: %.1f yrs)", name, overallScore, appliedRole, experienceYears);
    }
}
