package com.afadhitya.taskmanagement.application.port.in.admin;

import com.afadhitya.taskmanagement.application.dto.request.admin.UpdateLimitsRequest;

public interface UpdatePlanLimitsUseCase {
    void updateLimits(Long planId, UpdateLimitsRequest request);
}
