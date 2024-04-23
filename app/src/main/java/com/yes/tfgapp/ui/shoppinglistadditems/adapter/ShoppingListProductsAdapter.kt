package com.yes.tfgapp.ui.shoppinglistadditems.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.yes.tfgapp.R
import com.yes.tfgapp.databinding.ProductListRowBinding
import com.yes.tfgapp.domain.model.ProductModel

class ShoppingListProductsAdapter(private val onAddProductToList: (ProductModel) -> Unit): RecyclerView.Adapter<ShoppingListProductsAdapter.ShoppingListProductsViewHolder>(){

    private var productsList = emptyList<ProductModel>()

    inner class ShoppingListProductsViewHolder(private val binding: ProductListRowBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(currentItem: ProductModel, onAddProductToList: (ProductModel) -> Unit/*, currentShoppingList: ShoppingListModel*/){

            val backgroundColor = if(adapterPosition % 2 == 0){
                R.color.primaryGrey
            }else{
                R.color.primaryWhite
            }

            binding.root.setBackgroundColor(ContextCompat.getColor(binding.root.context, backgroundColor))
            binding.tvProductName.text = currentItem.name

            binding.ibAddProductToList.setOnClickListener{
                //val productShoppingList= ProductShoppingListModel(
                //    shoppingListId = 0,
                //    productId = currentItem.id
                //)
                //onAddProductToList(productShoppingList)
                onAddProductToList(currentItem)
            }
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
        holder.bind(currentItem, onAddProductToList/*, currentShoppingList*/)
    }

    fun setProductList(products: List<ProductModel>){
        this.productsList = products
    }

}