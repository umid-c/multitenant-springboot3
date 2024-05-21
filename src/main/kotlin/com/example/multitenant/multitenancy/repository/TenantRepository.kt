package com.example.multitenant.multitenancy.repository

import com.example.multitenant.multitenancy.domain.Tenant
import jakarta.persistence.LockModeType
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Lock
import org.springframework.data.jpa.repository.Query
import java.util.*

interface TenantRepository : JpaRepository<Tenant, String> {
    @Query("select t from Tenant t where t.tenantId = :tenantId")
    fun findByTenantId(tenantId: String): Optional<Tenant>

    fun findAllByCreatedIsFalse(): List<Tenant>
}
