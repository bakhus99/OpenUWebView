package com.student.openuwebview

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.student.openuwebview.databinding.ActivityMainBinding


class MainActivity : AppCompatActivity() {
    @SuppressLint("SetJavaScriptEnabled")
    private lateinit var navController: NavController
    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setTheme(R.style.Theme_OpenUWebView)
        setContentView(binding.root)

        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.findNavController()

        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.forumFragment,
                R.id.chatFragment,
                R.id.coursesFragment,
                R.id.startFragment,
                R.id.viewPagerFragment,
                R.id.openUFragment
            )
        )
        setupActionBarWithNavController(navController, appBarConfiguration)

        binding.bottomDrawer.setupWithNavController(navController)

    }

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp() || super.onSupportNavigateUp()
    }

    fun setBottomNavigationVisibility(visibility: Int) {
        binding.bottomDrawer.visibility = visibility
    }


}