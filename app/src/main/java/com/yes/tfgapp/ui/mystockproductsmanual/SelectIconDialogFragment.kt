package com.yes.tfgapp.ui.mystockproductsmanual

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.GridLayoutManager
import com.yes.tfgapp.databinding.FragmentSelectIconDialogBinding
import com.yes.tfgapp.ui.mystockproductsmanual.adapter.SelectIconAdapter

class SelectIconDialogFragment(private val icons: List<Int>, private val listener: (Int) -> Unit) : DialogFragment() {

    private lateinit var binding : FragmentSelectIconDialogBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSelectIconDialogBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.recyclerViewIcons.layoutManager = GridLayoutManager(context, 4)
        binding.recyclerViewIcons.adapter = SelectIconAdapter(icons) {
            listener(it)
            dismiss()
        }
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT)
    }


}
