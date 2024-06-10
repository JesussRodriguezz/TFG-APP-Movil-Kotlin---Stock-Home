package com.yes.tfgapp.domain.model

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Parcelize
@Entity(tableName = "stock_product")
data class StockProductModel (
    @PrimaryKey
    val id: String,
    val name: String,
    var expirationDate: String = "",
    val image: String? = null,
    val icon: Int? = null,
    val addedDate: String = getCurrentDate(),
    var daysToExpire: Int = 0,
    var categoryId: Int = 0,
    var nutriscoreGrade: String = "",
    var nutriscoreScore: Int = 0,
    var ingredientsText: String = "",
    var ingredientsTextEs: String = "",
    var quantity: String = "",
    var fatLevel: String = "",
    var saltLevel: String = "",
    var saturatedFatLevel: String = "",
    var sugarsLevel: String = "",
    var calories: String = "",
    var fat: String = "",
    var saturatedFat: String = "",
    var carbohydrates: String = "",
    var salt: String = ""

): Parcelable {
    companion object {
        fun getCurrentDate(): String {
            val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            return dateFormat.format(Date())
        }

    }
}
