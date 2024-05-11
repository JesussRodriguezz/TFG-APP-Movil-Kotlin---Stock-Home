package com.yes.tfgapp.ui.shoppinglistdetail.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.yes.tfgapp.R
import com.yes.tfgapp.databinding.MyProductsListRowBinding
import com.yes.tfgapp.domain.model.CategoryModel
import com.yes.tfgapp.domain.model.ProductModel
import com.yes.tfgapp.domain.model.ProductShoppingListModel
import com.yes.tfgapp.domain.model.ShoppingListModel

class ShoppingListDetailBoughtAdapter(
    private val currentShoppingList: ShoppingListModel,
    private val setProductIsNotBought: (ProductShoppingListModel) -> Unit,
    private val getCategoryById: (Int, (CategoryModel?) -> Unit) -> Unit
) : RecyclerView.Adapter<ShoppingListDetailBoughtAdapter.ShoppingListDetailBoughtViewHolder>() {

    private var shoppingListProductsBoughtList = emptyList<ProductModel>()

    inner class ShoppingListDetailBoughtViewHolder(private val binding: MyProductsListRowBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(
            currentItem: ProductModel,
            setProductIsNotBought: (ProductShoppingListModel) -> Unit,
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
            getCategoryById(currentItem.categoryId){category ->
                binding.ivProductIcon.setImageResource(category!!.icon)
            }
            binding.tvMyProductName.paint.isStrikeThruText = true

            binding.ibBoughtProduct.setOnClickListener {
                val productShoppingList = ProductShoppingListModel(
                    productId = currentItem.id,
                    shoppingListId = currentShoppingList.id,
                    isBought = false
                )
                setProductIsNotBought(productShoppingList)
            }

        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ShoppingListDetailBoughtViewHolder {
        val binding =
            MyProductsListRowBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ShoppingListDetailBoughtViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return shoppingListProductsBoughtList.size
    }

    override fun onBindViewHolder(holder: ShoppingListDetailBoughtViewHolder, position: Int) {
        val currentItem = shoppingListProductsBoughtList[position]
        holder.bind(currentItem, setProductIsNotBought, currentShoppingList,getCategoryById)
    }

    fun setData(product: List<ProductModel>) {
        this.shoppingListProductsBoughtList = product
        notifyDataSetChanged()
    }
}