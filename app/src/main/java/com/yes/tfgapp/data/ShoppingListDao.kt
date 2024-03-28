package com.yes.tfgapp.data

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
    suspend fun addShoppingList(shoppingList: ShoppingListModel): Unit

    @Update
    suspend fun updateShoppingList(shoppingList: ShoppingListModel): Unit

    @Delete
    suspend fun deleteShoppingList(shoppingList: ShoppingListModel): Unit

    @Query("SELECT * FROM shopping_list ORDER BY id ASC")
    fun readAllData(): LiveData<List<ShoppingListModel>>

    @Query("DELETE FROM shopping_list WHERE id = :id")
    abstract fun deleteShoppingListById(id: Int)
}