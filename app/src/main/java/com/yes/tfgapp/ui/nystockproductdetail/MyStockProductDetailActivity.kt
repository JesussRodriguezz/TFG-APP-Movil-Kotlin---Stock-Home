package com.yes.tfgapp.ui.nystockproductdetail

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
import androidx.constraintlayout.widget.ConstraintSet

import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import com.squareup.picasso.Picasso
import com.yes.tfgapp.R
import com.yes.tfgapp.databinding.ActivityMyStockProductDetailBinding
import com.yes.tfgapp.databinding.ActivityMyStockProductScanBinding
import com.yes.tfgapp.domain.model.CategoryModel
import com.yes.tfgapp.domain.model.StockProductModel
import com.yes.tfgapp.ui.mystock.MyStockViewModel
import com.yes.tfgapp.ui.mystockproductsmanual.adapter.ChooseCategoryAdapterManual
import com.yes.tfgapp.ui.shoppinglistadditems.ShoppingListAddItemsViewModel
import kotlinx.coroutines.launch
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.concurrent.TimeUnit

class MyStockProductDetailActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMyStockProductDetailBinding
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
        binding = ActivityMyStockProductDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initUI()
        initListeners()
    }

    private fun initUI() {

        stockProduct = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getParcelableExtra("currentStockProduct", StockProductModel::class.java)!!
        } else {
            @Suppress("DEPRECATION")
            intent.getParcelableExtra<StockProductModel>("currentStockProduct")!!
        }
        mStockViewModel = ViewModelProvider(this)[MyStockViewModel::class.java]
        mShoppingListAddItemsViewModel =
            ViewModelProvider(this)[ShoppingListAddItemsViewModel::class.java]

        btnAddStockProduct = binding.efAddStockItem
        btnCloseDialog = binding.ibBackArrow
        binding.tvProductId.text = stockProduct.name
       /* binding.tvNutriScoreGrade.text = stockProduct.nutriscoreGrade
        binding.tvNutriScoreScore.text = stockProduct.nutriscoreScore.toString()
        if (stockProduct.ingredientsTextEs != "") {
            binding.tvIngredientsText.text = stockProduct.ingredientsTextEs
        } else {
            binding.tvIngredientsText.text = stockProduct.ingredientsText
        }*/

        getCategoryById(stockProduct.categoryId){category ->
            binding.ivCategoryIcon.setImageResource(category!!.icon)
            binding.tvCategoryName.text = category.name
        }

        if (stockProduct.image != null) {
            Picasso.get().load(stockProduct.image).into(binding.ivProductApiSearchImage)
        } else if (stockProduct.icon != null) {
            binding.ivProductApiSearchImage.setImageResource(stockProduct.icon!!)
        } else {
            binding.ivProductApiSearchImage.setImageResource(R.drawable.ic_others_category) // Imagen por defecto si no hay imagen ni icono
        }

        binding.tvChangeExpireData.text = stockProduct.expirationDate
        expireData = stockProduct.expirationDate

        val rvCategories = binding.rvCategories
        rvCategories.layoutManager = GridLayoutManager(this, 3, GridLayoutManager.HORIZONTAL, false)
        rvCategories.adapter = chooseCategoryAdapter
        mShoppingListAddItemsViewModel.readAllDataCategory.observe(this) { categories ->
            chooseCategoryAdapter.setCategoriesListModified(categories, stockProduct.categoryId)
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



        binding.cvCategory.setOnClickListener {
            binding.rvCategories.visibility = View.VISIBLE
            binding.llCategorySuggested.visibility = View.GONE
            binding.divider3.visibility = View.GONE
            binding.divider4.visibility = View.VISIBLE
            binding.ibDropUp.visibility = View.VISIBLE

            val constraintSet = ConstraintSet()
            constraintSet.clone(binding.constraintLayout)

            // Cambia la restricción de layout_constraintTop_toBottomOf de llNutrients
            constraintSet.connect(
                R.id.llNutrients, // ID de la vista cuyo constraint se quiere cambiar
                ConstraintSet.TOP, // Lado al que queremos conectar
                R.id.divider4, // ID de la vista a la que queremos conectar
                ConstraintSet.BOTTOM // Lado de la vista a la que queremos conectar
            )

            // Aplica las nuevas restricciones al ConstraintLayout
            constraintSet.applyTo(binding.constraintLayout)

        }
        binding.ibDropUp.setOnClickListener{
            binding.rvCategories.visibility = View.GONE
            binding.llCategorySuggested.visibility = View.VISIBLE
            binding.divider3.visibility = View.VISIBLE
            binding.divider4.visibility = View.GONE
            binding.ibDropUp.visibility = View.GONE

            val constraintSet = ConstraintSet()
            constraintSet.clone(binding.constraintLayout)

            // Establece la restricción de layout_constraintTop_toBottomOf de llNutrients
            constraintSet.connect(
                R.id.llNutrients, // ID de la vista cuyo constraint se quiere cambiar
                ConstraintSet.TOP, // Lado al que queremos conectar
                R.id.divider3, // ID de la vista a la que queremos conectar
                ConstraintSet.BOTTOM, // Lado de la vista a la que queremos conectar
                0 // Margen
            )

            // Aplica las nuevas restricciones al ConstraintLayout
            constraintSet.applyTo(binding.constraintLayout)

        }

        binding.cvChangeExpireData.setOnClickListener {
            showChangeExpireDataDialog()
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
            // Set NumberPickers to the current date
            val currentDate = Calendar.getInstance()
            npDay.value = currentDate.get(Calendar.DAY_OF_MONTH)
            npMonth.value = currentDate.get(Calendar.MONTH) + 1 // Months are 0-based
            npYear.value = currentDate.get(Calendar.YEAR)
        } else {
            // Parse expireData
            val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            try {
                val expireDate = dateFormat.parse(expireData)
                expireDate?.let {
                    val calendar = Calendar.getInstance()
                    calendar.time = it

                    npDay.value = calendar.get(Calendar.DAY_OF_MONTH)
                    npMonth.value = calendar.get(Calendar.MONTH) + 1 // Months are 0-based
                    npYear.value = calendar.get(Calendar.YEAR)
                }
            } catch (e: ParseException) {
                e.printStackTrace()
                // Set NumberPickers to the current date if parsing fails
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

    private fun getCategoryById(id: Int, callback: (CategoryModel?) -> Unit) {
        lifecycleScope.launch {
            try {
                val category = mShoppingListAddItemsViewModel.getCategoryById(id)
                callback(category)  // Pasamos el resultado al callback
            } catch (e: Exception) {
                callback(null)  // En caso de error, podríamos pasar null o manejar el error de otra forma
            }
        }
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
                    // Si el producto se añadió correctamente, actualiza y muestra un Toast
                    mStockViewModel.updateStockProduct(updatedStockProduct)
                    Toast.makeText(this, "Producto añadido correctamente", Toast.LENGTH_SHORT)
                        .show()
                } else {
                    // Si el producto ya existe, muestra un Toast de advertencia
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