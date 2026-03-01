package com.afadhitya.taskmanagement.adapter.out.feature;

import com.afadhitya.taskmanagement.domain.feature.PlanConfigurationEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PlanConfigurationRepository extends JpaRepository<PlanConfigurationEntity, Long> {

    Optional<PlanConfigurationEntity> findByPlanTierAndIsActiveTrue(String planTier);

    Optional<PlanConfigurationEntity> findByIsDefaultTrue();

    @Query("""
        SELECT pf.isEnabled 
        FROM PlanFeatureEntity pf 
        JOIN pf.feature f 
        WHERE pf.planConfiguration.id = :planConfigId 
        AND f.code = :featureCode
        """)
    Optional<Boolean> isFeatureEnabled(
        @Param("planConfigId") Long planConfigId,
        @Param("featureCode") String featureCode
    );

    @Query("""
        SELECT pl.limitValue 
        FROM PlanLimitEntity pl 
        WHERE pl.planConfiguration.id = :planConfigId 
        AND pl.limitType = :limitType
        """)
    Optional<Integer> getLimit(
        @Param("planConfigId") Long planConfigId,
        @Param("limitType") String limitType
    );
}
