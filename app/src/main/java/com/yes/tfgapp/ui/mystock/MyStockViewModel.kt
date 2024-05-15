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
}