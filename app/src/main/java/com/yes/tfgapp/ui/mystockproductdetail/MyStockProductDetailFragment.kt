package com.yes.tfgapp.ui.mystockproductdetail

import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.navigation.fragment.navArgs
import com.yes.tfgapp.R
import com.yes.tfgapp.databinding.FragmentMyStockProductDetailBinding
import com.yes.tfgapp.ui.home.MainActivity
import com.yes.tfgapp.ui.mystock.MyStockFragment

class MyStockProductDetailFragment : DialogFragment() {

    private lateinit var binding: FragmentMyStockProductDetailBinding

    private val args: MyStockProductDetailFragmentArgs by navArgs()
    private lateinit var btnBack : View
    private lateinit var btnBack2 : View

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMyStockProductDetailBinding.inflate(inflater, container, false)
        initUI()
        initListeners()
        return binding.root
    }



    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        dialog?.window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )
    }

    private fun initUI() {
        btnBack = binding.efTestBackButton
        btnBack2 = binding.efTestBackButton2


        if (args.productName.isNotEmpty()) {
            binding.tvProductId.text = args.productName
        }
    }

    private fun initListeners() {
        btnBack.setOnClickListener {
            (parentFragment as? MyStockFragment)?.showCamera()
        }

        btnBack2.setOnClickListener {
            (activity as MainActivity).showToolbar()
            (activity as MainActivity).showBottomNavInsta()
            dismiss()
        }
    }

}
