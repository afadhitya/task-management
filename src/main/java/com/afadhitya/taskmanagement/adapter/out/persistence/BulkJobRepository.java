package com.afadhitya.taskmanagement.adapter.out.persistence;

import com.afadhitya.taskmanagement.domain.entity.BulkJob;
import com.afadhitya.taskmanagement.domain.enums.JobStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BulkJobRepository extends JpaRepository<BulkJob, String> {

    List<BulkJob> findByStatus(JobStatus status);
}
