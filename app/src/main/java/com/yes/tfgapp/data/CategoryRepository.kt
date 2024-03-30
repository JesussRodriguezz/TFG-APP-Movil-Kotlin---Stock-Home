package com.yes.tfgapp.data

import androidx.lifecycle.LiveData
import com.yes.tfgapp.domain.model.CategoryModel
import com.yes.tfgapp.domain.model.FixedCategories

class CategoryRepository(private val categoryDao: CategoryDao) {
    val readAllData: LiveData<List<CategoryModel>> = categoryDao.readAllData()

    suspend fun addFixedCategories(){
        val fixedCategories= FixedCategories.entries
        for(categ in fixedCategories){
            val categoryExists= categoryDao.getCategoryById(categ.id)
            val category = CategoryModel(categ.id, categ.cateogory_name)
            if(categoryExists==null){
                categoryDao.addCategory(category)
            }
        }
    }
    suspend fun addCategory(category: CategoryModel){
        categoryDao.addCategory(category)
    }

    suspend fun updateCategory(category: CategoryModel){
        categoryDao.updateCategory(category)
    }

    suspend fun deleteCategory(category: CategoryModel){
        categoryDao.deleteCategory(category)
    }

    suspend fun updateCategories(updatedCategories: List<CategoryModel>) {
        categoryDao.updateCategories(updatedCategories)

    }
}