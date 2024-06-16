package com.yes.tfgapp.ui.nystockproductdetail

import android.annotation.SuppressLint
import android.app.Dialog
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.NumberPicker

import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import com.squareup.picasso.Picasso
import com.yes.tfgapp.R
import com.yes.tfgapp.databinding.ActivityMyStockProductDetailBinding
import com.yes.tfgapp.domain.model.StockProductModel
import com.yes.tfgapp.ui.mystock.MyStockViewModel
import com.yes.tfgapp.ui.mystockproductsmanual.adapter.ChooseCategoryAdapterManual
import com.yes.tfgapp.ui.shoppinglistadditems.ShoppingListAddItemsViewModel
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import java.util.concurrent.TimeUnit

class MyStockProductDetailActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMyStockProductDetailBinding
    private lateinit var stockProduct: StockProductModel
    private var chooseCategoryAdapter: ChooseCategoryAdapterManual = ChooseCategoryAdapterManual(
        onChangeCategory = { changeCategorySelected() }
    )
    private lateinit var mShoppingListAddItemsViewModel: ShoppingListAddItemsViewModel
    private lateinit var btnCloseDialog: View
    private lateinit var mStockViewModel: MyStockViewModel
    private var selectedCategory: Int = 14

    private var expireData: String = "__ / __ / ____"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMyStockProductDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initUI()
        initListeners()
    }


    /*getCategoryById(stockProduct.categoryId){category ->
        binding.ivCategoryIcon.setImageResource(category!!.icon)
        binding.tvCategoryName.text = category.name
    }*/

    @SuppressLint("SetTextI18n")
    private fun initUI() {
        setupExpandableRows()

        stockProduct = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getParcelableExtra("currentStockProduct", StockProductModel::class.java)!!
        } else {
            @Suppress("DEPRECATION")
            intent.getParcelableExtra("currentStockProduct")!!
        }
        mStockViewModel = ViewModelProvider(this)[MyStockViewModel::class.java]
        mShoppingListAddItemsViewModel =
            ViewModelProvider(this)[ShoppingListAddItemsViewModel::class.java]

        btnCloseDialog = binding.ibBackArrow
        if (stockProduct.isScanned) {
            binding.tvProductId.text = stockProduct.name + " - " + stockProduct.quantity
        } else {
            binding.tvProductId.text = stockProduct.name
            binding.llProductInfo.visibility = View.GONE
            binding.llNutritionalInfo.visibility = View.GONE
            binding.llIngredients.visibility = View.GONE
            binding.divider3.visibility = View.GONE
            binding.divider4.visibility = View.GONE
            binding.divider5.visibility = View.GONE
            binding.divider6.visibility = View.GONE

        }

        if (stockProduct.ingredientsTextEs != "" && stockProduct.ingredientsTextEs != null) {
            binding.tvIngredientsText.text = stockProduct.ingredientsTextEs
        } else if (stockProduct.ingredientsText != "" && stockProduct.ingredientsText != null) {
            binding.tvIngredientsText.text = stockProduct.ingredientsText
        } else {
            binding.tvIngredientsText.text = "No hay información disponible"
        }



        binding.tvBarCode.text = stockProduct.id

        binding.tvCuantity.text = replaceIfNullOrEmpty(stockProduct.quantity)
        binding.tvServingSize.text = replaceIfNullOrEmpty(stockProduct.servingSize)
        binding.tvPerServingSize.text = replaceIfNullOrEmpty(stockProduct.servingSize)
        binding.tvBrands.text = replaceIfNullOrEmpty(stockProduct.brands)
        binding.tvGeneralName.text = replaceIfNullOrEmpty(stockProduct.genericNameEs)


        binding.tvCalories.text = formatNutrientValue(stockProduct.calories)
        binding.tvGrasas.text = formatNutrientValue(stockProduct.fat)
        binding.tvSatFat.text = formatNutrientValue(stockProduct.saturatedFat)
        binding.tvCarbohydrates.text = formatNutrientValue(stockProduct.carbohydrates)
        binding.tvSalt.text = formatNutrientValue(stockProduct.salt)
        binding.tvProteins.text = formatNutrientValue(stockProduct.proteins)

        binding.tvCalories100g.text = formatNutrientValue(stockProduct.calories100g)
        binding.tvGrasas100g.text = formatNutrientValue(stockProduct.fat100g)
        binding.tvSatFat100g.text = formatNutrientValue(stockProduct.saturatedFat100g)
        binding.tvCarbohydrates100g.text = formatNutrientValue(stockProduct.carbohydrates100g)
        binding.tvSalt100g.text = formatNutrientValue(stockProduct.salt100g)
        binding.tvProteins100g.text = formatNutrientValue(stockProduct.proteins100g)

        when (stockProduct.nutriscoreGrade) {
            "a" -> {
                binding.ivNutriScore.setImageResource(R.drawable.nutriscore_a)
                binding.ivNutriScore2.setImageResource(R.drawable.nutriscore_a)
                binding.tvMsgNutriScore.text =
                    "¡Excelente elección! Este producto tiene un alto puntaje nutricional y es una opción muy saludable."
            }

            "b" -> {
                binding.ivNutriScore.setImageResource(R.drawable.nutriscore_b)
                binding.ivNutriScore2.setImageResource(R.drawable.nutriscore_b)
                binding.tvMsgNutriScore.text =
                    "Buena opción nutricional. Este producto es una opción bastante saludable."
            }

            "c" -> {
                binding.ivNutriScore.setImageResource(R.drawable.nutriscore_c)
                binding.ivNutriScore2.setImageResource(R.drawable.nutriscore_c)
                binding.tvMsgNutriScore.text =
                    "Moderado puntaje nutricional. Este producto está en un punto medio en términos de salud. "
            }

            "d" -> {
                binding.ivNutriScore.setImageResource(R.drawable.nutriscore_d)
                binding.ivNutriScore2.setImageResource(R.drawable.nutriscore_d)
                binding.tvMsgNutriScore.text =
                    "¡Bajo puntaje nutricional! Es posible que existan alternativas más saludables."
            }

            "e" -> {
                binding.ivNutriScore.setImageResource(R.drawable.nutriscore_e)
                binding.ivNutriScore2.setImageResource(R.drawable.nutriscore_e)
                binding.tvMsgNutriScore.text =
                    "¡Muy bajo puntaje nutricional! Es posible que existan alternativas más saludables."
            }

            else -> {
                binding.ivNutriScore2.setImageResource(R.drawable.ic_warning)
                binding.tvMsgNutriScore.text =
                    "No se ha podido calcular el puntaje nutricional de este producto."
            }
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

    private fun formatNutrientValue(unit: String?): String {
        if (unit != null) {
            return if (!unit.contains("null") && !unit.contains("-1.0")) {
                "$unit"
            } else {
                "?"
            }
        }
        return "?"
    }

    private fun replaceIfNullOrEmpty(value: String?): String {
        return value?.takeIf { it.isNotEmpty() } ?: "?"
    }


    private fun initListeners() {


        btnCloseDialog.setOnClickListener {
            finish()
        }


        /*
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

                }*/

        binding.cvChangeExpireData.setOnClickListener {
            showChangeExpireDataDialog()
        }

    }

    private fun setupExpandableRows() {
        val llExpireData = findViewById<LinearLayout>(R.id.llExpireData)
        val llExpireDataContent = findViewById<LinearLayout>(R.id.llExpireDataContent)
        val ibExpireDataArrow = binding.ibExpireDataArrow

        val llCategoria = findViewById<LinearLayout>(R.id.llCategoria)
        val llCategoriaContent = findViewById<LinearLayout>(R.id.llCategoriaContent)
        val ibCategoryArrow = binding.ibCategoryArrow

        val llProductInfo = findViewById<LinearLayout>(R.id.llProductInfo)
        val llProductInfoContent = findViewById<LinearLayout>(R.id.llProductInfoContent)
        val ibProductInfoArrow = binding.ibProductInfoArrow

        val llNutritionalInfo = findViewById<LinearLayout>(R.id.llNutritionalInfo)
        val llNutritionalInfoContent = findViewById<LinearLayout>(R.id.llNutritionalInfoContent)
        val ibNutritionalInfoArrow = binding.ibNutritionalInfoArrow

        val llIngredients = findViewById<LinearLayout>(R.id.llIngredients)
        val llIngredientsContent = findViewById<LinearLayout>(R.id.llIngredientsContent)
        val ibIngredientsArrow = binding.ibIngredientsArrow

        llExpireData.setOnClickListener {
            toggleVisibility(llExpireDataContent, ibExpireDataArrow)
        }
        ibExpireDataArrow.setOnClickListener {
            toggleVisibility(llExpireDataContent, ibExpireDataArrow)
        }

        llCategoria.setOnClickListener {
            toggleVisibility(llCategoriaContent, ibCategoryArrow)
        }
        ibCategoryArrow.setOnClickListener {
            toggleVisibility(llCategoriaContent, ibCategoryArrow)
        }

        llProductInfo.setOnClickListener {
            toggleVisibility(llProductInfoContent, ibProductInfoArrow)
        }
        ibProductInfoArrow.setOnClickListener {
            toggleVisibility(llProductInfoContent, ibProductInfoArrow)
        }

        llNutritionalInfo.setOnClickListener {
            toggleVisibility(llNutritionalInfoContent, ibNutritionalInfoArrow)
        }
        ibNutritionalInfoArrow.setOnClickListener {
            toggleVisibility(llNutritionalInfoContent, ibNutritionalInfoArrow)
        }

        llIngredients.setOnClickListener {
            toggleVisibility(llIngredientsContent, ibIngredientsArrow)
        }
        ibIngredientsArrow.setOnClickListener {
            toggleVisibility(llIngredientsContent, ibIngredientsArrow)
        }

    }

    private fun toggleVisibility(view: View, arrow: ImageButton?) {
        if (view.visibility == View.GONE) {
            view.visibility = View.VISIBLE
            arrow?.setImageResource(R.drawable.ic_drop_down_arrow) // Cambia el icono a ic_dropdown
        } else {
            view.visibility = View.GONE
            arrow?.setImageResource(R.drawable.ic_right_arrow) // Cambia el icono a ic_right_arrow
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
            updateStockProductExpireData()

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


    private fun updateStockProductExpireData() {
        val stockProduct = this.stockProduct
        val daysToExpire = daysBetweenDates(stockProduct.addedDate, expireData).toInt()
        val updatedStockProduct = stockProduct.copy(
            expirationDate = expireData,
            daysToExpire = daysToExpire,
            categoryId = selectedCategory
        )
        mStockViewModel.updateStockProduct(updatedStockProduct)
    }

    private fun updateStockProductCategory() {
        val stockProduct = this.stockProduct
        val updatedStockProduct = stockProduct.copy(
            categoryId = selectedCategory
        )
        mStockViewModel.updateStockProduct(updatedStockProduct)
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
        updateStockProductCategory()
    }
}