package com.yes.tfgapp.ui.mystockproductscan

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.navArgs
import com.squareup.picasso.Picasso
import com.yes.tfgapp.R
import com.yes.tfgapp.databinding.FragmentMyStockProductDetailBinding
import com.yes.tfgapp.ui.home.MainActivity
import com.yes.tfgapp.ui.mystock.MyStockFragment
import com.yes.tfgapp.ui.mystock.MyStockViewModel
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import java.util.concurrent.TimeUnit

class MyStockProductDetailFragment : DialogFragment() {

    private lateinit var binding: FragmentMyStockProductDetailBinding

    private val args: MyStockProductDetailFragmentArgs by navArgs()
    private lateinit var btnAddStockProduct : View
    private lateinit var btnCloseDialog : View
    private lateinit var mStockViewModel: MyStockViewModel
    private var selectedExpireDateButton: Button? = null




    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMyStockProductDetailBinding.inflate(inflater, container, false)
        initUI()
        initListeners()
        return binding.root
    }

    override fun onStart() {
        dialog?.window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )
        super.onStart()
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


        btnAddStockProduct = binding.efAddStockItem
        btnCloseDialog = binding.ibBackArrow

        if(args.currentStockProduct.id=="error"){
            binding.tvProductId.text = "Product not found"
            //binding.ivProductApiSearchImage.setImageResource(R.drawable.ic_delete)
        }else{
            binding.tvProductId.text = args.currentStockProduct.name
            Picasso.get()
                .load(args.currentStockProduct.image)
                .resize(300, 350)  // Ajusta las dimensiones de la imagen a las deseadas
                .centerCrop()      // Ajusta la imagen para llenar las dimensiones
                .into(binding.ivProductApiSearchImage)
        }

    }

    private fun initListeners() {
        btnAddStockProduct.setOnClickListener {
            addStockProduct()
            (parentFragment as? MyStockFragment)?.showCamera()
        }

        btnCloseDialog.setOnClickListener {
            (activity as MainActivity).showToolbar()
            (activity as MainActivity).showBottomNavInsta()
            dismiss()
        }

        binding.btn1Week.setOnClickListener {
            updateButtonStates(binding.btn1Week, listOf(binding.btn2Weeks, binding.btn1Month, binding.btn2Months))
        }

        binding.btn2Weeks.setOnClickListener {
            updateButtonStates(binding.btn2Weeks, listOf(binding.btn1Week, binding.btn1Month, binding.btn2Months))
        }

        binding.btn1Month.setOnClickListener {
            updateButtonStates(binding.btn1Month, listOf(binding.btn1Week, binding.btn2Weeks, binding.btn2Months))
        }

        binding.btn2Months.setOnClickListener {
            updateButtonStates(binding.btn2Months, listOf(binding.btn1Week, binding.btn2Weeks, binding.btn1Month))
        }

    }

    private fun updateButtonStates(selectedButton: Button, otherButtons: List<Button>) {
        selectedButton.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.accentRed))
        selectedButton.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
        selectedExpireDateButton=selectedButton

        otherButtons.forEach { button ->
            button.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.primaryGrey))
            button.setTextColor(ContextCompat.getColor(requireContext(), R.color.black))
        }
    }


    private fun addStockProduct() {
        val stockProduct = args.currentStockProduct
        val expirationDate = when (selectedExpireDateButton) {
            binding.btn1Week -> addDaysToDate(stockProduct.addedDate, 7)
            binding.btn2Weeks -> addDaysToDate(stockProduct.addedDate, 14)
            binding.btn1Month -> addDaysToDate(stockProduct.addedDate, 30)
            binding.btn2Months -> addDaysToDate(stockProduct.addedDate, 60)
            else -> stockProduct.addedDate
        }
        val daysToExpire = daysBetweenDates(stockProduct.addedDate, expirationDate).toInt()
        val updatedStockProduct = stockProduct.copy(expirationDate = expirationDate, daysToExpire = daysToExpire)
        mStockViewModel.updateStockProduct(updatedStockProduct)
        mStockViewModel.addProduct(updatedStockProduct)
    }


    fun addDaysToDate(date: String, days: Int): String {
        val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        val calendar = Calendar.getInstance()
        calendar.time = dateFormat.parse(date) ?: return date
        calendar.add(Calendar.DAY_OF_YEAR, days)
        return dateFormat.format(calendar.time)
    }

    fun daysBetweenDates(startDate: String, endDate: String): Long {
        val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        val start = dateFormat.parse(startDate)
        val end = dateFormat.parse(endDate)
        if (start != null && end != null) {
            val diffInMillis = end.time - start.time
            return TimeUnit.DAYS.convert(diffInMillis, TimeUnit.MILLISECONDS)
        }
        return 0
    }

}
