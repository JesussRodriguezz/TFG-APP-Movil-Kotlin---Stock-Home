package com.yes.tfgapp.domain.model

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.android.parcel.Parcelize


@Parcelize
@Entity(tableName = "product")
data class ProductModel(
    @PrimaryKey(autoGenerate = true)
    val id:Int,
    val name: String,
    val category: String
): Parcelable