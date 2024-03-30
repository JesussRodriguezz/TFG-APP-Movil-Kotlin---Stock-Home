package com.yes.tfgapp.domain.model

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.android.parcel.Parcelize

@Entity(tableName = "category")
@Parcelize
data class CategoryModel(
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    val name: String,
    val isSelected : Boolean = false
): Parcelable