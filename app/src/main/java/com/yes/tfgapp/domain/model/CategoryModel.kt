package com.yes.tfgapp.domain.model

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.yes.tfgapp.R
import kotlinx.android.parcel.Parcelize

@Entity(tableName = "category")
@Parcelize
data class CategoryModel(
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    val name: String,
    var isSelected : Boolean = false,
    val icon : Int = R.drawable.ic_panaderia
): Parcelable