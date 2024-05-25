package com.yes.tfgapp.ui.mystock

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.content.ContentValues
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.journeyapps.barcodescanner.ScanContract
import com.journeyapps.barcodescanner.ScanIntentResult
import com.journeyapps.barcodescanner.ScanOptions
import com.yes.tfgapp.R
import com.yes.tfgapp.data.network.ProductsApiService
import com.yes.tfgapp.data.network.response.StockProductResponse
import com.yes.tfgapp.data.worker.MyWorker
import com.yes.tfgapp.ui.home.MainActivity
import com.yes.tfgapp.databinding.FragmentMyStockBinding
import com.yes.tfgapp.domain.model.StockProductModel
import com.yes.tfgapp.ui.mystock.adapter.StockProductAdapter
import com.yes.tfgapp.ui.mystockproductscan.MyStockProductDetailFragment
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.Calendar
import java.util.concurrent.TimeUnit


class MyStockFragment : Fragment() {

    private lateinit var binding: FragmentMyStockBinding
    private var currentDialog: MyStockProductDetailFragment? = null
    private var rotate = false

    private lateinit var btnScanBarcode: View
    private lateinit var btnManualAdd: View
    private lateinit var scanBarcode: View
    private lateinit var manualAdd: View
    private lateinit var btnNewStockProduct: View

    private lateinit var retrofit: Retrofit

    private val stockProductAdapter = StockProductAdapter(
        onClickDelete = { onClickDeleteStockProduct(it) }
    )
    private lateinit var mStockViewModel: MyStockViewModel

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
        getProductApi(string)
    }

    override fun onResume() {
        super.onResume()
        val orderByOptions = resources.getStringArray(R.array.order_by_options_stock_products)
        val arrayAdapter = ArrayAdapter(requireContext(), R.layout.dropdown_item, orderByOptions)
        binding.autoCompleteTextView.setAdapter(arrayAdapter)
        manualAdd.isVisible = false
        scanBarcode.isVisible = false
        (activity as MainActivity).showBottomNavInsta()
        (activity as MainActivity).showToolbar()
        rotate = false
        (activity as MainActivity).setToolbarTitle("Mi stock")
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMyStockBinding.inflate(inflater, container, false)
        initUI()
        retrofit = getRetrofit()
        initListeners()
        scheduleInitialWork()
        return binding.root
    }

    private fun scheduleInitialWork() {
        val currentDate= Calendar.getInstance()
        val nextRun = Calendar.getInstance()

        nextRun.set(Calendar.HOUR_OF_DAY, 0)
        nextRun.set(Calendar.MINUTE, 5)
        nextRun.set(Calendar.SECOND, 0)
        nextRun.set(Calendar.MILLISECOND, 0)
        nextRun.add(Calendar.DAY_OF_YEAR, 1)

        val initialDelay = nextRun.timeInMillis - currentDate.timeInMillis
        Log.d(ContentValues.TAG, "Initial delay: $initialDelay")

        val workRequest =OneTimeWorkRequestBuilder<MyWorker>()
            .setInitialDelay(10, TimeUnit.SECONDS)
            .build()

        //val workRequest =OneTimeWorkRequestBuilder<MyWorker>()
        //    .setInitialDelay(initialDelay.toLong(), TimeUnit.MILLISECONDS)
        //    .build()

        WorkManager.getInstance(requireContext()).enqueue(workRequest)
    }


    private fun getProductApi(productCode: String) {
        CoroutineScope(Dispatchers.IO).launch {
            val myResponse: Response<StockProductResponse> =
                retrofit.create(ProductsApiService::class.java).getProduct(productCode)
            if (myResponse.isSuccessful) {
                val response: StockProductResponse? = myResponse.body()
                if (response != null) {
                    withContext(Dispatchers.Main) {
                        openProductDetailDialog(response)
                    }
                } else {
                    withContext(Dispatchers.Main) {
                        openProductDetailDialog(null)
                    }
                }
                Log.i("yes", "funciona :)")
            } else {
                Log.i("yes", "No funciona")
            }
        }
    }

    private fun openProductDetailDialog(product: StockProductResponse?) {
        currentDialog?.dismiss()
        val stockProduct = if (product != null) {
            StockProductModel(
                id = product.code,
                name = product.product.productName,
                image = product.product.productImage
            )
        } else {
            StockProductModel(
                id = "error",
                name = "Error: Producto no encontrado",
                image = "" // Puedes usar una imagen predeterminada o dejarlo vacío
            )
        }

        val dialogFragment = MyStockProductDetailFragment()
        dialogFragment.arguments = Bundle().apply {
            putParcelable("currentStockProduct", stockProduct)
        }
        (activity as MainActivity).hideBottomNav()
        (activity as MainActivity).hideToolbar()
        currentDialog = dialogFragment
        dialogFragment.show(childFragmentManager, "MyStockProductDetailFragment")

    }


    private fun initUI() {

        val rvStockProduct = binding.rvMyStock
        rvStockProduct.adapter = stockProductAdapter
        val layoutManager = GridLayoutManager(requireContext(), 2)

        rvStockProduct.layoutManager = layoutManager
        mStockViewModel = ViewModelProvider(this).get(MyStockViewModel::class.java)
        mStockViewModel.readAllData.observe(viewLifecycleOwner) { stockProduct ->
            stockProductAdapter.setData(stockProduct)
        }

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
            //show
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

    fun showCamera() {

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

    private fun onClickDeleteStockProduct(stockProduct: StockProductModel) {
        mStockViewModel.deleteStockProduct(stockProduct)
    }

    private fun getRetrofit(): Retrofit {
        val okHttpClient = OkHttpClient.Builder()
            .connectTimeout(30, TimeUnit.SECONDS) // Tiempo de espera para la conexión.
            .readTimeout(30, TimeUnit.SECONDS) // Tiempo de espera para la lectura de datos.
            .writeTimeout(30, TimeUnit.SECONDS) // Tiempo de espera para la escritura de datos.
            .build()

        return Retrofit
            .Builder()
            .baseUrl("https://es.openfoodfacts.org/")
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }


}