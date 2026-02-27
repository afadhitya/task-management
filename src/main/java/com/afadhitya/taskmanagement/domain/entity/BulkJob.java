package com.afadhitya.taskmanagement.domain.entity;

import com.afadhitya.taskmanagement.domain.enums.JobStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "bulk_jobs")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BulkJob {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private JobStatus status = JobStatus.PENDING;

    @Column(name = "total_items")
    private Integer totalItems;

    @Column(name = "processed_items")
    @Builder.Default
    private Integer processedItems = 0;

    @Column(name = "failed_items")
    @Builder.Default
    private Integer failedItems = 0;

    @Column(name = "job_type", nullable = false)
    private String jobType;

    @Column(name = "error_message", columnDefinition = "TEXT")
    private String errorMessage;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by", nullable = false)
    private User createdBy;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @Column(name = "completed_at")
    private LocalDateTime completedAt;

    public double getProgressPercentage() {
        if (totalItems == null || totalItems == 0) {
            return 0.0;
        }
        return (double) processedItems / totalItems * 100;
    }
}
