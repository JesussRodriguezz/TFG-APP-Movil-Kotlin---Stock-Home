package com.yes.tfgapp.ui.mystockproductscan

import android.app.Dialog
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.NumberPicker
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import com.squareup.picasso.Picasso
import com.yes.tfgapp.R
import com.yes.tfgapp.databinding.ActivityMyStockProductScanBinding
import com.yes.tfgapp.domain.model.StockProductModel
import com.yes.tfgapp.ui.mystock.MyStockViewModel
import com.yes.tfgapp.ui.mystockproductsmanual.adapter.ChooseCategoryAdapterManual
import com.yes.tfgapp.ui.shoppinglistadditems.ShoppingListAddItemsViewModel
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.concurrent.TimeUnit

class MyStockProductScanActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMyStockProductScanBinding
    private lateinit var stockProduct: StockProductModel
    private var chooseCategoryAdapter: ChooseCategoryAdapterManual = ChooseCategoryAdapterManual(
        onChangeCategory = { changeCategorySelected() }
    )
    private lateinit var mShoppingListAddItemsViewModel: ShoppingListAddItemsViewModel
    private lateinit var btnAddStockProduct: View
    private lateinit var btnCloseDialog: View
    private lateinit var mStockViewModel: MyStockViewModel
    private var selectedExpireDateButton: Button? = null
    private var selectedCategory: Int = 14

    private var expireData: String = "__ / __ / ____"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMyStockProductScanBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initUI()
        initListeners()


    }

    private fun initUI() {
        binding.ivCategoryIcon.setImageResource(R.drawable.ic_others_category)
        binding.tvCategoryName.text = "Otros"

        stockProduct = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getParcelableExtra("currentStockProduct", StockProductModel::class.java)!!
        } else {
            @Suppress("DEPRECATION")
            intent.getParcelableExtra("currentStockProduct")!!
        }
        mStockViewModel = ViewModelProvider(this)[MyStockViewModel::class.java]
        mShoppingListAddItemsViewModel =
            ViewModelProvider(this)[ShoppingListAddItemsViewModel::class.java]

        btnAddStockProduct = binding.efAddStockItem
        btnCloseDialog = binding.ibBackArrow

        if (stockProduct.id == "error") {
            binding.tvProductId.text = "Product not found"
        } else {
            binding.tvProductId.text = stockProduct.name
            Picasso.get()
                .load(stockProduct.image)
                .resize(300, 350)  // Ajusta las dimensiones de la imagen a las deseadas
                .centerCrop()      // Ajusta la imagen para llenar las dimensiones
                .into(binding.ivProductApiSearchImage)
        }

        val rvCategories = binding.rvCategories
        rvCategories.layoutManager = GridLayoutManager(this, 3, GridLayoutManager.HORIZONTAL, false)
        rvCategories.adapter = chooseCategoryAdapter
        mShoppingListAddItemsViewModel.readAllDataCategory.observe(this) { categories ->
            chooseCategoryAdapter.setCategoriesListModified(categories, 14)
            chooseCategoryAdapter.notifyDataSetChanged()
        }
    }

    private fun initListeners() {


        btnAddStockProduct.setOnClickListener {
            addStockProduct()
            finish()
        }

        btnCloseDialog.setOnClickListener {
            finish()
        }

        binding.btn1Week.setOnClickListener {
            expireData = addDaysToDate(getCurrentDate(), 7)
            binding.tvChangeExpireData.text =
                formatDate(SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).parse(expireData)!!)

            updateButtonStates(
                binding.btn1Week,
                listOf(binding.btn2Weeks, binding.btn1Month, binding.btn2Months)
            )

        }

        binding.btn2Weeks.setOnClickListener {
            expireData = addDaysToDate(getCurrentDate(), 14)
            binding.tvChangeExpireData.text =
                formatDate(SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).parse(expireData)!!)

            updateButtonStates(
                binding.btn2Weeks,
                listOf(binding.btn1Week, binding.btn1Month, binding.btn2Months)
            )
        }

        binding.btn1Month.setOnClickListener {
            expireData = addDaysToDate(getCurrentDate(), 30)
            binding.tvChangeExpireData.text =
                formatDate(SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).parse(expireData)!!)

            updateButtonStates(
                binding.btn1Month,
                listOf(binding.btn1Week, binding.btn2Weeks, binding.btn2Months)
            )
        }

        binding.btn2Months.setOnClickListener {
            expireData = addDaysToDate(getCurrentDate(), 90)
            binding.tvChangeExpireData.text =
                formatDate(SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).parse(expireData)!!)

            updateButtonStates(
                binding.btn2Months,
                listOf(binding.btn1Week, binding.btn1Month, binding.btn2Weeks)
            )
        }

        binding.tvChangeCategory.setOnClickListener {
            binding.rvCategories.visibility = if (binding.rvCategories.visibility == View.VISIBLE) {
                View.GONE
            } else {
                View.VISIBLE
            }
            binding.llCategorySuggested.visibility =
                if (binding.llCategorySuggested.visibility == View.VISIBLE) {
                    View.GONE
                } else {
                    View.VISIBLE
                }
            binding.tvChangeCategory.visibility =
                if (binding.tvChangeCategory.visibility == View.VISIBLE) {
                    View.GONE
                } else {
                    View.VISIBLE
                }
        }

        binding.cvChangeExpireData.setOnClickListener {
            showChangeExpireDataDialog()
        }

    }

    private fun updateButtonStates(selectedButton: Button, otherButtons: List<Button>) {
        selectedButton.setBackgroundColor(ContextCompat.getColor(this, R.color.accentRed))
        selectedButton.setTextColor(ContextCompat.getColor(this, R.color.white))
        selectedExpireDateButton = selectedButton

        otherButtons.forEach { button ->
            button.setBackgroundColor(ContextCompat.getColor(this, R.color.primaryGrey))
            button.setTextColor(ContextCompat.getColor(this, R.color.black))
        }
    }

    private fun showChangeExpireDataDialog() {

        val dialog = Dialog(this)
        dialog.setContentView(R.layout.dialog_change_expire_data)
        dialog.window?.setLayout(
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        configureManualData(dialog)
        dialog.show()
    }

    private fun configureManualData(dialog: Dialog) {
        val npDay: NumberPicker = dialog.findViewById(R.id.npDay)
        val npMonth: NumberPicker = dialog.findViewById(R.id.npMonth)
        val npYear: NumberPicker = dialog.findViewById(R.id.npYear)

        npDay.minValue = 1
        npDay.maxValue = 31

        val months = arrayOf(
            "Jan", "Feb", "Mar", "Apr", "May", "Jun",
            "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"
        )
        npMonth.displayedValues = months
        npMonth.minValue = 1
        npMonth.maxValue = 12

        val currentYear = Calendar.getInstance().get(Calendar.YEAR)
        npYear.minValue = currentYear
        npYear.maxValue = currentYear + 100

        if (expireData == "__ / __ / ____") {

            val currentDate = Calendar.getInstance()
            npDay.value = currentDate.get(Calendar.DAY_OF_MONTH)
            npMonth.value = currentDate.get(Calendar.MONTH) + 1
            npYear.value = currentDate.get(Calendar.YEAR)
        } else {

            val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            try {
                val expireDate = dateFormat.parse(expireData)
                expireDate?.let {
                    val calendar = Calendar.getInstance()
                    calendar.time = it

                    npDay.value = calendar.get(Calendar.DAY_OF_MONTH)
                    npMonth.value = calendar.get(Calendar.MONTH) + 1
                    npYear.value = calendar.get(Calendar.YEAR)
                }
            } catch (e: ParseException) {
                e.printStackTrace()

                val currentDate = Calendar.getInstance()
                npDay.value = currentDate.get(Calendar.DAY_OF_MONTH)
                npMonth.value = currentDate.get(Calendar.MONTH) + 1
                npYear.value = currentDate.get(Calendar.YEAR)
            }
        }

        dialog.findViewById<Button>(R.id.btnOk).setOnClickListener {
            val day = npDay.value
            val month = npMonth.value
            val year = npYear.value

            expireData = "$day/$month/$year"

            val formattedDate = formatDate(day, month, year)

            binding.tvChangeExpireData.text = formattedDate

            dialog.dismiss()
        }

        dialog.findViewById<Button>(R.id.btnCancel).setOnClickListener {
            dialog.dismiss()
        }
    }

    private fun formatDate(day: Int, month: Int, year: Int): String {
        val months = arrayOf(
            "Jan", "Feb", "Mar", "Apr", "May", "Jun",
            "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"
        )
        return String.format("%02d/%s/%d", day, months[month - 1], year)
    }

    private fun formatDate(date: Date): String {
        val calendar = Calendar.getInstance()
        calendar.time = date
        val day = calendar.get(Calendar.DAY_OF_MONTH)
        val month = calendar.get(Calendar.MONTH)
        val year = calendar.get(Calendar.YEAR)
        return formatDate(day, month + 1, year)
    }

    private fun getCurrentDate(): String {
        val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        return dateFormat.format(Date())
    }


    private fun addStockProduct() {
        val stockProduct = this.stockProduct

        val daysToExpire = daysBetweenDates(stockProduct.addedDate, expireData).toInt()
        val updatedStockProduct = stockProduct.copy(
            expirationDate = expireData,
            daysToExpire = daysToExpire,
            categoryId = selectedCategory
        )
        mStockViewModel.addProductIfNotExists(updatedStockProduct) { productAdded ->
            runOnUiThread {
                if (productAdded) {

                    mStockViewModel.updateStockProduct(updatedStockProduct)
                    Toast.makeText(this, "Producto añadido correctamente", Toast.LENGTH_SHORT)
                        .show()
                } else {

                    Toast.makeText(this, "¡El producto ya existe en tu stock!", Toast.LENGTH_SHORT)
                        .show()
                }
            }
        }
    }


    private fun addDaysToDate(date: String, days: Int): String {
        val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        val calendar = Calendar.getInstance()
        calendar.time = dateFormat.parse(date) ?: return date
        calendar.add(Calendar.DAY_OF_YEAR, days)
        return dateFormat.format(calendar.time)
    }

    private fun daysBetweenDates(startDate: String, endDate: String): Long {
        if (expireData != "__ / __ / ____") {
            val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            val start = dateFormat.parse(startDate)
            val end = dateFormat.parse(endDate)
            if (start != null && end != null) {
                val diffInMillis = end.time - start.time
                return TimeUnit.DAYS.convert(diffInMillis, TimeUnit.MILLISECONDS)
            }
        }
        return 0
    }

    private fun changeCategorySelected() {
        selectedCategory =
            chooseCategoryAdapter.publicCategoriesList[chooseCategoryAdapter.selectedItemPosition].id
    }


}