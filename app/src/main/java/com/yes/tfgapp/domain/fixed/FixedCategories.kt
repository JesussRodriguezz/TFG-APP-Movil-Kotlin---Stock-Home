package com.yes.tfgapp.domain.fixed

import com.yes.tfgapp.R

enum class FixedCategories(
    val id: Int,
    val cateogory_name: String,
    val icon: Int
) {
    FOOD(1, "Frutas", R.drawable.nestea),
    DRINKS(2, "Panadería",R.drawable.ic_panaderia),
    CLEANING(3, "Cleaning",R.drawable.ic_panaderia),
    COMIDA(4,"Comida",R.drawable.image_removebg_preview),
    BEBIDAS(5,"Bebidas",R.drawable.ic_panaderia),
    LIMPIEZA(6,"Limpieza",R.drawable.ic_panaderia),
    OTHERS(7,"Otros",R.drawable.ic_others_category)

}
