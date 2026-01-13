package com.example.keepaccount

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.keepaccount.databinding.FragmentBarcodeInvoiceBinding
import com.example.keepaccount.ui.adapter.BarCodeAndInvoiceFragmentViewPagerAdapter
import com.google.android.material.tabs.TabLayoutMediator

class BarCodeAndInvoiceFragment : Fragment() {
    private val tabTitleArray = arrayOf("載具設定", "發票對獎")
    private var _binding: FragmentBarcodeInvoiceBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentBarcodeInvoiceBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        super.onViewCreated(view, savedInstanceState)
        // 設定 ViewPager adapter
        val viewPager = binding.viewpager2
        viewPager.adapter = BarCodeAndInvoiceFragmentViewPagerAdapter(parentFragmentManager, lifecycle)

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
