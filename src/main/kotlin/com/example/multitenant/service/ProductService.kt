package com.example.multitenant.service

import com.example.multitenant.repository.ProductRepository

class ProductService(
private val dictionaryRepository: ProductRepository
)