package com.afadhitya.taskmanagement.adapter.out.feature;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PlanLimitRepository extends JpaRepository<PlanLimitEntity, Long> {
    Optional<PlanLimitEntity> findByPlanConfigurationIdAndLimitType(Long planConfigurationId, String limitType);
}
