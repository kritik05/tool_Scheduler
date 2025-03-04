package com.toolScheduler.ToolSchedulerApplication.repository;

import com.toolScheduler.ToolSchedulerApplication.model.Tenant;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TenantRepository extends JpaRepository<Tenant, Integer> {
}