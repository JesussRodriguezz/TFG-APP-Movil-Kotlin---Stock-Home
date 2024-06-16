package com.yes.tfgapp.ui.mystockproductsmanual


import android.app.Dialog
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.NumberPicker
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import com.yes.tfgapp.R
import com.yes.tfgapp.databinding.ActivityMyStockProductManualBinding
import com.yes.tfgapp.domain.model.CategoryModel
import com.yes.tfgapp.domain.model.StockProductModel
import com.yes.tfgapp.ui.mystock.MyStockViewModel
import com.yes.tfgapp.ui.mystockproductsmanual.adapter.ChooseCategoryAdapterManual
import com.yes.tfgapp.ui.shoppinglistadditems.ShoppingListAddItemsViewModel
import java.io.File
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.UUID
import java.util.concurrent.TimeUnit

class MyStockProductManualActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMyStockProductManualBinding
    private lateinit var mStockViewModel: MyStockViewModel
    private var chooseCategoryAdapter: ChooseCategoryAdapterManual = ChooseCategoryAdapterManual(
        onChangeCategory = { changeCategorySelected() }
    )
    private lateinit var mShoppingListAddItemsViewModel: ShoppingListAddItemsViewModel
    private var selectedCategory: Int = 14

    private var isPhotoSelected: Boolean = false
    private var selectedIconResource: Int? = null

    private var selectedExpireDateButton: Button? = null
    private lateinit var imageUrl: Uri
    private lateinit var captureIV: ImageView

    private var expireData :String = "__ / __ / ____"
    private lateinit var iconsCategories : List<Int>
    private lateinit var iconToCategoryMap: Map<Int, CategoryModel>


    private val contract =
        registerForActivityResult(ActivityResultContracts.TakePicture()) { success ->
            if (success) {
                captureIV.setImageURI(null)
                captureIV.setImageURI(imageUrl)
                binding.flAddPhoto.setBackgroundResource(R.drawable.image_border)
                binding.flAddPhoto.setPadding(10, 10, 10, 10)
                captureIV.setPadding(0, 0, 0, 0)
                binding.ivAddIcon.setBackgroundResource(R.color.primaryGrey)
                isPhotoSelected = true
                selectedIconResource = null
            } else {
                Toast.makeText(this, "Error al tomar la foto", Toast.LENGTH_SHORT).show()
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMyStockProductManualBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initUI()
        initListeners()
    }


    private fun initUI() {


        binding.ivCategoryIcon.setImageResource(R.drawable.ic_others_category)
        binding.tvCategoryName.text = "Otros"

        binding.tvChangeExpireData.text = expireData

        imageUrl = createImageUri()
        captureIV = binding.ivAddPhoto
        mStockViewModel = ViewModelProvider(this)[MyStockViewModel::class.java]

        mShoppingListAddItemsViewModel =
            ViewModelProvider(this)[ShoppingListAddItemsViewModel::class.java]


        val rvCategories = binding.rvCategories
        rvCategories.layoutManager = GridLayoutManager(this, 3, GridLayoutManager.HORIZONTAL, false)
        rvCategories.adapter = chooseCategoryAdapter
        mShoppingListAddItemsViewModel.readAllDataCategory.observe(this) { categories ->
            chooseCategoryAdapter.setCategoriesListModified(categories, 14)
            iconsCategories = categories.map { it.icon }
            iconToCategoryMap = categories.associateBy { it.icon }
            chooseCategoryAdapter.notifyDataSetChanged()
        }

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
            println("expireData: $expireData")

            val formattedDate = formatDate(day, month, year)

            binding.tvChangeExpireData.text = formattedDate

            dialog.dismiss()
        }

        dialog.findViewById<Button>(R.id.btnCancel).setOnClickListener {
            println("CANCELAR")
            dialog.dismiss()
        }
    }


    private fun createImageUri(): Uri {
        val imageDir = File(filesDir, "camera_photos")
        if (!imageDir.exists()) {
            imageDir.mkdirs()
        }
        val imageFile = File(imageDir, "${UUID.randomUUID()}.jpg")
        return FileProvider.getUriForFile(
            this,
            "com.yes.tfgapp.ui.mystockproductsmanual.FileProvider",
            imageFile
        )
    }

    private fun initListeners() {
        binding.ibBackArrow.setOnClickListener {
            finish()
        }
        binding.btn1Week.setOnClickListener {
            expireData = addDaysToDate(getCurrentDate(), 7)
            binding.tvChangeExpireData.text = formatDate(SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).parse(expireData)!!)
            //binding.tvChangeExpireData.text = expireData
            updateButtonStates(
                binding.btn1Week,
                listOf(binding.btn2Weeks, binding.btn1Month, binding.btn2Months)
            )

        }

        binding.btn2Weeks.setOnClickListener {
            expireData = addDaysToDate(getCurrentDate(), 14)
            binding.tvChangeExpireData.text = formatDate(SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).parse(expireData)!!)
            //binding.tvChangeExpireData.text = expireData
            updateButtonStates(
                binding.btn2Weeks,
                listOf(binding.btn1Week, binding.btn1Month, binding.btn2Months)
            )
        }

        binding.btn1Month.setOnClickListener {
            expireData = addDaysToDate(getCurrentDate(), 30)
            binding.tvChangeExpireData.text = formatDate(SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).parse(expireData)!!)
            //binding.tvChangeExpireData.text = expireData
            updateButtonStates(
                binding.btn1Month,
                listOf(binding.btn1Week, binding.btn2Weeks, binding.btn2Months)
            )
        }

        binding.btn2Months.setOnClickListener {
            expireData = addDaysToDate(getCurrentDate(), 90)
            binding.tvChangeExpireData.text = formatDate(SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).parse(expireData)!!)
            //binding.tvChangeExpireData.text = expireData
            updateButtonStates(
                binding.btn2Months,
                listOf(binding.btn1Week, binding.btn1Month, binding.btn2Weeks)
            )
        }

        captureIV.setOnClickListener {
            if (checkAndRequestPermissions()) {
                launchCamera()
            }
        }

        binding.ivAddIcon.setOnClickListener {
            showIconDialog()
        }
        binding.efAddStockItem.setOnClickListener {
            addStockProduct()
        }

        binding.cvCategory.setOnClickListener {
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

        }

        binding.cvChangeExpireData.setOnClickListener {
            showChangeExpireDataDialog()
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


    private fun checkAndRequestPermissions(): Boolean {
        val cameraPermission =
            ContextCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA)
        val storagePermission = ContextCompat.checkSelfPermission(
            this,
            android.Manifest.permission.WRITE_EXTERNAL_STORAGE
        )
        val listPermissionsNeeded = mutableListOf<String>()

        if (cameraPermission != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(android.Manifest.permission.CAMERA)
        }
        if (storagePermission != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
        }

        if (listPermissionsNeeded.isNotEmpty()) {
            ActivityCompat.requestPermissions(
                this,
                listPermissionsNeeded.toTypedArray(),
                PERMISSIONS_REQUEST_CODE
            )
            return false
        }
        return true
    }

    private fun launchCamera() {
        imageUrl = createImageUri()
        contract.launch(imageUrl)
    }

    private fun addStockProduct() {
        val name = binding.etNewProductName.text.toString()
        if (name.isEmpty()) {
            Toast.makeText(
                this,
                "El nombre del producto no puede estar vacío",
                Toast.LENGTH_SHORT
            ).show()
            return
        }

        val daysToExpire = daysBetweenDates(getCurrentDate(), expireData).toInt()

        val stockProduct = if (isPhotoSelected) {
            StockProductModel(
                id = UUID.randomUUID().toString(),
                name = name,
                image = imageUrl.toString(),
                expirationDate = expireData,
                daysToExpire = daysToExpire,
                categoryId = selectedCategory
            )
        } else {
            StockProductModel(
                id = UUID.randomUUID().toString(),
                name = name,
                icon = selectedIconResource,
                expirationDate = expireData,
                daysToExpire = daysToExpire,
                categoryId = selectedCategory
            )
        }

        //Toast.makeText(this, "Producto agregado: $name", Toast.LENGTH_SHORT).show()
        //mStockViewModel.addProduct(stockProduct)

        mStockViewModel.addProductIfNotExistsSameName(stockProduct) { productAdded ->
            runOnUiThread {
                if (productAdded) {
                    Toast.makeText(this, "Producto añadido correctamente", Toast.LENGTH_SHORT)
                        .show()
                } else {
                    Toast.makeText(this, "¡El producto ya existe en tu stock!", Toast.LENGTH_SHORT)
                        .show()
                }
            }
        }
        finish()
    }

    private fun addDaysToDate(date: String, days: Int): String {
        val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        val calendar = Calendar.getInstance()
        calendar.time = dateFormat.parse(date) ?: return date
        calendar.add(Calendar.DAY_OF_YEAR, days)
        return dateFormat.format(calendar.time)
    }

    private fun daysBetweenDates(startDate: String, endDate: String): Long {
        if(expireData!="__ / __ / ____"){
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

    private fun getCurrentDate(): String {
        val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        return dateFormat.format(Date())
    }

    private fun showIconDialog() {
        val dialog = SelectIconDialogFragment(iconsCategories) { selectedIcon ->
            binding.ivAddIcon.setImageResource(selectedIcon)
            binding.ivAddIcon.setBackgroundResource(R.drawable.image_border)
            binding.ivAddPhoto.setBackgroundResource(0)
            isPhotoSelected = false
            selectedIconResource = selectedIcon

            //val categoryId = FixedCategories.getCategoryIdByIcon(selectedIcon)
            //val categoryName = FixedCategories.getCategoryNameById(categoryId)

            val categoryId = iconToCategoryMap[selectedIcon]?.id ?: 14
            val categoryName = iconToCategoryMap[selectedIcon]?.name ?: "Otros"

            selectedCategory = categoryId

            binding.ivCategoryIcon.setImageResource(selectedIcon)
            binding.tvCategoryName.text = categoryName
            changeVisibilities()

            mShoppingListAddItemsViewModel.readAllDataCategory.observe(this) { categories ->
                chooseCategoryAdapter.setCategoriesListModified(categories, categoryId)
                chooseCategoryAdapter.notifyDataSetChanged()
            }
        }
        dialog.show(supportFragmentManager, "SelectIconDialogFragment")

    }

    private fun changeVisibilities() {
        binding.rvCategories.visibility = View.GONE
        binding.llCategorySuggested.visibility = View.VISIBLE
    }

    private fun changeCategorySelected() {
        selectedCategory =
            chooseCategoryAdapter.publicCategoriesList[chooseCategoryAdapter.selectedItemPosition].id
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

    companion object {
        private const val PERMISSIONS_REQUEST_CODE = 123
    }
}
