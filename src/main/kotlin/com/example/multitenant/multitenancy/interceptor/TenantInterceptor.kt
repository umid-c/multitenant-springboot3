package com.example.multitenant.multitenancy.interceptor

import com.example.multitenant.multitenancy.util.TenantContext
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import org.springframework.ui.ModelMap
import org.springframework.web.context.request.ServletWebRequest
import org.springframework.web.context.request.WebRequest
import org.springframework.web.context.request.WebRequestInterceptor

@Component
class TenantInterceptor(
    @Value("\${multitenancy.tenant.default-tenant:#{null}}")
    private val defaultTenant: String?
) : WebRequestInterceptor {

    override fun preHandle(request: WebRequest) {
        val tenantId: String = request.getHeader("X-TENANT-ID")
            ?: defaultTenant
            ?: (request as? ServletWebRequest)?.request?.serverName?.split(".")?.get(0)
            ?: throw IllegalStateException("No tenant ID found in request")

        TenantContext.setTenantId(tenantId)
    }

    override fun postHandle(request: WebRequest, model: ModelMap?) {
        TenantContext.clear()
    }

    override fun afterCompletion(request: WebRequest, ex: Exception?) {
        // NOOP
    }
}