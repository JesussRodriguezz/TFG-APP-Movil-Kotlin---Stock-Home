package com.yes.tfgapp.domain.fixed

import com.yes.tfgapp.R

enum class FixedCategories(
    val id: Int,
    val categoryName: String,
    val icon: Int
) {

    FYV(1, "Frutas-Verduras", R.drawable.ic_fyv),
    CARNES(2, "Carnes",R.drawable.ic_carnes),
    PYM(3, "Pescado-Marisco",R.drawable.ic_pescados),
    LATAS(4, "Latas",R.drawable.ic_latas),
    LACTEOS(5, "Lácteos",R.drawable.ic_lacteos),
    PANADERIA(6,"Panadería",R.drawable.ic_panaderia),
    BEBIDAS(7,"Bebidas",R.drawable.ic_bebidas),
    DESAYUNO(8,"Desayuno",R.drawable.ic_desayuno),
    EYS(9,"Especias-Salsas",R.drawable.ic_eys),
    CONGELADOS(10,"Congelados",R.drawable.ic_congelados),
    PASTA(11,"Pasta-Granos",R.drawable.ic_gyp),
    SNACKS(12,"Snacks-Dulces",R.drawable.ic_dys),
    LIMPIEZA(13,"Limpieza",R.drawable.ic_others_category),
    OTROS(14,"Otros",R.drawable.ic_others_category);

    companion object {
        fun getCategoryIdByIcon(icon: Int): Int {
            return values().find { it.icon == icon }?.id ?: OTROS.id
        }

        fun getCategoryNameById(id: Int): String {
            return values().find { it.id == id }?.categoryName ?: OTROS.categoryName
        }
    }


    /*FOOD(1, "Frutas", R.drawable.nestea),
    DRINKS(2, "Panadería",R.drawable.ic_panaderia),
    CLEANING(3, "Cleaning",R.drawable.ic_panaderia),
    COMIDA(4,"Comida",R.drawable.image_removebg_preview),
    BEBIDAS(5,"Bebidas",R.drawable.ic_panaderia),
    LIMPIEZA(6,"Limpieza",R.drawable.ic_panaderia),
    OTHERS(7,"Otros",R.drawable.ic_others_category)*/

}
