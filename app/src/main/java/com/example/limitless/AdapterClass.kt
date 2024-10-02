package com.example.limitless

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

// AdapterClass handles the binding of data to the RecyclerView
class AdapterClass(private val dataList: ArrayList<DataClass>): RecyclerView.Adapter<AdapterClass.ViewHolderClass>() {

    var onItemClick: ((DataClass) -> Unit)? = null

    // Inflates the item layout (R.layout.item_layout) and creates the ViewHolder
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolderClass {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_layout, parent, false)
        return ViewHolderClass(itemView)
    }

    // Binds data from the current item in dataList to the ViewHolder
    override fun onBindViewHolder(holder: ViewHolderClass, position: Int) {
        // Get the current data item
        val currentItem = dataList[position]

        // Bind the description, brand, and date data to the corresponding TextViews in the ViewHolder
        holder.rvDescription.text = currentItem.dataDescription
        holder.rvBrand.text = currentItem.dataBrand
        holder.rvDate.text = currentItem.dataDate

        // Use Glide to load the image into the ImageView
        Glide.with(holder.itemView.context)
            .load(currentItem.dataImage)
            .into(holder.rvImage)

        // Set up an item click listener and invoke the lambda function onItemClick when the item is clicked
        holder.itemView.setOnClickListener{
            onItemClick?.invoke(currentItem)
        }
    }

    // Returns the size of the dataList, which determines how many items will be displayed in the RecyclerView
    override fun getItemCount(): Int {
        return dataList.size
    }

    // ViewHolderClass holds the views for each item in the RecyclerView
    class ViewHolderClass(itemView: View): RecyclerView.ViewHolder(itemView) {
        val rvImage: ImageView = itemView.findViewById(R.id.image)
        val rvDescription: TextView = itemView.findViewById(R.id.description)
        val rvBrand: TextView = itemView.findViewById(R.id.brand)
        val rvDate: TextView = itemView.findViewById(R.id.date)
    }
}
