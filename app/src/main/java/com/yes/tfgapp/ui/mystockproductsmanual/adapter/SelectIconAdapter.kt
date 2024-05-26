package com.yes.tfgapp.ui.mystockproductsmanual.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.yes.tfgapp.R

class SelectIconAdapter(
    private val icons: List<Int>,
    private val listener: (Int) -> Unit
) : RecyclerView.Adapter<SelectIconAdapter.IconViewHolder>() {

    inner class IconViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val iconImageView: ImageView = itemView.findViewById(R.id.ivIcon)

        fun bind(icon: Int, listener: (Int) -> Unit) {
            iconImageView.setImageResource(icon)
            itemView.setOnClickListener { listener(icon) }
        }
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): IconViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.icon_item_row, parent, false)
        return IconViewHolder(view)
    }

    override fun onBindViewHolder(holder: IconViewHolder, position: Int) {
        val icon = icons[position]
        holder.bind(icon, listener)
    }

    override fun getItemCount(): Int = icons.size


}