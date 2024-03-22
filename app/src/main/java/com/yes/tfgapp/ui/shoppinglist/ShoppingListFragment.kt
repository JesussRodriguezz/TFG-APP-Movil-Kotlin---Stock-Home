package com.yes.tfgapp.ui.shoppinglist

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.yes.tfgapp.ui.home.MainActivity
import com.yes.tfgapp.R
import com.yes.tfgapp.databinding.FragmentShoppingListBinding
import com.yes.tfgapp.ui.shoppinglist.adapter.ShoppingListAdapter

class ShoppingListFragment : Fragment() {

    private var _binding: FragmentShoppingListBinding? = null
    private val binding get() = _binding!!

    private lateinit var mShoppingListViewModel: ShoppingListViewModel


    override fun onResume() {
        super.onResume()
        (activity as MainActivity).setToolbarTitle("Lista de la compra")
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
        val adapter = ShoppingListAdapter()
        val recyclerView = binding.rvShoppingList
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        mShoppingListViewModel = ViewModelProvider(this).get(ShoppingListViewModel::class.java)
        mShoppingListViewModel.readAllData.observe(viewLifecycleOwner, { shoppingList ->
            adapter.setData(shoppingList)
        })


    }

    private fun initListeners() {
        binding.extendedFab.setOnClickListener {
            binding.root.findNavController()
                .navigate(R.id.action_shoppingListFragment_to_addShoppingListFragment)
        }
    }

}