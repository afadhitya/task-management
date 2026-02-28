package com.afadhitya.taskmanagement.domain.feature;

/**
 * Enumeration of all numeric limit types that can be enforced per plan.
 * A value of -1 indicates unlimited.
 */
public enum LimitType {
    
    MAX_PROJECTS("MAX_PROJECTS", "Maximum projects per workspace"),
    MAX_MEMBERS("MAX_MEMBERS", "Maximum members per workspace"),
    MAX_STORAGE_MB("MAX_STORAGE_MB", "Maximum storage in MB per workspace"),
    MAX_TASKS_PER_PROJECT("MAX_TASKS_PER_PROJECT", "Maximum tasks per project");

    private final String code;
    private final String description;

    LimitType(String code, String description) {
        this.code = code;
        this.description = description;
    }

    /**
     * Unique code for the limit type, used in database and API.
     */
    public String getCode() {
        return code;
    }

    /**
     * Human-readable description of the limit.
     */
    public String getDescription() {
        return description;
    }
}
