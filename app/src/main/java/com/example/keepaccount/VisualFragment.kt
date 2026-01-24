package com.example.keepaccount

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import com.example.keepaccount.Extension.launchAndRepeatWithViewLifecycle
import com.example.keepaccount.ViewModels.SortType
import com.example.keepaccount.ViewModels.VisualSharedViewModel
import com.example.keepaccount.ViewModels.VisualUiEvent
import com.example.keepaccount.databinding.FragmentVisualBinding
import com.example.keepaccount.ui.adapter.VisualFragmentViewPagerAdapter
import com.google.android.material.tabs.TabLayoutMediator
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.activityViewModel
import java.util.Calendar

class VisualFragment : Fragment() {
    private var _binding: FragmentVisualBinding? = null
    private val binding get() = _binding!!

    private val sharedViewModel: VisualSharedViewModel by activityViewModel()

    private val calendar = Calendar.getInstance()
    private val year = calendar.get(Calendar.YEAR).toString()
    private val month = (calendar.get(Calendar.MONTH) + 1).toString()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentVisualBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        super.onViewCreated(view, savedInstanceState)

        setupViewPager()
        setupMenu()
        observeUiEvent()
    }

    // =========================
    // ViewPager / Tab
    // =========================
    private fun setupViewPager() {
        binding.viewpager2.adapter =
            VisualFragmentViewPagerAdapter(parentFragmentManager, lifecycle)

        val titles =
            arrayOf(
                getString(R.string.today_consumption_status),
                getString(R.string.monthly_consumption_status),
            )

        TabLayoutMediator(binding.tabLayout, binding.viewpager2) { tab, position ->
            tab.text = titles[position]
        }.attach()
    }

    // =========================
    // Menu（新 API）
    // =========================
    private fun setupMenu() {
        val menuHost = requireActivity() as MenuHost

        menuHost.addMenuProvider(
            object : MenuProvider {
                override fun onCreateMenu(
                    menu: Menu,
                    inflater: MenuInflater,
                ) {
                    inflater.inflate(R.menu.option_menu, menu)
                }

                override fun onMenuItemSelected(item: MenuItem): Boolean {
                    return when (item.itemId) {
                        R.id.menu_show_by_dscending -> {
                            sharedViewModel.setSort(SortType.COST_DESC)
                            true
                        }

                        R.id.menu_show_by_ascending -> {
                            sharedViewModel.setSort(SortType.COST_ASC)
                            true
                        }

                        R.id.menu_export_to_excel -> {
                            sharedViewModel.exportMonthlyConsumptionToExcel(year, month)
                            true
                        }

                        else -> false
                    }
                }
            },
            viewLifecycleOwner,
            Lifecycle.State.RESUMED,
        )
    }

    // =========================
    // ViewModel Event
    // =========================
    private fun observeUiEvent() {
        launchAndRepeatWithViewLifecycle {
            launch {
                sharedViewModel.uiEvent.collect { event ->
                    when (event) {
                        is VisualUiEvent.OpenExcel -> openExcel(event.uri)
                        is VisualUiEvent.ShowError ->
                            Toast.makeText(requireContext(), event.message, Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    // =========================
    // Open Excel
    // =========================
    private fun openExcel(uri: Uri) {
        // 先嘗試用 Excel / WPS 直接開
        val viewIntent =
            Intent(Intent.ACTION_VIEW).apply {
                setDataAndType(
                    uri,
                    "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
                )
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }

        val pm = requireContext().packageManager

        if (viewIntent.resolveActivity(pm) != null) {
            try {
                startActivity(viewIntent)
                return
            } catch (_: Exception) {
                // 有 App 但打不開，繼續 fallback
            }
        }

        // Google 試算表：用「分享」
        val shareIntent =
            Intent(Intent.ACTION_SEND).apply {
                type = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"
                putExtra(Intent.EXTRA_STREAM, uri)
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }

        startActivity(Intent.createChooser(shareIntent, "用試算表開啟"))
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
