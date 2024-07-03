package com.yes.tfgapp.ui.shoppinglistadditems

import android.animation.Animator
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.app.Application
import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateDecelerateInterpolator
import android.widget.Button
import android.widget.ImageButton
import android.widget.Toast
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.imageview.ShapeableImageView
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.yes.tfgapp.R
import com.yes.tfgapp.databinding.FragmentShoppingListAddItemsBinding
import com.yes.tfgapp.domain.fixed.FixedCategories
import com.yes.tfgapp.domain.model.CategoryModel
import com.yes.tfgapp.domain.model.ProductModel
import com.yes.tfgapp.domain.model.ProductShoppingListModel
import com.yes.tfgapp.domain.model.ShoppingListModel
import com.yes.tfgapp.ui.home.MainActivity
import com.yes.tfgapp.ui.mystockproductsmanual.SelectIconDialogFragment
import com.yes.tfgapp.ui.shoppinglist.ShoppingListViewModel
import com.yes.tfgapp.ui.shoppinglistadditems.adapter.ShoppingListCategoriesAdapter
import com.yes.tfgapp.ui.shoppinglistadditems.adapter.ShoppingListProductsAdapter
import com.yes.tfgapp.ui.shoppinglistdetail.ShoppingListDetailViewModel

class ShoppingListAddItemsFragment : Fragment() {


    private val args: ShoppingListAddItemsFragmentArgs by navArgs()

    private lateinit var binding: FragmentShoppingListAddItemsBinding

    private lateinit var mShoppingListAddItemsViewModel: ShoppingListAddItemsViewModel
    private lateinit var mShoppingListViewModel: ShoppingListViewModel
    private lateinit var mShoppingListDetailViewModel: ShoppingListDetailViewModel

    private var isCategoriesLoaded = false
    private var isProductsLoaded = false
    private var iconSelected = R.drawable.ic_nuevacategoria1

    private val icons = listOf(

        R.drawable.ic_new_cat1 ,
        R.drawable.ic_new_cat2 ,
        R.drawable.ic_new_cat3 ,
        R.drawable.ic_new_cat4 ,
        R.drawable.ic_new_cat5 ,
        R.drawable.ic_new_cat6 ,
        R.drawable.ic_new_cat7 ,
        R.drawable.ic_new_cat8 ,
        R.drawable.ic_new_cat9 ,
        R.drawable.ic_new_cat10 ,
        R.drawable.ic_new_cat11 ,
        R.drawable.ic_new_cat12 ,
        R.drawable.ic_new_cat13 ,
        R.drawable.ic_new_cat14 ,
        R.drawable.ic_new_cat15 ,
        R.drawable.ic_new_cat16 ,
        R.drawable.ic_new_cat17 ,
        R.drawable.ic_new_cat18 ,
        R.drawable.ic_new_cat19 ,
        R.drawable.ic_new_cat20 ,
        R.drawable.ic_new_cat21 ,
        R.drawable.ic_new_cat22 ,
        R.drawable.ic_new_cat23 ,
        R.drawable.ic_new_cat24 ,
        R.drawable.ic_new_cat25 ,
        R.drawable.ic_new_cat26 ,
        R.drawable.ic_new_cat27 ,
        R.drawable.ic_new_cat28 ,
        R.drawable.ic_new_cat29 ,
        R.drawable.ic_new_cat30 ,
        R.drawable.ic_new_cat31 ,
        R.drawable.ic_new_cat32 ,
        R.drawable.ic_new_cat33 ,
        R.drawable.ic_new_cat34 ,
        R.drawable.ic_new_cat35,
        R.drawable.ic_others_category
    )

    private val categoriesAdapter = ShoppingListCategoriesAdapter(
        onItemSelected = { position -> updateCategories(position) },
        onConfigureSelected = { category -> configureCategories(category) })
    private val productsAdapter = ShoppingListProductsAdapter(
        { product -> addProductToList(product) },
        { product -> deleteProductFromList(product) }
    )


    override fun onResume() {

        super.onResume()
        (activity as MainActivity).setToolbarTitle(args.CurrentShoppingList.name)
        (activity as MainActivity).activeButtonBack()

    }

    private fun unselectAllCategories() {
        mShoppingListAddItemsViewModel.readAllDataCategory.value?.map {
            it.copy(isSelected = false)
        }?.let { updatedCategories ->
            mShoppingListAddItemsViewModel.updateCategories(updatedCategories)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentShoppingListAddItemsBinding.inflate(inflater, container, false)
        initUI()
        initListeners()
        return binding.root
    }

    private fun initUI() {
        binding.progressBar.visibility = View.VISIBLE
        mShoppingListAddItemsViewModel =
            ViewModelProvider(this)[ShoppingListAddItemsViewModel::class.java]

        mShoppingListViewModel = ViewModelProvider(this)[ShoppingListViewModel::class.java]
        mShoppingListDetailViewModel = ViewModelProvider(
            this,
            ShoppingListDetailViewModelFactory(
                requireActivity().application,
                args.CurrentShoppingList
            )
        )[ShoppingListDetailViewModel::
        class.java]


        val categoriesRecyclerView = binding.rvCategories
        categoriesRecyclerView.layoutManager =
            GridLayoutManager(requireContext(), 2, GridLayoutManager.HORIZONTAL, false)
        categoriesRecyclerView.adapter = categoriesAdapter

        mShoppingListAddItemsViewModel.readAllDataCategory.observe(viewLifecycleOwner) { categories ->
            if (categories.isNotEmpty()) {
                categoriesAdapter.setCategoriesList(categories)
                categoriesAdapter.notifyDataSetChanged()
                isCategoriesLoaded = true
                checkDataLoaded()
            }
        }


        val productsRecyclerView = binding.rvProducts
        productsRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        productsRecyclerView.adapter = productsAdapter

        mShoppingListAddItemsViewModel.readAllDataProduct.observe(viewLifecycleOwner) { products ->
            if (products.isNotEmpty()) {

                val sortedProducts = products.sortedBy { it.name }


                productsAdapter.setProductList(sortedProducts)
                productsAdapter.notifyDataSetChanged()

                isProductsLoaded = true
                checkDataLoaded()
            }
        }

        mShoppingListDetailViewModel.allProductsShoppingListLiveData.observe(viewLifecycleOwner) { productsInShoppingList ->
            productsAdapter.setProductsInShoppingList(productsInShoppingList)
        }

    }

    private fun initListeners() {
        binding.btnCreateCategory.setOnClickListener {

            animateButtonClick(binding.btnCreateCategory) {
                showNewCategoryDialog()
            }
        }

        binding.btnSearchView.setOnClickListener {
            unselectAllCategories()
            val action =
                ShoppingListAddItemsFragmentDirections.actionShoppingListAddItemsFragmentToSearchProductsFragment(
                    args.CurrentShoppingList
                )
            binding.root.findNavController().navigate(action)
        }
    }

    private fun showNewCategoryDialog() {
        val dialog = Dialog(requireContext())
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
        dialog.setContentView(R.layout.dialog_new_category)
        dialog.window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        dialog.show()

        val btnChangeIcon = dialog.findViewById<ShapeableImageView>(R.id.ivAddIcon)
        btnChangeIcon.setOnClickListener {
            showSelectIconDialog(dialog)
        }

        val btnSaveCategory = dialog.findViewById<View>(R.id.btnCreateCategory)
        btnSaveCategory.setOnClickListener {

            animateButtonClick(btnSaveCategory) {
                createNewCategory(dialog)
            }
        }
    }

    private fun showSelectIconDialog(dialogNewCategory: Dialog) {

        val iconNewCategory = dialogNewCategory.findViewById<ShapeableImageView>(R.id.ivAddIcon)
        val dialog = SelectIconDialogFragment(icons) { selectedIcon ->
            iconNewCategory.setImageResource(selectedIcon)
            iconSelected = selectedIcon
        }
        dialog.show(parentFragmentManager, "SelectIconDialogFragment")
    }


    private fun createNewCategory(dialog: Dialog) {
        val newCategoryName =
            dialog.findViewById<TextInputEditText>(R.id.etNewCategoryName).text.toString()

        if (newCategoryName.isNotEmpty()) {
            val newCategory =
                CategoryModel(id = 0, name = newCategoryName, isSelected = false, isDefault = false, icon = iconSelected)
            mShoppingListAddItemsViewModel.addCategory(newCategory)
            Toast.makeText(
                requireContext(),
                getString(R.string.category_created_correctly), Toast.LENGTH_LONG
            )
                .show()
            dialog.hide()
        } else {
            Toast.makeText(requireContext(), getString(R.string.fill_the_name), Toast.LENGTH_LONG)
                .show()
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

    private fun checkDataLoaded() {
        if (isCategoriesLoaded && isProductsLoaded) {
            binding.progressBar.visibility = View.GONE
        } else {
            binding.progressBar.visibility = View.VISIBLE
        }
    }

    private fun updateCategories(category: CategoryModel) {

        val updatedCategory = category.copy(isSelected = !category.isSelected)
        mShoppingListAddItemsViewModel.updateCategory(updatedCategory)


        mShoppingListAddItemsViewModel.readAllDataCategory.value?.map {
            if (it.id == updatedCategory.id) {
                updatedCategory
            } else {
                it.copy(isSelected = false)
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
        dialog.window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        val ivCategoryIcon = dialog.findViewById<ShapeableImageView>(R.id.ivAddIcon)
        ivCategoryIcon.setImageResource(category.icon)
        iconSelected = category.icon
        dialog.show()
        val textInputLayout: TextInputLayout = dialog.findViewById(R.id.tilUpdateNameCategory)
        textInputLayout.hint = category.name

        ivCategoryIcon.setOnClickListener {
            showSelectIconDialog(dialog)
        }

        val btnSaveChanges = dialog.findViewById<Button>(R.id.btnSaveChangesCategory)
        btnSaveChanges.setOnClickListener {
            animateButtonClick(btnSaveChanges) {
                val newName =
                    dialog.findViewById<TextInputEditText?>(R.id.etUpdateNameCategory).text.toString()
                val newCategory = CategoryModel(
                    category.id,
                    newName,
                    category.isSelected,
                    icon = iconSelected,
                    isDefault = category.isDefault
                )
                if (newName.isNotEmpty()) {
                    mShoppingListAddItemsViewModel.updateCategory(newCategory)
                    dialog.hide()
                    Toast.makeText(
                        requireContext(),
                        getString(R.string.renamed_category_correctly), Toast.LENGTH_LONG
                    )
                        .show()
                } else {
                    Toast.makeText(
                        requireContext(),
                        getString(R.string.fill_the_name),
                        Toast.LENGTH_LONG
                    )
                        .show()
                }
            }
        }
        val btnDeleteCategory = dialog.findViewById<ImageButton>(R.id.ibDeleteCategory)
        if (category.isDefault) {
            btnDeleteCategory.visibility = View.GONE
        } else {
            btnDeleteCategory.visibility = View.VISIBLE
        }

        btnDeleteCategory.setOnClickListener {
            animateButtonClick(btnDeleteCategory) {
                deleteCategoryWithItemsUpdate(category, dialog)
            }
        }

    }

    private fun deleteCategoryWithItemsUpdate(category: CategoryModel, dialog: Dialog) {
        val otherCategory = FixedCategories.getCategoryIdByName("Otros")

        if (otherCategory != null) {
            mShoppingListAddItemsViewModel.deleteCategoryWithItemsUpdate(category, otherCategory)
            dialog.hide()
            Toast.makeText(
                requireContext(),
                getString(R.string.category_deleted_correctly),
                Toast.LENGTH_LONG
            ).show()
        } else {
            Toast.makeText(
                requireContext(), "No se encontro la categoria otros",
                Toast.LENGTH_LONG
            ).show()
        }

    }

    private fun updateProducts(category: CategoryModel) {
        if (category.isSelected) {
            val selectedTasks =
                mShoppingListAddItemsViewModel.readAllDataProduct.value?.filter { it.categoryId == category.id }


            val sortedSelectedTasks = selectedTasks?.sortedBy { it.name }


            productsAdapter.setProductList(sortedSelectedTasks!!)
            productsAdapter.notifyDataSetChanged()
        } else {

            val allProducts = mShoppingListAddItemsViewModel.readAllDataProduct.value
            val sortedAllProducts = allProducts?.sortedBy { it.name }


            productsAdapter.setProductList(sortedAllProducts!!)
            productsAdapter.notifyDataSetChanged()
        }
    }

    private fun addProductToList(product: ProductModel) {
        val productShoppingList = ProductShoppingListModel(
            shoppingListId = this.args.CurrentShoppingList.id,
            productId = product.id
        )
        mShoppingListAddItemsViewModel.addProductToList(productShoppingList)
    }

    private fun deleteProductFromList(product: ProductModel) {
        val productShoppingList = ProductShoppingListModel(
            shoppingListId = this.args.CurrentShoppingList.id,
            productId = product.id
        )
        mShoppingListAddItemsViewModel.deleteProductFromList(productShoppingList)
    }


}