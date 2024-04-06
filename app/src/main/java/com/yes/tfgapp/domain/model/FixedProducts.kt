package com.yes.tfgapp.domain.model

enum class FixedProducts(
    val id: Int,
    val product_name: String,
    val category_id: Int
) {
    Manzana(1, "Manzana", FixedCategories.COMIDA.id),
    Pera(2, "Pera", FixedCategories.COMIDA.id),
    Pan(3, "Pan", FixedCategories.BEBIDAS.id),
}