package com.prm.foodproductsmanager

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.DatePicker
import android.widget.EditText
import android.widget.RadioGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import java.text.SimpleDateFormat
import java.util.*

// This activity will add or edit data item as well
class AddItemActivity : AppCompatActivity(), DatePickerDialog.OnDateSetListener {

    private lateinit var etProductName: EditText
    private lateinit var etExpirationDate: EditText
    private lateinit var etQuantity: EditText
    private lateinit var btnSave: Button
    private lateinit var radioGroupCategory: RadioGroup
    private lateinit var calendar: Calendar
    private var editingItem: Item? = null // For editing item
    private var position: Int = -1


    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_item)

        etProductName = findViewById(R.id.etProductName)
        etExpirationDate = findViewById(R.id.etExpirationDate)
        etQuantity = findViewById(R.id.etQuantity)
        btnSave = findViewById(R.id.btnSave)
        radioGroupCategory = findViewById(R.id.radioGroupCategory)


        btnSave.setOnClickListener {
            saveItem()
        }
        // Initialize Calendar instance
        calendar = Calendar.getInstance()

        // Set click listener for etExpirationDate
        etExpirationDate.setOnClickListener {
            showDatePickerDialog()
        }
        /* Check if editing item data is passed via Intent */
        if (intent.hasExtra("editItem")) {
            btnSave.text = "Save Changes"
            @Suppress("DEPRECATION")
            editingItem = intent.getParcelableExtra("editItem")
            position = intent.getIntExtra("position",-1)

            populateFieldsForEditing(editingItem)
        }
    }

    private fun populateFieldsForEditing(item: Item?) {
        item?.apply {
            etProductName.setText(productName)
            // Format the expiration date
            val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            val formattedExpirationDate = dateFormat.format(expirationDate)
            etExpirationDate.setText(formattedExpirationDate)

            etQuantity.setText(quantity?.toString())
            // Set selected radio button based on category
            when (category) {
                "Food Products" -> radioGroupCategory.check(R.id.rbFoodProducts)
                "Medicines" -> radioGroupCategory.check(R.id.rbMedicines)
                "Cosmetics" -> radioGroupCategory.check(R.id.rbCosmetics)
            }
        }
    }

    private fun showDatePickerDialog() {
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(this, this, year, month, dayOfMonth)
        datePickerDialog.show()
    }

    override fun onDateSet(view: DatePicker?, year: Int, month: Int, dayOfMonth: Int) {
        calendar.set(Calendar.YEAR, year)
        calendar.set(Calendar.MONTH, month)
        calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)

        // Format the selected date and set it to etExpirationDate
        val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        val formattedDate = dateFormat.format(calendar.time)
        etExpirationDate.setText(formattedDate)
    }
    private fun saveItem() {
        val productName = etProductName.text.toString().trim()

        val expirationDate = if (editingItem == null) {
            calendar.time
        } else {
            val dateString = etExpirationDate.text.toString()
            val selectedDate = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).parse(dateString)!!
            selectedDate
        }
        // Use the selected date from the DatePicker
        val quantity = etQuantity.text.toString().toIntOrNull()

        if (productName.isEmpty()) {
            etProductName.error = "Product Name cannot be empty"
            return
        }
        // Get the selected category from the radio buttons
        val selectedCategory = when (radioGroupCategory.checkedRadioButtonId) {
            R.id.rbFoodProducts -> "Food Products"
            R.id.rbMedicines -> "Medicines"
            R.id.rbCosmetics -> "Cosmetics"
            else -> ""
        }


        if (selectedCategory.isEmpty()) {
            Toast.makeText(this,"Please! select category",Toast.LENGTH_SHORT).show()
            return
        }
        val currentDate = Date() // Get the current date
        if (expirationDate.before(currentDate)) {
            Toast.makeText(this, "Expiration date should be greater than the current date", Toast.LENGTH_SHORT).show()
            return
        }


        if (etExpirationDate.text == null || etExpirationDate.text.isEmpty()) {
            Toast.makeText(this,"Please! select Expiration Date of $selectedCategory",Toast.LENGTH_SHORT).show()
            return
        }

        // Add validation for expiration date not in the past (not implemented here)

        if (quantity == null) {
            etQuantity.error = "Quantity must be a numerical value"
            return
        }

        // If all data is valid, create a new Item object
        val newItem = Item(productName, expirationDate, selectedCategory , quantity)


        // Pass the new item back to MainActivity
        val intent = Intent()
        intent.putExtra("newItem", newItem)


        // send intent result with OK
        intent.putExtra("position", position)

        setResult(RESULT_OK, intent)
        finish()
    }
}
