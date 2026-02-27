package com.afadhitya.taskmanagement.adapter.out.persistence.bulkjob;

import com.afadhitya.taskmanagement.adapter.out.persistence.BulkJobRepository;
import com.afadhitya.taskmanagement.application.port.out.bulkjob.BulkJobPersistencePort;
import com.afadhitya.taskmanagement.domain.entity.BulkJob;
import com.afadhitya.taskmanagement.domain.enums.JobStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class BulkJobPersistenceAdapter implements BulkJobPersistencePort {

    private final BulkJobRepository bulkJobRepository;

    @Override
    public BulkJob save(BulkJob bulkJob) {
        return bulkJobRepository.save(bulkJob);
    }

    @Override
    public Optional<BulkJob> findById(String id) {
        return bulkJobRepository.findById(id);
    }

    @Override
    public List<BulkJob> findPendingJobs() {
        return bulkJobRepository.findByStatus(JobStatus.PENDING);
    }
}
