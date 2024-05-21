package com.example.multitenant.multitenancy.config.tenant.hibernate

import com.example.multitenant.multitenancy.util.TenantContext
import org.hibernate.cfg.AvailableSettings
import org.hibernate.context.spi.CurrentTenantIdentifierResolver
import org.springframework.boot.autoconfigure.orm.jpa.HibernatePropertiesCustomizer
import org.springframework.stereotype.Component


@Component("currentTenantIdentifierResolver")
class CurrentTenantIdentifierResolverImpl : CurrentTenantIdentifierResolver<String>, HibernatePropertiesCustomizer {

//    private var currentTenant: String = "unknown"
//
//    fun setCurrentTenant(tenant: String) {
//        currentTenant = tenant
//    }

    override fun resolveCurrentTenantIdentifier(): String {
//        try{
//            val tenantId = TenantIdHolder.currentTenantId
//            return tenantId
//
//        }catch(e: Exception){
//            return "BOOTSTRAP"
//        }
        val tenantId = TenantContext.getTenantId()
        return tenantId?:"dummy"
    }

    override fun validateExistingCurrentSessions(): Boolean {
        return true
    }

    override fun customize(hibernateProperties: MutableMap<String, Any>) {
        hibernateProperties[AvailableSettings.MULTI_TENANT_IDENTIFIER_RESOLVER] = this
    }
}