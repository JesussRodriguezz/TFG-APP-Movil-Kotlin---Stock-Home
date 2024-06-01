package com.yes.tfgapp.ui.searchproducts

import android.animation.Animator
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.app.Application
import android.app.Dialog
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateDecelerateInterpolator
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.yes.tfgapp.R
import com.yes.tfgapp.data.network.ProductsApiService
import com.yes.tfgapp.data.network.response.ProductSearchResponse
import com.yes.tfgapp.databinding.FragmentSearchProductsBinding
import com.yes.tfgapp.domain.model.CategoryModel
import com.yes.tfgapp.domain.model.ProductModel
import com.yes.tfgapp.domain.model.ProductShoppingListModel
import com.yes.tfgapp.domain.model.ShoppingListModel
import com.yes.tfgapp.ui.home.MainActivity
import com.yes.tfgapp.ui.searchproducts.adapter.ChooseCategoryAdapter
import com.yes.tfgapp.ui.searchproducts.adapter.ProductSearchAdapter
import com.yes.tfgapp.ui.searchproducts.adapter.ProductSearchApiAdapter
import com.yes.tfgapp.ui.shoppinglistadditems.ShoppingListAddItemsViewModel
import com.yes.tfgapp.ui.shoppinglistdetail.ShoppingListDetailViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit


class SearchProductsFragment : Fragment() {

    private val args: SearchProductsFragmentArgs by navArgs()

    private lateinit var binding: FragmentSearchProductsBinding
    private var productsAdapter: ProductSearchAdapter = ProductSearchAdapter(
        onAddProductToList = { product, position -> addProductToList(product, position) },
        getCategoryById = { id, callback -> getCategoryById(id, callback) },
        changeCategory = { product, position, isproductadded -> changeCategory(product, position,isproductadded) },
        onDeleteProductFromList = { product -> deleteProductFromList(product) }
    )
    private var chooseCategoryAdapter: ChooseCategoryAdapter = ChooseCategoryAdapter()
    private var productSearchApiAdapter: ProductSearchApiAdapter =
        ProductSearchApiAdapter { product, position -> addProductToList(product, position) }

    private lateinit var mShoppingListAddItemsViewModel: ShoppingListAddItemsViewModel
    private lateinit var mShoppingListDetailViewModel: ShoppingListDetailViewModel

    var searchModeLocal: Boolean = true
    private lateinit var retrofit: Retrofit


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSearchProductsBinding.inflate(inflater, container, false)
        initUI()
        initListeners()
        retrofit = getRetrofit()
        return binding.root
    }

    override fun onResume() {
        super.onResume()
        activateLocalOrExternalSearch(
            searchModeLocal,
            if (searchModeLocal) R.color.accentRed else R.color.primaryGrey,
            if (searchModeLocal) R.color.white else R.color.black,
            if (searchModeLocal) R.color.primaryGrey else R.color.accentRed,
            if (searchModeLocal) R.color.black else R.color.white
        )
    }


    private fun initListeners() {
        binding.btnLocalSearch.setOnClickListener {
            if (!searchModeLocal) {
                activateLocalOrExternalSearch(
                    true,
                    R.color.accentRed,
                    R.color.white,
                    R.color.primaryGrey,
                    R.color.black
                )
            }
        }
        binding.btnExternalSearch.setOnClickListener {
            if (searchModeLocal) {
                activateLocalOrExternalSearch(
                    false,
                    R.color.primaryGrey,
                    R.color.black,
                    R.color.accentRed,
                    R.color.white
                )
            }
        }
    }

    private fun activateLocalOrExternalSearch(
        isLocal: Boolean,
        backgroundColorLocal: Int,
        textColorLocal: Int,
        backgroundColorExternal: Int,
        textColorExternal: Int
    ) {
        binding.rvProductsSearch.isVisible = isLocal
        binding.rvProductsSearchApi.isVisible = !isLocal
        searchModeLocal = isLocal
        binding.btnLocalSearch.setBackgroundColor(
            ContextCompat.getColor(
                requireContext(),
                backgroundColorLocal
            )
        )
        binding.btnLocalSearch.setTextColor(
            ContextCompat.getColor(
                requireContext(),
                textColorLocal
            )
        )
        binding.btnExternalSearch.setBackgroundColor(
            ContextCompat.getColor(
                requireContext(),
                backgroundColorExternal
            )
        )
        binding.btnExternalSearch.setTextColor(
            ContextCompat.getColor(
                requireContext(),
                textColorExternal
            )
        )
    }

    private fun initUI() {
        mShoppingListDetailViewModel = ViewModelProvider(
            this,
            ShoppingListDetailViewModelFactory(
                requireActivity().application,
                args.CurrentShoppingList
            )
        )[ShoppingListDetailViewModel::
        class.java]
        mShoppingListDetailViewModel.allProductsShoppingListLiveData.observe(viewLifecycleOwner) { productsInShoppingList ->
            productsAdapter.setProductsInShoppingList(productsInShoppingList)
            productSearchApiAdapter.setProductsInShoppingList(productsInShoppingList)
        }


        binding.rvProductsSearchApi.setHasFixedSize(true)
        binding.rvProductsSearchApi.layoutManager = LinearLayoutManager(requireContext())
        binding.rvProductsSearchApi.adapter = productSearchApiAdapter

        mShoppingListAddItemsViewModel =
            ViewModelProvider(this)[ShoppingListAddItemsViewModel::class.java]

        mShoppingListAddItemsViewModel.readAllDataProduct.observe(viewLifecycleOwner) { products ->
            productSearchApiAdapter.setProductList(products)
            productsAdapter.notifyDataSetChanged()
        }

        val productSearchRecyclerView = binding.rvProductsSearch
        productSearchRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        productSearchRecyclerView.adapter = productsAdapter




        binding.searchView.setOnQueryTextListener(object :
            androidx.appcompat.widget.SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                if (!searchModeLocal) {
                    searchByProductName(query)
                }
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                filterProducts(newText)
                return false
            }
        })
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

    private fun getCategoryById(id: Int, callback: (CategoryModel?) -> Unit) {
        viewLifecycleOwner.lifecycleScope.launch {
            try {
                val category = mShoppingListAddItemsViewModel.getCategoryById(id)
                callback(category)  // Pasamos el resultado al callback
            } catch (e: Exception) {
                callback(null)  // En caso de error, podríamos pasar null o manejar el error de otra forma
            }
        }
    }

    private fun searchByProductName(query: String?) {

        binding.progressBar.isVisible = true
        CoroutineScope(Dispatchers.IO).launch {
            val myResponse: Response<ProductSearchResponse> =
                retrofit.create(ProductsApiService::class.java).searchApiProducts(query.orEmpty())
            if (myResponse.isSuccessful) {
                val response: ProductSearchResponse? = myResponse.body()
                if (response != null) {
                    Log.i("yes", "Funciona: contenido : $response")
                    (activity as MainActivity).runOnUiThread {
                        val filteredProducts = response.products.filter {
                            val productName = it.productName ?: ""
                            productName.isNotBlank() && productName.contains(
                                query.orEmpty(),
                                ignoreCase = true
                            )
                        }
                        if (filteredProducts.isEmpty()) {
                            Toast.makeText(
                                requireContext(),
                                getString(R.string.not_products_found), Toast.LENGTH_SHORT
                            ).show()
                            productSearchApiAdapter.setData(emptyList())
                        } else {
                            productSearchApiAdapter.setData(filteredProducts)
                        }
                        binding.progressBar.isVisible = false
                    }
                } else {
                    Log.i("yes", "Respuesta vacía")
                    (activity as MainActivity).runOnUiThread {
                        Toast.makeText(
                            requireContext(),
                            getString(R.string.error_obtaining_data), Toast.LENGTH_SHORT
                        ).show()
                        productSearchApiAdapter.setData(emptyList())
                        binding.progressBar.isVisible = false
                    }
                }
                Log.i("yes", "funciona :)")
            } else {
                Log.i("yes", "No funciona")
                (activity as MainActivity).runOnUiThread {
                    Toast.makeText(
                        requireContext(),
                        getString(R.string.error_with_search), Toast.LENGTH_SHORT
                    ).show()
                    productSearchApiAdapter.setData(emptyList())
                    binding.progressBar.isVisible = false
                }
            }
        }
    }

    private fun filterProducts(text: String?) {
        val filteredList = mShoppingListAddItemsViewModel.filterProducts(text)
        productsAdapter.setProductList(filteredList)
        productsAdapter.notifyDataSetChanged()

    }


    private fun addProductToList(product: ProductModel, position: Int) {
        if (!searchModeLocal || position == 0) {
            addExternalProductToList(product)
        } else {
            addLocalProductToList(product)
        }
    }

    private fun addLocalProductToList(product: ProductModel) {
        val productShoppingList = ProductShoppingListModel(
            shoppingListId = this.args.CurrentShoppingList.id,
            productId = product.id
        )
        mShoppingListAddItemsViewModel.addProductToList(productShoppingList)
    }

    private fun addExternalProductToList(product: ProductModel) {

        mShoppingListAddItemsViewModel.addProduct(product)
        observeProductIDAndAddToList()

    }

    private fun observeProductIDAndAddToList() {
        mShoppingListAddItemsViewModel.productIdLiveData.observe(viewLifecycleOwner) { productId ->
            if (productId != null) {
                val productShoppingList = ProductShoppingListModel(
                    shoppingListId = this@SearchProductsFragment.args.CurrentShoppingList.id,
                    productId = productId.toInt()
                )
                mShoppingListAddItemsViewModel.addProductToList(productShoppingList)
                mShoppingListAddItemsViewModel.productIdLiveData.value = null

                Handler(Looper.getMainLooper()).postDelayed({
                    val currentText = binding.searchView.query.toString()
                    println("currentText: $currentText")
                    val filteredList = mShoppingListAddItemsViewModel.filterProducts(currentText)
                    println("filteredList: $filteredList")
                    productsAdapter.setProductList(filteredList)
                    productsAdapter.notifyDataSetChanged()
                }, 100)
            }
        }
    }

    private fun changeCategory(product: ProductModel, position: Int, isProductAdded:Boolean) {
        val dialog = Dialog(requireContext())
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
        dialog.setContentView(R.layout.dialog_change_category)
        configureDialog(dialog, product, position)
        if (isProductAdded) {
            dialog.findViewById<ImageButton>(R.id.ibAddProductToListDialogChangeCategory)
                .setImageResource(R.drawable.ic_check)
        }else{
            dialog.findViewById<ImageButton>(R.id.ibAddProductToListDialogChangeCategory)
                .setImageResource(R.drawable.ic_add)
        }
        dialog.findViewById<ImageButton>(R.id.ibAddProductToListDialogChangeCategory)
            .setOnClickListener {
                if (isProductAdded) {
                    animateIconChange(
                        dialog.findViewById<ImageButton>(R.id.ibAddProductToListDialogChangeCategory),
                        R.drawable.ic_add
                    ) {
                        deleteProductFromList(product)
                        dialog.dismiss()
                    }
                } else {
                    animateIconChange(
                        dialog.findViewById<ImageButton>(R.id.ibAddProductToListDialogChangeCategory),
                        R.drawable.ic_check
                    ) {
                        addProductWithNewCategory(product, position)
                        dialog.dismiss()
                    }
                }
                /*animateIconChange(
                    dialog.findViewById<ImageButton>(R.id.ibAddProductToListDialogChangeCategory),
                    R.drawable.ic_check
                ) {
                    addProductWithNewCategory(product, position)
                    dialog.dismiss()
                }*/

            }
        dialog.findViewById<Button>(R.id.btnChangeCategoryChangeCategory).setOnClickListener {
            animateButtonClick(dialog.findViewById<Button>(R.id.btnChangeCategoryChangeCategory)) {
                updateCategoryProduct(product, dialog)
            }

        }
        dialog.show()
        dialog.setOnDismissListener {
            val filteredList =
                mShoppingListAddItemsViewModel.filterProducts(binding.searchView.query.toString())
            productsAdapter.setProductList(filteredList)
            productsAdapter.notifyDataSetChanged()
        }
    }

    private fun configureDialog(dialog: Dialog, product: ProductModel, position: Int) {
        if (position == 0) {
            dialog.findViewById<Button>(R.id.btnChangeCategoryChangeCategory).isVisible = false
        }

        dialog.findViewById<TextView>(R.id.tvProductNameDialogChangeCategory).text = product.name
        val layoutManager = GridLayoutManager(requireContext(), 3)
        layoutManager.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
            override fun getSpanSize(position: Int): Int {
                return if (position == 0) 3 else 1
            }
        }
        val rvCategories = dialog.findViewById<RecyclerView>(R.id.rvCategoriesDialogChangeCategory)
        rvCategories.layoutManager = layoutManager
        rvCategories.adapter = chooseCategoryAdapter
        mShoppingListAddItemsViewModel.readAllDataCategory.observe(viewLifecycleOwner) { categories ->
            chooseCategoryAdapter.setCategoriesListModified(categories, product.categoryId)
            chooseCategoryAdapter.notifyDataSetChanged() // Asegura que el adaptador se actualice
        }

        chooseCategoryAdapter.setFirstTimeClick()
    }

    private fun updateCategoryProduct(product: ProductModel, dialog: Dialog) {
        val categorySelected = chooseCategoryAdapter.selectedItemPosition
        val category = chooseCategoryAdapter.publicCategoriesList[categorySelected]
        val newProduct = product.copy(categoryId = category.id)
        mShoppingListAddItemsViewModel.updateProduct(newProduct)
        chooseCategoryAdapter.notifyDataSetChanged()

        Handler(Looper.getMainLooper()).postDelayed({
            dialog.dismiss()
        }, 500)
    }

    private fun addProductWithNewCategory(product: ProductModel, position: Int) {
        val categorySelected = chooseCategoryAdapter.selectedItemPosition
        val category = chooseCategoryAdapter.publicCategoriesList[categorySelected]
        val newProduct = product.copy(categoryId = category.id)
        mShoppingListAddItemsViewModel.updateProduct(newProduct)
        addProductToList(newProduct, position)
    }

    private fun deleteProductFromList(product: ProductModel) {
        println("deleteProductFromList")
        val productShoppingList = ProductShoppingListModel(
            shoppingListId = this.args.CurrentShoppingList.id,
            productId = product.id
        )
        mShoppingListAddItemsViewModel.deleteProductFromList(productShoppingList)
    }

    private fun animateIconChange(
        imageButton: ImageButton,
        newIconResId: Int,
        onAnimationEnd: () -> Unit
    ) {
        imageButton.setImageResource(newIconResId)
        imageButton.scaleX = 0.8f
        imageButton.scaleY = 0.8f
        imageButton.animate()
            .scaleX(1.2f)
            .scaleY(1.2f)
            .setDuration(100)
            .withEndAction {
                imageButton.animate()
                    .scaleX(1f)
                    .scaleY(1f)
                    .setDuration(100)
                    .withEndAction {
                        onAnimationEnd()
                    }.start()
            }.start()
    }

    private fun animateButtonClick(
        view: View,
        action: () -> Unit
    ) {
        val scaleXUp = ObjectAnimator.ofFloat(view, "scaleX", 1f, 1.1f)
        val scaleYUp = ObjectAnimator.ofFloat(view, "scaleY", 1f, 1.1f)
        val scaleXDown = ObjectAnimator.ofFloat(view, "scaleX", 1.1f, 1f)
        val scaleYDown = ObjectAnimator.ofFloat(view, "scaleY", 1.1f, 1f)

        scaleXUp.duration = 100
        scaleYUp.duration = 100
        scaleXDown.duration = 100
        scaleYDown.duration = 100

        val animatorSet = AnimatorSet()
        animatorSet.play(scaleXUp).with(scaleYUp).before(scaleXDown).before(scaleYDown)
        animatorSet.interpolator = AccelerateDecelerateInterpolator()

        animatorSet.addListener(object : Animator.AnimatorListener {
            override fun onAnimationStart(animation: Animator) {
                action()
            }

            override fun onAnimationEnd(animation: Animator) {

            }

            override fun onAnimationCancel(animation: Animator) {}
            override fun onAnimationRepeat(animation: Animator) {}
        })
        animatorSet.start()
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