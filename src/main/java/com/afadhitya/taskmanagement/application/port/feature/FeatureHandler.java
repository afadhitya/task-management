package com.afadhitya.taskmanagement.application.port.feature;

import com.afadhitya.taskmanagement.domain.feature.Feature;

/**
 * Pluggable handler for feature-specific logic.
 * Each feature (audit, notification, limits, etc.) implements this interface.
 * 
 * @param <R> Request type
 * @param <T> Response type
 */
public interface FeatureHandler<R, T> {

    /**
     * The feature this handler manages.
     */
    Feature getFeature();

    /**
     * VALIDATE phase: Check limits before execution.
     * Throw PlanLimitExceededException to block operation.
     * 
     * @param context Shared context across handlers
     * @param request The request object
     */
    default void validate(FeatureContext context, R request) {
        // Default: no validation
    }

    /**
     * PRE phase: Capture state before execution.
     * Only called if feature is enabled for the workspace.
     * Used for features that need before/after comparison (e.g., audit log).
     * 
     * @param context Shared context across handlers
     * @param request The request object
     */
    default void before(FeatureContext context, R request) {
        // Default: no pre-processing
    }

    /**
     * POST phase: React to successful execution.
     * Only called if feature is enabled for the workspace.
     * 
     * @param context Shared context across handlers
     * @param result The response object
     */
    default void after(FeatureContext context, T result) {
        // Default: no post-processing
    }

    /**
     * ERROR phase: React to failed execution.
     * Called regardless of feature enablement for cleanup purposes.
     * 
     * @param context Shared context across handlers
     * @param request The request object
     * @param error The exception that was thrown
     */
    default void onError(FeatureContext context, R request, Exception error) {
        // Default: no error handling
    }

    /**
     * ASYNC phase: Fire-and-forget operations.
     * Called in separate thread via @Async.
     * Only called if feature is enabled for the workspace.
     * 
     * @param context Shared context across handlers
     * @param result The response object
     */
    default void async(FeatureContext context, T result) {
        // Default: no async processing
    }
}
