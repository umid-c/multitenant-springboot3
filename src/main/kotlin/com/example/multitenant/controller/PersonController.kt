package com.example.multitenant.controller

import com.example.multitenant.domain.Product
import com.example.multitenant.repository.ProductRepository
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/person")
class PersonController(
    val productRepository: ProductRepository
) {
    @GetMapping
    fun getPerson(): List<Product> {
        return productRepository.findByName("umid")
    }
}