package com.yes.tfgapp.ui.shoppinglistadditems.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.yes.tfgapp.databinding.CategoryListRowBinding
import com.yes.tfgapp.domain.model.CategoryModel
import com.yes.tfgapp.R

class ShoppingListCategoriesAdapter(private val onItemSelected:(CategoryModel)-> Unit, private val onConfigureSelected:(CategoryModel)->Unit ): RecyclerView.Adapter<ShoppingListCategoriesAdapter.ShoppingListCategoriesViewHolder>() {

    private var categoriesList = emptyList<CategoryModel>()

    inner class ShoppingListCategoriesViewHolder(private val binding:CategoryListRowBinding) : RecyclerView.ViewHolder(binding.root)  {
        fun bind(currentItem: CategoryModel, onItemSelected: (CategoryModel) -> Unit, onConfigureSelected: (CategoryModel) -> Unit){
            binding.cvCategory.isSelected = currentItem.isSelected
            binding.tvCategoryName.text = currentItem.name
            val color = if(currentItem.isSelected){
                R.color.white
            }else{
                R.color.black
            }
            binding.ivCategoryIcon.setImageResource(currentItem.icon)
            binding.tvCategoryName.setTextColor(ContextCompat.getColor(binding.tvCategoryName.context, color))
            binding.root.setOnClickListener {
                    onItemSelected(currentItem)
            }

            binding.ibSettingsCategory.setOnClickListener {
                onConfigureSelected(currentItem)
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
        holder.bind(currentItem, onItemSelected, onConfigureSelected)
    }

    fun setCategoriesList(categories: List<CategoryModel>){
        this.categoriesList = categories
        notifyDataSetChanged()
    }
}