package com.yes.tfgapp.ui.home

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.yes.tfgapp.databinding.ActivityMainBinding
import com.yes.tfgapp.R

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController

    private lateinit var tvTitle: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initUI()
    }


    private fun initUI() {
        initNavigation()
    }

    private fun initNavigation() {
        val navHost= supportFragmentManager.findFragmentById(R.id.fragmentContainerView) as NavHostFragment
        navController = navHost.navController
        binding.bottomNavView.setupWithNavController(navController)

        tvTitle = binding.tvTitleToolbar
    }

    fun setToolbarTitle(title: String) {
        tvTitle.text = title
    }

    fun activeButtonBack() {
        binding.ibBackArrow.visibility = android.view.View.VISIBLE
        binding.ibBackArrow.setOnClickListener {
            //onBackPressed()
            navController.popBackStack()
        }
    }

    fun hideButtonBack() {
        binding.ibBackArrow.visibility = android.view.View.INVISIBLE
    }

    fun hideBottomNav() {
        binding.bottomNavView.visibility = android.view.View.GONE
    }

    fun showBottomNav() {
        binding.bottomNavView.visibility = android.view.View.VISIBLE
    }
}