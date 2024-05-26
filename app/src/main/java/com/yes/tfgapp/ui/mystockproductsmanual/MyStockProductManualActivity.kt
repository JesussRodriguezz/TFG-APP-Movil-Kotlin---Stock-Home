package com.yes.tfgapp.ui.mystockproductsmanual

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.yes.tfgapp.R
import com.yes.tfgapp.databinding.ActivityMyStockProductManualBinding
import com.yes.tfgapp.domain.model.StockProductModel
import com.yes.tfgapp.ui.mystock.MyStockViewModel
import java.io.File
import java.util.UUID
import kotlin.properties.Delegates

class MyStockProductManualActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMyStockProductManualBinding
    private lateinit var mStockViewModel: MyStockViewModel

    private val icons = listOf(
        R.drawable.ic_bebidas, R.drawable.ic_carnes, R.drawable.ic_congelados, R.drawable.ic_desayuno,
        R.drawable.ic_dys, R.drawable.ic_gyp, R.drawable.ic_desayuno, R.drawable.ic_carnes
    )
    private var isPhotoSelected: Boolean = false
    private var selectedIconResource: Int? = null

    private var selectedExpireDateButton: Button? = null
    private lateinit var imageUrl: Uri
    private lateinit var captureIV: ImageView
    private val contract = registerForActivityResult(ActivityResultContracts.TakePicture()) { success ->
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
        imageUrl = createImageUri()
        captureIV= binding.ivAddPhoto
        mStockViewModel = ViewModelProvider(this).get(MyStockViewModel::class.java)
    }

    private fun createImageUri(): Uri {
        val imageDir = File(filesDir, "camera_photos")
        if (!imageDir.exists()) {
            imageDir.mkdirs()
        }
        val imageFile = File(imageDir, "${UUID.randomUUID()}")
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
        captureIV.setOnClickListener {
            contract.launch(imageUrl)
        }

        binding.ivAddIcon.setOnClickListener {
            showIconDialog()
        }
        binding.efAddStockItem.setOnClickListener {
            addStockProduct()
            finish()
        }
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

            val stockProduct = if (isPhotoSelected) {
                StockProductModel(
                    id = UUID.randomUUID().toString(),
                    name = name,
                    image = imageUrl.toString()
                )
            } else {
                StockProductModel(
                    id = UUID.randomUUID().toString(),
                    name = name,
                    icon = selectedIconResource
                )
            }

            // Aquí puedes manejar el objeto stockProduct, por ejemplo, agregarlo a una lista o enviarlo a otra actividad
            Toast.makeText(this, "Producto agregado: $name", Toast.LENGTH_SHORT).show()
            mStockViewModel.addProduct(stockProduct)

        }




    private fun showIconDialog() {
        val dialog = SelectIconDialogFragment(icons) { selectedIcon ->
            binding.ivAddIcon.setImageResource(selectedIcon)
            binding.ivAddIcon.setBackgroundResource(R.drawable.image_border)
            binding.ivAddPhoto.setBackgroundResource(0)
            isPhotoSelected = false
            selectedIconResource = selectedIcon
        }
        dialog.show(supportFragmentManager, "SelectIconDialogFragment")
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



}