package com.afadhitya.taskmanagement.adapter.out.feature;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "plan_limits")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PlanLimitEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "plan_configuration_id", nullable = false)
    private PlanConfigurationEntity planConfiguration;

    @Column(name = "limit_type", nullable = false)
    private String limitType;

    @Column(name = "limit_value", nullable = false)
    private Integer limitValue;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
}
