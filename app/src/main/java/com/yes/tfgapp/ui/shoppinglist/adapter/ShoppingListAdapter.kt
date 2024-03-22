package com.yes.tfgapp.ui.shoppinglist.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.yes.tfgapp.databinding.ShoppingListRowBinding
import com.yes.tfgapp.domain.model.ShoppingListModel

class ShoppingListAdapter: RecyclerView.Adapter<ShoppingListAdapter.ShoppingListViewHolder>() {

    private var shoppingListList = emptyList<ShoppingListModel>()

    inner class ShoppingListViewHolder(private val binding: ShoppingListRowBinding) : RecyclerView.ViewHolder(binding.root)  {
        fun bind(currentItem: ShoppingListModel) {
            binding.tvShoppingListName.text = currentItem.name
            binding.tvShoppingListNumItems.text=currentItem.quantity.toString()
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ShoppingListViewHolder {
        val binding = ShoppingListRowBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ShoppingListViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return shoppingListList.size
    }

    override fun onBindViewHolder(holder: ShoppingListViewHolder, position: Int) {
        val currentItem = shoppingListList[position]
        holder.bind(currentItem)
    }

    fun setData(shoppingList: List<ShoppingListModel>){
        this.shoppingListList = shoppingList
        notifyDataSetChanged()
    }
}