package com.yes.tfgapp.ui.shoppinglistdetail.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.yes.tfgapp.databinding.CategoryListRowBinding
import com.yes.tfgapp.domain.model.CategoryModel
import com.yes.tfgapp.R

class ShoppingListCategoriesAdapter(private val onItemSelected:(Int)-> Unit ): RecyclerView.Adapter<ShoppingListCategoriesAdapter.ShoppingListCategoriesViewHolder>() {

    var categoriesList = emptyList<CategoryModel>()

    inner class ShoppingListCategoriesViewHolder(private val binding:CategoryListRowBinding) : RecyclerView.ViewHolder(binding.root)  {
        fun bind(currentItem: CategoryModel, onItemSelected: (Int) -> Unit){


            val color = if(currentItem.isSelected){
                R.color.accentRed
            }else{
                R.color.primaryGrey
            }

            binding.cvCategory.setBackgroundColor(ContextCompat.getColor(binding.cvCategory.context, color))

            //binding.cvCategory.isSelected = currentItem.isSelected
            binding.tvCategoryName.text = currentItem.name
            binding.root.setOnClickListener {
                onItemSelected(layoutPosition)
            }

        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ShoppingListCategoriesViewHolder {
        val binding = CategoryListRowBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ShoppingListCategoriesViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return categoriesList.size
    }

    override fun onBindViewHolder(holder: ShoppingListCategoriesViewHolder, position: Int) {
        val currentItem = categoriesList[position]
        holder.bind(currentItem, onItemSelected)
    }

    fun setData(categories: List<CategoryModel>){
        this.categoriesList = categories
        notifyDataSetChanged()
    }
}