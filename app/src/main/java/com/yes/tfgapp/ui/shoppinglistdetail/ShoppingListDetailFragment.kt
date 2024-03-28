package com.yes.tfgapp.ui.shoppinglistdetail

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.navArgs
import com.yes.tfgapp.R
import com.yes.tfgapp.databinding.FragmentShoppingListDetailBinding
import com.yes.tfgapp.ui.home.MainActivity

class ShoppingListDetailFragment : Fragment() {

    private val args: ShoppingListDetailFragmentArgs by navArgs()

    private lateinit var _binding: FragmentShoppingListDetailBinding
    private val binding get() = _binding!!

    override fun onResume() {
        super.onResume()
        (activity as MainActivity).setToolbarTitle(args.CurrentShoppingList.name)
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentShoppingListDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

}