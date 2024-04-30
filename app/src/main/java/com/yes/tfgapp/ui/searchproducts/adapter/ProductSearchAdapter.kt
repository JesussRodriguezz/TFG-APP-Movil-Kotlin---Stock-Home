package com.yes.tfgapp.ui.searchproducts.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.yes.tfgapp.R
import com.yes.tfgapp.databinding.ProductListRowBinding
import com.yes.tfgapp.databinding.ProductSearchRowBinding
import com.yes.tfgapp.domain.model.ProductModel
import com.yes.tfgapp.ui.shoppinglistadditems.adapter.ShoppingListProductsAdapter

class ProductSearchAdapter (private val onAddProductToList: (ProductModel) -> Unit): RecyclerView.Adapter<ProductSearchAdapter.ProductSearchViewHolder>(){

    private var productsList = emptyList<ProductModel>()

    inner class ProductSearchViewHolder(private val binding: ProductSearchRowBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(currentItem: ProductModel, onAddProductToList: (ProductModel) -> Unit){

            val backgroundColor = if(adapterPosition % 2 == 0){
                R.color.primaryGrey
            }else{
                R.color.primaryWhite
            }

            binding.root.setBackgroundColor(ContextCompat.getColor(binding.root.context, backgroundColor))
            binding.tvProductName.text = currentItem.name

            binding.ibAddProductToList.setOnClickListener{
                onAddProductToList(currentItem)
            }
        }

    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ProductSearchViewHolder {
        val binding = ProductSearchRowBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ProductSearchViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return productsList.size
    }

    override fun onBindViewHolder(holder: ProductSearchViewHolder, position: Int) {
        val currentItem = productsList[position]
        holder.bind(currentItem, onAddProductToList/*, currentShoppingList*/)
    }

    fun setProductList(products: List<ProductModel>){
        this.productsList = products
    }

}