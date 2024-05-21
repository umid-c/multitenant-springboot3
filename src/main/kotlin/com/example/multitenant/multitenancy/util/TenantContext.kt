package com.example.multitenant.multitenancy.util

import org.slf4j.Logger
import org.slf4j.LoggerFactory

object TenantContext {
    private val logger: Logger = LoggerFactory.getLogger(TenantContext::class.java)

    private val currentTenant = InheritableThreadLocal<String>()

    fun setTenantId(tenantId: String) {
        logger.debug("Setting tenantId to $tenantId")
        currentTenant.set(tenantId)
    }

    fun getTenantId(): String? {
        return currentTenant.get()
    }

    fun clear() {
        currentTenant.remove()
    }
//
//    var tenantId: String
//        get() = currentTenant.get()
//        set(tenantId) {
//            logger.debug("Setting tenantId to $tenantId")
//            currentTenant.set(tenantId)
//        }
//
//    fun clear() {
//        currentTenant.remove()
//    }
}