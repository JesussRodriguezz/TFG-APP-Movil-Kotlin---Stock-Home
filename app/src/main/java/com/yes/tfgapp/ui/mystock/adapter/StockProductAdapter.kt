package com.yes.tfgapp.ui.mystock.adapter

import android.animation.Animator
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateDecelerateInterpolator
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import com.yes.tfgapp.R
import com.yes.tfgapp.databinding.ProductMyStockRowBinding
import com.yes.tfgapp.domain.model.StockProductModel

class StockProductAdapter(
    private val onClickDelete: (StockProductModel) -> Unit
) : RecyclerView.Adapter<StockProductAdapter.StockProductViewHolder>() {

    private var stockProductList = emptyList<StockProductModel>()

    inner class StockProductViewHolder(private val binding: ProductMyStockRowBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(
            currentItem: StockProductModel,
            onClickDelete: (StockProductModel) -> Unit
        ) {
            binding.tvStockProductName.text = currentItem.name
            binding.tvStockProductExpireDate.text = currentItem.expirationDate
            binding.tvStockProductDaysToExpire.text = currentItem.daysToExpire.toString()
            println("CURRENT DATE: "+currentItem.addedDate)

            if (currentItem.image != null) {
                Picasso.get().load(currentItem.image).into(binding.ivStockProduct)
            } else if (currentItem.icon != null) {
                binding.ivStockProduct.setImageResource(currentItem.icon)
            } else {
                binding.ivStockProduct.setImageResource(R.drawable.ic_others_category) // Imagen por defecto si no hay imagen ni icono
            }

            binding.ibDeleteStockProduct.setOnClickListener {
                animateButtonClick(binding.ibDeleteStockProduct) {
                    onClickDelete(currentItem)
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

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StockProductViewHolder {
        val binding =
            ProductMyStockRowBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return StockProductViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return stockProductList.size
    }

    override fun onBindViewHolder(holder: StockProductViewHolder, position: Int) {
        val currentItem = stockProductList[position]
        holder.bind(currentItem, onClickDelete)
    }

    fun setData(stockProduct: List<StockProductModel>) {
        this.stockProductList = stockProduct
        notifyDataSetChanged()
    }
}