package com.example.keepaccount

import android.os.Bundle
import androidx.annotation.IdRes
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import com.example.keepaccount.ViewModels.MainActivityViewModel
import com.example.keepaccount.databinding.ActivityMainBinding
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController
    private lateinit var appBarConfiguration: AppBarConfiguration
    private val viewModel: MainActivityViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initParam()
        initView()
        observeTutorialState()
    }

    private fun initParam() {
        viewModel.loadTutorialState()
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
     * ⭐ 用 Activity lifecycleScope 來收 Flow
     */
    private fun observeTutorialState() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.tutorialStatus.collectLatest { state ->
                    state?.let {
                        if (!it) {
                            showTutorialDialog()
                        }
                    }
                }
            }
        }
    }

    /**
     * ⭐ MaterialAlertDialogBuilder 正確用 this
     */
    private fun showTutorialDialog() {
        MaterialAlertDialogBuilder(this)
            .setTitle(getString(android.R.string.dialog_alert_title))
            .setMessage(getString(R.string.jump_to_nav_event_list_fragment))
            .setCancelable(false)
            .setPositiveButton(getString(R.string.yes)) { dialog, _ ->
                navController.navigate(
                    NavHomeGraphDirections.actionGlobalToNavigationEventListFragment(),
                )
                binding.mainBottomNavigationView.selectedItemId = R.id.nav_event
                viewModel.saveTutorialState(true)
                dialog.dismiss()
            }
            .show()
    }

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp() || super.onSupportNavigateUp()
    }
}

private fun FragmentManager.findNavHostFragment(
    @IdRes id: Int,
) = findFragmentById(id) as NavHostFragment
