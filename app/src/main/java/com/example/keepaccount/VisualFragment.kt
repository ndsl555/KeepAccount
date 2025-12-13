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
import com.example.keepaccount.ui.adapter.ViewPagerAdapter
import com.google.android.material.tabs.TabLayoutMediator
import org.koin.androidx.viewmodel.ext.android.activityViewModel

class VisualFragment : Fragment() {
    private val tabTitleArray = arrayOf("今日消費狀況", "本月消費狀況")
    private var _binding: FragmentVisualBinding? = null
    private val binding get() = _binding!!
    private val sharedViewModel: VisualSharedViewModel by activityViewModel() // 或 by activityViewModels()

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
        viewPager.adapter = ViewPagerAdapter(parentFragmentManager, lifecycle)

        // 設定 TabLayout + ViewPager2
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
        if (item.itemId == R.id.menu_show_by_dscending) {
            sharedViewModel.setSort(SortType.COST_DESC)
            return true
        }
        if (item.itemId == R.id.menu_show_by_ascending) {
            sharedViewModel.setSort(SortType.COST_ASC)
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
