package com.yes.tfgapp.ui.searchproducts

import android.app.Dialog
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
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
import com.yes.tfgapp.ui.home.MainActivity
import com.yes.tfgapp.ui.searchproducts.adapter.ChooseCategoryAdapter
import com.yes.tfgapp.ui.searchproducts.adapter.ProductSearchAdapter
import com.yes.tfgapp.ui.searchproducts.adapter.ProductSearchApiAdapter
import com.yes.tfgapp.ui.shoppinglistadditems.ShoppingListAddItemsViewModel
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
    private var productsAdapter : ProductSearchAdapter = ProductSearchAdapter(
        onAddProductToList = { product,position -> addProductToList(product,position) },
        getCategoryById = { id, callback -> getCategoryById(id, callback) },
        changeCategory = {product,position -> changeCategory(product,position)}
    )
    private var chooseCategoryAdapter: ChooseCategoryAdapter = ChooseCategoryAdapter()
    private var productSearchApiAdapter: ProductSearchApiAdapter =
        ProductSearchApiAdapter { product,position -> addProductToList(product,position) }

    private lateinit var mShoppingListAddItemsViewModel: ShoppingListAddItemsViewModel

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

    private fun initListeners() {
        binding.btnLocalSearch.setOnClickListener {
            if (!searchModeLocal) {
                activateLocalOrExternalSearch(true,R.color.accentRed,R.color.white,R.color.primaryGrey,R.color.black)
            }
        }
        binding.btnExternalSearch.setOnClickListener {
            if (searchModeLocal) {
                activateLocalOrExternalSearch(false,R.color.primaryGrey,R.color.black,R.color.accentRed,R.color.white)
            }
        }
    }
    private fun activateLocalOrExternalSearch(isLocal: Boolean,backgroundColorLocal: Int, textColorLocal: Int, backgroundColorExternal: Int, textColorExternal: Int) {
        binding.rvProductsSearch.isVisible = isLocal
        binding.rvProductsSearchApi.isVisible = !isLocal
        searchModeLocal = isLocal
        binding.btnLocalSearch.setBackgroundColor(ContextCompat.getColor(requireContext(),backgroundColorLocal))
        binding.btnLocalSearch.setTextColor(ContextCompat.getColor(requireContext(),textColorLocal))
        binding.btnExternalSearch.setBackgroundColor(ContextCompat.getColor(requireContext(),backgroundColorExternal))
        binding.btnExternalSearch.setTextColor(ContextCompat.getColor(requireContext(),textColorExternal))
    }

    private fun initUI() {

        binding.rvProductsSearchApi.setHasFixedSize(true)
        binding.rvProductsSearchApi.layoutManager = LinearLayoutManager(requireContext())
        binding.rvProductsSearchApi.adapter = productSearchApiAdapter

        mShoppingListAddItemsViewModel =
            ViewModelProvider(this).get(ShoppingListAddItemsViewModel::class.java)

        val productSearchRecyclerView = binding.rvProductsSearch
        productSearchRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        productSearchRecyclerView.adapter = productsAdapter


        binding.searchView.setOnQueryTextListener(object :
            androidx.appcompat.widget.SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                if(!searchModeLocal){
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
                            !it.productName.isNullOrBlank() && it.productName.contains(
                                query.orEmpty(),
                                ignoreCase = true
                            )
                        }
                        productSearchApiAdapter.setData(filteredProducts)
                        binding.progressBar.isVisible = false
                    }
                }
                Log.i("yes", "funciona :)")
            } else {
                Log.i("yes", "No funciona")
            }
        }

    }

    private fun filterProducts(text: String?) {
        val filteredList = mShoppingListAddItemsViewModel.filterProducts(text)
        productsAdapter.setProductList(filteredList)
        productsAdapter.notifyDataSetChanged()

    }


    private fun addProductToList(product: ProductModel, position: Int) {
        if (!searchModeLocal || position==0) {
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
            }
        }
    }

    private fun changeCategory(product: ProductModel, position: Int){
        val dialog = Dialog(requireContext())
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
        dialog.setContentView(R.layout.dialog_change_category)
        configureDialog(dialog,product,position)
        dialog.findViewById<ImageButton>(R.id.ibAddProductToListDialogChangeCategory).setOnClickListener{
            addProductWithNewCategory(product,position)
        }
        dialog.findViewById<Button>(R.id.btnChangeCategoryChangeCategory).setOnClickListener {
            updateCategoryProduct(product)
        }
        dialog.show()
        dialog.setOnDismissListener {
            val filteredList = mShoppingListAddItemsViewModel.filterProducts(binding.searchView.query.toString())
            productsAdapter.setProductList(filteredList)
            productsAdapter.notifyDataSetChanged()
        }
    }

    private fun configureDialog(dialog: Dialog, product: ProductModel, position: Int) {
        if(position == 0){
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

    private fun updateCategoryProduct(product: ProductModel) {
        val categorySelected = chooseCategoryAdapter.selectedItemPosition
        val category = chooseCategoryAdapter.publicCategoriesList[categorySelected]
        val newProduct = product.copy(categoryId = category.id)
        mShoppingListAddItemsViewModel.updateProduct(newProduct)
        chooseCategoryAdapter.notifyDataSetChanged()
    }

    private fun addProductWithNewCategory(product: ProductModel, position: Int) {
        val categorySelected=chooseCategoryAdapter.selectedItemPosition
        val category = chooseCategoryAdapter.publicCategoriesList[categorySelected]
        val newProduct = product.copy(categoryId = category.id)
        mShoppingListAddItemsViewModel.updateProduct(newProduct)
        addProductToList(newProduct,position)
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