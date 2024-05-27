package com.yes.tfgapp.ui.mystock

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.yes.tfgapp.data.AppDataBase
import com.yes.tfgapp.data.repository.StockProductRepository
import com.yes.tfgapp.domain.model.ProductModel
import com.yes.tfgapp.domain.model.StockProductModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MyStockViewModel(application: Application) : AndroidViewModel(application) {

    private val stockProductRepository: StockProductRepository
    val readAllData : LiveData<List<StockProductModel>>
    val productIdLiveData = MutableLiveData<String>()

    private var isNameAsc = true
    private var isAddedDateAsc = true
    private var isExpiryDateAsc = true

    init {
        val stockProductDao = AppDataBase.getDatabase(application).stockProductDao()
        stockProductRepository = StockProductRepository(stockProductDao)
        readAllData = stockProductRepository.readAllData
    }

    fun addStockProduct(stockProduct: StockProductModel) {
        viewModelScope.launch(Dispatchers.IO) {
            stockProductRepository.addStockProduct(stockProduct)
        }
    }

    fun addProduct(stockProduct: StockProductModel) {
        viewModelScope.launch(Dispatchers.IO) {
            val existingProduct = stockProductRepository.findStockProductByName(stockProduct.name)
            if (existingProduct == null) {
                val productId = stockProductRepository.addStockProduct(stockProduct)
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