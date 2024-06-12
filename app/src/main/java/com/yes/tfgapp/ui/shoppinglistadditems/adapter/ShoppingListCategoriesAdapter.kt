package com.yes.tfgapp.ui.shoppinglistadditems.adapter

import android.animation.Animator
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateDecelerateInterpolator
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.yes.tfgapp.databinding.CategoryListRowBinding
import com.yes.tfgapp.domain.model.CategoryModel
import com.yes.tfgapp.R

class ShoppingListCategoriesAdapter(
    private val onItemSelected: (CategoryModel) -> Unit,
    private val onConfigureSelected: (CategoryModel) -> Unit
) : RecyclerView.Adapter<ShoppingListCategoriesAdapter.ShoppingListCategoriesViewHolder>() {

    private var categoriesList = emptyList<CategoryModel>()

    inner class ShoppingListCategoriesViewHolder(private val binding: CategoryListRowBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(
            currentItem: CategoryModel,
            onItemSelected: (CategoryModel) -> Unit,
            onConfigureSelected: (CategoryModel) -> Unit
        ) {
            binding.cvCategory.isSelected = currentItem.isSelected
            binding.tvCategoryName.text = currentItem.name
            val color = if (currentItem.isSelected) {
                R.color.white
            } else {
                R.color.black
            }
            binding.ivCategoryIcon.setImageResource(currentItem.icon)
            binding.tvCategoryName.setTextColor(
                ContextCompat.getColor(
                    binding.tvCategoryName.context,
                    color
                )
            )
            binding.root.setOnClickListener {
                onItemSelected(currentItem)
            }

            fun dpToPx(dp: Int, context: Context): Int {
                val scale = context.resources.displayMetrics.density
                return (dp * scale + 0.5f).toInt()
            }

            if (currentItem.isDefault) {
                val widthInPx = dpToPx(100, binding.tvCategoryName.context) // 100dp to pixels
                val layoutParams = binding.tvCategoryName.layoutParams
                layoutParams.width = widthInPx
                binding.tvCategoryName.layoutParams = layoutParams
                binding.ibSettingsCategory.visibility = View.INVISIBLE
            } else {
                val widthInPx = dpToPx(85, binding.tvCategoryName.context) // 70dp to pixels
                val layoutParams = binding.tvCategoryName.layoutParams
                layoutParams.width = widthInPx
                binding.tvCategoryName.layoutParams = layoutParams
                binding.ibSettingsCategory.visibility = View.VISIBLE
            }

            binding.ibSettingsCategory.setOnClickListener {
                animateButtonClick(binding.ibSettingsCategory) {
                    onConfigureSelected(currentItem)
                }

                //onConfigureSelected(currentItem)
            }
        }

        private fun animateButtonClick(
            view: View,
            action: () -> Unit
        ) {
            // Crear animación de escalado
            val scaleXUp = ObjectAnimator.ofFloat(view, "scaleX", 1f, 1.2f)
            val scaleYUp = ObjectAnimator.ofFloat(view, "scaleY", 1f, 1.2f)
            val scaleXDown = ObjectAnimator.ofFloat(view, "scaleX", 1.2f, 1f)
            val scaleYDown = ObjectAnimator.ofFloat(view, "scaleY", 1.2f, 1f)

            // Configurar la duración de las animaciones
            scaleXUp.duration = 100
            scaleYUp.duration = 100
            scaleXDown.duration = 100
            scaleYDown.duration = 100

            // Crear un AnimatorSet para secuenciar las animaciones
            val animatorSet = AnimatorSet()
            animatorSet.play(scaleXUp).with(scaleYUp).before(scaleXDown).before(scaleYDown)
            animatorSet.interpolator = AccelerateDecelerateInterpolator()

            // Iniciar la animación y realizar la acción de borrado al finalizar
            animatorSet.addListener(object : Animator.AnimatorListener {
                override fun onAnimationStart(animation: Animator) {}
                override fun onAnimationEnd(animation: Animator) {
                    // Acción a realizar después de la animación
                    action()
                }

                override fun onAnimationCancel(animation: Animator) {}
                override fun onAnimationRepeat(animation: Animator) {}
            })
            animatorSet.start()
        }

    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ShoppingListCategoriesViewHolder {
        val binding =
            CategoryListRowBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ShoppingListCategoriesViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return categoriesList.size
    }

    override fun onBindViewHolder(holder: ShoppingListCategoriesViewHolder, position: Int) {
        val currentItem = categoriesList[position]
        holder.bind(currentItem, onItemSelected, onConfigureSelected)
    }

    fun setCategoriesList(categories: List<CategoryModel>) {
        this.categoriesList = categories
        notifyDataSetChanged()
    }
}