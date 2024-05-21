package com.example.multitenant.config
import com.example.multitenant.multitenancy.interceptor.TenantInterceptor
import org.springframework.context.annotation.Configuration
import org.springframework.web.servlet.config.annotation.InterceptorRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer

@Configuration
class WebConfiguration(
    private val tenantInterceptor: TenantInterceptor
) : WebMvcConfigurer {

    override fun addInterceptors(registry: InterceptorRegistry) {
        registry.addWebRequestInterceptor(tenantInterceptor)
    }
}