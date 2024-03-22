package com.yes.tfgapp.ui.mystock

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.yes.tfgapp.ui.home.MainActivity
import com.yes.tfgapp.databinding.FragmentMyStockBinding


class MyStockFragment : Fragment() {

    private var _binding: FragmentMyStockBinding? = null
    private val binding get() = _binding!!

    override fun onResume() {
        super.onResume()
        (activity as MainActivity).setToolbarTitle("Mi stock")
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentMyStockBinding.inflate(inflater, container, false)
        return binding.root
    }

}