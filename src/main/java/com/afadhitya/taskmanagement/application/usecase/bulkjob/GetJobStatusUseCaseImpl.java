package com.afadhitya.taskmanagement.application.usecase.bulkjob;

import com.afadhitya.taskmanagement.application.dto.response.BulkJobResponse;
import com.afadhitya.taskmanagement.application.mapper.BulkJobMapper;
import com.afadhitya.taskmanagement.application.port.in.bulkjob.GetJobStatusUseCase;
import com.afadhitya.taskmanagement.application.port.out.bulkjob.BulkJobPersistencePort;
import com.afadhitya.taskmanagement.domain.entity.BulkJob;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class GetJobStatusUseCaseImpl implements GetJobStatusUseCase {

    private final BulkJobPersistencePort bulkJobPersistencePort;
    private final BulkJobMapper bulkJobMapper;

    @Override
    public BulkJobResponse getJobStatus(String jobId) {
        BulkJob bulkJob = bulkJobPersistencePort.findById(jobId)
                .orElseThrow(() -> new IllegalArgumentException("Job not found with id: " + jobId));
        return bulkJobMapper.toResponse(bulkJob);
    }
}
