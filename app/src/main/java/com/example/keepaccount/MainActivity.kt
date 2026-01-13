package com.example.keepaccount

import android.os.Bundle
import android.widget.Toast
import androidx.annotation.IdRes
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import com.example.keepaccount.Utils.NetworkUtils
import com.example.keepaccount.Utils.NetworkUtils.getNetworkType
import com.example.keepaccount.Utils.NetworkUtils.isNetworkConnected
import com.example.keepaccount.databinding.ActivityMainBinding
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController
    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var tutorialPref: TutorialPref //  用 SharedPreferences 存取

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        tutorialPref = TutorialPref(this) //  初始化 Pref
        checkNetworkStatus()
        observeNetworkState()
        initView()
        checkTutorialStatus() //  啟動時檢查是否要顯示教學 Dialog
    }

    private fun observeNetworkState() {
        lifecycleScope.launch {
            NetworkUtils.networkState.collect { isConnected ->
                if (isConnected) {
                    // 網路恢复
                    showNetworkRestored()
                } else {
                    // 網路斷開
                    showNetworkLost()
                }
            }
        }
    }

    private fun showNetworkRestored() {
        // 显示網路恢复提示
        Toast.makeText(this, "網路已恢复", Toast.LENGTH_SHORT).show()
    }

    private fun showNetworkLost() {
        // 显示網路斷開提示
        showNoWifiDialog()
        Toast.makeText(this, "網路已斷開", Toast.LENGTH_SHORT).show()
    }

    private fun checkNetworkStatus() {
        if (isNetworkConnected(baseContext)) {
            val networkType = getNetworkType(baseContext)
            Toast.makeText(this, "網路已連接 - $networkType", Toast.LENGTH_SHORT).show()
        } else {
            showNoWifiDialog()
            Toast.makeText(this, "網路未連接", Toast.LENGTH_SHORT).show()
        }
    }

    private fun showNoWifiDialog() {
        NoNetworkDialogFragment().show(supportFragmentManager, "NoNetworkDialogFragment")
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
                    R.id.navigationBarCodeAndInvoiceFragment,
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

                R.id.nav_barcode_invoice ->
                    navController.navigate(NavHomeGraphDirections.actionGlobalToNavigationBarCodeAndInvoiceFragment())

                R.id.nav_report ->
                    navController.navigate(NavHomeGraphDirections.actionGlobalToNavigationVisualFragment())
            }
            true
        }
    }

    /**
     *  不用 Flow，不用 DB，不用 ViewModel
     */
    private fun checkTutorialStatus() {
        if (!tutorialPref.isTutorialShown()) {
            showTutorialDialog()
        }
    }

    /**
     *  用 SharedPreferences 控制之後不再跳
     */
    private fun showTutorialDialog() {
        MaterialAlertDialogBuilder(this)
            .setTitle(getString(android.R.string.dialog_alert_title))
            .setMessage(getString(R.string.jump_to_nav_event_list_fragment))
            .setCancelable(false)
            .setPositiveButton(getString(R.string.yes)) { dialog, _ ->

                //  記錄為「已讀」
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
 *  Extension 幫你找 NavHostFragment
 */
private fun FragmentManager.findNavHostFragment(
    @IdRes id: Int,
) = findFragmentById(id) as NavHostFragment
