package com.prm.foodproductsmanager

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import java.text.SimpleDateFormat
import java.util.*

class ProductsAdapter(private val items: MutableList<Item>, private val onItemRemoved: () -> Unit) : RecyclerView.Adapter<ProductsAdapter.ViewHolder>() {
    private lateinit var context: Context

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvProductName: TextView = itemView.findViewById(R.id.tvProductName)
        val tvExpirationDate: TextView = itemView.findViewById(R.id.tvExpirationDate)
        val tvCategory: TextView = itemView.findViewById(R.id.tvCategory)
        val tvQuantity: TextView = itemView.findViewById(R.id.tvQuantity)

        init {
            itemView.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    val item = items[position]
                    if (isProductUsable(item.expirationDate)) {
                        // Enable preview and editing
                        launchEditPreviewScreen(item,position)
                    } else {
                        // Display message about impossibility of editing
                        displayMessage("This product is expired and cannot be edited.")
                    }
                }
            }

            itemView.setOnLongClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    val item = items[position]
                    if (isProductUsable(item.expirationDate)) {
                        showDeleteConfirmationDialog(item)
                    } else {
                        displayMessage("This product is expired and cannot be deleted.")
                    }
                }
                true
            }
        }
    }
    // Function to handle item clicks
    private fun launchEditPreviewScreen(item: Item, position: Int) {
        val intent = Intent(context, AddItemActivity::class.java)
        intent.putExtra("position",position)
        intent.putExtra("editItem", item) // Pass the item data to AddItemActivity for editing
        @Suppress("DEPRECATION")
        (context as AppCompatActivity).startActivityForResult(intent, 1)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        context = parent.context
        val view = LayoutInflater.from(context).inflate(R.layout.rv_items, parent, false)
        return ViewHolder(view)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val currentItem = items[position]

        // Bind data to views
        holder.tvProductName.text = "Product Name: ${currentItem.productName}"
        holder.tvExpirationDate.text = "Expiration Date: ${formatDate(currentItem.expirationDate)}"
        holder.tvCategory.text = "Category: ${currentItem.category}"
        holder.tvQuantity.text = "Quantity: ${currentItem.quantity ?: "N/A"}"
    }

    override fun getItemCount(): Int {
        return items.size
    }
    @SuppressLint("NotifyDataSetChanged")
    private fun sortItemsByExpirationDate() {
        items.sortBy { it.expirationDate }
        notifyDataSetChanged()
    }


    private fun formatDate(date: Date): String {
        val sdf = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
        return sdf.format(date)
    }

    private fun isProductUsable(expirationDate: Date): Boolean {

        // Compare the expiration date with the current date
        return expirationDate > Date()
    }

    private fun displayMessage(message: String) {
        // Display a message to the user
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }

    private fun showDeleteConfirmationDialog(item: Item) {
        AlertDialog.Builder(context)
            .setTitle("Delete Item")
            .setMessage("Are you sure you want to delete this item?")
            .setPositiveButton("Yes") { _, _ ->
                removeItem(item)

            }
            .setNegativeButton("No", null)
            .show()
    }
    fun addItem(item: Item) {
        items.add(item)
        sortItemsByExpirationDate()
        notifyItemInserted(items.size - 1)
    }
    fun editItemByPosition(item: Item, pos : Int) {
        items[pos] = item
        sortItemsByExpirationDate()
        notifyItemChanged(pos)
    }

    private fun removeItem(item: Item) {
        val position = items.indexOf(item)
        if (position != -1) {
            // Remove the item from the original items list
            items.removeAt(position)
            // Notify the adapter of the item removal
            notifyItemRemoved(position)
            // Notify the adapter that the dataset has changed
            notifyItemRangeChanged(position, itemCount)

            // sort data
            sortItemsByExpirationDate()

            // Invoke the callback function after item removal
            onItemRemoved.invoke()
        }
    }


}
