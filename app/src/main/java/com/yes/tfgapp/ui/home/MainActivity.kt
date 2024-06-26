package com.yes.tfgapp.ui.home

import android.content.Context
import android.os.Bundle
import android.util.TypedValue
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
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
        window.statusBarColor = ContextCompat.getColor(this, R.color.primaryDark)
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
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.fragmentContainerView) as NavHostFragment
        navController = navHostFragment.navController
        binding.bottomNavView.setupWithNavController(navController)

        tvTitle = binding.tvTitleToolbar

        navController.addOnDestinationChangedListener { _, destination, _ ->
            when (destination.id) {
                R.id.shoppingListFragment, R.id.myStockFragment -> {
                    binding.ibBackArrow.visibility = View.INVISIBLE
                }
                else -> {
                    binding.ibBackArrow.visibility = View.VISIBLE
                }
            }
        }

        binding.bottomNavView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.shoppingListFragment -> {
                    navController.navigate(R.id.shoppingListFragment)
                    true
                }
                R.id.myStockFragment -> {
                    navController.navigate(R.id.myStockFragment)
                    true
                }
                else -> false
            }
        }
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

    private fun hideBottomNav() {
        binding.bottomNavView.visibility = View.GONE
    }

    fun showToolbar() {
        binding.toolbar.visibility = View.VISIBLE
    }

    fun showBottomNavInsta() {
        binding.bottomNavView.visibility = View.VISIBLE
    }



    private fun showBottomNav() {
        binding.bottomNavView.apply {
            alpha = 0f
            visibility = View.VISIBLE
            animate()
                .alpha(1f)
                .setDuration(500)
                .setInterpolator(AccelerateDecelerateInterpolator())
                .start()
        }
    }
}