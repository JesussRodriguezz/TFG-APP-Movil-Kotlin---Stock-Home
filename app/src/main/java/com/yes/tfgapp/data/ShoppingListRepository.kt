package com.yes.tfgapp.data

import androidx.lifecycle.LiveData
import com.yes.tfgapp.domain.model.ShoppingListModel

class ShoppingListRepository(private val shoppingListDao: ShoppingListDao) {

    val readAllData: LiveData<List<ShoppingListModel>> = shoppingListDao.readAllData()

    suspend fun addShoppingList(shoppingList: ShoppingListModel){
        shoppingListDao.addShoppingList(shoppingList)
    }

    suspend fun updateShoppingList(shoppingList: ShoppingListModel){
        shoppingListDao.updateShoppingList(shoppingList)
    }

    suspend fun deleteShoppingList(shoppingList: ShoppingListModel){
        shoppingListDao.deleteShoppingList(shoppingList)
    }


}