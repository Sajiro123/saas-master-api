package com.saas.master.tenants.repository;

import com.saas.master.tenants.entity.PlanSuscripcion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PlanSuscripcionRepository extends JpaRepository<PlanSuscripcion, String> {
    List<PlanSuscripcion> findByVerticalIdAndEstaActivoTrue(String verticalId);
}
