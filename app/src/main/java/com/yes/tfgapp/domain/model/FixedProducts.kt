package com.yes.tfgapp.domain.model

enum class FixedProducts(
    val id: Int,
    val product_name: String,
    val category: String
) {
    Manzana(1, "Manzana", "Frutas"),
    Pera(2, "Pera", "Frutas"),
    Pan(3, "Pan", "Panadería"),
}