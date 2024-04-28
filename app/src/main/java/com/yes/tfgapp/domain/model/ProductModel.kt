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
        onDelete = ForeignKey.CASCADE // Esto especifica que si se elimina una categoría, se eliminarán todos los productos relacionados
    )]
)
data class ProductModel(
    @PrimaryKey(autoGenerate = true)
    val id:Int = 0,
    val name: String,
    val categoryId: Int = 7
): Parcelable