package com.saas.master.rbac.repository;

import com.saas.master.rbac.entity.Accion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface AccionRepository extends JpaRepository<Accion, UUID> {
    Optional<Accion> findByCodigo(String codigo);
    List<Accion> findByModulo(String modulo);
}
