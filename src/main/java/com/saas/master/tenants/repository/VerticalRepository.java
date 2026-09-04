package com.saas.master.tenants.repository;

import com.saas.master.tenants.entity.Vertical;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface VerticalRepository extends JpaRepository<Vertical, String> {
}
