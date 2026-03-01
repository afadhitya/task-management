package com.afadhitya.taskmanagement.adapter.out.feature;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PlanFeatureRepository extends JpaRepository<PlanFeatureEntity, Long> {

    List<PlanFeatureEntity> findByPlanConfigurationId(Long planConfigurationId);

    @Modifying
    @Query("UPDATE PlanFeatureEntity pf SET pf.isEnabled = :isEnabled WHERE pf.planConfiguration.id = :planId AND pf.feature.code = :featureCode")
    int updateFeatureStatus(@Param("planId") Long planId, @Param("featureCode") String featureCode, @Param("isEnabled") Boolean isEnabled);
}
