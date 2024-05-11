package com.yes.tfgapp.ui.searchproducts.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.yes.tfgapp.R
import com.yes.tfgapp.databinding.ProductSearchRowBinding
import com.yes.tfgapp.domain.model.CategoryModel
import com.yes.tfgapp.domain.model.ProductModel
import com.yes.tfgapp.domain.model.ProductShoppingListModel

class ProductSearchAdapter(
    private val onAddProductToList: (ProductModel, Int) -> Unit,
    private val getCategoryById: (Int, (CategoryModel?) -> Unit) -> Unit,
    private val changeCategory: (ProductModel, Int) -> Unit,
    private val onDeleteProductFromList: (ProductModel) -> Unit
) : RecyclerView.Adapter<ProductSearchAdapter.ProductSearchViewHolder>() {

    private var productsList = emptyList<ProductModel>()
    private var productsInShoppingList = emptyList<ProductShoppingListModel>()

    inner class ProductSearchViewHolder(private val binding: ProductSearchRowBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(
            currentItem: ProductModel,
            onAddProductToList: (ProductModel, Int) -> Unit,
            getCategoryById: (Int, (CategoryModel?) -> Unit) -> Unit,
            changeCategory: (ProductModel, Int) -> Unit,
            onDeleteProductFromList: (ProductModel) -> Unit
        ) {

            val backgroundColor = if (adapterPosition % 2 == 0) {
                R.color.primaryGrey
            } else {
                R.color.primaryWhite
            }

            binding.root.setBackgroundColor(
                ContextCompat.getColor(
                    binding.root.context,
                    backgroundColor
                )
            )
            binding.tvProductName.text = currentItem.name

            if(productsInShoppingList.any { it.productId == currentItem.id }){
                binding.ibAddProductToList.setImageResource(R.drawable.ic_check)
            }else{
                binding.ibAddProductToList.setImageResource(R.drawable.ic_add)
            }

            getCategoryById(currentItem.categoryId) { category ->
                binding.ivProductIcon.setImageResource(category!!.icon)
            }

            binding.ivProductIcon.setOnClickListener {
                changeCategory(currentItem, adapterPosition)
            }

            binding.ibAddProductToList.setOnClickListener {
                if (binding.ibAddProductToList.drawable.constantState == ContextCompat.getDrawable(binding.root.context,R.drawable.ic_check)!!.constantState) {
                    onDeleteProductFromList(currentItem)
                } else {
                    onAddProductToList(currentItem, adapterPosition)
                }

            }
        }

    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ProductSearchViewHolder {
        val binding =
            ProductSearchRowBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ProductSearchViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return productsList.size
    }

    override fun onBindViewHolder(holder: ProductSearchViewHolder, position: Int) {
        val currentItem = productsList[position]
        holder.bind(currentItem, onAddProductToList, getCategoryById, changeCategory,onDeleteProductFromList)
    }

    fun setProductList(products: List<ProductModel>) {
        this.productsList = products
    }

    fun setProductsInShoppingList(productsInShoppingList: List<ProductShoppingListModel>){
        this.productsInShoppingList = productsInShoppingList
        notifyDataSetChanged()
    }

}