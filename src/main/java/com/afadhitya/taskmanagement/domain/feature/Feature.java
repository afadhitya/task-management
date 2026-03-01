package com.afadhitya.taskmanagement.domain.feature;

/**
 * Enumeration of all available features in the system.
 * Each feature has a code, category, and default timing for execution.
 */
public enum Feature {
    
    AUDIT_LOG("audit_log", FeatureCategory.SECURITY, FeatureTiming.POST),
    NOTIFICATIONS("notifications", FeatureCategory.COLLABORATION, FeatureTiming.POST),
    ADVANCED_SEARCH("advanced_search", FeatureCategory.PRODUCTIVITY, FeatureTiming.ASYNC),
    WEBHOOKS("webhooks", FeatureCategory.INTEGRATION, FeatureTiming.ASYNC),
    ATTACHMENTS("attachments", FeatureCategory.COLLABORATION, FeatureTiming.VALIDATE),
    BULK_OPERATIONS("bulk_operations", FeatureCategory.PRODUCTIVITY, FeatureTiming.VALIDATE),
    PROJECT_LIMITS("project_limits", FeatureCategory.LIMITS, FeatureTiming.VALIDATE),
    MEMBER_LIMITS("member_limits", FeatureCategory.LIMITS, FeatureTiming.VALIDATE),
    STORAGE_LIMITS("storage_limits", FeatureCategory.LIMITS, FeatureTiming.VALIDATE);

    private final String code;
    private final FeatureCategory category;
    private final FeatureTiming defaultTiming;

    Feature(String code, FeatureCategory category, FeatureTiming defaultTiming) {
        this.code = code;
        this.category = category;
        this.defaultTiming = defaultTiming;
    }

    /**
     * Unique code for the feature, used in database and API.
     */
    public String getCode() {
        return code;
    }

    /**
     * Category of the feature for grouping and UI display.
     */
    public FeatureCategory getCategory() {
        return category;
    }

    /**
     * Default timing for when this feature's handler should execute.
     */
    public FeatureTiming getDefaultTiming() {
        return defaultTiming;
    }
}
