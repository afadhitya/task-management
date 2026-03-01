package com.afadhitya.taskmanagement.application.port.feature;

import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;
import java.util.Map;

/**
 * Shared context across all handlers in a single operation.
 * Allows handlers to share data (e.g., audit handler needs to know if notification was sent).
 */
@Getter
public class FeatureContext {

    private final Long workspaceId;
    private final Long actorId;
    private final Map<String, Object> attributes = new HashMap<>();

    @Setter
    private boolean executionFailed = false;

    public FeatureContext(Long workspaceId, Long actorId) {
        this.workspaceId = workspaceId;
        this.actorId = actorId;
    }

    /**
     * Store an attribute in the context.
     */
    public void setAttribute(String key, Object value) {
        attributes.put(key, value);
    }

    /**
     * Retrieve an attribute from the context.
     */
    @SuppressWarnings("unchecked")
    public <T> T getAttribute(String key) {
        return (T) attributes.get(key);
    }

    /**
     * Check if an attribute exists in the context.
     */
    public boolean hasAttribute(String key) {
        return attributes.containsKey(key);
    }
}
