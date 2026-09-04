package com.saas.master.tenants.repository;

import com.saas.master.tenants.entity.Negocio;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface NegocioRepository extends JpaRepository<Negocio, UUID> {
    Optional<Negocio> findBySubdominio(String subdominio);
    Optional<Negocio> findByNumeroIdentificacion(String numeroIdentificacion);
    boolean existsBySubdominio(String subdominio);
    boolean existsByNumeroIdentificacion(String numeroIdentificacion);
}
