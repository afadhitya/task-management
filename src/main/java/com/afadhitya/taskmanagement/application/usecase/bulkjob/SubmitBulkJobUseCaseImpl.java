package com.afadhitya.taskmanagement.application.usecase.bulkjob;

import com.afadhitya.taskmanagement.application.dto.request.BulkUpdateTasksRequest;
import com.afadhitya.taskmanagement.application.dto.response.BulkJobResponse;
import com.afadhitya.taskmanagement.application.mapper.BulkJobMapper;
import com.afadhitya.taskmanagement.application.port.in.bulkjob.SubmitBulkJobUseCase;
import com.afadhitya.taskmanagement.application.port.out.bulkjob.BulkJobPersistencePort;
import com.afadhitya.taskmanagement.application.port.out.user.UserPersistencePort;
import com.afadhitya.taskmanagement.domain.entity.BulkJob;
import com.afadhitya.taskmanagement.domain.entity.User;
import com.afadhitya.taskmanagement.domain.enums.JobStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class SubmitBulkJobUseCaseImpl implements SubmitBulkJobUseCase {

    private final BulkJobPersistencePort bulkJobPersistencePort;
    private final UserPersistencePort userPersistencePort;
    private final BulkJobProcessor bulkJobProcessor;
    private final BulkJobMapper bulkJobMapper;

    @Override
    public BulkJobResponse submitBulkUpdateTasks(BulkUpdateTasksRequest request, Long createdByUserId) {
        User createdBy = userPersistencePort.findById(createdByUserId)
                .orElseThrow(() -> new IllegalArgumentException("User not found with id: " + createdByUserId));

        BulkJob bulkJob = BulkJob.builder()
                .status(JobStatus.PENDING)
                .totalItems(request.taskIds().size())
                .jobType("BULK_UPDATE_TASKS")
                .createdBy(createdBy)
                .build();

        BulkJob savedJob = bulkJobPersistencePort.save(bulkJob);

        // Trigger async processing
        bulkJobProcessor.processBulkUpdateTasks(savedJob.getId(), request);

        return bulkJobMapper.toResponse(savedJob);
    }
}
