package com.example.keepaccount

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.keepaccount.Entity.InvoiceNumber
import com.example.keepaccount.Entity.isReady
import com.example.keepaccount.Extension.launchAndRepeatWithViewLifecycle
import com.example.keepaccount.ViewModels.LotteryCheckViewModel
import com.example.keepaccount.databinding.FragmentLotterycheckBinding
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel
import kotlin.collections.any
import kotlin.collections.forEach
import kotlin.collections.forEachIndexed
import kotlin.collections.isNotEmpty
import kotlin.text.getOrNull
import kotlin.text.isEmpty
import kotlin.text.last
import kotlin.text.orEmpty
import kotlin.text.startsWith
import kotlin.text.takeLast

class LotteryCheckFragment : Fragment() {
    private var _binding: FragmentLotterycheckBinding? = null
    private val binding get() = _binding!!

    private val viewModel: LotteryCheckViewModel by viewModel()

    private var lotteryNumber = InvoiceNumber(1, "", "", "", emptyList())
    private var allPrizes = emptyList<Prize>()

    enum class WinningState {
        INITIAL,
        BINGO,
        MAYBE,
        NONE,
    }

    private val prizeViewMap by lazy {
        listOf(
            binding.layoutFirstPrize1,
            binding.layoutFirstPrize2,
            binding.layoutFirstPrize3,
            binding.layoutSpecialistPrize,
            binding.layoutSpecialPrize,
        )
    }
    private val bingoContent by lazy {
        listOf(
            binding.eightNumberHint,
            binding.firstPrizeMappingContainer,
            binding.specialPrizeMappingContainer,
        )
    }

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
        initView()
        setupSwipeToRefresh()
        setupLotteryInput()
    }

    private fun initView() {
        launchAndRepeatWithViewLifecycle {
            launch {
                viewModel.isLoading.collect { isLoading ->
                    binding.swipeRefreshLayout.isRefreshing = isLoading
                    val visibility = if (isLoading) View.GONE else View.VISIBLE
                    val visibility2 = if (isLoading) View.VISIBLE else View.GONE
                    binding.lotteryInputContainer.visibility = visibility
                    binding.hint.visibility = visibility
                    binding.hintTopic.visibility = visibility
                    binding.prizeContainer.visibility = visibility
                    binding.loadingContainer.visibility = visibility2
                }
            }

            launch {
                viewModel.lotteryNumber.collect { invoiceNumber ->
                    if (invoiceNumber.isReady()) {
                        onInvoiceNumberReady(invoiceNumber)
                    }
                }
            }
        }
    }

    private fun onInvoiceNumberReady(invoiceNumber: InvoiceNumber) {
        lotteryNumber = invoiceNumber

        allPrizes =
            listOf(
                Prize("頭獎", invoiceNumber.firstPrize[0], ""),
                Prize("頭獎", invoiceNumber.firstPrize[1], ""),
                Prize("頭獎", invoiceNumber.firstPrize[2], ""),
                Prize("特別獎", invoiceNumber.specialistPrize, "需八碼全中"),
                Prize("特獎", invoiceNumber.specialPrize, "需八碼全中"),
            )

        initCol()
        updatePrizeVisibility("")
        checkHintText("")
    }

    private fun initCol() =
        with(binding) {
            tvFirstPrize1Number.text = allPrizes[0].number
            tvFirstPrize2Number.text = allPrizes[1].number
            tvFirstPrize3Number.text = allPrizes[2].number
            tvSpecialistPrizeNumber.text = allPrizes[3].number
            tvSpecialPrizeNumber.text = allPrizes[4].number
            firstPrizeMappingContainer.visibility = View.GONE
            specialPrizeMappingContainer.visibility = View.GONE
            eightNumberHint.visibility = View.GONE
            hintTopic.text = lotteryNumber.topic
        }

    private fun setupSwipeToRefresh() {
        binding.swipeRefreshLayout.setOnRefreshListener {
            viewModel.getLotteryNumberFromDB()
        }
    }

    private fun setupLotteryInput() {
        val digitViews =
            listOf(
                binding.lotteryDigit1,
                binding.lotteryDigit2,
                binding.lotteryDigit3,
            )

        val clickListener =
            View.OnClickListener {
                binding.hiddenInput.requestFocus()
                val imm =
                    context?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.showSoftInput(binding.hiddenInput, InputMethodManager.SHOW_IMPLICIT)
            }

        binding.lotteryInputContainer.setOnClickListener(clickListener)
        digitViews.forEach { it.setOnClickListener(clickListener) }

        binding.hiddenInput.addTextChangedListener(
            object : TextWatcher {
                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int,
                ) = Unit

                override fun onTextChanged(
                    s: CharSequence?,
                    start: Int,
                    before: Int,
                    count: Int,
                ) = Unit

                override fun afterTextChanged(s: Editable?) {
                    var text = s?.toString().orEmpty()
                    if (text.length > 3) { // 超過 3 碼 → 重置，只留最後一碼
                        val lastChar = text.last().toString()
                        s?.replace(0, s.length, lastChar)
                        text = lastChar
                    }
                    // 正常顯示（0~3 碼）
                    digitViews.forEachIndexed { index, textView ->
                        textView.text = text.getOrNull(index)?.toString() ?: ""
                    }
                    checkHintText(text)
                    updatePrizeVisibility(text)
                }
            },
        )
    }

    private fun checkWinningResult(
        input: String,
        invoice: InvoiceNumber,
    ): WinningCheckResult {
        if (input.isEmpty()) return WinningCheckResult(WinningState.INITIAL)

        val matchedPrizes = mutableListOf<PrizeType>()

        // 頭獎
        invoice.firstPrize.forEachIndexed { index, number ->
            if (number.takeLast(3).startsWith(input)) {
                if (input.length == 3) matchedPrizes.add(PrizeType.FirstPrize(index))
            }
        }

        // 特別獎
        if (invoice.specialistPrize.takeLast(3).startsWith(input)) {
            if (input.length == 3) matchedPrizes.add(PrizeType.SpecialistPrize)
        }

        // 特獎
        if (invoice.specialPrize.takeLast(3).startsWith(input)) {
            if (input.length == 3) matchedPrizes.add(PrizeType.SpecialPrize)
        }

        return when {
            matchedPrizes.isNotEmpty() -> WinningCheckResult(WinningState.BINGO, matchedPrizes)
            invoice.firstPrize.any { it.takeLast(3).startsWith(input) } ||
                invoice.specialistPrize.takeLast(3).startsWith(input) ||
                invoice.specialPrize.takeLast(3).startsWith(input) -> WinningCheckResult(WinningState.MAYBE)
            else -> WinningCheckResult(WinningState.NONE)
        }
    }

    private fun updatePrizeVisibility(input: String) {
        val result = checkWinningResult(input, lotteryNumber)

        when (result.state) {
            WinningState.INITIAL,
            WinningState.NONE,
            -> {
                prizeViewMap.forEach { it.visibility = View.VISIBLE }
                bingoContent.forEach { it.visibility = View.GONE }
            }

            WinningState.MAYBE -> {
                bingoContent.forEach { it.visibility = View.GONE }
                prizeViewMap.forEachIndexed { index, layout ->
                    val number = allPrizes[index].number
                    layout.visibility =
                        if (number.takeLast(3).startsWith(input)) View.VISIBLE else View.GONE
                }
            }

            WinningState.BINGO -> {
                // 先隱藏全部
                prizeViewMap.forEach { it.visibility = View.GONE }
                bingoContent.forEach { it.visibility = View.GONE }

                // 顯示中獎的獎項
                result.winningPrizes.forEach { prize ->
                    when (prize) {
                        is PrizeType.FirstPrize -> {
                            prizeViewMap[prize.index].visibility = View.VISIBLE
                            bingoContent[1].visibility = View.VISIBLE
                        }
                        PrizeType.SpecialistPrize -> {
                            prizeViewMap[3].visibility = View.VISIBLE
                            bingoContent[0].visibility = View.VISIBLE
                            bingoContent[2].visibility = View.VISIBLE
                            binding.eightNumberHint.visibility = View.VISIBLE
                            binding.eightNumberHint.text = getString(R.string.first_prize_bonus, "1000")
                        }
                        PrizeType.SpecialPrize -> {
                            prizeViewMap[4].visibility = View.VISIBLE
                            bingoContent[0].visibility = View.VISIBLE
                            bingoContent[2].visibility = View.VISIBLE
                            binding.eightNumberHint.visibility = View.VISIBLE
                            binding.eightNumberHint.text = getString(R.string.first_prize_bonus, "200")
                        }
                    }
                }
            }
        }
    }

    private fun checkHintText(input: String) {
        val result = checkWinningResult(input, lotteryNumber)

        when (result.state) {
            WinningState.INITIAL -> {
                binding.hint.text = getString(R.string.hint_enter_numbers)
                binding.hint.setTextColor(
                    ContextCompat.getColor(requireContext(), R.color.black),
                )
            }

            WinningState.MAYBE -> {
                binding.hint.text = ""
                binding.hint.setTextColor(
                    ContextCompat.getColor(requireContext(), R.color.black),
                )
            }

            WinningState.NONE -> {
                binding.hint.text = getString(R.string.hint_sorry_try_again)
                binding.hint.setTextColor(
                    ContextCompat.getColor(requireContext(), R.color.black),
                )
            }

            WinningState.BINGO -> {
                when {
                    result.winningPrizes.any {
                        it is PrizeType.SpecialistPrize || it is PrizeType.SpecialPrize
                    } -> {
                        binding.hint.text = getString(R.string.hint_check_numbers)
                    }

                    result.winningPrizes.any { it is PrizeType.FirstPrize } -> {
                        binding.hint.text = getString(R.string.congrats)
                    }
                }

                binding.hint.setTextColor(
                    ContextCompat.getColor(requireContext(), R.color.orange_700),
                )
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

// 表示不同獎項
sealed class PrizeType {
    data class FirstPrize(val index: Int) : PrizeType() // 第幾組頭獎

    object SpecialistPrize : PrizeType()

    object SpecialPrize : PrizeType()
}

// 表示判斷結果
data class WinningCheckResult(
    val state: LotteryCheckFragment.WinningState,
    val winningPrizes: List<PrizeType> = emptyList(), // 可以中多個獎
)

data class Prize(
    val type: String,
    val number: String,
    val result: String,
)
