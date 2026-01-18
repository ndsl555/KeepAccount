package com.example.keepaccount

import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.keepaccount.ViewModels.SortType
import com.example.keepaccount.ViewModels.VisualSharedViewModel
import com.example.keepaccount.databinding.FragmentVisualBinding
import com.example.keepaccount.ui.adapter.VisualFragmentViewPagerAdapter
import com.google.android.material.tabs.TabLayoutMediator
import org.koin.androidx.viewmodel.ext.android.activityViewModel
import java.util.Calendar

class VisualFragment : Fragment() {
    private var _binding: FragmentVisualBinding? = null
    private val binding get() = _binding!!
    private val sharedViewModel: VisualSharedViewModel by activityViewModel() // 或 by activityViewModels()
    val calendar = Calendar.getInstance()
    val year = calendar.get(Calendar.YEAR)
    val month = calendar.get(Calendar.MONTH) + 1

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
        setHasOptionsMenu(true)
        // 設定 ViewPager adapter
        val viewPager = binding.viewpager2
        viewPager.adapter = VisualFragmentViewPagerAdapter(parentFragmentManager, lifecycle)

        // 設定 TabLayout + ViewPager2
        val tabTitleArray = arrayOf(getString(R.string.today_consumption_status), getString(R.string.monthly_consumption_status))
        TabLayoutMediator(binding.tabLayout, viewPager) { tab, position ->
            tab.text = tabTitleArray[position]
        }.attach()
    }

    override fun onCreateOptionsMenu(
        menu: Menu,
        inflater: MenuInflater,
    ) {
        inflater.inflate(R.menu.option_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
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
                sharedViewModel.exportMonthlyConsumptionToExcel(year.toString(), month.toString())
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
