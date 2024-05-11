package com.yes.tfgapp.data.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.yes.tfgapp.domain.model.ShoppingListModel

@Dao
interface ShoppingListDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addShoppingList(shoppingList: ShoppingListModel)
    @Update
    suspend fun updateShoppingList(shoppingList: ShoppingListModel)
    @Delete
    suspend fun deleteShoppingList(shoppingList: ShoppingListModel)
    @Query("SELECT * FROM shopping_list ORDER BY id ASC")
    fun readAllData(): LiveData<List<ShoppingListModel>>

}