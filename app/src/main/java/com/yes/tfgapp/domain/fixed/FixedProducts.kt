package com.yes.tfgapp.domain.fixed

enum class FixedProducts(
    val id: Int = 0,
    val productName: String,
    val categoryId: Int
) {
    Manzana(productName="Manzana", categoryId= FixedCategories.COMIDA.id),
    Pera( productName="Pera",categoryId= FixedCategories.COMIDA.id),
    Pan( productName="Pan", categoryId= FixedCategories.BEBIDAS.id),
    Leche( productName="Leche", categoryId= FixedCategories.BEBIDAS.id),
    Detergente( productName="Detergente", categoryId= FixedCategories.LIMPIEZA.id),
    Lavavajillas( productName="Lavavajillas", categoryId= FixedCategories.LIMPIEZA.id),



}