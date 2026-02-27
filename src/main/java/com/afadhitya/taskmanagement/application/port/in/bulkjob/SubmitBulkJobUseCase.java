package com.afadhitya.taskmanagement.application.port.in.bulkjob;

import com.afadhitya.taskmanagement.application.dto.request.BulkUpdateTasksRequest;
import com.afadhitya.taskmanagement.application.dto.response.BulkJobResponse;

public interface SubmitBulkJobUseCase {

    BulkJobResponse submitBulkUpdateTasks(Long projectId, BulkUpdateTasksRequest request, Long createdByUserId);
}
