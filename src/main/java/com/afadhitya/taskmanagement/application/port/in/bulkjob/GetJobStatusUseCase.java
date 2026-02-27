package com.afadhitya.taskmanagement.application.port.in.bulkjob;

import com.afadhitya.taskmanagement.application.dto.response.BulkJobResponse;

public interface GetJobStatusUseCase {

    BulkJobResponse getJobStatus(String jobId);
}
