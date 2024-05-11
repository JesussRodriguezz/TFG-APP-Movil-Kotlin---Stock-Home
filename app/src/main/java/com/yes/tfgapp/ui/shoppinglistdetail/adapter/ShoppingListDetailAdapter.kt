package com.yes.tfgapp.ui.shoppinglistdetail.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.yes.tfgapp.R
import com.yes.tfgapp.databinding.MyProductsListRowBinding
import com.yes.tfgapp.domain.model.ProductModel
import com.yes.tfgapp.domain.model.ProductShoppingListModel
import com.yes.tfgapp.domain.model.ShoppingListModel

class ShoppingListDetailAdapter(
    private val currentShoppingList: ShoppingListModel,
    private val setProductIsBought: (ProductShoppingListModel) -> Unit
) : RecyclerView.Adapter<ShoppingListDetailAdapter.ShoppingListDetailViewHolder>() {

    private var shoppingListProductsList = emptyList<ProductModel>()

    inner class ShoppingListDetailViewHolder(private val binding: MyProductsListRowBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(
            currentItem: ProductModel,
            setProductIsBought: (ProductShoppingListModel) -> Unit,
            currentShoppingList: ShoppingListModel
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

            // Set the checkbox to the value of isBought
            //binding.cbTask.isChecked = false

            binding.ibBoughtProduct.setOnClickListener {
                val productShoppingList = ProductShoppingListModel(
                    productId = currentItem.id,
                    shoppingListId = currentShoppingList.id,
                    isBought = true
                )
                setProductIsBought(productShoppingList)
                println("Product bought")
            }


            /*binding.cbTask.setOnCheckedChangeListener { buttonView, isChecked ->

                val productShoppingList = ProductShoppingListModel(productId = currentItem.id, shoppingListId =currentShoppingList.id , isBought = true)
                setProductIsBought(productShoppingList)
                println("Product bought")
            }*/
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
        holder.bind(currentItem, setProductIsBought, currentShoppingList)
    }

    fun setData(product: List<ProductModel>) {
        this.shoppingListProductsList = product
        notifyDataSetChanged()
    }


}