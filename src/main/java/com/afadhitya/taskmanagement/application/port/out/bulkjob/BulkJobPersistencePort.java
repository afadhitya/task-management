package com.afadhitya.taskmanagement.application.port.out.bulkjob;

import com.afadhitya.taskmanagement.domain.entity.BulkJob;

import java.util.List;
import java.util.Optional;

public interface BulkJobPersistencePort {

    BulkJob save(BulkJob bulkJob);

    Optional<BulkJob> findById(String id);

    List<BulkJob> findPendingJobs();
}
