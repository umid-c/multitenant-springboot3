package com.example.multitenant.domain

import jakarta.persistence.*
import lombok.Builder
import lombok.Getter
import lombok.NoArgsConstructor
import lombok.Setter
import java.time.Instant

@Entity
@Table(name = "product")
@Getter
@Setter
@NoArgsConstructor
class Product (
    @Id
    @Column(name = "id", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: String,

    val name: String,

    val version: Int
)