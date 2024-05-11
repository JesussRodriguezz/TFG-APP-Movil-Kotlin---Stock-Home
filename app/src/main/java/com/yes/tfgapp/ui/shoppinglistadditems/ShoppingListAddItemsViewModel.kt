package com.yes.tfgapp.ui.shoppinglistadditems

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.yes.tfgapp.data.AppDataBase
import com.yes.tfgapp.data.repository.CategoryRepository
import com.yes.tfgapp.data.repository.ProductRepository
import com.yes.tfgapp.data.repository.ProductShoppingListRepository
import com.yes.tfgapp.data.repository.ShoppingListRepository
import com.yes.tfgapp.domain.model.CategoryModel
import com.yes.tfgapp.domain.model.ProductModel
import com.yes.tfgapp.domain.model.ProductShoppingListModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ShoppingListAddItemsViewModel(application: Application) : AndroidViewModel(application) {

    private val shoppingListRepository: ShoppingListRepository
    private val productRepository: ProductRepository
    private val categoryRepository: CategoryRepository
    private val productShoppingListRepository: ProductShoppingListRepository

    val readAllDataProduct: LiveData<List<ProductModel>>
    val readAllDataCategory: LiveData<List<CategoryModel>>

    private val allProductsLiveData: LiveData<List<ProductModel>>
    private var allProducts: List<ProductModel> = emptyList()

    val productIdLiveData = MutableLiveData<Long>()
    private val productCategoryIdLiveData = MutableLiveData<Int>()

    init {
        val shoppingListDao = AppDataBase.getDatabase(application).shoppingListDao()
        shoppingListRepository = ShoppingListRepository(shoppingListDao)
        val productDao = AppDataBase.getDatabase(application).productDao()
        productRepository = ProductRepository(productDao)
        readAllDataProduct = productRepository.readAllData

        val categoryDao = AppDataBase.getDatabase(application).categoryDao()
        categoryRepository = CategoryRepository(categoryDao)
        readAllDataCategory = categoryRepository.readAllData

        val productShoppingListDato = AppDataBase.getDatabase(application).productShoppingListDao()
        productShoppingListRepository = ProductShoppingListRepository(productShoppingListDato)

        allProductsLiveData = productRepository.readAllData

        allProductsLiveData.observeForever { products ->
            allProducts = products ?: emptyList()
        }

    }

    fun updateCategory(category: CategoryModel) {
        viewModelScope.launch(Dispatchers.IO) {
            categoryRepository.updateCategory(category)
        }
    }

    fun updateCategories(updatedCategories: List<CategoryModel>) {
        viewModelScope.launch(Dispatchers.IO) {
            categoryRepository.updateCategories(updatedCategories)
        }

    }

    fun addCategory(category: CategoryModel) {
        viewModelScope.launch(Dispatchers.IO) {
            categoryRepository.addCategory(category)
        }
    }

    fun deleteCategory(category: CategoryModel) {
        viewModelScope.launch(Dispatchers.IO) {
            categoryRepository.deleteCategory(category)
        }
    }

    fun addProductToList(productShoppingList: ProductShoppingListModel) {
        viewModelScope.launch(Dispatchers.IO) {
            productShoppingListRepository.addProductShoppingList(productShoppingList)
        }
    }

    fun filterProducts(text: String?): List<ProductModel> {
        val productsToShow = mutableListOf<ProductModel>()
        text?.let {
            if (it.isNotEmpty()) {
                val temporalProduct = ProductModel(name = it)
                productsToShow.add(temporalProduct)
                productsToShow.addAll(allProducts.filter { product ->
                    product.name.contains(it, ignoreCase = true)
                })
            }
        }
        return productsToShow
    }

    fun addProduct(product: ProductModel) {
        viewModelScope.launch(Dispatchers.IO) {
            val existingProduct = productRepository.findProductByName(product.name)
            if (existingProduct == null) {
                // El producto no existe, insertar en la base de datos
                val productId = productRepository.insertProduct(product)
                productIdLiveData.postValue(productId)
            } else {
                // El producto ya existe, opcionalmente actualizar o simplemente usar el ID existente
                productIdLiveData.postValue(existingProduct.id.toLong())
            }
        }
    }

    override fun onCleared() {
        //remove observer
        allProductsLiveData.removeObserver { }
        super.onCleared()
    }

    suspend fun getCategoryById(id: Int): CategoryModel {
        return categoryRepository.getCategoryById(id)
    }

    fun updateProduct(product: ProductModel) {
        viewModelScope.launch(Dispatchers.IO) {
            productRepository.updateProduct(product)
            productCategoryIdLiveData.postValue(product.categoryId)
        }
    }

    fun deleteProductFromList(productShoppingList: ProductShoppingListModel) {
        viewModelScope.launch(Dispatchers.IO) {
            productShoppingListRepository.deleteProductShoppingList(productShoppingList)
        }

    }
}