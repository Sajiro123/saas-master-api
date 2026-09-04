package com.saas.master.rbac.repository;

import com.saas.master.rbac.entity.UsuarioNegocio;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface UsuarioNegocioRepository extends JpaRepository<UsuarioNegocio, UUID> {
    List<UsuarioNegocio> findByNegocioId(UUID negocioId);
    Optional<UsuarioNegocio> findByNegocioIdAndEmail(UUID negocioId, String email);
    Optional<UsuarioNegocio> findByEmail(String email);
}
