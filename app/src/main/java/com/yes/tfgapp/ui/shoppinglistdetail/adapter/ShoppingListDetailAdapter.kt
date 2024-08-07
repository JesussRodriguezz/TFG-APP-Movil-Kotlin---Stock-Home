package com.yes.tfgapp.ui.shoppinglistdetail.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageButton
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.yes.tfgapp.R
import com.yes.tfgapp.databinding.MyProductsListRowBinding
import com.yes.tfgapp.domain.model.CategoryModel
import com.yes.tfgapp.domain.model.ProductModel
import com.yes.tfgapp.domain.model.ProductShoppingListModel
import com.yes.tfgapp.domain.model.ShoppingListModel

class ShoppingListDetailAdapter(
    private val currentShoppingList: ShoppingListModel,
    private val setProductIsBought: (ProductShoppingListModel) -> Unit,
    private val getCategoryById: (Int, (CategoryModel?) -> Unit) -> Unit
) : RecyclerView.Adapter<ShoppingListDetailAdapter.ShoppingListDetailViewHolder>() {

    private var shoppingListProductsList = emptyList<ProductModel>()

    inner class ShoppingListDetailViewHolder(private val binding: MyProductsListRowBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(
            currentItem: ProductModel,
            setProductIsBought: (ProductShoppingListModel) -> Unit,
            currentShoppingList: ShoppingListModel,
            getCategoryById: (Int, (CategoryModel?) -> Unit) -> Unit
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

            binding.tvMyProductName.text = currentItem.name
            binding.ibBoughtProduct.setImageResource(R.drawable.ic_unchecked)

            getCategoryById(currentItem.categoryId){category ->
                binding.ivProductIcon.setImageResource(category!!.icon)
            }

            binding.ibBoughtProduct.setOnClickListener {
                animateIconChange(binding.ibBoughtProduct, R.drawable.ic_check) {
                    val productShoppingList = ProductShoppingListModel(
                        productId = currentItem.id,
                        shoppingListId = currentShoppingList.id,
                        isBought = true
                    )
                    setProductIsBought(productShoppingList)

                }
            }
        }

        private fun animateIconChange(
            imageButton: ImageButton,
            newIconResId: Int,
            onAnimationEnd: () -> Unit
        ) {
            imageButton.setImageResource(newIconResId)
            imageButton.scaleX = 0.8f
            imageButton.scaleY = 0.8f
            imageButton.animate()
                .scaleX(1.2f)
                .scaleY(1.2f)
                .setDuration(100)
                .withEndAction {
                    imageButton.animate()
                        .scaleX(1f)
                        .scaleY(1f)
                        .setDuration(100)
                        .withEndAction {
                            onAnimationEnd()
                        }.start()
                }.start()
        }

    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ShoppingListDetailViewHolder {
        val binding =
            MyProductsListRowBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ShoppingListDetailViewHolder(binding)

    }

    override fun getItemCount(): Int {
        return shoppingListProductsList.size
    }

    override fun onBindViewHolder(holder: ShoppingListDetailViewHolder, position: Int) {
        val currentItem = shoppingListProductsList[position]
        holder.bind(currentItem, setProductIsBought, currentShoppingList,getCategoryById)
    }

    fun setData(product: List<ProductModel>) {
        this.shoppingListProductsList = product
        notifyDataSetChanged()
    }


}