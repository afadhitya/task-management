package com.afadhitya.taskmanagement.application.port.in.admin;

import com.afadhitya.taskmanagement.application.dto.response.admin.PlanSummaryResponse;

import java.util.List;

public interface ListPlansUseCase {
    List<PlanSummaryResponse> listPlans();
}
