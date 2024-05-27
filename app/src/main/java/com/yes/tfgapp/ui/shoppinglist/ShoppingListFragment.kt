package com.yes.tfgapp.ui.shoppinglist

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
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
        onClickOpenConfiguration = { onConfigureItem(it) },
        onGetShoppingListProductsCount={ getShoppingListProductsCount(it)}
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

        mShoppingListViewModel = ViewModelProvider(this).get(ShoppingListViewModel::class.java)
        mShoppingListViewModel.readAllData.observe(viewLifecycleOwner){ shoppingList ->
            adapter.setData(shoppingList)
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

        val dialog = Dialog(requireContext())
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
        dialog.setContentView(R.layout.dialog_configure_shopping_list)
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
                } else {
                    Toast.makeText(
                        requireContext(),
                        "Please fill out all fields.",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }


            //animateButtonClick(btnSaveChanges, {
            //    if (newName.isNotEmpty()){
            //        mShoppingListViewModel.updateShoppingList(newShoppingList)
            //    }else{
            //        Toast.makeText(requireContext(), "Please fill out all fields.", Toast.LENGTH_LONG).show()
            //    }
            //}, dialog)

        }

        val btnDeleteList = dialog.findViewById<ImageButton>(R.id.ibDeleteList)
        btnDeleteList.setOnClickListener {
            animateButtonClick(btnDeleteList) {
                mShoppingListViewModel.deleteShoppingList(shoppingList)
                dialog.hide()
            }
        }
    }

    fun animateButtonClick(
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
        dialog.show()
        val btnAdd: Button = dialog.findViewById(R.id.btnCreateList)
        btnAdd.setOnClickListener {
            animateButtonClick(btnAdd) {
                addShoppingList(dialog)
                dialog.hide()
            }
        }
        //btnAdd.setOnClickListener {
        //    animateButtonClick(btnAdd, {
        //        addShoppingList(dialog)
        //    }, dialog)
        //}
    }

    private fun addShoppingList(dialog: Dialog) {
        val name = dialog.findViewById<TextInputEditText?>(R.id.etNewListName).text.toString()
        if (inputCheck(name)) {
            val shoppingList = ShoppingListModel(0, name, 0)
            mShoppingListViewModel.addShoppingList(shoppingList)
            Toast.makeText(requireContext(), "Successfully added!", Toast.LENGTH_LONG).show()
        } else {
            Toast.makeText(requireContext(), "Please fill out all fields.", Toast.LENGTH_LONG)
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
        ).get(
            ShoppingListDetailViewModel::
            class.java
        )

        //devuelve el tamaño de la lista de productos de la lista de la compra
        return mShoppingListDetailViewModel.allProductsShoppingList.size
    }

}