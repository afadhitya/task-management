package com.afadhitya.taskmanagement.adapter.out.feature;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PlanFeatureRepository extends JpaRepository<PlanFeatureEntity, Long> {
    Optional<PlanFeatureEntity> findByPlanConfigurationIdAndFeatureId(Long planConfigurationId, Long featureId);
}
