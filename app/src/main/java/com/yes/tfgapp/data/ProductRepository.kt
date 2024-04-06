package com.yes.tfgapp.data

import androidx.lifecycle.LiveData
import com.yes.tfgapp.domain.model.CategoryModel
import com.yes.tfgapp.domain.model.FixedProducts
import com.yes.tfgapp.domain.model.ProductModel

class ProductRepository(private val productDao: ProductDao) {
    val readAllData: LiveData<List<ProductModel>> = productDao.readAllData()

    suspend fun addFixedProducts(){
        val fixedProducts= FixedProducts.entries
        for(product in fixedProducts){
            val productExists= productDao.getProductById(product.id)
            val prod = ProductModel(product.id, product.product_name, product.category_id)
            if(productExists==null){
                productDao.addProduct(prod)
            }
        }
    }
    suspend fun addProduct(product: ProductModel){
        productDao.addProduct(product)
    }

    suspend fun updateProduct(product: ProductModel){
        productDao.updateProduct(product)
    }

    suspend fun deleteProduct(product: ProductModel){
        productDao.deleteProduct(product)
    }

    /*fun productsByCategory(category: CategoryModel): LiveData<List<ProductModel>> {

        val example = productDao.productsByCategoryNoLive(category.name)
        println("AAA $example")

        return productDao.productsByCategory(category.name)
    }*/



}