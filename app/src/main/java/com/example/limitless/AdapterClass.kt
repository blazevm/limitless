package com.example.limitless

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide


class AdapterClass(private val dataList: ArrayList<DataClass>): RecyclerView.Adapter<AdapterClass.ViewHolderClass>() {

    var onItemClick: ((DataClass) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolderClass {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_layout, parent, false)
        return ViewHolderClass(itemView)
    }

    override fun onBindViewHolder(holder: ViewHolderClass, position: Int) {
        val currentItem = dataList[position]
        holder.rvDescription.text = currentItem.dataDescription
        holder.rvBrand.text = currentItem.dataBrand
        holder.rvDate.text = currentItem.dataDate
        Glide.with(holder.itemView.context)
            .load(currentItem.dataImage)
            .into(holder.rvImage)

        holder.itemView.setOnClickListener{
            onItemClick?.invoke(currentItem)
        }
    }

    override fun getItemCount(): Int {
        return dataList.size
    }

    class ViewHolderClass(itemView: View): RecyclerView.ViewHolder(itemView) {
        val rvImage:ImageView = itemView.findViewById(R.id.image)
        val rvDescription:TextView = itemView.findViewById(R.id.description)
        val rvBrand:TextView = itemView.findViewById(R.id.brand)
        val rvDate:TextView = itemView.findViewById(R.id.date)
    }
}