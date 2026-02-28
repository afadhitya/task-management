package com.afadhitya.taskmanagement.application.event;

import com.afadhitya.taskmanagement.application.dto.request.BulkUpdateTasksRequest;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class BulkJobSubmittedEvent {

    private final String jobId;
    private final BulkUpdateTasksRequest request;
}
