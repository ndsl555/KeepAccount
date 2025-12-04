package com.example.keepaccount

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.keepaccount.databinding.FragmentVisualBinding
import com.example.keepaccount.ui.Adapter.ViewPagerAdapter
import com.google.android.material.tabs.TabLayoutMediator

class VisualFragment : Fragment() {
    private val tabTitleArray = arrayOf("今日消費狀況", "本月消費狀況")
    private var _binding: FragmentVisualBinding? = null
    private val binding get() = _binding!!

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
        // 設定 ViewPager Adapter
        val viewPager = binding.viewpager2
        viewPager.adapter = ViewPagerAdapter(parentFragmentManager, lifecycle)

        // 設定 TabLayout + ViewPager2
        TabLayoutMediator(binding.tabLayout, viewPager) { tab, position ->
            tab.text = tabTitleArray[position]
        }.attach()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
