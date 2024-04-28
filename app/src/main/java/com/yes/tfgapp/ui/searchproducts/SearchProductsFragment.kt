package com.yes.tfgapp.ui.searchproducts

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.yes.tfgapp.R
import com.yes.tfgapp.data.network.ProductsApiService
import com.yes.tfgapp.data.network.response.ProductSearchResponse
import com.yes.tfgapp.data.repository.ProductRepository
import com.yes.tfgapp.databinding.FragmentSearchProductsBinding
import com.yes.tfgapp.domain.model.ProductModel
import com.yes.tfgapp.domain.model.ProductShoppingListModel
import com.yes.tfgapp.ui.home.MainActivity
import com.yes.tfgapp.ui.searchproducts.adapter.ProductSearchApiAdapter
import com.yes.tfgapp.ui.shoppinglistadditems.ShoppingListAddItemsViewModel
import com.yes.tfgapp.ui.shoppinglistadditems.adapter.ShoppingListProductsAdapter
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
    private var productsAdapter =
        ShoppingListProductsAdapter { product -> addProductToList(product) }
    private var productSearchApiAdapter: ProductSearchApiAdapter =
        ProductSearchApiAdapter { product -> addProductToList(product) }

    private lateinit var mShoppingListAddItemsViewModel: ShoppingListAddItemsViewModel

    var searchModeLocal: Boolean = true
    private lateinit var retrofit: Retrofit



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSearchProductsBinding.inflate(inflater, container, false)
        initUI()
        initListeners()
        retrofit = getRetrofit()
        return binding.root
    }

    private fun initListeners() {
        binding.btnLocalSearch.setOnClickListener {
            if (!searchModeLocal) {
                binding.rvProductsSearch.isVisible = true
                binding.rvProductsSearchApi.isVisible = false

                searchModeLocal = true
                binding.btnLocalSearch.setBackgroundColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.accentRed
                    )
                )
                binding.btnLocalSearch.setTextColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.white
                    )
                )
                binding.btnExternalSearch.setBackgroundColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.primaryGrey
                    )
                )
                binding.btnExternalSearch.setTextColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.black
                    )
                )
            }

        }
        binding.btnExternalSearch.setOnClickListener {
            if (searchModeLocal) {
                binding.rvProductsSearch.isVisible = false
                binding.rvProductsSearchApi.isVisible = true
                searchModeLocal = false
                binding.btnLocalSearch.setBackgroundColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.primaryGrey
                    )
                )
                binding.btnLocalSearch.setTextColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.black
                    )
                )
                binding.btnExternalSearch.setBackgroundColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.accentRed
                    )
                )
                binding.btnExternalSearch.setTextColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.white
                    )
                )

            }

        }
    }

    private fun initUI() {

        //productSearchApiAdapter  = ProductSearchApiAdapter()
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
                searchByProductName(query)
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                filterProducts(newText)
                return false
            }
        })

        //mShoppingListAddItemsViewModel.readAllDataProduct.observe(viewLifecycleOwner, { products ->
        //    productsAdapter.setProductList(products)
        //    productsAdapter.notifyDataSetChanged()
        //})

    }

    private fun searchByProductName(query: String?) {

        binding.progressBar.isVisible = true
        CoroutineScope(Dispatchers.IO).launch {
            val myResponse: Response<ProductSearchResponse> =
                retrofit.create(ProductsApiService::class.java).searchProducts(query.orEmpty())
            if (myResponse.isSuccessful) {
                val response: ProductSearchResponse? = myResponse.body()
                if (response != null) {
                    Log.i("yes", "Funciona: contenido : ${response.toString()}")
                    (activity as MainActivity).runOnUiThread {
                        productSearchApiAdapter.setData(response.products)
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
        if (searchModeLocal) {
            val filteredList = mShoppingListAddItemsViewModel.filterProducts(text)
            productsAdapter.setProductList(filteredList)
            productsAdapter.notifyDataSetChanged()
        } else {

        }

    }


    private fun addProductToList(product: ProductModel) {
        if (this@SearchProductsFragment.args.CurrentShoppingList != null) {
            println("Current shopping list: ${this@SearchProductsFragment.args.CurrentShoppingList.name}")
        } else {
            println("Current shopping list is null")
        }

        if (searchModeLocal) {
            addLocalProductToList(product)
        } else {
            addExternalProductToList(product)
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
            // Asegúrate de que este código solo se ejecute una vez por cada inserción
            if (productId != null) {
                val productShoppingList = ProductShoppingListModel(
                    shoppingListId = this@SearchProductsFragment.args.CurrentShoppingList.id,
                    productId = productId.toInt()
                )
                mShoppingListAddItemsViewModel.addProductToList(productShoppingList)
                // Opcional: resetea el LiveData para evitar duplicados en futuras inserciones
                mShoppingListAddItemsViewModel.productIdLiveData.value = null
            }
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
            .baseUrl("https://world.openfoodfacts.org/")
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }
}