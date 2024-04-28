package com.yes.tfgapp.domain.model

enum class FixedCategories(
    val id: Int,
    val cateogory_name: String
) {
    FOOD(1, "Frutas"),
    DRINKS(2, "Panadería"),
    CLEANING(3, "Cleaning"),
    COMIDA(4,"Comida"),
    BEBIDAS(5,"Bebidas"),
    LIMPIEZA(6,"Limpieza"),
    OTHERS(7,"Otros")

}
