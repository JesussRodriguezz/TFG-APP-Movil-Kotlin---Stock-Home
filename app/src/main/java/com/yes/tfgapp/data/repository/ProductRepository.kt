package com.yes.tfgapp.data.repository

import androidx.lifecycle.LiveData
import com.yes.tfgapp.data.dao.ProductDao
import com.yes.tfgapp.domain.fixed.FixedProducts
import com.yes.tfgapp.domain.model.ProductModel

class ProductRepository(private val productDao: ProductDao) {
    val readAllData: LiveData<List<ProductModel>> = productDao.readAllData()

    suspend fun addFixedProducts(){
        val fixedProducts= FixedProducts.entries
        for(product in fixedProducts){
            val productExists= productDao.getProductById(product.id)
            val prod = ProductModel(product.id, product.productName, product.categoryId)
            if(productExists==null){
                productDao.addProduct(prod)
            }
        }
    }

    fun insertProduct(product: ProductModel): Long {
        return productDao.insertProduct(product)
    }

    suspend fun updateProduct(product: ProductModel){
        productDao.updateProduct(product)
    }

    fun getProductsById(productIds: List<Int>): LiveData<List<ProductModel>> {
        return productDao.getProductsById(productIds)

    }

    fun findProductByName(name: String): ProductModel {
        return productDao.findProductByName(name)

    }

    fun updateItemsCategory(id: Int, id1: Int) {
        productDao.updateItemsCategory(id, id1)

    }


}