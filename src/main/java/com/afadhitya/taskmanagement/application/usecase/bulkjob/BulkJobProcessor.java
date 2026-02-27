package com.afadhitya.taskmanagement.application.usecase.bulkjob;

import com.afadhitya.taskmanagement.application.dto.request.BulkUpdateTasksRequest;
import com.afadhitya.taskmanagement.application.port.out.bulkjob.BulkJobPersistencePort;
import com.afadhitya.taskmanagement.application.port.out.task.TaskPersistencePort;
import com.afadhitya.taskmanagement.domain.entity.BulkJob;
import com.afadhitya.taskmanagement.domain.entity.Task;
import com.afadhitya.taskmanagement.domain.enums.JobStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Set;

@Slf4j
@Component
@RequiredArgsConstructor
public class BulkJobProcessor {

    private final BulkJobPersistencePort bulkJobPersistencePort;
    private final TaskPersistencePort taskPersistencePort;

    @Async
    @Transactional
    public void processBulkUpdateTasks(String jobId, BulkUpdateTasksRequest request) {
        log.info("Starting bulk update job: {} for {} tasks", jobId, request.taskIds().size());

        BulkJob job = bulkJobPersistencePort.findById(jobId)
                .orElseThrow(() -> new IllegalArgumentException("Job not found: " + jobId));

        try {
            job.setStatus(JobStatus.PROCESSING);
            bulkJobPersistencePort.save(job);

            int processed = 0;
            int failed = 0;

            for (Long taskId : request.taskIds()) {
                try {
                    Task task = taskPersistencePort.findById(taskId).orElse(null);
                    if (task == null) {
                        failed++;
                        continue;
                    }

                    if (request.status() != null) {
                        task.setStatus(request.status());
                    }
                    if (request.priority() != null) {
                        task.setPriority(request.priority());
                    }
                    if (request.assigneeIds() != null) {
                        if (Boolean.TRUE.equals(request.replaceAssignees())) {
                            task.getAssigneeIds().clear();
                            task.getAssigneeIds().addAll(request.assigneeIds());
                        } else {
                            task.getAssigneeIds().addAll(request.assigneeIds());
                        }
                    }

                    taskPersistencePort.save(task);
                    processed++;

                    // Update progress periodically
                    if (processed % 10 == 0) {
                        job.setProcessedItems(processed);
                        job.setFailedItems(failed);
                        bulkJobPersistencePort.save(job);
                    }

                } catch (Exception e) {
                    log.error("Error processing task {} in job {}", taskId, jobId, e);
                    failed++;
                }
            }

            job.setStatus(JobStatus.COMPLETED);
            job.setProcessedItems(processed);
            job.setFailedItems(failed);
            job.setCompletedAt(LocalDateTime.now());
            bulkJobPersistencePort.save(job);

            log.info("Completed bulk update job: {} - Processed: {}, Failed: {}", jobId, processed, failed);

        } catch (Exception e) {
            log.error("Error processing bulk job: {}", jobId, e);
            job.setStatus(JobStatus.FAILED);
            job.setErrorMessage(e.getMessage());
            bulkJobPersistencePort.save(job);
        }
    }
}
