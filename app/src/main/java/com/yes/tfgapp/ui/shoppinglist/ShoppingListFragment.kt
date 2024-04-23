package com.yes.tfgapp.ui.shoppinglist

import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.yes.tfgapp.ui.home.MainActivity
import com.yes.tfgapp.R
import com.yes.tfgapp.databinding.FragmentShoppingListBinding
import com.yes.tfgapp.domain.model.ShoppingListModel
import com.yes.tfgapp.ui.shoppinglist.adapter.ShoppingListAdapter
import kotlin.math.log

class ShoppingListFragment : Fragment() {

    private var _binding: FragmentShoppingListBinding? = null
    private val binding get() = _binding!!

    private lateinit var mShoppingListViewModel: ShoppingListViewModel


    override fun onResume() {
        super.onResume()
        (activity as MainActivity).setToolbarTitle("Lista de la compra")
        (activity as MainActivity).hideButtonBack()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentShoppingListBinding.inflate(inflater, container, false)
        initUI()
        initListeners()
        return binding.root
    }

    private fun initUI() {
        val adapter = ShoppingListAdapter(
            onClickOpenConfiguration={onConfigureItem(it)}
        )
        val recyclerView = binding.rvShoppingList
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        mShoppingListViewModel = ViewModelProvider(this).get(ShoppingListViewModel::class.java)
        mShoppingListViewModel.readAllData.observe(viewLifecycleOwner, { shoppingList ->
            adapter.setData(shoppingList)
        })


    }

    fun onConfigureItem(shoppingList: ShoppingListModel) {
        val dialog =Dialog(requireContext())
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
        dialog.setContentView(R.layout.dialog_configure_shopping_list)
        dialog.show()
        val textInputLayout:TextInputLayout = dialog.findViewById(R.id.tilUpdateNameList)
        textInputLayout.hint = shoppingList.name


        val btnSaveChanges = dialog.findViewById<Button>(R.id.btnSaveChanges)
        btnSaveChanges.setOnClickListener {
            val newName = dialog.findViewById<TextInputEditText?>(R.id.etUpdateNameList).text.toString()
            val newShoppingList = ShoppingListModel(shoppingList.id, newName, shoppingList.quantity)
            mShoppingListViewModel.updateShoppingList(newShoppingList)
            dialog.hide()
        }
        val btnDeleteList = dialog.findViewById<ImageButton>(R.id.ibDeleteList)
        btnDeleteList.setOnClickListener {
            mShoppingListViewModel.deleteShoppingList(shoppingList)
            dialog.hide()
        }
    }

    private fun initListeners() {

        binding.extendedFab.setOnClickListener {
            showDialogNewList()
        }
    }

    private fun showDialogNewList() {
        val dialog = Dialog(requireContext())
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)

        dialog.setContentView(R.layout.dialog_new_shopping_list)
        dialog.show()
        val btnAdd:Button = dialog.findViewById(R.id.btnCreateList)
        btnAdd.setOnClickListener {
            addShoppingList(dialog)
        }
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
        return !(name.isEmpty())
    }

}