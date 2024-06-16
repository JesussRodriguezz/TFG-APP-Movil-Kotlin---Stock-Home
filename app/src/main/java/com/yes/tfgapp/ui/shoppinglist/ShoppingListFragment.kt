package com.yes.tfgapp.ui.shoppinglist

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
import android.widget.LinearLayout
import android.widget.Toast
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.yes.tfgapp.ui.home.MainActivity
import com.yes.tfgapp.R
import com.yes.tfgapp.databinding.FragmentShoppingListBinding
import com.yes.tfgapp.domain.model.ShoppingListModel
import com.yes.tfgapp.ui.shoppinglist.adapter.ShoppingListAdapter
import com.yes.tfgapp.ui.shoppinglistdetail.ShoppingListDetailViewModel

class ShoppingListFragment : Fragment() {

    private lateinit var binding: FragmentShoppingListBinding
    private lateinit var mShoppingListViewModel: ShoppingListViewModel
    private lateinit var mShoppingListDetailViewModel: ShoppingListDetailViewModel


    private val adapter = ShoppingListAdapter(
        onClickOpenConfiguration = { onConfigureItem(it) }
    )

    override fun onResume() {
        super.onResume()
        (activity as MainActivity).setToolbarTitle("Lista de la compra")
        (activity as MainActivity).hideButtonBack()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentShoppingListBinding.inflate(inflater, container, false)
        initUI()
        initListeners()
        return binding.root
    }

    private fun initUI() {



        val recyclerView = binding.rvShoppingList
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(requireContext())



        mShoppingListViewModel = ViewModelProvider(this)[ShoppingListViewModel::class.java]
        mShoppingListViewModel.readAllData.observe(viewLifecycleOwner) { shoppingList ->
            adapter.setData(shoppingList)
            // Ajustar visibilidad del LinearLayout
            if (shoppingList.isEmpty()) {
                binding.llNoLists.visibility = View.VISIBLE
            } else {
                binding.llNoLists.visibility = View.GONE
            }
        }
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

    private fun onConfigureItem(shoppingList: ShoppingListModel) {

        showOptionsDialog(shoppingList)

    }

    private fun showOptionsDialog(shoppingList: ShoppingListModel) {

        val dialog = Dialog(requireContext())
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
        dialog.setContentView(R.layout.dialog_edit_shopping_list)
        dialog.window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        dialog.show()

        dialog.findViewById<LinearLayout>(R.id.option1).setOnClickListener {
            showDialogRenameList(shoppingList)
            dialog.dismiss()

        }

        dialog.findViewById<LinearLayout>(R.id.option2).setOnClickListener {
            emptyAllList(shoppingList)
            dialog.dismiss()
            Toast.makeText(requireContext(),
                getString(R.string.empty_list_correctly), Toast.LENGTH_LONG)
                .show()

        }

        dialog.findViewById<LinearLayout>(R.id.option3).setOnClickListener {
            mShoppingListViewModel.deleteShoppingList(shoppingList)
            dialog.dismiss()
            Toast.makeText(requireContext(),
                getString(R.string.deleted_list_correctly), Toast.LENGTH_LONG)
                .show()
        }

        dialog.show()
    }

    private fun emptyAllList(shoppingList: ShoppingListModel) {

        mShoppingListDetailViewModel = ViewModelProvider(
            this,
            ShoppingListDetailViewModelFactory(
                requireActivity().application,
                shoppingList
            )
        )[ShoppingListDetailViewModel::
        class.java]
        mShoppingListDetailViewModel.emptyAllList(shoppingList)
    }

    private fun showDialogRenameList(shoppingList: ShoppingListModel) {
        val dialog = Dialog(requireContext())
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
        dialog.setContentView(R.layout.dialog_configure_shopping_list)
        dialog.window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        dialog.show()
        val textInputLayout: TextInputLayout = dialog.findViewById(R.id.tilUpdateNameList)
        textInputLayout.hint = shoppingList.name

        val btnSaveChanges = dialog.findViewById<Button>(R.id.btnSaveChanges)
        btnSaveChanges.setOnClickListener {
            val newName =
                dialog.findViewById<TextInputEditText?>(R.id.etUpdateNameList).text.toString()
            val newShoppingList = ShoppingListModel(shoppingList.id, newName, shoppingList.quantity)

            animateButtonClick(btnSaveChanges) {
                if (newName.isNotEmpty()) {
                    mShoppingListViewModel.updateShoppingList(newShoppingList)
                    dialog.hide()
                    Toast.makeText(requireContext(),
                        getString(R.string.renamed_list_correctly), Toast.LENGTH_LONG)
                        .show()
                } else {
                    Toast.makeText(requireContext(),
                        getString(R.string.fill_the_name), Toast.LENGTH_LONG)
                        .show()
                }
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

    private fun initListeners() {
        binding.extendedFab.setOnClickListener {
            // Animación de rotación de 360 grados
            val rotation = ObjectAnimator.ofFloat(binding.extendedFab, "rotation", 0f, 180f)
            rotation.duration = 400 // Duración de la animación en milisegundos
            rotation.interpolator = AccelerateDecelerateInterpolator() // Interpolador para una animación suave
            rotation.start()
            showDialogNewList()
        }
    }

    private fun showDialogNewList() {
        val dialog = Dialog(requireContext())
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)

        dialog.setContentView(R.layout.dialog_new_shopping_list)
        dialog.window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        dialog.show()
        val btnAdd: Button = dialog.findViewById(R.id.btnCreateList)
        btnAdd.setOnClickListener {
            animateButtonClick(btnAdd) {
                addShoppingList(dialog)
                dialog.hide()
            }
        }

    }

    private fun addShoppingList(dialog: Dialog) {
        val name = dialog.findViewById<TextInputEditText?>(R.id.etNewListName).text.toString()
        if (inputCheck(name)) {
            val shoppingList = ShoppingListModel(0, name, 0)
            mShoppingListViewModel.addShoppingList(shoppingList)
            Toast.makeText(requireContext(),
                getString(R.string.list_created_correctly), Toast.LENGTH_LONG).show()
        } else {
            Toast.makeText(requireContext(), getString(R.string.fill_the_name), Toast.LENGTH_LONG)
                .show()
        }
        dialog.hide()
    }

    private fun inputCheck(name: String): Boolean {
        return (name.isNotEmpty())
    }

    private fun getShoppingListProductsCount(currentShoppingList: ShoppingListModel): Int {
        mShoppingListDetailViewModel = ViewModelProvider(
            this,
            ShoppingListDetailViewModelFactory(
                requireActivity().application,
                currentShoppingList
            )
        )[ShoppingListDetailViewModel::
        class.java]

        //devuelve el tamaño de la lista de productos de la lista de la compra
        return mShoppingListDetailViewModel.allProductsShoppingList.size
    }

}