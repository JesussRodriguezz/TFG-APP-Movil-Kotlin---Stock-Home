package com.yes.tfgapp.ui.searchproducts.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import com.yes.tfgapp.R
import com.yes.tfgapp.data.network.response.ProductItemResponse
import com.yes.tfgapp.databinding.ProductApiSearchRowBinding
import com.yes.tfgapp.domain.model.ProductModel

class ProductSearchApiAdapter(private val onAddProductToList: (ProductModel) -> Unit): RecyclerView.Adapter<ProductSearchApiAdapter.ProductSearchViewHolder>(){

    private var productApiSearchList = emptyList<ProductItemResponse>()
    inner class ProductSearchViewHolder(private val binding: ProductApiSearchRowBinding ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(currentItem: ProductItemResponse,onAddProductToList: (ProductModel) -> Unit) {
            val backgroundColor = if(adapterPosition % 2 == 0){
                R.color.primaryGrey
            }else{
                R.color.primaryWhite
            }
            binding.root.setBackgroundColor(ContextCompat.getColor(binding.root.context, backgroundColor))

            binding.tvProductApiSearchName.text = currentItem.productName
            Picasso.get().load(currentItem.productImage).into(binding.ivProductApiSearchImage)

            binding.ibBoughtProduct.setOnClickListener{
                onAddProductToList(ProductModel(name = currentItem.productName))
            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductSearchViewHolder {
        val binding = ProductApiSearchRowBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ProductSearchViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return productApiSearchList.size
    }

    override fun onBindViewHolder(holder: ProductSearchViewHolder, position: Int) {
        val currentItem = productApiSearchList[position]
        holder.bind(currentItem,onAddProductToList)
    }

    fun setData(productItemResponse: List<ProductItemResponse>){
        this.productApiSearchList = productItemResponse
        notifyDataSetChanged()
    }
}


