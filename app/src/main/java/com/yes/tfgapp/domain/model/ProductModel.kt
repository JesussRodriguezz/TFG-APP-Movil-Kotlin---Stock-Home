package com.yes.tfgapp.domain.model

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import kotlinx.android.parcel.Parcelize


@Parcelize
@Entity(
    tableName = "product",
    foreignKeys = [ForeignKey(
        entity = CategoryModel::class,
        parentColumns = ["id"],
        childColumns = ["categoryId"],
        onDelete = ForeignKey.CASCADE
    )]
)
data class ProductModel(
    @PrimaryKey(autoGenerate = true)
    val id:Int = 0,
    val name: String,
    val categoryId: Int = 14
): Parcelable