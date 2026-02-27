package com.afadhitya.taskmanagement.application.usecase.bulkjob;

import com.afadhitya.taskmanagement.application.dto.request.BulkUpdateTasksRequest;
import com.afadhitya.taskmanagement.application.dto.response.BulkJobResponse;
import com.afadhitya.taskmanagement.application.mapper.BulkJobMapper;
import com.afadhitya.taskmanagement.application.port.in.bulkjob.SubmitBulkJobUseCase;
import com.afadhitya.taskmanagement.application.port.out.bulkjob.BulkJobPersistencePort;
import com.afadhitya.taskmanagement.application.port.out.task.TaskPersistencePort;
import com.afadhitya.taskmanagement.application.port.out.user.UserPersistencePort;
import com.afadhitya.taskmanagement.domain.entity.BulkJob;
import com.afadhitya.taskmanagement.domain.entity.Task;
import com.afadhitya.taskmanagement.domain.entity.User;
import com.afadhitya.taskmanagement.domain.enums.JobStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class SubmitBulkJobUseCaseImpl implements SubmitBulkJobUseCase {

    private final BulkJobPersistencePort bulkJobPersistencePort;
    private final UserPersistencePort userPersistencePort;
    private final TaskPersistencePort taskPersistencePort;
    private final BulkJobProcessor bulkJobProcessor;
    private final BulkJobMapper bulkJobMapper;

    @Override
    public BulkJobResponse submitBulkUpdateTasks(Long projectId, BulkUpdateTasksRequest request, Long createdByUserId) {
        User createdBy = userPersistencePort.findById(createdByUserId)
                .orElseThrow(() -> new IllegalArgumentException("User not found with id: " + createdByUserId));

        // Validate all tasks belong to the same project
        validateTasksBelongToProject(projectId, request.taskIds());

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

    private void validateTasksBelongToProject(Long projectId, Set<Long> taskIds) {
        // Bulk fetch all tasks in single query
        List<Task> tasks = taskPersistencePort.findAllById(taskIds);

        // Check if any task IDs were not found
        Set<Long> foundTaskIds = tasks.stream()
                .map(Task::getId)
                .collect(Collectors.toSet());

        Set<Long> notFoundIds = taskIds.stream()
                .filter(id -> !foundTaskIds.contains(id))
                .collect(Collectors.toSet());

        if (!notFoundIds.isEmpty()) {
            throw new IllegalArgumentException("Tasks not found with ids: " + notFoundIds);
        }

        // Validate all tasks belong to the specified project
        for (Task task : tasks) {
            if (!task.getProject().getId().equals(projectId)) {
                throw new IllegalArgumentException(
                        "Task " + task.getId() + " does not belong to project " + projectId + ". " +
                        "All tasks in bulk update must belong to the same project.");
            }
        }
    }
}
