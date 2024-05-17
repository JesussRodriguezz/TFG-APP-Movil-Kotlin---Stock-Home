package com.yes.tfgapp.domain.model

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.android.parcel.Parcelize


@Parcelize
@Entity(tableName = "stock_product")
data class StockProductModel (
    @PrimaryKey
    val id: String,
    val name: String,
    val expirationDate: String = "",
    val image: String
): Parcelable