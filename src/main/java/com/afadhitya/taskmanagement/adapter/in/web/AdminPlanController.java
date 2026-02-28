package com.afadhitya.taskmanagement.adapter.in.web;

import com.afadhitya.taskmanagement.application.dto.request.admin.UpdateFeaturesRequest;
import com.afadhitya.taskmanagement.application.dto.request.admin.UpdateLimitsRequest;
import com.afadhitya.taskmanagement.application.dto.response.admin.PlanDetailResponse;
import com.afadhitya.taskmanagement.application.dto.response.admin.PlanSummaryResponse;
import com.afadhitya.taskmanagement.application.port.in.admin.*;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/plans")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminPlanController {

    private final ListPlansUseCase listPlansUseCase;
    private final GetPlanUseCase getPlanUseCase;
    private final UpdatePlanFeaturesUseCase updatePlanFeaturesUseCase;
    private final UpdatePlanLimitsUseCase updatePlanLimitsUseCase;

    @GetMapping
    public ResponseEntity<List<PlanSummaryResponse>> listPlans() {
        return ResponseEntity.ok(listPlansUseCase.listPlans());
    }

    @GetMapping("/{id}")
    public ResponseEntity<PlanDetailResponse> getPlan(@PathVariable Long id) {
        return ResponseEntity.ok(getPlanUseCase.getPlan(id));
    }

    @PatchMapping("/{id}/features")
    public ResponseEntity<Void> updateFeatures(
        @PathVariable Long id,
        @RequestBody @Valid UpdateFeaturesRequest request
    ) {
        updatePlanFeaturesUseCase.updateFeatures(id, request);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/{id}/limits")
    public ResponseEntity<Void> updateLimits(
        @PathVariable Long id,
        @RequestBody @Valid UpdateLimitsRequest request
    ) {
        updatePlanLimitsUseCase.updateLimits(id, request);
        return ResponseEntity.ok().build();
    }
}
