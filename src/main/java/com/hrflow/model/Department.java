package com.hrflow.model;

public enum Department {
    EXECUTIVE("Executive Board"),
    ENGINEERING("Software Engineering"),
    PRODUCT("Product Management"),
    HUMAN_RESOURCES("Human Resources"),
    FINANCE("Finance & Accounts"),
    MARKETING("Marketing & Growth"),
    DESIGN("Product Design & UX");

    private final String displayName;

    Department(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
