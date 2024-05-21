package com.example.multitenant.multitenancy.config.tenant

import jakarta.persistence.EntityManagerFactory
import org.hibernate.cfg.AvailableSettings
import org.hibernate.context.spi.CurrentTenantIdentifierResolver
import org.hibernate.engine.jdbc.connections.spi.MultiTenantConnectionProvider
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.beans.factory.annotation.Value
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory
import org.springframework.boot.autoconfigure.orm.jpa.JpaProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Primary
import org.springframework.data.jpa.repository.config.EnableJpaRepositories
import org.springframework.orm.hibernate5.SpringBeanContainer
import org.springframework.orm.jpa.JpaTransactionManager
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter

@Configuration
@EnableJpaRepositories(
    basePackages = ["com.example.multitenant.repository"],
    entityManagerFactoryRef = "tenantEntityManagerFactory",
    transactionManagerRef = "tenantTransactionManager"
)
class TenantPersistenceConfig(
    private val beanFactory: ConfigurableListableBeanFactory,
    private val jpaProperties: JpaProperties,
    @Value("\${multitenancy.tenant.entityManager.packages}") private val entityPackages: String
) {

    @Primary
    @Bean
    fun tenantEntityManagerFactory(
        @Qualifier("schemaBasedMultiTenantConnectionProvider") connectionProvider: MultiTenantConnectionProvider<String> ,
        @Qualifier("currentTenantIdentifierResolver") tenantResolver: CurrentTenantIdentifierResolver<String>
    ): LocalContainerEntityManagerFactoryBean {

        val properties = HashMap<String, Any>(jpaProperties.properties).apply {
            put(AvailableSettings.PHYSICAL_NAMING_STRATEGY, "org.hibernate.boot.model.naming.CamelCaseToUnderscoresNamingStrategy")
            put(AvailableSettings.IMPLICIT_NAMING_STRATEGY, "org.springframework.boot.orm.jpa.hibernate.SpringImplicitNamingStrategy")
            put(AvailableSettings.BEAN_CONTAINER, SpringBeanContainer(beanFactory))
            remove(AvailableSettings.DEFAULT_SCHEMA)
//            put(AvailableSettings.MULTI_TENANT, MultiTenancyStrategy.SCHEMA)
            put(AvailableSettings.MULTI_TENANT_CONNECTION_PROVIDER, connectionProvider)
            put(AvailableSettings.MULTI_TENANT_IDENTIFIER_RESOLVER, tenantResolver)
        }

        val emfBean = LocalContainerEntityManagerFactoryBean().apply {
            persistenceUnitName = "tenant-persistence-unit"
            setPackagesToScan(entityPackages)
            jpaVendorAdapter = HibernateJpaVendorAdapter()
            setJpaPropertyMap(properties)
        }

//        emfBean.jpaPropertyMap = properties

        return emfBean
    }

    @Primary
    @Bean
    fun tenantTransactionManager(
        @Qualifier("tenantEntityManagerFactory") emf: EntityManagerFactory
    ): JpaTransactionManager {
        return JpaTransactionManager().apply {
            entityManagerFactory = emf
        }
    }
}
