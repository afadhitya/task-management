package com.afadhitya.taskmanagement.application.port.in.admin;

import com.afadhitya.taskmanagement.application.dto.request.admin.UpdateFeaturesRequest;

public interface UpdatePlanFeaturesUseCase {
    void updateFeatures(Long planId, UpdateFeaturesRequest request);
}
