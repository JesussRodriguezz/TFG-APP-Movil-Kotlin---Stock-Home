package com.yes.tfgapp.ui.shoppinglistdetail

import android.app.Application
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.yes.tfgapp.databinding.FragmentShoppingListDetailBinding
import com.yes.tfgapp.domain.model.ProductShoppingListModel
import com.yes.tfgapp.domain.model.ShoppingListModel
import com.yes.tfgapp.ui.home.MainActivity
import com.yes.tfgapp.ui.shoppinglistdetail.adapter.ShoppingListDetailAdapter
import com.yes.tfgapp.ui.shoppinglistdetail.adapter.ShoppingListDetailBoughtAdapter
import java.util.Timer
import kotlin.concurrent.schedule


class ShoppingListDetailFragment : Fragment() {


    private val args: ShoppingListDetailFragmentArgs by navArgs()
    private lateinit var binding : FragmentShoppingListDetailBinding
    private lateinit var mShoppingListDetailViewModel: ShoppingListDetailViewModel


    override fun onResume() {
        super.onResume()
        (activity as MainActivity).setToolbarTitle(args.currentShoppingList.name)
        (activity as MainActivity).activeButtonBack()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentShoppingListDetailBinding.inflate(inflater, container, false)
        initUI()
        initListeners()
        return binding.root
    }

    private fun initListeners() {
        this.binding.fabAddProducts.setOnClickListener{
            val action = ShoppingListDetailFragmentDirections.actionShoppingListDetailFragmentToShoppingListAddItemsFragment(args.currentShoppingList)
            binding.root.findNavController().navigate(action)
        }
    }

    private fun initUI() {
        val myProductsAdapter = ShoppingListDetailAdapter(args.currentShoppingList){
                product -> setProductIsBought(product)
        }
        val myProductsBoughtAdapter = ShoppingListDetailBoughtAdapter(args.currentShoppingList){
            product -> setProductIsNotBought(product)
        }

        mShoppingListDetailViewModel = ViewModelProvider(this, ShoppingListDetailViewModelFactory(requireActivity().application, args.currentShoppingList)).get(ShoppingListDetailViewModel::class.java)
        val myProductsRecyclerView=binding.rvShoppingListDetailProductsToBuy
        myProductsRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        myProductsRecyclerView.adapter = myProductsAdapter

        val myProductsBoughtRecyclerView= binding.rvShoppingListDetailBoughtProducts
        myProductsBoughtRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        myProductsBoughtRecyclerView.adapter = myProductsBoughtAdapter

        mShoppingListDetailViewModel.readAllDataProductShoppingList.observe(viewLifecycleOwner) { productShoppingList ->
            mShoppingListDetailViewModel.getProductsForShoppingList(productShoppingList).observe(viewLifecycleOwner) { products ->
                myProductsAdapter.setData(products)
            }
        }

        mShoppingListDetailViewModel.readAllDataProductBoughtShoppingList.observe(viewLifecycleOwner) { productShoppingList ->
            mShoppingListDetailViewModel.getProductsForShoppingList(productShoppingList).observe(viewLifecycleOwner) { products ->
                myProductsBoughtAdapter.setData(products)
            }
        }
    }



    inner class ShoppingListDetailViewModelFactory(private val application: Application, private val currentShoppingList: ShoppingListModel) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(ShoppingListDetailViewModel::class.java)) {
                return ShoppingListDetailViewModel(application, currentShoppingList) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }

    private fun setProductIsBought(productShoppingList: ProductShoppingListModel){
        Timer("SettingUp", false).schedule(100) {
            mShoppingListDetailViewModel.updateProductIsBought(productShoppingList)
        }
    }

    private fun setProductIsNotBought(product: ProductShoppingListModel) {

        Timer("SettingUp", false).schedule(100) {
            mShoppingListDetailViewModel.updateProductIsNotBought(product)
        }

    }


}