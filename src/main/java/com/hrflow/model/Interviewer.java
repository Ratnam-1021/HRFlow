package com.hrflow.model;

import java.util.Objects;

/**
 * Represents an interviewer panel member assigned to interview candidates.
 */
public class Interviewer {
    private final String id;
    private final String name;
    private final String role;
    private final String email;

    public Interviewer(String id, String name, String role, String email) {
        this.id = Objects.requireNonNull(id);
        this.name = Objects.requireNonNull(name);
        this.role = role;
        this.email = email;
    }

    public String getId() { return id; }
    public String getName() { return name; }
    public String getRole() { return role; }
    public String getEmail() { return email; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Interviewer that)) return false;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return name + " (" + role + ")";
    }
}
