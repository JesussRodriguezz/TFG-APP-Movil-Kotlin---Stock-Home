package com.yes.tfgapp.ui.shoppinglist

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.yes.tfgapp.R
import com.yes.tfgapp.databinding.FragmentAddShoppingListBinding
import com.yes.tfgapp.domain.model.ShoppingListModel

class AddShoppingListFragment : Fragment() {

    private lateinit var mShoppingListViewModel: ShoppingListViewModel
    private lateinit var binding : FragmentAddShoppingListBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentAddShoppingListBinding.inflate(inflater, container, false)

        initUI()
        initListeners()



        return binding.root
    }

    private fun initUI() {
        mShoppingListViewModel = ViewModelProvider(this).get(ShoppingListViewModel::class.java)
    }

    private fun initListeners() {
        binding.btnAdd.setOnClickListener {
            addShoppingList()
        }
    }

    private fun addShoppingList() {
        val name = binding.etShoppingListName.text.toString()
        val numProducts = binding.etNumItems.text.toString()

        if (inputCheck(name, numProducts)) {
            val shoppingList = ShoppingListModel(0, name, Integer.parseInt(numProducts))
            mShoppingListViewModel.addShoppingList(shoppingList)
            Toast.makeText(requireContext(), "Successfully added!", Toast.LENGTH_LONG).show()
            findNavController().navigate(R.id.action_addShoppingListFragment_to_shoppingListFragment)
        } else {
            Toast.makeText(requireContext(), "Please fill out all fields.", Toast.LENGTH_LONG)
                .show()
        }

    }

    private fun inputCheck(name: String, numProducts: String): Boolean {
        return !(name.isEmpty() && numProducts.isEmpty())
    }

}