package com.yes.tfgapp.ui.mystockproductscan

import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
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
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import java.util.concurrent.TimeUnit

class MyStockProductScanActivity : AppCompatActivity() {

    private lateinit var binding : ActivityMyStockProductScanBinding
    private lateinit var stockProduct: StockProductModel
    private var chooseCategoryAdapter: ChooseCategoryAdapterManual = ChooseCategoryAdapterManual(
        onChangeCategory= {changeCategorySelected()}
    )
    private lateinit var mShoppingListAddItemsViewModel: ShoppingListAddItemsViewModel
    private lateinit var btnAddStockProduct : View
    private lateinit var btnCloseDialog : View
    private lateinit var mStockViewModel: MyStockViewModel
    private var selectedExpireDateButton: Button? = null
    private var selectedCategory: Int = 14
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
            intent.getParcelableExtra<StockProductModel>("currentStockProduct")!!
        }
        mStockViewModel= ViewModelProvider(this)[MyStockViewModel::class.java]
        mShoppingListAddItemsViewModel =
            ViewModelProvider(this)[ShoppingListAddItemsViewModel::class.java]

        btnAddStockProduct = binding.efAddStockItem
        btnCloseDialog = binding.ibBackArrow

        if(stockProduct.id=="error"){
            binding.tvProductId.text = "Product not found"
        }else{
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
            updateButtonStates(binding.btn1Week, listOf(binding.btn2Weeks, binding.btn1Month, binding.btn2Months))
        }

        binding.btn2Weeks.setOnClickListener {
            updateButtonStates(binding.btn2Weeks, listOf(binding.btn1Week, binding.btn1Month, binding.btn2Months))
        }

        binding.btn1Month.setOnClickListener {
            updateButtonStates(binding.btn1Month, listOf(binding.btn1Week, binding.btn2Weeks, binding.btn2Months))
        }

        binding.btn2Months.setOnClickListener {
            updateButtonStates(binding.btn2Months, listOf(binding.btn1Week, binding.btn2Weeks, binding.btn1Month))
        }

        binding.tvChangeCategory.setOnClickListener {
            binding.rvCategories.visibility = if (binding.rvCategories.visibility == View.VISIBLE) {
                View.GONE
            } else {
                View.VISIBLE
            }
            binding.llCategorySuggested.visibility = if (binding.llCategorySuggested.visibility == View.VISIBLE) {
                View.GONE
            } else {
                View.VISIBLE
            }
            binding.tvChangeCategory.visibility = if (binding.tvChangeCategory.visibility == View.VISIBLE) {
                View.GONE
            } else {
                View.VISIBLE
            }
        }

    }

    private fun updateButtonStates(selectedButton: Button, otherButtons: List<Button>) {
        selectedButton.setBackgroundColor(ContextCompat.getColor(this, R.color.accentRed))
        selectedButton.setTextColor(ContextCompat.getColor(this, R.color.white))
        selectedExpireDateButton=selectedButton

        otherButtons.forEach { button ->
            button.setBackgroundColor(ContextCompat.getColor(this, R.color.primaryGrey))
            button.setTextColor(ContextCompat.getColor(this, R.color.black))
        }
    }


    private fun addStockProduct() {
        val stockProduct = this.stockProduct
        val expirationDate = when (selectedExpireDateButton) {
            binding.btn1Week -> addDaysToDate(stockProduct.addedDate, 7)
            binding.btn2Weeks -> addDaysToDate(stockProduct.addedDate, 14)
            binding.btn1Month -> addDaysToDate(stockProduct.addedDate, 30)
            binding.btn2Months -> addDaysToDate(stockProduct.addedDate, 60)
            else -> stockProduct.addedDate
        }
        val daysToExpire = daysBetweenDates(stockProduct.addedDate, expirationDate).toInt()
        val updatedStockProduct = stockProduct.copy(expirationDate = expirationDate, daysToExpire = daysToExpire, categoryId = selectedCategory)
        mStockViewModel.addProductIfNotExists(updatedStockProduct) { productAdded ->
            runOnUiThread {
                if (productAdded) {
                    // Si el producto se añadió correctamente, actualiza y muestra un Toast
                    mStockViewModel.updateStockProduct(updatedStockProduct)
                    Toast.makeText(this, "Producto añadido correctamente", Toast.LENGTH_SHORT).show()
                } else {
                    // Si el producto ya existe, muestra un Toast de advertencia
                    Toast.makeText(this, "¡El producto ya existe en tu stock!", Toast.LENGTH_SHORT).show()
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
        val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        val start = dateFormat.parse(startDate)
        val end = dateFormat.parse(endDate)
        if (start != null && end != null) {
            val diffInMillis = end.time - start.time
            return TimeUnit.DAYS.convert(diffInMillis, TimeUnit.MILLISECONDS)
        }
        return 0
    }

    private fun changeCategorySelected(){
        selectedCategory =chooseCategoryAdapter.publicCategoriesList[chooseCategoryAdapter.selectedItemPosition].id
    }


}