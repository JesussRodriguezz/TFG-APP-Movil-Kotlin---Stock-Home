package com.yes.tfgapp.ui.shoppinglistdetail

import android.animation.Animator
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.app.Application
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateDecelerateInterpolator
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.yes.tfgapp.databinding.FragmentShoppingListDetailBinding
import com.yes.tfgapp.domain.model.CategoryModel
import com.yes.tfgapp.domain.model.ProductShoppingListModel
import com.yes.tfgapp.domain.model.ShoppingListModel
import com.yes.tfgapp.ui.home.MainActivity
import com.yes.tfgapp.ui.shoppinglistadditems.ShoppingListAddItemsViewModel
import com.yes.tfgapp.ui.shoppinglistdetail.adapter.ShoppingListDetailAdapter
import com.yes.tfgapp.ui.shoppinglistdetail.adapter.ShoppingListDetailBoughtAdapter
import kotlinx.coroutines.launch
import java.util.Timer
import kotlin.concurrent.schedule


class ShoppingListDetailFragment : Fragment() {


    private val args: ShoppingListDetailFragmentArgs by navArgs()
    private lateinit var binding: FragmentShoppingListDetailBinding
    private lateinit var mShoppingListDetailViewModel: ShoppingListDetailViewModel
    private lateinit var mShoppingListAddItemsViewModel: ShoppingListAddItemsViewModel


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
        binding.fabAddProducts.setOnClickListener {

            animateButtonClick(binding.fabAddProducts) {
                val action =
                    ShoppingListDetailFragmentDirections.actionShoppingListDetailFragmentToShoppingListAddItemsFragment(
                        args.currentShoppingList
                    )
                binding.root.findNavController().navigate(action)
            }

            //val action =
            //    ShoppingListDetailFragmentDirections.actionShoppingListDetailFragmentToShoppingListAddItemsFragment(
            //        args.currentShoppingList
            //    )
            //binding.root.findNavController().navigate(action)
        }


    }

    private fun initUI() {
        mShoppingListAddItemsViewModel =
            ViewModelProvider(this)[ShoppingListAddItemsViewModel::class.java]


        val myProductsAdapter = ShoppingListDetailAdapter(
            args.currentShoppingList,
            { productShoppingList -> setProductIsBought(productShoppingList) },
            { id, callback -> getCategoryById(id, callback) }
        )
        val myProductsBoughtAdapter = ShoppingListDetailBoughtAdapter(
            args.currentShoppingList,
            { productShoppingList -> setProductIsNotBought(productShoppingList) },
            { id, callback -> getCategoryById(id, callback) }
        )


        mShoppingListDetailViewModel = ViewModelProvider(
            this,
            ShoppingListDetailViewModelFactory(
                requireActivity().application,
                args.currentShoppingList
            )
        )[ShoppingListDetailViewModel::
        class.java]
        val myProductsRecyclerView = binding.rvShoppingListDetailProductsToBuy
        myProductsRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        myProductsRecyclerView.adapter = myProductsAdapter

        val myProductsBoughtRecyclerView = binding.rvShoppingListDetailBoughtProducts
        myProductsBoughtRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        myProductsBoughtRecyclerView.adapter = myProductsBoughtAdapter

        mShoppingListDetailViewModel.readAllDataProductShoppingList.observe(viewLifecycleOwner)
        { productShoppingList ->
            mShoppingListDetailViewModel.getProductsForShoppingList(productShoppingList)
                .observe(viewLifecycleOwner) { products ->
                    myProductsAdapter.setData(products)
                }
        }

        mShoppingListDetailViewModel.readAllDataProductBoughtShoppingList.observe(viewLifecycleOwner)
        { productShoppingList ->
            mShoppingListDetailViewModel.getProductsForShoppingList(productShoppingList)
                .observe(viewLifecycleOwner) { products ->
                    myProductsBoughtAdapter.setData(products)
                }
        }
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
            override fun onAnimationStart(animation: Animator) {}
            override fun onAnimationEnd(animation: Animator) {
                action()
            }
            override fun onAnimationCancel(animation: Animator) {}
            override fun onAnimationRepeat(animation: Animator) {}
        })
        animatorSet.start()
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

    private fun setProductIsBought(productShoppingList: ProductShoppingListModel) {
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