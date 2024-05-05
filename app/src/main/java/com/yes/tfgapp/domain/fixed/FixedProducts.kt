package com.yes.tfgapp.domain.fixed

enum class FixedProducts(
    val id: Int = 0,
    val product_name: String,
    val category_id: Int
) {
    Manzana(product_name="Manzana", category_id= FixedCategories.COMIDA.id),
    Pera( product_name="Pera",category_id= FixedCategories.COMIDA.id),
    Pan( product_name="Pan", category_id= FixedCategories.BEBIDAS.id),
    Leche( product_name="Leche", category_id= FixedCategories.BEBIDAS.id),
    Detergente( product_name="Detergente", category_id= FixedCategories.LIMPIEZA.id),
    Lavavajillas( product_name="Lavavajillas", category_id= FixedCategories.LIMPIEZA.id),



}