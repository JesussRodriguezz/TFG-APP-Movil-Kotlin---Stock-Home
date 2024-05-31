package com.yes.tfgapp.ui.mystockproductsmanual


import android.content.pm.PackageManager
import androidx.activity.result.contract.ActivityResultContracts.RequestPermission
import androidx.core.app.ActivityCompat
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import com.yes.tfgapp.R
import com.yes.tfgapp.databinding.ActivityMyStockProductManualBinding
import com.yes.tfgapp.domain.fixed.FixedCategories
import com.yes.tfgapp.domain.model.StockProductModel
import com.yes.tfgapp.ui.mystock.MyStockViewModel
import com.yes.tfgapp.ui.mystockproductsmanual.adapter.ChooseCategoryAdapterManual
import com.yes.tfgapp.ui.searchproducts.adapter.ChooseCategoryAdapter
import com.yes.tfgapp.ui.shoppinglistadditems.ShoppingListAddItemsViewModel
import java.io.File
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
        onChangeCategory= {changeCategorySelected()}
    )
    private lateinit var mShoppingListAddItemsViewModel: ShoppingListAddItemsViewModel
    private var selectedCategory: Int = 14

    private val icons = listOf(
        R.drawable.ic_fyv,
        R.drawable.ic_carnes,
        R.drawable.ic_pescados,
        R.drawable.ic_latas,
        R.drawable.ic_lacteos,
        R.drawable.ic_panaderia,
        R.drawable.ic_bebidas,
        R.drawable.ic_desayuno,
        R.drawable.ic_eys,
        R.drawable.ic_congelados,
        R.drawable.ic_gyp,
        R.drawable.ic_dys,
        R.drawable.ic_others_category,
    )
    private var isPhotoSelected: Boolean = false
    private var selectedIconResource: Int? = null

    private var selectedExpireDateButton: Button? = null
    private lateinit var imageUrl: Uri
    private lateinit var captureIV: ImageView

    private val cameraPermissionLauncher =
        registerForActivityResult(RequestPermission()) { isGranted ->
            if (isGranted) {
                launchCamera()
            } else {
                Toast.makeText(this, "Permiso de cámara denegado", Toast.LENGTH_SHORT).show()
            }
        }

    private val storagePermissionLauncher =
        registerForActivityResult(RequestPermission()) { isGranted ->
            if (isGranted) {
                captureIV.performClick()
            } else {
                Toast.makeText(this, "Permiso de almacenamiento denegado", Toast.LENGTH_SHORT)
                    .show()
            }
        }

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
            chooseCategoryAdapter.notifyDataSetChanged()
        }


        /*val rvCategories = dialog.findViewById<RecyclerView>(R.id.rvCategories)
        rvCategories.layoutManager = GridLayoutManager(requireContext(), 2, GridLayoutManager.HORIZONTAL, false)
        rvCategories.adapter = chooseCategoryAdapter
        mShoppingListAddItemsViewModel.readAllDataCategory.observe(viewLifecycleOwner) { categories ->
            chooseCategoryAdapter.setCategoriesListModified(categories, 14)
            chooseCategoryAdapter.notifyDataSetChanged()
        }*/
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
            updateButtonStates(
                binding.btn1Week,
                listOf(binding.btn2Weeks, binding.btn1Month, binding.btn2Months)
            )
        }

        binding.btn2Weeks.setOnClickListener {
            updateButtonStates(
                binding.btn2Weeks,
                listOf(binding.btn1Week, binding.btn1Month, binding.btn2Months)
            )
        }

        binding.btn1Month.setOnClickListener {
            updateButtonStates(
                binding.btn1Month,
                listOf(binding.btn1Week, binding.btn2Weeks, binding.btn2Months)
            )
        }

        binding.btn2Months.setOnClickListener {
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
        val expirationDate = when (selectedExpireDateButton) {
            binding.btn1Week -> addDaysToDate(getCurrentDate(), 7)
            binding.btn2Weeks -> addDaysToDate(getCurrentDate(), 14)
            binding.btn1Month -> addDaysToDate(getCurrentDate(), 30)
            binding.btn2Months -> addDaysToDate(getCurrentDate(), 60)
            else -> getCurrentDate()
        }
        val daysToExpire = daysBetweenDates(getCurrentDate(), expirationDate).toInt()

        val stockProduct = if (isPhotoSelected) {
            StockProductModel(
                id = UUID.randomUUID().toString(),
                name = name,
                image = imageUrl.toString(),
                expirationDate = expirationDate,
                daysToExpire = daysToExpire,
                categoryId = selectedCategory
            )
        } else {
            StockProductModel(
                id = UUID.randomUUID().toString(),
                name = name,
                icon = selectedIconResource,
                expirationDate = expirationDate,
                daysToExpire = daysToExpire,
                categoryId = selectedCategory
            )
        }

        Toast.makeText(this, "Producto agregado: $name", Toast.LENGTH_SHORT).show()
        mStockViewModel.addProduct(stockProduct)
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
        val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        val start = dateFormat.parse(startDate)
        val end = dateFormat.parse(endDate)
        if (start != null && end != null) {
            val diffInMillis = end.time - start.time
            return TimeUnit.DAYS.convert(diffInMillis, TimeUnit.MILLISECONDS)
        }
        return 0
    }

    private fun getCurrentDate(): String {
        val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        return dateFormat.format(Date())
    }

    private fun showIconDialog() {
        val dialog = SelectIconDialogFragment(icons) { selectedIcon ->
            binding.ivAddIcon.setImageResource(selectedIcon)
            binding.ivAddIcon.setBackgroundResource(R.drawable.image_border)
            binding.ivAddPhoto.setBackgroundResource(0)
            isPhotoSelected = false
            selectedIconResource = selectedIcon
            val categoryId = FixedCategories.getCategoryIdByIcon(selectedIcon)
            val categoryName = FixedCategories.getCategoryNameById(categoryId)

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
        binding.tvChangeCategory.visibility = View.VISIBLE
    }

    private fun changeCategorySelected(){
        selectedCategory =chooseCategoryAdapter.publicCategoriesList[chooseCategoryAdapter.selectedItemPosition].id
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
