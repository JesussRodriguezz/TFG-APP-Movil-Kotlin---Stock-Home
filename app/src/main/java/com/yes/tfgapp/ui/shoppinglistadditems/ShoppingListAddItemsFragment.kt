package com.yes.tfgapp.ui.shoppinglistadditems

import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.SearchView
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.yes.tfgapp.R
import com.yes.tfgapp.databinding.FragmentShoppingListAddItemsBinding
import com.yes.tfgapp.domain.model.CategoryModel
import com.yes.tfgapp.domain.model.ProductModel
import com.yes.tfgapp.domain.model.ProductShoppingListModel
import com.yes.tfgapp.ui.home.MainActivity
import com.yes.tfgapp.ui.shoppinglist.ShoppingListViewModel
import com.yes.tfgapp.ui.shoppinglistadditems.adapter.ShoppingListCategoriesAdapter
import com.yes.tfgapp.ui.shoppinglistadditems.adapter.ShoppingListProductsAdapter

class ShoppingListAddItemsFragment : Fragment() {


    private val args: ShoppingListAddItemsFragmentArgs by navArgs()

    private lateinit var binding: FragmentShoppingListAddItemsBinding

    private lateinit var mShoppingListAddItemsViewModel: ShoppingListAddItemsViewModel
    private lateinit var mShoppingListViewModel: ShoppingListViewModel

    val categoriesAdapter = ShoppingListCategoriesAdapter(
        onItemSelected = { position -> updateCategories(position) },
        onConfigureSelected = { category -> configureCategories(category)})
    val productsAdapter = ShoppingListProductsAdapter { product -> addProductToList(product) }



    override fun onResume() {
        super.onResume()
        (activity as MainActivity).setToolbarTitle(args.CurrentShoppingList.name)
        (activity as MainActivity).activeButtonBack()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentShoppingListAddItemsBinding.inflate(inflater, container, false)
        initUI()
        initListeners()
        return binding.root
    }

    private fun initUI() {
        mShoppingListAddItemsViewModel =
            ViewModelProvider(this).get(ShoppingListAddItemsViewModel::class.java)

        mShoppingListViewModel = ViewModelProvider(this).get(ShoppingListViewModel::class.java)


        val categoriesRecyclerView = binding.rvCategories
        categoriesRecyclerView.layoutManager =
            GridLayoutManager(requireContext(), 2, GridLayoutManager.HORIZONTAL, false)
        categoriesRecyclerView.adapter = categoriesAdapter

        mShoppingListAddItemsViewModel.readAllDataCategory.observe(viewLifecycleOwner, { categories ->
            categoriesAdapter.setCategoriesList(categories)
            categoriesAdapter.notifyDataSetChanged() // Asegura que el adaptador se actualice
        })


        val productsRecyclerView = binding.rvProducts
        productsRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        productsRecyclerView.adapter = productsAdapter

        mShoppingListAddItemsViewModel.readAllDataProduct.observe(viewLifecycleOwner, { products ->
            productsAdapter.setProductList(products)
            productsAdapter.notifyDataSetChanged()
        })

        binding.btnSearchView.setOnClickListener{
            val action = ShoppingListAddItemsFragmentDirections.actionShoppingListAddItemsFragmentToSearchProductsFragment(args.CurrentShoppingList)
            binding.root.findNavController().navigate(action)
        }

    }

    private fun initListeners() {
        binding.btnCreateCategory.setOnClickListener {
            val dialog = Dialog(requireContext())
            dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
            dialog.setContentView(R.layout.dialog_new_category)
            dialog.show()

            val btnSaveCategory = dialog.findViewById<View>(R.id.btnCreateCategory)
            btnSaveCategory.setOnClickListener {
                val newCategoryName =
                    dialog.findViewById<TextInputEditText>(R.id.etNewCategoryName).text.toString()

                if (!newCategoryName.isEmpty()) {
                    val newCategory = CategoryModel(0, newCategoryName, false)
                    mShoppingListAddItemsViewModel.addCategory(newCategory)
                    Toast.makeText(requireContext(), "Successfully added!", Toast.LENGTH_LONG)
                        .show()
                    dialog.hide()
                } else {
                    Toast.makeText(requireContext(), "Please fill the name", Toast.LENGTH_LONG)
                        .show()
                }
            }
        }
    }

    private fun updateCategories(category: CategoryModel) {
        // Cambia el estado de is selected de la categoría
        val updatedCategory = category.copy(isSelected = !category.isSelected)
        mShoppingListAddItemsViewModel.updateCategory(updatedCategory)

        // Cambia el resto de categorías a no seleccionadas
        mShoppingListAddItemsViewModel.readAllDataCategory.value?.map {
            if (it.id == updatedCategory.id) {
                updatedCategory // Mantén la categoría actualizada
            } else {
                it.copy(isSelected = false) // Cambia el resto de las categorías a no seleccionadas
            }
        }?.let { updatedCategories ->
            mShoppingListAddItemsViewModel.updateCategories(updatedCategories)
        }
        updateProducts(updatedCategory)
    }


    private fun configureCategories(category: CategoryModel) {
        val dialog = Dialog(requireContext())
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
        dialog.setContentView(R.layout.dialog_configure_category)
        dialog.show()
        val textInputLayout: TextInputLayout = dialog.findViewById(R.id.tilUpdateNameCategory)
        textInputLayout.hint = category.name

        val btnSaveChanges = dialog.findViewById<Button>(R.id.btnSaveChangesCategory)
        btnSaveChanges.setOnClickListener {
            val newName = dialog.findViewById<TextInputEditText?>(R.id.etUpdateNameCategory).text.toString()
            val newCategory = CategoryModel(category.id, newName, category.isSelected, icon=category.icon)
            mShoppingListAddItemsViewModel.updateCategory(newCategory)
            dialog.hide()
        }
        val btnDeleteCategory= dialog.findViewById<ImageButton>(R.id.ibDeleteCategory)
        btnDeleteCategory.setOnClickListener {
            mShoppingListAddItemsViewModel.deleteCategory(category)
            dialog.hide()
        }

    }

    private fun updateProducts(category: CategoryModel) {

        if (category.isSelected) {
            val selectedTasks =
                mShoppingListAddItemsViewModel.readAllDataProduct.value?.filter { it.categoryId == category.id }

            productsAdapter.setProductList(selectedTasks!!)
            productsAdapter.notifyDataSetChanged()
        } else {
            productsAdapter.setProductList(mShoppingListAddItemsViewModel.readAllDataProduct.value!!)
            productsAdapter.notifyDataSetChanged()
        }
    }

    private fun addProductToList(product: ProductModel){
        val productShoppingList= ProductShoppingListModel(
            shoppingListId = this.args.CurrentShoppingList.id,
            productId = product.id
        )
        mShoppingListAddItemsViewModel.addProductToList(productShoppingList)
    }
}