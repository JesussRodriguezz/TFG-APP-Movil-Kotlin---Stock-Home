package com.yes.tfgapp.ui.mystockproductdetail

import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.navArgs
import com.squareup.picasso.Picasso
import com.yes.tfgapp.R
import com.yes.tfgapp.databinding.FragmentMyStockProductDetailBinding
import com.yes.tfgapp.ui.home.MainActivity
import com.yes.tfgapp.ui.mystock.MyStockFragment
import com.yes.tfgapp.ui.mystock.MyStockViewModel

class MyStockProductDetailFragment : DialogFragment() {

    private lateinit var binding: FragmentMyStockProductDetailBinding

    private val args: MyStockProductDetailFragmentArgs by navArgs()
    private lateinit var btnAddStockProduct : View
    private lateinit var btnCloseDialog : View
    private lateinit var mStockViewModel: MyStockViewModel

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
        mStockViewModel= ViewModelProvider(this).get(MyStockViewModel::class.java)


        btnAddStockProduct = binding.efAddStockProduct
        btnCloseDialog = binding.efCloseDialog

        if(args.currentStockProduct.id=="error"){
            binding.tvProductId.text = "Product not found"
            //binding.ivProductApiSearchImage.setImageResource(R.drawable.ic_delete)
        }else{
            binding.tvProductId.text = args.currentStockProduct.name
            Picasso.get().load(args.currentStockProduct.image).into(binding.ivProductApiSearchImage)
        }

    }

    private fun initListeners() {
        btnAddStockProduct.setOnClickListener {
            addStockProduct()
            //(parentFragment as? MyStockFragment)?.showCamera()
        }

        btnCloseDialog.setOnClickListener {
            (activity as MainActivity).showToolbar()
            (activity as MainActivity).showBottomNavInsta()
            dismiss()
        }

    }

    private fun addStockProduct() {
        val stockProduct = args.currentStockProduct
        //mStockViewModel.addStockProduct(stockProduct)
        mStockViewModel.addProduct(stockProduct)

    }

}
