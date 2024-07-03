package com.yes.tfgapp.ui.shoppinglist

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.yes.tfgapp.data.AppDataBase
import com.yes.tfgapp.data.repository.ProductRepository
import com.yes.tfgapp.data.repository.CategoryRepository
import com.yes.tfgapp.data.repository.ShoppingListRepository
import com.yes.tfgapp.domain.model.ShoppingListModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ShoppingListViewModel(application: Application) : AndroidViewModel(application) {
    private val sharedPreferences =
        application.getSharedPreferences("sharedPrefs", Application.MODE_PRIVATE)

    val readAllData: LiveData<List<ShoppingListModel>>
    private val repository: ShoppingListRepository

    private val productRepository: ProductRepository
    private val categoryRepository: CategoryRepository


    init {
        val shoppingListDao = AppDataBase.getDatabase(application).shoppingListDao()
        repository = ShoppingListRepository(shoppingListDao)
        readAllData = repository.readAllData

        val productDao = AppDataBase.getDatabase(application).productDao()
        productRepository = ProductRepository(productDao)

        val categoryDao = AppDataBase.getDatabase(application).categoryDao()
        categoryRepository = CategoryRepository(categoryDao)

        val firstTime = sharedPreferences.getBoolean("firstTime", true)
        if (firstTime) {
            viewModelScope.launch(Dispatchers.IO) {
                categoryRepository.addFixedCategories()
                productRepository.addFixedProducts()
            }
            sharedPreferences.edit().putBoolean("firstTime", false).apply()
        }

        viewModelScope.launch(Dispatchers.IO) {
            categoryRepository.updateAllCategories(false)
        }
    }

    fun addShoppingList(shoppingList: ShoppingListModel) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.addShoppingList(shoppingList)
        }
    }

    fun deleteShoppingList(shoppingList: ShoppingListModel) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteShoppingList(shoppingList)
        }
    }

    fun updateShoppingList(shoppingList: ShoppingListModel) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.updateShoppingList(shoppingList)
        }
    }


}