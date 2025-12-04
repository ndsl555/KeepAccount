package com.example.keepaccount

import android.os.Bundle
import androidx.annotation.IdRes
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentManager
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import com.example.keepaccount.databinding.ActivityMainBinding
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController
    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var tutorialPref: TutorialPref // ⭐ 用 SharedPreferences 存取

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        tutorialPref = TutorialPref(this) // ⭐ 初始化 Pref

        initView()
        checkTutorialStatus() // ⭐ 啟動時檢查是否要顯示教學 Dialog
    }

    private fun initView() {
        val bottomNavView: BottomNavigationView = findViewById(R.id.mainBottomNavigationView)
        val navHostFragment = supportFragmentManager.findNavHostFragment(R.id.navHostView)
        navController = navHostFragment.navController
        navController.setGraph(R.navigation.nav_home_graph)

        appBarConfiguration =
            AppBarConfiguration(
                setOf(
                    R.id.navigationItemListFragment,
                    R.id.navigationEventListFragment,
                    R.id.navigationStripFragment,
                    R.id.navigationBarcodeFragment,
                    R.id.navigationVisualFragment,
                ),
            )

        setupActionBarWithNavController(navController, appBarConfiguration)

        bottomNavView.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.nav_list ->
                    navController.navigate(NavHomeGraphDirections.actionGlobalToNavigationitemListFragment())

                R.id.nav_event ->
                    navController.navigate(NavHomeGraphDirections.actionGlobalToNavigationEventListFragment())

                R.id.nav_budget ->
                    navController.navigate(NavHomeGraphDirections.actionGlobalToNavigationStripFragment())

                R.id.nav_barcode ->
                    navController.navigate(NavHomeGraphDirections.actionGlobalToNavigationBarcodeFragment())

                R.id.nav_report ->
                    navController.navigate(NavHomeGraphDirections.actionGlobalToNavigationVisualFragment())
            }
            true
        }
    }

    /**
     * ⭐ 不用 Flow，不用 DB，不用 ViewModel
     */
    private fun checkTutorialStatus() {
        if (!tutorialPref.isTutorialShown()) {
            showTutorialDialog()
        }
    }

    /**
     * ⭐ 用 SharedPreferences 控制之後不再跳
     */
    private fun showTutorialDialog() {
        MaterialAlertDialogBuilder(this)
            .setTitle(getString(android.R.string.dialog_alert_title))
            .setMessage(getString(R.string.jump_to_nav_event_list_fragment))
            .setCancelable(false)
            .setPositiveButton(getString(R.string.yes)) { dialog, _ ->

                // ⭐ 記錄為「已讀」
                tutorialPref.setTutorialShown()

                // 導頁
                navController.navigate(
                    NavHomeGraphDirections.actionGlobalToNavigationEventListFragment(),
                )
                binding.mainBottomNavigationView.selectedItemId = R.id.nav_event

                dialog.dismiss()
            }
            .show()
    }

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp() || super.onSupportNavigateUp()
    }
}

/**
 * ⭐ Extension 幫你找 NavHostFragment
 */
private fun FragmentManager.findNavHostFragment(
    @IdRes id: Int,
) = findFragmentById(id) as NavHostFragment
