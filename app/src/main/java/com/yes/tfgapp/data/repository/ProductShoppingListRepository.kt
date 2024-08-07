package com.yes.tfgapp.data.repository

import androidx.lifecycle.LiveData
import com.yes.tfgapp.data.dao.ProductShoppingListDao
import com.yes.tfgapp.domain.model.ProductShoppingListModel

class ProductShoppingListRepository(private val productShoppingListDAO: ProductShoppingListDao) {


    suspend fun addProductShoppingList(productShoppingList: ProductShoppingListModel) {
        val existingProductShoppingList = productShoppingListDAO.getProductShoppingList(
            productShoppingList.productId,
            productShoppingList.shoppingListId
        )
        if (existingProductShoppingList == null) {
            productShoppingListDAO.addProductShoppingList(productShoppingList)
            productShoppingListDAO.incrementQuantity(productShoppingList.shoppingListId)
        }
    }

    suspend fun updateProductShoppingListBoughtProduct(productShoppingList: ProductShoppingListModel) {
        productShoppingListDAO.updateProductShoppingList(productShoppingList)
        productShoppingListDAO.incrementQuantityBought(productShoppingList.shoppingListId)
    }

    suspend fun updateProductShoppingListNotBoughtProduct(productShoppingList: ProductShoppingListModel) {
        productShoppingListDAO.updateProductShoppingList(productShoppingList)
        productShoppingListDAO.decrementQuantityBought(productShoppingList.shoppingListId)
    }


    fun getProductsForShoppingList(id: Int): LiveData<List<ProductShoppingListModel>> {
        return productShoppingListDAO.getAllMyProductsToBuy(id)
    }

    fun getProductsBoughtForShoppingList(id: Int): LiveData<List<ProductShoppingListModel>> {
        return productShoppingListDAO.getAllMyProductsBought(id)
    }

    fun getAllProductsInShoppingList(id: Int): LiveData<List<ProductShoppingListModel>> {
        return productShoppingListDAO.getAllProductsInShoppingList(id)
    }

    suspend fun deleteProductShoppingList(productShoppingList: ProductShoppingListModel) {
        productShoppingListDAO.deleteProductShoppingList(productShoppingList)
        productShoppingListDAO.decrementQuantity(productShoppingList.shoppingListId)
    }

    suspend fun emptyAllList(id: Int) {
        productShoppingListDAO.emptyAllList(id)
        productShoppingListDAO.decrementAllQuantities(id)
    }


}