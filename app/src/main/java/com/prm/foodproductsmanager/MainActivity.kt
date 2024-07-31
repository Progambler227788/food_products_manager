package com.prm.foodproductsmanager

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import java.util.*

class MainActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: ProductsAdapter
    private lateinit var tvSummary: TextView

    private val requestCodeFetched = 1 // Request for adding or editing

    @Suppress("DEPRECATION")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Initialize and fetch variable values
        recyclerView = findViewById(R.id.recyclerViewCustomers)
        tvSummary = findViewById(R.id.tvSummary)

        // Populate recycler view items with sample data
        adapter = ProductsAdapter(getSampleData()) {
            updateSummary()
        }

        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(this)

        updateSummary() // Update data

        // Button to add new item
        findViewById<Button>(R.id.btnAddNewCustomer).setOnClickListener {
            startActivityForResult(Intent(this, AddItemActivity::class.java), requestCodeFetched )
        }
    }

    // Fetch Result after Edit or Add
    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == requestCodeFetched && resultCode == Activity.RESULT_OK) {
            @Suppress("DEPRECATION") val item = data?.getParcelableExtra<Item>("newItem")
            val position = data?.getIntExtra("position",-1)

            if(position==-1) {
                item?.let {
                    addItem(it) // For adding
                }
            }
            else {
                item?.let {
                    editItemByPosition(it, position!!) // For editing
                }
            }
        }
    }
    // Add Items
    private fun addItem(item: Item) {
        adapter.addItem(item)
        updateSummary()
    }
    // Edit Items By position
    private fun editItemByPosition(item: Item, index : Int) {
        adapter.editItemByPosition(item,index)
    }

    // Update Data
    @SuppressLint("SetTextI18n")
    private fun updateSummary() {
        val itemCount = adapter.itemCount
        tvSummary.text = "Summary: $itemCount items"
    }

    private fun getSampleData(): MutableList<Item> {
        // Date variables
        val currentDate = Calendar.getInstance()
        currentDate.add(Calendar.DAY_OF_YEAR, 1)
        val nextDayDate = currentDate.time
        currentDate.add(Calendar.DAY_OF_YEAR, 100)
        val nextDayDate1 = currentDate.time
        // Sample Data
        val sampleData = mutableListOf(
            Item("Zinger Burger", nextDayDate1, "Food Products", 5),
            Item("Healing Tablet", nextDayDate, "Medicines", 3),
            Item("Beauty Cream", Date(), "Cosmetics", 10),
            Item("Crusty Pizza", nextDayDate1, "Food Products", 5),
            Item("Pain Killer", nextDayDate, "Medicines", 3),
            Item("Product 3", Date(), "Cosmetics", 10)

            )

        val sortedSampleData = sampleData.sortedBy { it.expirationDate }
        return sortedSampleData.toMutableList()
    }
}
