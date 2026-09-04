package com.saas.master.tenants.repository;

import com.saas.master.tenants.entity.UsuarioGlobal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface UsuarioGlobalRepository extends JpaRepository<UsuarioGlobal, UUID> {
    Optional<UsuarioGlobal> findByEmail(String email);
    boolean existsByEmail(String email);
    List<UsuarioGlobal> findByNegocioId(UUID negocioId);
}
