package com.example.multitenant.multitenancy.config.master

import java.util.HashMap
import jakarta.persistence.EntityManagerFactory
import javax.sql.DataSource
import lombok.RequiredArgsConstructor
import org.hibernate.cfg.AvailableSettings
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.beans.factory.annotation.Value
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory
import org.springframework.boot.autoconfigure.orm.jpa.JpaProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.jpa.repository.config.EnableJpaRepositories
import org.springframework.orm.hibernate5.SpringBeanContainer
import org.springframework.orm.jpa.JpaTransactionManager
import org.springframework.orm.jpa.JpaVendorAdapter
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter

@Configuration
@EnableJpaRepositories(
    basePackages = ["com.example.multitenant.multitenancy.repository"],
    entityManagerFactoryRef = "masterEntityManagerFactory",
    transactionManagerRef = "masterTransactionManager"
)
class MasterPersistenceConfig(
    private val beanFactory: ConfigurableListableBeanFactory,
    private val jpaProperties: JpaProperties,
    @Value("\${multitenancy.master.entityManager.packages}") private val entityPackages: String
) {

    @Bean
    fun masterEntityManagerFactory(dataSource: DataSource): LocalContainerEntityManagerFactoryBean {
        val properties = HashMap<String, Any>(jpaProperties.properties).apply {
            put(AvailableSettings.PHYSICAL_NAMING_STRATEGY, "org.hibernate.boot.model.naming.CamelCaseToUnderscoresNamingStrategy")
            put(AvailableSettings.IMPLICIT_NAMING_STRATEGY, "org.springframework.boot.orm.jpa.hibernate.SpringImplicitNamingStrategy")
            put(AvailableSettings.BEAN_CONTAINER, SpringBeanContainer(beanFactory))
        }
        val em = LocalContainerEntityManagerFactoryBean().apply {
            persistenceUnitName = "master-persistence-unit"
            setPackagesToScan(entityPackages)
            setDataSource(dataSource)
            jpaVendorAdapter = HibernateJpaVendorAdapter()
            setJpaPropertyMap(properties)
        }
        return em
    }

    @Bean
    fun masterTransactionManager(@Qualifier("masterEntityManagerFactory") emf: EntityManagerFactory): JpaTransactionManager {
        return JpaTransactionManager().apply {
            entityManagerFactory = emf
        }
    }
}
