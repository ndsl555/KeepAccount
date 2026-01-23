package com.example.keepaccount

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.keepaccount.databinding.FragmentLotterycheckBinding
import com.google.android.material.tabs.TabLayoutMediator

class LotteryCheckFragment : Fragment() {
    private var _binding: FragmentLotterycheckBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentLotterycheckBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        super.onViewCreated(view, savedInstanceState)

        val adapter = ViewPagerAdapter(this)
        binding.viewPager.adapter = adapter

        TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, position ->
            tab.text =
                when (position) {
                    0 -> getString(R.string.manual_check)
                    1 -> getString(R.string.qr_code_scan)
                    else -> null
                }
        }.attach()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

sealed class PrizeType {
    data class FirstPrize(val index: Int) : PrizeType() // 第幾組頭獎

    object SpecialistPrize : PrizeType()

    object SpecialPrize : PrizeType()
}

// 表示判斷結果
data class WinningCheckResult(
    val state: WinningState,
    val winningPrizes: List<PrizeType> = emptyList(), // 可以中多個獎
)

data class Prize(
    val type: String,
    val number: String,
    val result: String,
)

enum class WinningState {
    INITIAL,
    BINGO,
    MAYBE,
    NONE,
}

enum class QrWinningType {
    SPECIALIST_PRIZE, // 特別獎
    SPECIAL_PRIZE, // 特獎
    FIRST_PRIZE, // 頭獎
    NONE,
}

data class QrWinningResult(
    val type: QrWinningType,
    val money: String? = null,
)
