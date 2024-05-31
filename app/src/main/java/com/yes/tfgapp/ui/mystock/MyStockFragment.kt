package com.yes.tfgapp.ui.mystock

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.app.Application
import android.app.Dialog
import android.content.ContentValues
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateDecelerateInterpolator
import android.widget.ArrayAdapter
import android.widget.LinearLayout
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.work.OneTimeWorkRequestBuilder
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
import com.yes.tfgapp.domain.model.ProductModel
import com.yes.tfgapp.domain.model.ProductShoppingListModel
import com.yes.tfgapp.domain.model.ShoppingListModel
import com.yes.tfgapp.domain.model.StockProductModel
import com.yes.tfgapp.ui.mystock.adapter.ChooseShoppingListAdapter
import com.yes.tfgapp.ui.mystock.adapter.StockProductAdapter
import com.yes.tfgapp.ui.mystockproductscan.MyStockProductDetailFragment
import com.yes.tfgapp.ui.mystockproductsmanual.MyStockProductManualActivity
import com.yes.tfgapp.ui.searchproducts.adapter.ChooseCategoryAdapter
import com.yes.tfgapp.ui.shoppinglist.ShoppingListViewModel
import com.yes.tfgapp.ui.shoppinglistadditems.ShoppingListAddItemsViewModel
import com.yes.tfgapp.ui.shoppinglistdetail.ShoppingListDetailViewModel
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

    private lateinit var chooseShoppingListAdapter: ChooseShoppingListAdapter
    private lateinit var mShoppingListViewModel: ShoppingListViewModel
    private lateinit var mShoppingListAddItemsViewModel: ShoppingListAddItemsViewModel
    private lateinit var mShoppingListDetailViewModel: ShoppingListDetailViewModel

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
        binding.orderBy.setAdapter(arrayAdapter)
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
        val currentDate = Calendar.getInstance()
        val nextRun = Calendar.getInstance()

        nextRun.set(Calendar.HOUR_OF_DAY, 0)
        nextRun.set(Calendar.MINUTE, 5)
        nextRun.set(Calendar.SECOND, 0)
        nextRun.set(Calendar.MILLISECOND, 0)
        nextRun.add(Calendar.DAY_OF_YEAR, 1)

        val initialDelay = nextRun.timeInMillis - currentDate.timeInMillis
        Log.d(ContentValues.TAG, "Initial delay: $initialDelay")

        val workRequest = OneTimeWorkRequestBuilder<MyWorker>()
            .setInitialDelay(50, TimeUnit.SECONDS)
            .build()

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
        val layoutManager = GridLayoutManager(requireContext(), 3)

        rvStockProduct.layoutManager = layoutManager
        mStockViewModel = ViewModelProvider(this)[MyStockViewModel::class.java]
        mStockViewModel.readAllData.observe(viewLifecycleOwner) { stockProduct ->
            stockProductAdapter.setData(stockProduct)
        }
        mShoppingListViewModel = ViewModelProvider(this)[ShoppingListViewModel::class.java]
        mShoppingListAddItemsViewModel =
            ViewModelProvider(this)[ShoppingListAddItemsViewModel::class.java]

        binding.orderBy.setOnItemClickListener { _, _, position, _ ->
            when (position) {
                0 -> {
                    mStockViewModel.getStockProductsOrderedByName()
                        .observe(viewLifecycleOwner) { stockProduct ->
                            stockProductAdapter.setData(stockProduct)
                        }
                }

                1 -> {
                    mStockViewModel.getStockProductsOrderedByExpiryDate()
                        .observe(viewLifecycleOwner) { stockProduct ->
                            stockProductAdapter.setData(stockProduct)
                        }
                }

                2 -> {
                    mStockViewModel.getStockProductsOrderedByAddedDate()
                        .observe(viewLifecycleOwner) { stockProduct ->
                            stockProductAdapter.setData(stockProduct)
                        }
                }
            }
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
            openProductManualFragment()
        }

        btnNewStockProduct.setOnClickListener {
            toogleFabMode(it)
        }
    }

    private fun openProductManualFragment() {
        val intent = Intent(activity, MyStockProductManualActivity::class.java)
        startActivity(intent)

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
        println("stock product cat id: ${stockProduct.categoryId}")
        showDeleteDialog(stockProduct)
        //mStockViewModel.deleteStockProduct(stockProduct)
    }

    private fun showDeleteDialog(stockProduct: StockProductModel) {
        val dialog = Dialog(requireContext())
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
        dialog.setContentView(R.layout.dialog_delete_stock_product)
        dialog.window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )

        dialog.show()

        dialog.findViewById<LinearLayout>(R.id.option1).setOnClickListener {
            checkShoppingListsAndProceed(stockProduct)
            dialog.dismiss()
        }

        dialog.findViewById<LinearLayout>(R.id.option2).setOnClickListener {
            mStockViewModel.deleteStockProduct(stockProduct)
            dialog.dismiss()
        }
    }

    private fun checkShoppingListsAndProceed(stockProduct: StockProductModel) {
        mShoppingListViewModel.readAllData.observe(viewLifecycleOwner) { shoppingLists ->
            mShoppingListViewModel.readAllData.removeObservers(viewLifecycleOwner)
            when {
                shoppingLists.isEmpty() -> {
                    Toast.makeText(requireContext(),
                        getString(R.string.doestn_exist_any_shopping_list), Toast.LENGTH_SHORT).show()
                }
                shoppingLists.size == 1 -> {
                    val product = ProductModel(
                        name = stockProduct.name,
                        categoryId = stockProduct.categoryId
                    )
                    val singleShoppingList = shoppingLists[0]
                    addExternalProductToList(product, singleShoppingList, null, stockProduct)

                }
                else -> {
                    showChooseShoppingListDialog(stockProduct, shoppingLists)
                }
            }
        }
    }

    private fun showChooseShoppingListDialog(stockProduct: StockProductModel, shoppingLists: List<ShoppingListModel>) {
        val dialog = Dialog(requireContext())
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
        dialog.setContentView(R.layout.dialog_add_product_to_shopping_list_after_delete)
        dialog.window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )

        val rvShoppingList = dialog.findViewById<RecyclerView>(R.id.rvShoppingLists)
        rvShoppingList.layoutManager = LinearLayoutManager(requireContext())

        val chooseShoppingListAdapter = ChooseShoppingListAdapter(
            stockProduct = stockProduct,
            onAddProductToList = { product, shoppingList, dialog, stockproduct ->
                addExternalProductToList(product, shoppingList, dialog, stockproduct)
            },
            dialog = dialog
        )

        rvShoppingList.adapter = chooseShoppingListAdapter
        chooseShoppingListAdapter.setShoppingLists(shoppingLists)

        dialog.show()
    }

    private fun addExternalProductToList(
        product: ProductModel,
        shoppingList: ShoppingListModel,
        dialog: Dialog?,
        stockProduct: StockProductModel
    ) {
        mShoppingListDetailViewModel = ViewModelProvider(
            this,
            ShoppingListDetailViewModelFactory(
                requireActivity().application,
                shoppingList
            )
        )[ShoppingListDetailViewModel::class.java]

        mShoppingListAddItemsViewModel.addProduct(product)
        observeProductIDAndAddToList(shoppingList, dialog, stockProduct)
    }

    private fun observeProductIDAndAddToList(
        shoppingList: ShoppingListModel,
        dialog: Dialog?,
        stockProduct: StockProductModel
    ) {
        val productIdObserver = object : Observer<Long?> {
            override fun onChanged(productId: Long?) {
                if (productId != null) {
                    val productShoppingList = ProductShoppingListModel(
                        shoppingListId = shoppingList.id,
                        productId = productId.toInt()
                    )
                    mShoppingListAddItemsViewModel.addProductToList(productShoppingList)
                    dialog?.dismiss() // Cierra el diálogo si no es nulo
                    mStockViewModel.deleteStockProduct(stockProduct)
                    // Remove the observer after the operation is done
                    mShoppingListAddItemsViewModel.productIdLiveData.removeObserver(this)
                    mShoppingListAddItemsViewModel.productIdLiveData.value = null
                    Toast.makeText(requireContext(),"Se añade el producto: ${stockProduct.name} a la lista de la compra : ${shoppingList.name}", Toast.LENGTH_SHORT).show()
                }
            }
        }
        mShoppingListAddItemsViewModel.productIdLiveData.observe(
            viewLifecycleOwner,
            productIdObserver
        )
    }

    inner class ShoppingListDetailViewModelFactory(
        private val application: Application,
        private val currentShoppingList: ShoppingListModel
    ) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(ShoppingListDetailViewModel::class.java)) {
                return ShoppingListDetailViewModel(application, currentShoppingList) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
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