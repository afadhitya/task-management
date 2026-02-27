package com.afadhitya.taskmanagement.adapter.in.web;

import com.afadhitya.taskmanagement.application.dto.response.BulkJobResponse;
import com.afadhitya.taskmanagement.application.port.in.bulkjob.GetJobStatusUseCase;
import com.afadhitya.taskmanagement.infrastructure.config.OpenApiConfig;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/jobs")
@RequiredArgsConstructor
@SecurityRequirement(name = OpenApiConfig.SECURITY_SCHEME_NAME)
public class JobController {

    private final GetJobStatusUseCase getJobStatusUseCase;

    @GetMapping("/{jobId}")
    public ResponseEntity<BulkJobResponse> getJobStatus(@PathVariable String jobId) {
        BulkJobResponse response = getJobStatusUseCase.getJobStatus(jobId);
        return ResponseEntity.ok(response);
    }
}
