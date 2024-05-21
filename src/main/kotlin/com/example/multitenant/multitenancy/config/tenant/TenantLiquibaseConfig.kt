package com.example.multitenant.multitenancy.config.tenant

import com.example.multitenant.multitenancy.config.tenant.liquibase.DynamicSchemaBasedMultiTenantSpringLiquibase
import com.example.multitenant.multitenancy.repository.TenantRepository
import javax.sql.DataSource
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.boot.autoconfigure.liquibase.LiquibaseProperties
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Lazy

@Lazy(false)
@Configuration
@ConditionalOnProperty(name = ["multitenancy.tenant.liquibase.enabled"], havingValue = "true", matchIfMissing = true)
class TenantLiquibaseConfig {

    @Bean
    @ConfigurationProperties("multitenancy.tenant.liquibase")
    fun tenantLiquibaseProperties(): LiquibaseProperties {
        return LiquibaseProperties()
    }

    @Bean
    fun tenantLiquibase(
        masterTenantRepository: TenantRepository,
        dataSource: DataSource,
        @Qualifier("tenantLiquibaseProperties")
        liquibaseProperties: LiquibaseProperties
    ): DynamicSchemaBasedMultiTenantSpringLiquibase {
        return DynamicSchemaBasedMultiTenantSpringLiquibase(masterTenantRepository, dataSource, liquibaseProperties)
    }
}
