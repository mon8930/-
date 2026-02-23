package com.kritsanapong.myinventory

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class ProductAdapter(
    private var products: MutableList<Product>,
    private val listener: OnProductActionListener
) : RecyclerView.Adapter<ProductAdapter.ViewHolder>() {

    interface OnProductActionListener {
        fun onEdit(product: Product)
        fun onDelete(product: Product)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_product, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val product = products[position]
        holder.tvName.text     = product.name
        holder.tvPrice.text    = "ราคา: ${product.price} บาท"
        holder.tvQuantity.text = "จำนวน: ${product.quantity}"

        holder.btnEdit.setOnClickListener   { listener.onEdit(product) }
        holder.btnDelete.setOnClickListener { listener.onDelete(product) }
    }

    override fun getItemCount() = products.size

    fun updateList(newList: MutableList<Product>) {
        products = newList
        notifyDataSetChanged()
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvName:     TextView    = itemView.findViewById(R.id.tvProductName)
        val tvPrice:    TextView    = itemView.findViewById(R.id.tvProductPrice)
        val tvQuantity: TextView    = itemView.findViewById(R.id.tvProductQuantity)
        val btnEdit:    ImageButton = itemView.findViewById(R.id.btnEdit)
        val btnDelete:  ImageButton = itemView.findViewById(R.id.btnDelete)
    }
}