package com.saas.master.rbac.repository;

import com.saas.master.rbac.entity.Perfil;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface PerfilRepository extends JpaRepository<Perfil, UUID> {
    Optional<Perfil> findByCodigo(String codigo);
    List<Perfil> findByNegocioIdOrEsSistemaTrue(UUID negocioId);
}
