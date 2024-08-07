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
    LIMPIEZA(13,"Limpieza",R.drawable.ic_limpieza),
    OTROS(14,"Otros",R.drawable.ic_others_category);

    companion object {
        fun getCategoryIdByName(name: String): Int {
            return entries.find { it.categoryName == name }?.id ?: OTROS.id
        }

    }

}
