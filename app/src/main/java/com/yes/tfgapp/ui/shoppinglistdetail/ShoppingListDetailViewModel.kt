package com.yes.tfgapp.ui.shoppinglistdetail

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.yes.tfgapp.data.AppDataBase
import com.yes.tfgapp.data.CategoryRepository
import com.yes.tfgapp.data.ProductRepository
import com.yes.tfgapp.data.ShoppingListRepository
import com.yes.tfgapp.domain.model.CategoryModel
import com.yes.tfgapp.domain.model.ProductModel
import com.yes.tfgapp.domain.model.ShoppingListModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ShoppingListDetailViewModel(application: Application): AndroidViewModel(application){
    private val sharedPreferences = application.getSharedPreferences("sharedPrefs", Application.MODE_PRIVATE)

    private val shoppingListRepository: ShoppingListRepository
    private val productRepository: ProductRepository
    private val categoryRepository: CategoryRepository

    val readAllDataProduct: LiveData<List<ProductModel>>
    var readProductsByCategory: LiveData<List<ProductModel>>? = null

    val readAllDataCategory: LiveData<List<CategoryModel>>


    init {

        val shoppingListDao = AppDataBase.getDatabase(application).shoppingListDao()
        shoppingListRepository = ShoppingListRepository(shoppingListDao)
        val productDao = AppDataBase.getDatabase(application).productDao()
        productRepository = ProductRepository(productDao)
        readAllDataProduct = productRepository.readAllData

        val categoryDao = AppDataBase.getDatabase(application).categoryDao()
        categoryRepository = CategoryRepository(categoryDao)
        readAllDataCategory = categoryRepository.readAllData


    }

    fun updateCategory(category: CategoryModel){
        viewModelScope.launch(Dispatchers.IO) {
            categoryRepository.updateCategory(category)
        }
    }

    fun updateCategories(updatedCategories: List<CategoryModel>) {
        viewModelScope.launch(Dispatchers.IO) {
            categoryRepository.updateCategories(updatedCategories)
        }

    }

    fun addCategory(category: CategoryModel){
        viewModelScope.launch(Dispatchers.IO) {
            categoryRepository.addCategory(category)
        }
    }

    fun deleteCategory(category: CategoryModel) {
        viewModelScope.launch(Dispatchers.IO) {
            categoryRepository.deleteCategory(category)
        }

    }

}