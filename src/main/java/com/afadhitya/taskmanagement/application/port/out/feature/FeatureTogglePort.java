package com.afadhitya.taskmanagement.application.port.out.feature;

import com.afadhitya.taskmanagement.domain.feature.Feature;
import com.afadhitya.taskmanagement.domain.feature.LimitType;

/**
 * Output port for checking feature enablement and limits.
 * Implemented by the adapter layer.
 */
public interface FeatureTogglePort {

    /**
     * Check if a feature is enabled for a workspace.
     * 
     * @param workspaceId The workspace ID
     * @param feature The feature to check
     * @return true if the feature is enabled
     */
    boolean isEnabled(Long workspaceId, Feature feature);

    /**
     * Get the limit value for a workspace.
     * A value of -1 indicates unlimited.
     * 
     * @param workspaceId The workspace ID
     * @param limitType The type of limit
     * @return The limit value
     */
    int getLimit(Long workspaceId, LimitType limitType);

    /**
     * Invalidate the cache for a workspace.
     * Should be called when a workspace's plan configuration changes.
     * 
     * @param workspaceId The workspace ID
     */
    void invalidateCache(Long workspaceId);
}
