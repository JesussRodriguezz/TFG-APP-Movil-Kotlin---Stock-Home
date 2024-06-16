package com.yes.tfgapp.ui.shoppinglist.adapter

import android.animation.Animator
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateDecelerateInterpolator
import androidx.recyclerview.widget.RecyclerView
import com.yes.tfgapp.databinding.ShoppingListRowBinding
import com.yes.tfgapp.domain.model.ShoppingListModel
import androidx.navigation.findNavController
import com.yes.tfgapp.R
import com.yes.tfgapp.ui.shoppinglist.ShoppingListFragmentDirections
import kotlin.math.roundToInt

class ShoppingListAdapter(
    private val onClickOpenConfiguration: (ShoppingListModel) -> Unit
) : RecyclerView.Adapter<ShoppingListAdapter.ShoppingListViewHolder>() {

    private var shoppingListList = emptyList<ShoppingListModel>()

    inner class ShoppingListViewHolder(private val binding: ShoppingListRowBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(
            currentItem: ShoppingListModel,
            onClickOpenConfiguration: (ShoppingListModel) -> Unit
        ) {
            binding.tvShoppingListName.text = currentItem.name
            binding.tvShoppingListNumBoughtItems.text = currentItem.quantityBought.toString()
            binding.tvShoppingListNumItems.text = currentItem.quantity.toString()


            val percentage = if (currentItem.quantity != 0) {
                (currentItem.quantityBought.toDouble() / currentItem.quantity.toDouble() * 100).roundToInt()
            } else {
                0
            }
            val progressBarColor = when {
                percentage == 100 -> R.color.progress8
                percentage >= 80 -> R.color.progress7
                percentage >= 65 -> R.color.progress6
                percentage >= 50 -> R.color.progress5
                percentage >= 35 -> R.color.progress4
                percentage >= 20 -> R.color.progress5
                percentage >= 5 -> R.color.progress2
                else -> R.color.progress1
            }

            binding.progressBar.setDividerColorResource(progressBarColor)
            binding.ibSettings.setOnClickListener {
                val rotation = ObjectAnimator.ofFloat(binding.ibSettings, "rotation", 0f, 180f)
                rotation.duration = 400 // Duración de la animación en milisegundos
                rotation.interpolator =
                    AccelerateDecelerateInterpolator() // Interpolador para una animación suave
                rotation.start()
                onClickOpenConfiguration(currentItem)
            }

            binding.root.setOnClickListener {
                val actionToAddItems =
                    ShoppingListFragmentDirections.actionShoppingListFragmentToShoppingListAddItemsFragment(
                        currentItem
                    )
                val actionToDetail =
                    ShoppingListFragmentDirections.actionShoppingListFragmentToShoppingListDetailFragment(
                        currentItem
                    )

                animateButtonClick(binding.root) {
                    if (currentItem.quantity > 0) {
                        binding.root.findNavController().navigate(actionToDetail)
                    } else {
                        binding.root.findNavController().navigate(actionToAddItems)
                    }
                }

            }
        }

        private fun animateButtonClick(
            view: View,
            action: () -> Unit
        ) {
            val scaleXUp = ObjectAnimator.ofFloat(view, "scaleX", 1f, 1.1f)
            val scaleYUp = ObjectAnimator.ofFloat(view, "scaleY", 1f, 1.1f)
            val scaleXDown = ObjectAnimator.ofFloat(view, "scaleX", 1.1f, 1f)
            val scaleYDown = ObjectAnimator.ofFloat(view, "scaleY", 1.1f, 1f)

            scaleXUp.duration = 100
            scaleYUp.duration = 100
            scaleXDown.duration = 100
            scaleYDown.duration = 100

            val animatorSet = AnimatorSet()
            animatorSet.play(scaleXUp).with(scaleYUp).before(scaleXDown).before(scaleYDown)
            animatorSet.interpolator = AccelerateDecelerateInterpolator()

            animatorSet.addListener(object : Animator.AnimatorListener {
                override fun onAnimationStart(animation: Animator) {}
                override fun onAnimationEnd(animation: Animator) {
                    action()
                }

                override fun onAnimationCancel(animation: Animator) {}
                override fun onAnimationRepeat(animation: Animator) {}
            })
            animatorSet.start()
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ShoppingListViewHolder {
        val binding =
            ShoppingListRowBinding.inflate(LayoutInflater.from(parent.context), parent, false)

        return ShoppingListViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return shoppingListList.size
    }

    override fun onBindViewHolder(holder: ShoppingListViewHolder, position: Int) {
        val currentItem = shoppingListList[position]
        holder.bind(currentItem, onClickOpenConfiguration)
    }

    fun setData(shoppingList: List<ShoppingListModel>) {
        this.shoppingListList = shoppingList
        notifyDataSetChanged()
    }
}