package com.yes.tfgapp.ui.shoppinglistadditems.adapter


import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageButton
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.yes.tfgapp.R
import com.yes.tfgapp.databinding.ProductListRowBinding
import com.yes.tfgapp.domain.model.ProductModel
import com.yes.tfgapp.domain.model.ProductShoppingListModel

class ShoppingListProductsAdapter(
    private val onAddProductToList: (ProductModel) -> Unit,
    private val onDeleteProductFromList: (ProductModel) -> Unit
) : RecyclerView.Adapter<ShoppingListProductsAdapter.ShoppingListProductsViewHolder>() {

    private var productsList = emptyList<ProductModel>()
    private var productsInShoppingList = emptyList<ProductShoppingListModel>()

    inner class ShoppingListProductsViewHolder(private val binding: ProductListRowBinding) :
        RecyclerView.ViewHolder(binding.root) {

        private var isProductAdded = false


        fun bind(
            currentItem: ProductModel,
            onAddProductToList: (ProductModel) -> Unit,
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

            binding.ibAddProductToList.setOnClickListener {
                if (isProductAdded) {
                    animateIconChange(binding.ibAddProductToList, R.drawable.ic_add) {
                        onDeleteProductFromList(currentItem)
                    }
                } else {
                    animateIconChange(binding.ibAddProductToList, R.drawable.ic_check) {
                        onAddProductToList(currentItem)
                    }
                }
                isProductAdded = !isProductAdded
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
    ): ShoppingListProductsViewHolder {
        val binding =
            ProductListRowBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ShoppingListProductsViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return productsList.size
    }

    override fun onBindViewHolder(holder: ShoppingListProductsViewHolder, position: Int) {
        val currentItem = productsList[position]
        holder.bind(currentItem, onAddProductToList, onDeleteProductFromList)
    }

    fun setProductList(products: List<ProductModel>) {
        this.productsList = products
    }

    fun setProductsInShoppingList(productsInShoppingList: List<ProductShoppingListModel>) {
        this.productsInShoppingList = productsInShoppingList
        notifyDataSetChanged()
    }

}