package com.yes.tfgapp.ui.mystock

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.yes.tfgapp.data.AppDataBase
import com.yes.tfgapp.data.repository.StockProductRepository
import com.yes.tfgapp.domain.model.StockProductModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MyStockViewModel(application: Application) : AndroidViewModel(application) {

    private val stockProductRepository: StockProductRepository
    val readAllData : LiveData<List<StockProductModel>>

    private var isNameAsc = true
    private var isAddedDateAsc = true
    private var isExpiryDateAsc = true

    init {
        val stockProductDao = AppDataBase.getDatabase(application).stockProductDao()
        stockProductRepository = StockProductRepository(stockProductDao)
        readAllData = stockProductRepository.readAllData
    }


    fun addProduct(stockProduct: StockProductModel) {
        viewModelScope.launch(Dispatchers.IO) {
            stockProductRepository.addStockProduct(stockProduct)
        }
    }

    fun addProductIfNotExistsSameName(stockProduct: StockProductModel, callback: (Boolean) -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            val existingStockProduct = stockProductRepository.getStockProductByProductName(stockProduct.name)
            println("existingStockProduct: $existingStockProduct")
            if (existingStockProduct == null) {
                stockProductRepository.addStockProduct(stockProduct)
                callback(true) // Indica que el producto se añadió correctamente
            } else {
                callback(false) // Indica que el producto ya existe
            }
        }
    }

    fun addProductIfNotExists(stockProduct: StockProductModel, callback: (Boolean) -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            val existingStockProduct = stockProductRepository.getStockProductByProductId(stockProduct.id)
            println("existingStockProduct: $existingStockProduct")
            if (existingStockProduct == null) {
                stockProductRepository.addStockProduct(stockProduct)
                callback(true) // Indica que el producto se añadió correctamente
            } else {
                callback(false) // Indica que el producto ya existe
            }
        }
    }

    fun updateStockProduct(stockProduct: StockProductModel) {
        viewModelScope.launch(Dispatchers.IO) {
            stockProductRepository.updateStockProduct(stockProduct)
        }
    }

    fun deleteStockProduct(stockProduct: StockProductModel) {
        viewModelScope.launch(Dispatchers.IO) {
            stockProductRepository.deleteStockProduct(stockProduct)
        }
    }

    fun getStockProductsOrderedByName(): LiveData<List<StockProductModel>> {
        return if (isNameAsc) {
            isNameAsc = false
            stockProductRepository.getStockProductsOrderedByNameAsc()
        } else {
            isNameAsc = true
            stockProductRepository.getStockProductsOrderedByNameDesc()
        }
    }

    fun getStockProductsOrderedByAddedDate(): LiveData<List<StockProductModel>> {
        return if (isAddedDateAsc) {
            isAddedDateAsc = false
            stockProductRepository.getStockProductsOrderedByAddedDateAsc()
        } else {
            isAddedDateAsc = true
            stockProductRepository.getStockProductsOrderedByAddedDateDesc()
        }
    }

    fun getStockProductsOrderedByExpiryDate(): LiveData<List<StockProductModel>> {
        return if (isExpiryDateAsc) {
            isExpiryDateAsc = false
            stockProductRepository.getStockProductsOrderedByExpiryDateAsc()
        } else {
            isExpiryDateAsc = true
            stockProductRepository.getStockProductsOrderedByExpiryDateDesc()
        }
    }


}