package com.yes.tfgapp.ui.mystock.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import com.yes.tfgapp.databinding.ProductMyStockRowBinding
import com.yes.tfgapp.databinding.ShoppingListRowBinding
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
            Picasso.get().load(currentItem.image).into(binding.ivStockProduct)
            binding.ibDeleteStockProduct.setOnClickListener {
                onClickDelete(currentItem)
            }
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