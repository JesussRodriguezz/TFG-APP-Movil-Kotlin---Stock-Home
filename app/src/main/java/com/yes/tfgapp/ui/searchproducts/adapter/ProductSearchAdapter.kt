package com.yes.tfgapp.ui.searchproducts.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageButton
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

            private var isProductAdded = false

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

            isProductAdded = if (productsInShoppingList.any { it.productId == currentItem.id }) {
                binding.ibAddProductToList.setImageResource(R.drawable.ic_check)
                true
            } else {
                binding.ibAddProductToList.setImageResource(R.drawable.ic_add)
                false
            }

            getCategoryById(currentItem.categoryId) { category ->
                binding.ivProductIcon.setImageResource(category!!.icon)
            }

            binding.ivProductIcon.setOnClickListener {
                changeCategory(currentItem, adapterPosition)
            }

            binding.ibAddProductToList.setOnClickListener {

                if (isProductAdded) {
                    animateIconChange(binding.ibAddProductToList, R.drawable.ic_add) {
                        onDeleteProductFromList(currentItem)
                    }
                } else {
                    animateIconChange(binding.ibAddProductToList, R.drawable.ic_check) {
                        onAddProductToList(currentItem, adapterPosition)
                    }
                }

            }
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
        holder.bind(
            currentItem,
            onAddProductToList,
            getCategoryById,
            changeCategory,
            onDeleteProductFromList
        )
    }

    fun setProductList(products: List<ProductModel>) {
        this.productsList = products
    }

    fun setProductsInShoppingList(productsInShoppingList: List<ProductShoppingListModel>) {
        this.productsInShoppingList = productsInShoppingList
        notifyDataSetChanged()
    }

}