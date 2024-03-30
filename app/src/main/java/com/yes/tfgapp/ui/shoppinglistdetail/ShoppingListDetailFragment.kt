package com.yes.tfgapp.ui.shoppinglistdetail

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.yes.tfgapp.R
import com.yes.tfgapp.databinding.FragmentShoppingListDetailBinding
import com.yes.tfgapp.domain.model.CategoryModel
import com.yes.tfgapp.ui.home.MainActivity
import com.yes.tfgapp.ui.shoppinglist.ShoppingListViewModel
import com.yes.tfgapp.ui.shoppinglistdetail.adapter.ShoppingListCategoriesAdapter
import com.yes.tfgapp.ui.shoppinglistdetail.adapter.ShoppingListProductsAdapter

class ShoppingListDetailFragment : Fragment() {


    private val args: ShoppingListDetailFragmentArgs by navArgs()

    private lateinit var _binding: FragmentShoppingListDetailBinding
    private val binding get() = _binding!!

    private lateinit var mShoppingListDetailViewModel: ShoppingListDetailViewModel

    val categoriesAdapter = ShoppingListCategoriesAdapter() { updateCategories(it) }
    val productsAdapter= ShoppingListProductsAdapter()



    override fun onResume() {
        super.onResume()
        (activity as MainActivity).setToolbarTitle(args.CurrentShoppingList.name)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentShoppingListDetailBinding.inflate(inflater, container, false)
        initUI()
        return binding.root
    }

    private fun initUI() {
        mShoppingListDetailViewModel =
            ViewModelProvider(this).get(ShoppingListDetailViewModel::class.java)


        //val categoriesAdapter = ShoppingListCategoriesAdapter() { updateCategories(it) }
        val categoriesRecyclerView = binding.rvCategories
        categoriesRecyclerView.layoutManager =
            GridLayoutManager(requireContext(), 2, GridLayoutManager.HORIZONTAL, false)

        categoriesRecyclerView.adapter = categoriesAdapter

        mShoppingListDetailViewModel.readAllDataCategory.observe(viewLifecycleOwner, { categories ->
            categoriesAdapter.categoriesList = categories
            categoriesAdapter.notifyDataSetChanged() // Asegura que el adaptador se actualice
        })


        val productsRecyclerView = binding.rvProducts
        productsRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        productsRecyclerView.adapter = productsAdapter

        mShoppingListDetailViewModel.readAllDataProduct.observe(viewLifecycleOwner, { products ->
            productsAdapter.productsList = products
            productsAdapter.notifyDataSetChanged()
        })

        //El rollo no es que con observe tengas que asignarle ese valor todo el rato, el observe solo actualiza una variable cuando cambia.
        //Para nuestro caso tenemos que tener en el adapter una variable (categoriesList) que primero la inicialicemos con el observer readAllData
        //y luego cuando queramos cambiarla, simplemente le asignamos el nuevo valor y llamamos a notifyDataSetChanged() para que se actualice la vista.

    }



    /*private fun updateCategories(position: Int) {

        mShoppingListDetailViewModel.readAllDataCategory.value?.forEachIndexed { index, category ->
            val updatedCategory = category.copy(isSelected = false)
            mShoppingListDetailViewModel.updateCategory(updatedCategory)
        }

        val selectedCategory = mShoppingListDetailViewModel.readAllDataCategory.value?.get(position)
        selectedCategory?.let {
            if (it.isSelected) {
                val updatedCategory = it.copy(isSelected = false)
                mShoppingListDetailViewModel.updateCategory(updatedCategory)
            }else{
                val updatedCategory = it.copy(isSelected = true)
                mShoppingListDetailViewModel.updateCategory(updatedCategory)
            }
        }
        updateProducts(selectedCategory!!)
    }*/

    private fun updateCategories(position: Int) {
        // Actualiza todas las categorías para que no estén seleccionadas
        mShoppingListDetailViewModel.readAllDataCategory.value?.mapIndexed { index, category ->
            category.copy(isSelected = index == position)
        }?.let { updatedCategories ->
            mShoppingListDetailViewModel.updateCategories(updatedCategories)
        }

        // Actualiza la categoría seleccionada para alternar su estado de selección
        mShoppingListDetailViewModel.readAllDataCategory.value?.get(position)?.let { selectedCategory ->
            val updatedCategory = selectedCategory.copy(isSelected = !selectedCategory.isSelected)
            mShoppingListDetailViewModel.updateCategory(updatedCategory)
            updateProducts(updatedCategory)
        }
    }

    private fun updateProducts(category: CategoryModel) {
        val selectedTasks =
            mShoppingListDetailViewModel.readAllDataProduct.value?.filter { it.category == category.name }
        println("Selected tasks: $selectedTasks")
        productsAdapter.productsList = selectedTasks!!
        productsAdapter.notifyDataSetChanged()

        //mShoppingListDetailViewModel.updateProductsByCategory(category)

    }
}