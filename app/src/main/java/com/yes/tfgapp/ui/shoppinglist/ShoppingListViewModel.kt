package com.yes.tfgapp.ui.shoppinglist

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.yes.tfgapp.data.AppDataBase
import com.yes.tfgapp.data.ShoppingListRepository
import com.yes.tfgapp.domain.model.ShoppingListModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ShoppingListViewModel(application: Application): AndroidViewModel(application){
    val readAllData: LiveData<List<ShoppingListModel>>
    private val repository: ShoppingListRepository

    init {
        val shoppingListDao = AppDataBase.getDatabase(application).shoppingListDao()
        repository = ShoppingListRepository(shoppingListDao)
        readAllData = repository.readAllData
    }

    fun addShoppingList(shoppingList: ShoppingListModel){
        viewModelScope.launch(Dispatchers.IO) {
            repository.addShoppingList(shoppingList)
        }
    }

    fun deleteShoppingList(shoppingList: ShoppingListModel){
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteShoppingList(shoppingList)
        }
    }

    fun deleteShoppingList(id: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteShoppingListById(id)
        }
    }

    fun updateShoppingList(shoppingList: ShoppingListModel){
        viewModelScope.launch(Dispatchers.IO) {
            repository.updateShoppingList(shoppingList)
        }
    }

}