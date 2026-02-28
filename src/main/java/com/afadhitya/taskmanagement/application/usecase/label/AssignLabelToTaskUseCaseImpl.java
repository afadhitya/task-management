package com.afadhitya.taskmanagement.application.usecase.label;

import com.afadhitya.taskmanagement.application.dto.response.LabelResponse;
import com.afadhitya.taskmanagement.application.mapper.LabelMapper;
import com.afadhitya.taskmanagement.application.port.in.label.AssignLabelToTaskUseCase;
import com.afadhitya.taskmanagement.application.port.out.label.LabelPersistencePort;
import com.afadhitya.taskmanagement.application.port.out.task.TaskLabelPersistencePort;
import com.afadhitya.taskmanagement.application.port.out.task.TaskPersistencePort;
import com.afadhitya.taskmanagement.domain.entity.Label;
import com.afadhitya.taskmanagement.domain.entity.Task;
import com.afadhitya.taskmanagement.domain.entity.TaskLabel;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class AssignLabelToTaskUseCaseImpl implements AssignLabelToTaskUseCase {

    private final TaskPersistencePort taskPersistencePort;
    private final LabelPersistencePort labelPersistencePort;
    private final TaskLabelPersistencePort taskLabelPersistencePort;
    private final LabelMapper labelMapper;

    @Override
    public LabelResponse assignLabelToTask(Long taskId, Long labelId, Long currentUserId) {
        Task task = taskPersistencePort.findById(taskId)
                .orElseThrow(() -> new IllegalArgumentException("Task not found with id: " + taskId));

        Label label = labelPersistencePort.findById(labelId)
                .orElseThrow(() -> new IllegalArgumentException("Label not found with id: " + labelId));

        // Validate that the label belongs to the same workspace/project as the task
        Long taskProjectId = task.getProject().getId();
        Long taskWorkspaceId = task.getProject().getWorkspace().getId();
        Long labelWorkspaceId = label.getWorkspace().getId();

        if (!taskWorkspaceId.equals(labelWorkspaceId)) {
            throw new IllegalArgumentException("Label does not belong to the same workspace as the task");
        }

        // For project-specific labels, verify they belong to the same project
        if (!label.isGlobal()) {
            Long labelProjectId = label.getProject().getId();
            if (!taskProjectId.equals(labelProjectId)) {
                throw new IllegalArgumentException("Project-specific label can only be assigned to tasks in the same project");
            }
        }

        // Check if label is already assigned
        if (taskLabelPersistencePort.existsByTaskIdAndLabelId(taskId, labelId)) {
            throw new IllegalArgumentException("Label is already assigned to this task");
        }

        TaskLabel taskLabel = TaskLabel.builder()
                .task(task)
                .label(label)
                .build();

        taskLabelPersistencePort.save(taskLabel);

        return labelMapper.toResponse(label);
    }
}
