package com.yes.tfgapp.ui.shoppinglistdetail.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.yes.tfgapp.databinding.CategoryListRowBinding
import com.yes.tfgapp.databinding.ProductListRowBinding
import com.yes.tfgapp.domain.model.ProductModel

class ShoppingListProductsAdapter(): RecyclerView.Adapter<ShoppingListProductsAdapter.ShoppingListProductsViewHolder>(){

    var productsList = emptyList<ProductModel>()

    inner class ShoppingListProductsViewHolder(private val binding: ProductListRowBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(currentItem: ProductModel) {
            binding.tvProductName.text = currentItem.name + " - " + currentItem.category
        }

    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ShoppingListProductsViewHolder {
        val binding = ProductListRowBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ShoppingListProductsViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return productsList.size
    }

    override fun onBindViewHolder(holder: ShoppingListProductsViewHolder, position: Int) {
        val currentItem = productsList[position]
        holder.bind(currentItem)
    }

    fun setData(products: List<ProductModel>){
        this.productsList = products
        notifyDataSetChanged()
    }
}