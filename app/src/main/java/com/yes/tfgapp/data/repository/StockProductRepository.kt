package com.yes.tfgapp.data.repository

import androidx.lifecycle.LiveData
import com.yes.tfgapp.data.dao.StockProductDao
import com.yes.tfgapp.domain.model.StockProductModel

class StockProductRepository(private val stockProductDao: StockProductDao) {
    val readAllData: LiveData<List<StockProductModel>> = stockProductDao.readAllData()
    suspend fun addStockProduct(stockProduct: StockProductModel){
        stockProductDao.addStockProduct(stockProduct)
    }

    suspend fun addStockProducts(stockProducts: List<StockProductModel>){
        stockProductDao.addStockProducts(stockProducts)
    }

    suspend fun updateStockProduct(stockProduct: StockProductModel){
        stockProductDao.updateStockProduct(stockProduct)
    }

    suspend fun deleteStockProduct(stockProduct: StockProductModel){
        stockProductDao.deleteStockProduct(stockProduct)
    }

    suspend fun getStockProductById(id: Int): StockProductModel {
        return stockProductDao.getStockProductById(id)
    }

    fun findStockProductByName(name: String): StockProductModel {
        return stockProductDao.findStockProductByName(name)

    }

}