package com.afadhitya.taskmanagement.domain.feature;

/**
 * Defines when a feature handler should execute during the request lifecycle.
 */
public enum FeatureTiming {
    
    /**
     * Execute before the main operation to validate limits.
     * Can throw exceptions to block the operation.
     */
    VALIDATE,
    
    /**
     * Execute before the main operation to capture state.
     * Used for features that need before/after comparison (e.g., audit log).
     */
    PRE,
    
    /**
     * Execute after the main operation completes successfully.
     * Used for features that react to the result (e.g., notifications).
     */
    POST,
    
    /**
     * Execute asynchronously after the main operation.
     * Used for non-blocking features (e.g., search indexing).
     */
    ASYNC
}
