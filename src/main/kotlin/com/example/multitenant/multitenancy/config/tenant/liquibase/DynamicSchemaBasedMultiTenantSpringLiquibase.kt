package com.example.multitenant.multitenancy.config.tenant.liquibase

import com.example.multitenant.multitenancy.domain.Tenant
import com.example.multitenant.multitenancy.repository.TenantRepository
import liquibase.exception.LiquibaseException
import liquibase.integration.spring.SpringLiquibase
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.InitializingBean
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.autoconfigure.liquibase.LiquibaseProperties
import org.springframework.context.ResourceLoaderAware
import org.springframework.core.io.ResourceLoader
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import javax.sql.DataSource

@Component
class DynamicSchemaBasedMultiTenantSpringLiquibase(
    private val masterTenantRepository: TenantRepository,
    private val dataSource: DataSource,
    @Qualifier("tenantLiquibaseProperties") private val liquibaseProperties: LiquibaseProperties
) : InitializingBean, ResourceLoaderAware {

    private lateinit var resourceLoader: ResourceLoader
    private val log = LoggerFactory.getLogger(DynamicSchemaBasedMultiTenantSpringLiquibase::class.java)
/*
    @Scheduled(fixedDelay = 600)
    fun afterPropertidesSet() {
        log.info("Schema based multitenancy enabled")
        val ree= masterTenantRepository.findAllByCreatedIsFalse()
        if(ree.isNotEmpty())
            this.runOnAllSchemas(dataSource,ree)
        log.info("All tenants have been created")
    }*/
    override fun afterPropertiesSet() {
        log.info("Schema based multitenancy enabled")
        this.runOnAllSchemas(dataSource, masterTenantRepository.findAll())
    }

    protected fun runOnAllSchemas(dataSource: DataSource, tenants: Collection<Tenant>) {
        for (tenant in tenants) {
            log.info("Initializing Liquibase for tenant ${tenant.tenantId}")
            val liquibase = this.getSpringLiquibase(dataSource, tenant.schema)
            liquibase.afterPropertiesSet()
            log.info("Liquibase ran for tenant ${tenant.tenantId}")
        }
    }

    protected fun getSpringLiquibase(dataSource: DataSource, schema: String): SpringLiquibase {
        return SpringLiquibase().apply {
            setResourceLoader(this@DynamicSchemaBasedMultiTenantSpringLiquibase.resourceLoader)
            setDataSource(dataSource)
            defaultSchema = schema
            setChangeLogParameters(liquibaseProperties.parameters?.apply {
                put("schema", schema)
            } ?: mapOf("schema" to schema))
            changeLog = liquibaseProperties.changeLog
            contexts = liquibaseProperties.contexts
            liquibaseSchema = liquibaseProperties.liquibaseSchema
            liquibaseTablespace = liquibaseProperties.liquibaseTablespace
            databaseChangeLogTable = liquibaseProperties.databaseChangeLogTable
            databaseChangeLogLockTable = liquibaseProperties.databaseChangeLogLockTable
            isDropFirst = liquibaseProperties.isDropFirst
            setShouldRun(liquibaseProperties.isEnabled)
            setRollbackFile(liquibaseProperties.rollbackFile)
            setLabelFilter(liquibaseProperties.labelFilter)
            isTestRollbackOnUpdate = liquibaseProperties.isTestRollbackOnUpdate
        }
    }

    override fun setResourceLoader(resourceLoader: ResourceLoader) {
        this.resourceLoader = resourceLoader
    }
}
