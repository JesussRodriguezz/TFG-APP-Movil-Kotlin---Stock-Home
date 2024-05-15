package com.yes.tfgapp.ui.home

import android.content.Context
import android.os.Bundle
import android.util.TypedValue
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.yes.tfgapp.R
import com.yes.tfgapp.databinding.ActivityMainBinding


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
        keyboardBehaviour()
    }

    private fun keyboardBehaviour() {
        var isKeyboardVisible = false

        val activityRootView = binding.activityRoot
        activityRootView.viewTreeObserver.addOnGlobalLayoutListener {
            val heightDiff = activityRootView.rootView.height - activityRootView.height
            val keyboardVisible = heightDiff > dpToPx(this, 200f)

            if (keyboardVisible && !isKeyboardVisible) {
                hideBottomNav()
            } else if (!keyboardVisible && isKeyboardVisible) {
                showBottomNav()
            }

            isKeyboardVisible = keyboardVisible
        }
    }

    private fun dpToPx(context: Context, valueInDp: Float): Float {
        val metrics = context.resources.displayMetrics
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, valueInDp, metrics)
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
        binding.ibBackArrow.visibility = View.VISIBLE
        binding.ibBackArrow.setOnClickListener {
            navController.popBackStack()
        }
    }

    fun hideButtonBack() {
        binding.ibBackArrow.visibility = View.INVISIBLE
    }

    fun hideBottomNav() {
        binding.bottomNavView.visibility = View.GONE
    }

    fun showToolbar() {
        binding.toolbar.visibility = View.VISIBLE
    }

    fun showBottomNavInsta() {
        binding.bottomNavView.visibility = View.VISIBLE
    }



    fun hideToolbar() {
        binding.toolbar.visibility = View.GONE
    }

    private fun showBottomNav() {
        binding.bottomNavView.apply {
            alpha = 0f // Configura la transparencia inicialmente a 0
            visibility = View.VISIBLE
            animate()
                .alpha(1f) // Establece la transparencia final a 1
                .setDuration(500) // Duración de la animación en milisegundos
                .setInterpolator(AccelerateDecelerateInterpolator()) // Interpolador para suavizar la animación
                .start() // Inicia la animación
        }
    }
}