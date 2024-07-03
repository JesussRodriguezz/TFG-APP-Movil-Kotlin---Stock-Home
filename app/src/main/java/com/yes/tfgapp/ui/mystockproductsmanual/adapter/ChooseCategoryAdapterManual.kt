package com.yes.tfgapp.ui.mystockproductsmanual.adapter



import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.yes.tfgapp.databinding.ChooseCategoryRowBinding
import com.yes.tfgapp.domain.model.CategoryModel

class ChooseCategoryAdapterManual(
    private val onChangeCategory: () -> Unit
): RecyclerView.Adapter<ChooseCategoryAdapterManual.ChooseCategoryViewHolder>(){

    private var categoriesList = emptyList<CategoryModel>()
    var publicCategoriesList = emptyList<CategoryModel>()
    var selectedItemPosition = 0



    inner class ChooseCategoryViewHolder(private val binding: ChooseCategoryRowBinding) : RecyclerView.ViewHolder(binding.root) {


        init {
            binding.root.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    val item = categoriesList[position]
                    val previousItem = categoriesList[selectedItemPosition]
                    previousItem.isSelected = false
                    item.isSelected = true  // Marcar el actual
                    selectedItemPosition = position  // Actualizar la posición seleccionada
                    onChangeCategory()
                    notifyDataSetChanged()  // Notificar para rebind todos los ViewHolder
                }
            }
        }
        fun bind(currentItem: CategoryModel) {
            binding.tvCategoryName.text = currentItem.name
            binding.ivCategoryIcon.setImageResource(currentItem.icon)
            binding.cvCategory.isSelected = currentItem.isSelected
        }


    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ChooseCategoryAdapterManual.ChooseCategoryViewHolder {
        val binding = ChooseCategoryRowBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ChooseCategoryViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return categoriesList.size
    }

    override fun onBindViewHolder(holder: ChooseCategoryAdapterManual.ChooseCategoryViewHolder, position: Int) {
        val currentItem = categoriesList[position]
        holder.bind(currentItem)
    }



    fun setCategoriesListModified(categories: List<CategoryModel>, myCategoryId: Int) {

        val list = categories.toMutableList()
        val myCategoryIndex = list.indexOfFirst { it.id == myCategoryId }
        if (myCategoryIndex != -1) {
            val myCategory = list.removeAt(myCategoryIndex)
            list.add(0, myCategory)
        }

        this.categoriesList = list
        this.publicCategoriesList = list

        // Actualizar la selección del primer elemento
        selectedItemPosition = 0
        for (i in categoriesList.indices) {
            categoriesList[i].isSelected = i == selectedItemPosition
        }

        notifyDataSetChanged()
    }


}