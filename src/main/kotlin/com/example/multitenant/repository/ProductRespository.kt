package com.example.multitenant.repository

import com.example.multitenant.domain.Product
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.CrudRepository
import org.springframework.data.repository.query.Param

interface ProductRepository : JpaRepository<Product, String> {

    @Query(value = "SELECT * FROM product WHERE name = :name", nativeQuery = true)
    fun findByName(@Param("name") name: String): List<Product>
}