package com.afadhitya.taskmanagement.adapter.out.persistence;

import com.afadhitya.taskmanagement.domain.feature.PlanLimitEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface PlanLimitRepository extends JpaRepository<PlanLimitEntity, Long> {

    @Modifying
    @Query("UPDATE PlanLimitEntity pl SET pl.limitValue = :limitValue WHERE pl.planConfiguration.id = :planId AND pl.limitType = :limitType")
    int updateLimitValue(@Param("planId") Long planId, @Param("limitType") String limitType, @Param("limitValue") Integer limitValue);
}
