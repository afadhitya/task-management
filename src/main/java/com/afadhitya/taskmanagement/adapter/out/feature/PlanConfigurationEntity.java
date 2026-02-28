package com.afadhitya.taskmanagement.adapter.out.feature;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "plan_configurations")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PlanConfigurationEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "plan_tier", nullable = false)
    private String planTier;

    @Column(nullable = false)
    private String name;

    private String description;

    @Column(name = "is_active", nullable = false)
    @Builder.Default
    private Boolean isActive = true;

    @Column(name = "is_default", nullable = false)
    @Builder.Default
    private Boolean isDefault = false;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "planConfiguration", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<PlanFeatureEntity> planFeatures = new ArrayList<>();

    @OneToMany(mappedBy = "planConfiguration", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<PlanLimitEntity> planLimits = new ArrayList<>();
}
