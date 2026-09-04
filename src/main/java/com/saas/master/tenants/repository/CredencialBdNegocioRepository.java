package com.saas.master.tenants.repository;

import com.saas.master.tenants.entity.CredencialBdNegocio;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface CredencialBdNegocioRepository extends JpaRepository<CredencialBdNegocio, UUID> {
}
