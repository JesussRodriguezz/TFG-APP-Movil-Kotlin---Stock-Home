package com.yes.tfgapp.ui.shoppinglistdetail

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.yes.tfgapp.data.AppDataBase
import com.yes.tfgapp.data.repository.ProductRepository
import com.yes.tfgapp.data.repository.ProductShoppingListRepository
import com.yes.tfgapp.domain.model.ProductModel
import com.yes.tfgapp.domain.model.ProductShoppingListModel
import com.yes.tfgapp.domain.model.ShoppingListModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ShoppingListDetailViewModel(aplication: Application, currentShoppingList: ShoppingListModel): AndroidViewModel(aplication){


    private val productShoppingListRepository: ProductShoppingListRepository
    private val productRepository: ProductRepository

    val readAllDataProductShoppingList: LiveData<List<ProductShoppingListModel>>
    val readAllDataProductBoughtShoppingList: LiveData<List<ProductShoppingListModel>>

    val allProductsShoppingListLiveData : LiveData<List<ProductShoppingListModel>>
    var allProductsShoppingList: List<ProductShoppingListModel> = emptyList()

    init{
        val productShoppingListDao = AppDataBase.getDatabase(aplication).productShoppingListDao()
        productShoppingListRepository = ProductShoppingListRepository(productShoppingListDao)

        val productDao = AppDataBase.getDatabase(aplication).productDao()
        productRepository = ProductRepository(productDao)

        readAllDataProductShoppingList = productShoppingListRepository.getProductsForShoppingList(currentShoppingList.id)
        readAllDataProductBoughtShoppingList= productShoppingListRepository.getProductsBoughtForShoppingList(currentShoppingList.id)

        allProductsShoppingListLiveData = productShoppingListRepository.getAllProductsInShoppingList(currentShoppingList.id)
        allProductsShoppingListLiveData.observeForever { products ->
            allProductsShoppingList = products ?: emptyList()
        }
    }

    fun getProductsForShoppingList(productShoppingList: List<ProductShoppingListModel>): LiveData<List<ProductModel>> {
        val productIds = productShoppingList.map { it.productId }
        return productRepository.getProductsById(productIds)
    }

    fun updateProductIsBought(productShoppingList: ProductShoppingListModel) {
        viewModelScope.launch(Dispatchers.IO) {
            productShoppingListRepository.updateProductShoppingListBoughtProduct(productShoppingList)
        }
    }

    fun updateProductIsNotBought(productShoppingList: ProductShoppingListModel) {
        viewModelScope.launch(Dispatchers.IO) {
            productShoppingListRepository.updateProductShoppingListNotBoughtProduct(productShoppingList)
        }
    }

    fun emptyAllList(shoppingList: ShoppingListModel) {
        viewModelScope.launch(Dispatchers.IO) {
            productShoppingListRepository.emptyAllList(shoppingList.id)
        }

    }

}