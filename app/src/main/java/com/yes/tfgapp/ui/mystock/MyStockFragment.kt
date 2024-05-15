package com.yes.tfgapp.ui.mystock

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation.AnimationListener
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import com.journeyapps.barcodescanner.ScanContract
import com.journeyapps.barcodescanner.ScanIntentResult
import com.journeyapps.barcodescanner.ScanOptions
import com.yes.tfgapp.R
import com.yes.tfgapp.ui.home.MainActivity
import com.yes.tfgapp.databinding.FragmentMyStockBinding
import java.util.jar.Manifest


class MyStockFragment : Fragment() {
    //hola

    private lateinit var binding: FragmentMyStockBinding

    private var rotate = false

    private lateinit var btnScanBarcode: View
    private lateinit var btnManualAdd: View
    private lateinit var scanBarcode: View
    private lateinit var manualAdd: View

    private lateinit var btnNewStockProduct: View

    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) {
                showCamera()
            } else {
                Toast.makeText(context, "Permiso denegado", Toast.LENGTH_SHORT).show()
            }
        }


    private val scanLauncher =
        registerForActivityResult(ScanContract()) { result: ScanIntentResult ->
            run {
                if (result.contents == null) {
                    Toast.makeText(
                        context,
                        "Calcelled",
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    setResult(result.contents)
                }
            }
        }

    private fun setResult(string: String) {
        Toast.makeText(context, "El valor escaneado es: $string", Toast.LENGTH_SHORT).show()
    }


    override fun onResume() {
        super.onResume()
        val orderByOptions = resources.getStringArray(R.array.order_by_options_stock_products)
        val arrayAdapter = ArrayAdapter(requireContext(), R.layout.dropdown_item, orderByOptions)
        binding.autoCompleteTextView.setAdapter(arrayAdapter)
        (activity as MainActivity).setToolbarTitle("Mi stock")
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMyStockBinding.inflate(inflater, container, false)
        initUI()
        initListeners()
        return binding.root
    }

    private fun initUI() {
        scanBarcode = binding.llScanBarcode
        manualAdd = binding.llManualAdd
        btnScanBarcode = binding.fabScanBarcode
        btnManualAdd = binding.fabManualAdd
        btnNewStockProduct = binding.efNewStockItem
        initShowOut(scanBarcode)
        initShowOut(manualAdd)

    }

    private fun initListeners() {
        btnScanBarcode.setOnClickListener {
            checkPermissionCamera()
            //requestCameraPermission()
        }

        btnManualAdd.setOnClickListener {
            Toast.makeText(context, "Manual add", Toast.LENGTH_SHORT).show()
        }

        btnNewStockProduct.setOnClickListener {
            toogleFabMode(it)
        }
    }

    private fun requestCameraPermission() {
        requestPermissionLauncher.launch(android.Manifest.permission.CAMERA)
    }

    private fun checkPermissionCamera() {
        if (ContextCompat.checkSelfPermission(
                requireContext(),
                android.Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            showCamera()
        } else if (shouldShowRequestPermissionRationale(android.Manifest.permission.CAMERA)) {
            Toast.makeText(
                context,
                "Se necesita permiso para acceder a la cámara",
                Toast.LENGTH_SHORT
            ).show()
        } else {
            requestPermissionLauncher.launch(android.Manifest.permission.CAMERA)

        }

    }

    private fun showCamera() {

        val options = ScanOptions()
        options.setDesiredBarcodeFormats(ScanOptions.ALL_CODE_TYPES)
        options.setPrompt("Escanea el código de barras")
        options.setCameraId(0)
        options.setBeepEnabled(false)
        options.setBarcodeImageEnabled(true)
        options.setOrientationLocked(false)

        scanLauncher.launch(options)
    }

    private fun toogleFabMode(v: View) {
        rotate = rotateFab(v, !rotate)
        if (rotate) {
            showIn(scanBarcode)
            showIn(manualAdd)
        } else {
            showOut(scanBarcode)
            showOut(manualAdd)
        }
    }

    private fun showIn(view: View) {
        view.apply {
            visibility = View.VISIBLE
            translationY = height.toFloat()
            alpha = 0f
            animate()
                .setDuration(200)
                .translationY(0f)
                .setListener(object : AnimatorListenerAdapter() {})
                .alpha(1f)
                .start()
        }
    }

    private fun showOut(view: View) {
        view.apply {
            visibility = View.VISIBLE
            translationY = 0f
            alpha = 1f
            animate()
                .setDuration(200)
                .translationY(height.toFloat())
                .setListener(object : AnimatorListenerAdapter() {
                    override fun onAnimationEnd(animation: Animator) {
                        visibility = View.GONE
                        super.onAnimationEnd(animation)
                    }
                })
                .alpha(1f)
                .start()
        }
    }

    private fun initShowOut(v: View) {
        v.apply {
            visibility = View.GONE
            translationY = height.toFloat()
            alpha = 0f

        }
    }


    private fun rotateFab(v: View, rotate: Boolean): Boolean {
        v.animate()
            .setDuration(200)
            .setListener(object : AnimatorListenerAdapter() {})
            .rotation(if (rotate) 180f else 0f)
        return rotate
    }


}