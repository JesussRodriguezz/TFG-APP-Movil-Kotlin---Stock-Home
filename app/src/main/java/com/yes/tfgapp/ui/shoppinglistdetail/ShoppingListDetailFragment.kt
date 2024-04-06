package com.yes.tfgapp.ui.shoppinglistdetail

import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.yes.tfgapp.R
import com.yes.tfgapp.databinding.FragmentShoppingListDetailBinding
import com.yes.tfgapp.domain.model.CategoryModel
import com.yes.tfgapp.domain.model.ShoppingListModel
import com.yes.tfgapp.ui.home.MainActivity
import com.yes.tfgapp.ui.shoppinglist.ShoppingListViewModel
import com.yes.tfgapp.ui.shoppinglistdetail.adapter.ShoppingListCategoriesAdapter
import com.yes.tfgapp.ui.shoppinglistdetail.adapter.ShoppingListProductsAdapter

class ShoppingListDetailFragment : Fragment() {


    private val args: ShoppingListDetailFragmentArgs by navArgs()

    private lateinit var _binding: FragmentShoppingListDetailBinding
    private val binding get() = _binding!!

    private lateinit var mShoppingListDetailViewModel: ShoppingListDetailViewModel

    val categoriesAdapter = ShoppingListCategoriesAdapter(
        onItemSelected = { position -> updateCategories(position) },
        onConfigureSelected = { category -> configureCategories(category) })
    val productsAdapter = ShoppingListProductsAdapter()


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
        initListeners()
        return binding.root
    }

    private fun initUI() {
        mShoppingListDetailViewModel =
            ViewModelProvider(this).get(ShoppingListDetailViewModel::class.java)


        val categoriesRecyclerView = binding.rvCategories
        categoriesRecyclerView.layoutManager =
            GridLayoutManager(requireContext(), 2, GridLayoutManager.HORIZONTAL, false)
        categoriesRecyclerView.adapter = categoriesAdapter

        //CAMBIAR CATEGORIES LIST PARA QUE SEA UNA VARIABLE PRIVADA Y CREAR UNA FUNCIÓN QUE ACTUALICE ESA VARIABLE
        mShoppingListDetailViewModel.readAllDataCategory.observe(viewLifecycleOwner, { categories ->
            //categoriesAdapter.categoriesList = categories
            categoriesAdapter.setCategoriesList(categories)
            categoriesAdapter.notifyDataSetChanged() // Asegura que el adaptador se actualice
        })


        val productsRecyclerView = binding.rvProducts
        productsRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        productsRecyclerView.adapter = productsAdapter

        mShoppingListDetailViewModel.readAllDataProduct.observe(viewLifecycleOwner, { products ->
            //productsAdapter.productsList = products
            productsAdapter.setProductList(products)
            productsAdapter.notifyDataSetChanged()
        })

        //El rollo no es que con observe tengas que asignarle ese valor todo el rato, el observe solo actualiza una variable cuando cambia.
        //Para nuestro caso tenemos que tener en el adapter una variable (categoriesList) que primero la inicialicemos con el observer readAllData
        //y luego cuando queramos cambiarla, simplemente le asignamos el nuevo valor y llamamos a notifyDataSetChanged() para que se actualice la vista.

    }

    private fun initListeners() {
        binding.btnCreateList.setOnClickListener {
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
                    mShoppingListDetailViewModel.addCategory(newCategory)
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
        mShoppingListDetailViewModel.updateCategory(updatedCategory)

        // Cambia el resto de categorías a no seleccionadas
        mShoppingListDetailViewModel.readAllDataCategory.value?.map {
            if (it.id == updatedCategory.id) {
                updatedCategory // Mantén la categoría actualizada
            } else {
                it.copy(isSelected = false) // Cambia el resto de las categorías a no seleccionadas
            }
        }?.let { updatedCategories ->
            mShoppingListDetailViewModel.updateCategories(updatedCategories)
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
            val newCategory = CategoryModel(category.id, newName, category.isSelected)
            mShoppingListDetailViewModel.updateCategory(newCategory)
            dialog.hide()
        }
        val btnDeleteCategory= dialog.findViewById<ImageButton>(R.id.ibDeleteCategory)
        btnDeleteCategory.setOnClickListener {
            mShoppingListDetailViewModel.deleteCategory(category)
            dialog.hide()
        }

    }

    private fun updateProducts(category: CategoryModel) {

        if (category.isSelected) {
            val selectedTasks =
                mShoppingListDetailViewModel.readAllDataProduct.value?.filter { it.categoryId == category.id }

            productsAdapter.setProductList(selectedTasks!!)
            productsAdapter.notifyDataSetChanged()
        } else {
            productsAdapter.setProductList(mShoppingListDetailViewModel.readAllDataProduct.value!!)
            productsAdapter.notifyDataSetChanged()
        }


    }
}