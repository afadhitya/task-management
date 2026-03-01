package com.afadhitya.taskmanagement.application.port.in.admin;

import com.afadhitya.taskmanagement.application.dto.response.admin.PlanDetailResponse;

public interface GetPlanUseCase {
    PlanDetailResponse getPlan(Long planId);
}
