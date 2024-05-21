package com.example.multitenant.multitenancy.config.tenant.hibernate

import com.example.multitenant.multitenancy.repository.TenantRepository
import com.github.benmanes.caffeine.cache.Caffeine
import com.github.benmanes.caffeine.cache.LoadingCache
import jakarta.annotation.PostConstruct
import org.hibernate.engine.jdbc.connections.spi.MultiTenantConnectionProvider
import org.hibernate.service.UnknownUnwrapTypeException
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import java.sql.Connection
import java.sql.SQLException
import java.util.concurrent.TimeUnit
import javax.sql.DataSource

@Component("schemaBasedMultiTenantConnectionProvider")
class SchemaBasedMultiTenantConnectionProvider(
    private val datasource: DataSource,
    private val tenantRepository: TenantRepository,
    @Value("\${multitenancy.master.schema:#{null}}") private val masterSchema: String?,
    @Value("\${multitenancy.schema-cache.maximumSize:1000}") private val maximumSize: Long,
    @Value("\${multitenancy.schema-cache.expireAfterAccess:10}") private val expireAfterAccess: Int
) : MultiTenantConnectionProvider<String> {

    private lateinit var tenantSchemas: LoadingCache<String, String>

    @PostConstruct
    private fun createCache() {
        val tenantsCacheBuilder = Caffeine.newBuilder().apply {
            if (maximumSize != null) {
                maximumSize(maximumSize)
            }
            if (expireAfterAccess != null) {
                expireAfterAccess(expireAfterAccess.toLong(), TimeUnit.MINUTES)
            }
        }
        tenantSchemas = tenantsCacheBuilder.build { tenantId ->
            tenantRepository.findByTenantId(tenantId)
                .orElseThrow { RuntimeException("No such tenant: $tenantId") }
                .schema
        }
    }

    @Throws(SQLException::class)
    override fun getAnyConnection(): Connection {
        return datasource.connection
    }

    @Throws(SQLException::class)
    override fun releaseAnyConnection(connection: Connection) {
        connection.close()
    }

    @Throws(SQLException::class)
    override fun getConnection(tenantIdentifier: String): Connection {
        val tenantSchema = tenantSchemas.get(tenantIdentifier)
        val connection = getAnyConnection()
        connection.schema = tenantSchema
        return connection
    }

    @Throws(SQLException::class)
    override fun releaseConnection(tenantIdentifier: String, connection: Connection) {
        connection.schema = masterSchema
        releaseAnyConnection(connection)
    }

    override fun supportsAggressiveRelease(): Boolean {
        return false
    }

    override fun <T : Any?> unwrap(unwrapType: Class<T>): T {
        if (MultiTenantConnectionProvider::class.java.isAssignableFrom(unwrapType)) {
            return unwrapType.cast(this)
        } else {
            throw UnknownUnwrapTypeException(unwrapType)
        }
    }

    override fun isUnwrappableAs(unwrapType: Class<*>): Boolean {
        return MultiTenantConnectionProvider::class.java.isAssignableFrom(unwrapType)
    }
}