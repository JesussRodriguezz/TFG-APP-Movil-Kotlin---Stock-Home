package com.yes.tfgapp.ui.searchproducts.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageButton
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import com.yes.tfgapp.R
import com.yes.tfgapp.data.network.response.ProductItemResponse
import com.yes.tfgapp.databinding.ProductApiSearchRowBinding
import com.yes.tfgapp.domain.model.ProductModel
import com.yes.tfgapp.domain.model.ProductShoppingListModel

class ProductSearchApiAdapter(private val onAddProductToList: (ProductModel, Int) -> Unit) :
    RecyclerView.Adapter<ProductSearchApiAdapter.ProductSearchViewHolder>() {

    private var productApiSearchList = emptyList<ProductItemResponse>()
    private var productsInShoppingList = emptyList<ProductShoppingListModel>()
    private var productsAll = emptyList<ProductModel>()

    inner class ProductSearchViewHolder(private val binding: ProductApiSearchRowBinding) :
        RecyclerView.ViewHolder(binding.root) {
        private var isProductAdded = false

        fun bind(
            currentItem: ProductItemResponse,
            onAddProductToList: (ProductModel, Int) -> Unit
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

            binding.tvProductApiSearchName.text = currentItem.productName
            Picasso.get().load(currentItem.productImage).into(binding.ivProductApiSearchImage)

            val matchedProduct = productsAll.find { it.name == currentItem.productName }
            if (matchedProduct != null) {
                isProductAdded = productsInShoppingList.any { it.productId == matchedProduct.id }
            } else {
                isProductAdded = false
            }

            if (isProductAdded) {
                binding.ibBoughtProduct.setImageResource(R.drawable.ic_check)
            } else {
                binding.ibBoughtProduct.setImageResource(R.drawable.ic_add)
            }

            binding.ibBoughtProduct.setOnClickListener {
                animateIconChange(binding.ibBoughtProduct, R.drawable.ic_check) {
                    onAddProductToList(ProductModel(name = currentItem.productName), adapterPosition)
                }
            }

            /*binding.ibAddProductToList.setOnClickListener {

                if (isProductAdded) {
                    animateIconChange(binding.ibAddProductToList, R.drawable.ic_add) {
                        onDeleteProductFromList(currentItem)
                    }
                } else {
                    animateIconChange(binding.ibAddProductToList, R.drawable.ic_check) {
                        onAddProductToList(currentItem, adapterPosition)
                    }
                }

            }*/


        }

        private fun animateIconChange(
            imageButton: ImageButton,
            newIconResId: Int,
            onAnimationEnd: () -> Unit
        ) {
            imageButton.setImageResource(newIconResId)
            imageButton.animate()
                .scaleX(1.4f)
                .scaleY(1.4f)
                .setDuration(25)
                .withEndAction {
                    imageButton.animate()
                        .scaleX(1f)
                        .scaleY(1f)
                        .setDuration(25)
                        .withEndAction {
                            onAnimationEnd()
                        }.start()
                }.start()
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductSearchViewHolder {
        val binding =
            ProductApiSearchRowBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ProductSearchViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return productApiSearchList.size
    }

    override fun onBindViewHolder(holder: ProductSearchViewHolder, position: Int) {
        val currentItem = productApiSearchList[position]
        holder.bind(currentItem, onAddProductToList)
    }

    fun setData(productItemResponse: List<ProductItemResponse>) {
        this.productApiSearchList = productItemResponse
        notifyDataSetChanged()
    }

    fun setProductsInShoppingList(productsInShoppingList: List<ProductShoppingListModel>) {
        this.productsInShoppingList = productsInShoppingList
        notifyDataSetChanged()
    }

    fun setProductList(products: List<ProductModel>) {
        this.productsAll = products
        notifyDataSetChanged()
    }
}


