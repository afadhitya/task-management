package com.afadhitya.taskmanagement.domain.exception;

import com.afadhitya.taskmanagement.domain.feature.LimitType;
import lombok.Getter;

/**
 * Exception thrown when a workspace exceeds its plan limit.
 * Contains details about the limit type, current usage, and maximum allowed.
 */
@Getter
public class PlanLimitExceededException extends RuntimeException {

    private final LimitType limitType;
    private final int currentUsage;
    private final int limit;

    public PlanLimitExceededException(LimitType limitType, int currentUsage, int limit) {
        super(String.format("Plan limit exceeded: %s (used: %d, limit: %d)", 
            limitType.getCode(), currentUsage, limit));
        this.limitType = limitType;
        this.currentUsage = currentUsage;
        this.limit = limit;
    }
}
