package com.example.multitenant.multitenancy.domain

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import lombok.Getter
import lombok.Setter

@Entity
@Getter
@Setter
class Tenant (
    @Id
    @Column(name = "tenant_id")
    val tenantId: String,

    @Column(name = "schema_id")
    val schema: String,

    @Column(name = "is_created")
    val created: Boolean
)