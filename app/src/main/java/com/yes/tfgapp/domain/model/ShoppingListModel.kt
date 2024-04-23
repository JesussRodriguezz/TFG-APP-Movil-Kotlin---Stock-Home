package com.yes.tfgapp.domain.model

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Parcelize
@Entity(tableName = "shopping_list")
data class ShoppingListModel(
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    val name: String,
    val quantity: Int,
    val quantityBought: Int = 0
): Parcelable