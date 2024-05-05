package com.yes.tfgapp.data.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.yes.tfgapp.domain.model.CategoryModel

@Dao
interface CategoryDao {
    @Insert
    suspend fun addCategory(category: CategoryModel)

    @Update
    suspend fun updateCategory(category: CategoryModel)

    @Delete
    suspend fun deleteCategory(category: CategoryModel)

    @Query("SELECT * FROM category WHERE id = :id")
    suspend fun getCategoryById(id: Int): CategoryModel

    @Query("SELECT * FROM category ORDER BY id ASC")
    fun readAllData(): LiveData<List<CategoryModel>>

    @Update
    suspend fun updateCategories(updatedCategories: List<CategoryModel>)

    @Query("UPDATE category SET isSelected = :selected")
    fun updateAllCategories(selected: Boolean)


}