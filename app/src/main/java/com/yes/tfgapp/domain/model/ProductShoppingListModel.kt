package com.yes.tfgapp.domain.model

import androidx.room.Entity
import androidx.room.ForeignKey


@Entity(tableName = "product_shopping_list",
    primaryKeys = ["productId", "shoppingListId"],
    foreignKeys = [
        ForeignKey(entity = ProductModel::class,
            parentColumns = ["id"],
            childColumns = ["productId"],
            onDelete = ForeignKey.CASCADE),
        ForeignKey(entity = ShoppingListModel::class,
            parentColumns = ["id"],
            childColumns = ["shoppingListId"],
            onDelete = ForeignKey.CASCADE)
    ])
data class ProductShoppingListModel (
    val productId: Int,
    val shoppingListId: Int,
    val isBought: Boolean = false
)