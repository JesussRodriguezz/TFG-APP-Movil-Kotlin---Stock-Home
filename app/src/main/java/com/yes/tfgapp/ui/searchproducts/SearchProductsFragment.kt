package com.yes.tfgapp.ui.searchproducts

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.core.content.ContextCompat.getSystemService
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.yes.tfgapp.R
import com.yes.tfgapp.databinding.FragmentSearchProductsBinding
import com.yes.tfgapp.domain.model.ProductModel
import com.yes.tfgapp.domain.model.ProductShoppingListModel
import com.yes.tfgapp.ui.home.MainActivity
import com.yes.tfgapp.ui.shoppinglistadditems.ShoppingListAddItemsViewModel
import com.yes.tfgapp.ui.shoppinglistadditems.adapter.ShoppingListProductsAdapter

class SearchProductsFragment : Fragment() {

    private val args: SearchProductsFragmentArgs by navArgs()

    private lateinit var binding: FragmentSearchProductsBinding
    val productsAdapter = ShoppingListProductsAdapter { product -> addProductToList(product) }

    private lateinit var mShoppingListAddItemsViewModel: ShoppingListAddItemsViewModel

    override fun onResume() {
        super.onResume()
        // Haz que la searchview se active al abrir el fragment simulando que el usuario ha pulsado sobre ella
        binding.searchView.requestFocus()
        val imm = context?.getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
        imm?.showSoftInput(binding.searchView, InputMethodManager.SHOW_IMPLICIT)

        //(activity as MainActivity).hideBottomNav()
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSearchProductsBinding.inflate(inflater, container, false)
        initUI()
        initListeners()
        return binding.root
    }

    private fun initListeners() {
    }

    private fun initUI() {

        mShoppingListAddItemsViewModel = ViewModelProvider(this).get(ShoppingListAddItemsViewModel::class.java)

       // val productSearchRecyclerView = binding.rvProductsSearch
       // productSearchRecyclerView.layoutManager = LinearLayoutManager(requireContext())
       // productSearchRecyclerView.adapter = productsAdapter




    }

    private fun addProductToList(product: ProductModel){
        //mShoppingListDetailViewModel.addProductToList(productShoppingList)
        if(this.args.CurrentShoppingList != null){
            println("Current shopping list: ${this.args.CurrentShoppingList.name}")
        }else{
            println("Current shopping list is null")
        }
        println("Product added to list: ${product.name}")
        val productShoppingList= ProductShoppingListModel(
            shoppingListId = this.args.CurrentShoppingList.id,
            productId = product.id
        )
        println("ProductShoppingList: ${productShoppingList.productId}")
        println("ShoppingList: ${productShoppingList.shoppingListId}")
        println("ProductShoppingList is bought: ${productShoppingList.isBought}")
        mShoppingListAddItemsViewModel.addProductToList(productShoppingList)

    }
}