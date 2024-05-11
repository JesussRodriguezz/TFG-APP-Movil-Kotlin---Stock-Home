package com.yes.tfgapp.ui.searchproducts.adapter

import android.content.Context
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.yes.tfgapp.R
import com.yes.tfgapp.databinding.ChooseCategoryRowBinding
import com.yes.tfgapp.domain.model.CategoryModel

class ChooseCategoryAdapter: RecyclerView.Adapter<ChooseCategoryAdapter.ChooseCategoryViewHolder>(){

    private var categoriesList = emptyList<CategoryModel>()
    var publicCategoriesList = emptyList<CategoryModel>()
    var selectedItemPosition = 0
    private var firstTimeClick = true


    inner class ChooseCategoryViewHolder(private val binding: ChooseCategoryRowBinding) : RecyclerView.ViewHolder(binding.root) {


        init {
            if(firstTimeClick){
                selectedItemPosition = 0
                val firstItem = categoriesList[selectedItemPosition]
                firstItem.isSelected = true
                for (i in 1 until categoriesList.size){
                    categoriesList[i].isSelected = false
                }
                firstTimeClick = false
            }
            binding.root.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    val item = categoriesList[position]
                    val previousItem = categoriesList[selectedItemPosition]
                    previousItem.isSelected = false  // Desmarcar el anterior
                    item.isSelected = true  // Marcar el actual
                    selectedItemPosition = position  // Actualizar la posición seleccionada
                    notifyDataSetChanged()  // Notificar para rebind todos los ViewHolder

                }
            }
        }
        fun bind(currentItem: CategoryModel){
            binding.tvCategoryName.text = currentItem.name
            binding.ivCategoryIcon.setImageResource(currentItem.icon)
            binding.cvCategory.isSelected = currentItem.isSelected
            if (adapterPosition == 0) {
                setCategoryToCenterPosition()
            } else {
                setCategoryToDefaultPosition()
            }
        }

        private fun setCategoryToCenterPosition() {
            val dialogWidthPx = dpToPx(400, binding.root.context)  // Ancho del diálogo en píxeles
            val cardWidthPx = binding.root.context.resources.getDimensionPixelSize(R.dimen.card_width)
            val totalPadding = dialogWidthPx - cardWidthPx
            val sidePadding = (totalPadding / 2) - dpToPx(12, binding.root.context)  // Restar el margen izquierdo del diálogo

            val layoutParams = binding.cvCategory.layoutParams as ViewGroup.MarginLayoutParams
            layoutParams.setMargins(sidePadding, 0, sidePadding, 0)
            binding.cvCategory.layoutParams = layoutParams
        }

        private fun setCategoryToDefaultPosition() {
            val layoutParams = binding.cvCategory.layoutParams as ViewGroup.MarginLayoutParams
            layoutParams.setMargins(0, 32, 0, 0)
            binding.cvCategory.layoutParams = layoutParams
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

    fun setFirstTimeClick(){
        firstTimeClick = true
    }

    fun setCategoriesListModified(categories: List<CategoryModel>,myCategoryId: Int ){
        val list = categories.toMutableList()
        val myCategoryIndex = list.indexOfFirst { it.id == myCategoryId }
        if (myCategoryIndex != -1) {
            val myCategory = list.removeAt(myCategoryIndex)
            list.add(0, myCategory)
        }
        this.categoriesList = list
        this.publicCategoriesList = list
        notifyDataSetChanged()

    }

}