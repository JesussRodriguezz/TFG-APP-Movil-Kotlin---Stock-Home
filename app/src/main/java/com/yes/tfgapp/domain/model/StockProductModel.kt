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

): Parcelable {
    companion object {
        fun getCurrentDate(): String {
            val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            return dateFormat.format(Date())
        }

    }
}
