package com.yes.tfgapp.ui.mystock.adapter

import android.app.Dialog
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.yes.tfgapp.R
import com.yes.tfgapp.databinding.ChooseShoppingListRowBinding
import com.yes.tfgapp.domain.model.ProductModel
import com.yes.tfgapp.domain.model.ShoppingListModel
import com.yes.tfgapp.domain.model.StockProductModel
import kotlin.math.roundToInt

class ChooseShoppingListAdapter(
    private val dialog: Dialog,
    private val stockProduct: StockProductModel,
    private val onAddProductToList: (ProductModel, ShoppingListModel, Dialog, StockProductModel) -> Unit
) :
    RecyclerView.Adapter<ChooseShoppingListAdapter.ChooseShoppingListViewHolder>() {

    private var shoppingLists = emptyList<ShoppingListModel>()

    inner class ChooseShoppingListViewHolder(private val binding: ChooseShoppingListRowBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(
            currentItem: ShoppingListModel,
            onAddProductToList: (ProductModel, ShoppingListModel, Dialog, StockProductModel) -> Unit,
            dialog: Dialog
        ) {
            binding.tvShoppingListName.text = currentItem.name
            binding.tvShoppingListNumBoughtItems.text = currentItem.quantityBought.toString()
            binding.tvShoppingListNumItems.text = currentItem.quantity.toString()
            val percentage = if (currentItem.quantity != 0) {
                (currentItem.quantityBought.toDouble() / currentItem.quantity.toDouble() * 100).roundToInt()
            } else {
                0
            }
            val progressBarColor = when {
                percentage == 100 -> R.color.progress5
                percentage >= 75 -> R.color.progress4
                percentage >= 50 -> R.color.progress3
                percentage >= 25 -> R.color.progress2
                else -> R.color.progress1
            }
            binding.progressBar.setDividerColorResource(progressBarColor)

            val product = ProductModel(
                name = stockProduct.name,
                categoryId = stockProduct.categoryId
            )

            binding.root.setOnClickListener {
                onAddProductToList(product, currentItem, dialog, stockProduct)
            }
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ChooseShoppingListAdapter.ChooseShoppingListViewHolder {
        val binding =
            ChooseShoppingListRowBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ChooseShoppingListViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return shoppingLists.size
    }

    override fun onBindViewHolder(
        holder: ChooseShoppingListAdapter.ChooseShoppingListViewHolder,
        position: Int
    ) {
        val currentItem = shoppingLists[position]
        holder.bind(currentItem, onAddProductToList, dialog)
    }

    fun setShoppingLists(shoppingLists: List<ShoppingListModel>) {
        this.shoppingLists = shoppingLists
        notifyDataSetChanged()
    }
}