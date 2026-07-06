package com.neuroforge_nexus.model;

public enum Role {
    ADMIN("Admin"),
    PROJECT_MANAGER("Project Manager"),
    DEVELOPER("Developer"),
    TESTER("Tester"),
    DEVOPS_ENGINEER("DevOps Engineer");

    private final String displayName;

    Role(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
