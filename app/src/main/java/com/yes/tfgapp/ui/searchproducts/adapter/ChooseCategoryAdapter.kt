package com.yes.tfgapp.ui.searchproducts.adapter

import android.content.Context
import android.util.TypedValue
import android.view.Gravity
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.yes.tfgapp.R
import com.yes.tfgapp.databinding.CategoryListRowBinding
import com.yes.tfgapp.databinding.ChooseCategoryRowBinding
import com.yes.tfgapp.domain.model.CategoryModel
import com.yes.tfgapp.ui.shoppinglistadditems.adapter.ShoppingListCategoriesAdapter

class ChooseCategoryAdapter(): RecyclerView.Adapter<ChooseCategoryAdapter.ChooseCategoryViewHolder>(){

    private var categoriesList = emptyList<CategoryModel>()

    inner class ChooseCategoryViewHolder(private val binding: ChooseCategoryRowBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(currentItem: CategoryModel){
            binding.tvCategoryName.text = currentItem.name
            binding.ivCategoryIcon.setImageResource(currentItem.icon)
            //si la posicion es 0 haz algo
            if (position == 0) {
                val dialogWidthPx = dpToPx(400, binding.root.context)  // Ancho del diálogo en píxeles
                val cardWidthPx = binding.root.context.resources.getDimensionPixelSize(R.dimen.card_width)
                // Calcular el padding para centrar ajustando para el margen izquierdo
                val totalPadding = dialogWidthPx - cardWidthPx
                val sidePadding = (totalPadding / 2) - dpToPx(12, binding.root.context)  // Restar el margen izquierdo del diálogo

                val layoutParams = binding.cvCategory.layoutParams as ViewGroup.MarginLayoutParams
                layoutParams.setMargins(sidePadding, 0, sidePadding, 0)
                binding.cvCategory.layoutParams = layoutParams
            } else {
                // Resetear a los márgenes originales para otros ítems
                val layoutParams = binding.cvCategory.layoutParams as ViewGroup.MarginLayoutParams
                layoutParams.setMargins(0, 32, 0, 0)
                binding.cvCategory.layoutParams = layoutParams
            }
        }
        private fun Int.toDp(context: Context): Int {
            return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, this.toFloat(), context.resources.displayMetrics).toInt()
        }

        private fun dpToPx(dp: Int, context: Context): Int {
            return TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                dp.toFloat(),
                context.resources.displayMetrics
            ).toInt()
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ChooseCategoryAdapter.ChooseCategoryViewHolder {
        val binding = ChooseCategoryRowBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ChooseCategoryViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return categoriesList.size
    }

    override fun onBindViewHolder(holder: ChooseCategoryAdapter.ChooseCategoryViewHolder, position: Int) {
        val currentItem = categoriesList[position]
        holder.bind(currentItem)
    }

    fun setCategoriesList(categories: List<CategoryModel>){
        this.categoriesList = categories
        notifyDataSetChanged()
    }

}