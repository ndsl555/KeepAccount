package com.example.keepaccount

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.text.Spannable
import android.text.SpannableString
import android.text.TextWatcher
import android.text.style.ForegroundColorSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.keepaccount.Entity.InvoiceNumber
import com.example.keepaccount.Entity.isReady
import com.example.keepaccount.Extension.launchAndRepeatWithViewLifecycle
import com.example.keepaccount.ViewModels.LotteryCheckViewModel
import com.example.keepaccount.databinding.FragmentLotteryManualCheckBinding
import com.example.keepaccount.util.ScreenshotUtil
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

class LotteryManualCheckFragment : Fragment() {
    private var _binding: FragmentLotteryManualCheckBinding? = null
    private val binding get() = _binding!!
    private var count = 0

    private val viewModel: LotteryCheckViewModel by viewModel()

    private var lotteryNumber = InvoiceNumber(1, "", "", "", emptyList())
    private var allPrizes = emptyList<Prize>()

    private lateinit var prizeNumberViewMap: Map<View, TextView>

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
            binding.chipScreenshot,
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentLotteryManualCheckBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        super.onViewCreated(view, savedInstanceState)
        binding.chipScreenshot.setOnClickListener {
            ScreenshotUtil.captureAndSave(
                context = requireContext(),
                view = binding.containerLayout,
                onSuccess = { uri ->
                    Snackbar.make(binding.root, "截圖已儲存", Snackbar.LENGTH_LONG)
                        .setAction(getString(R.string.share)) {
                            shareImage(uri)
                        }
                        .show()
                },
                onError = {
                    Toast.makeText(requireContext(), "截圖失敗", Toast.LENGTH_SHORT).show()
                },
            )
        }

        prizeNumberViewMap =
            mapOf(
                binding.layoutFirstPrize1 to binding.tvFirstPrize1Number,
                binding.layoutFirstPrize2 to binding.tvFirstPrize2Number,
                binding.layoutFirstPrize3 to binding.tvFirstPrize3Number,
                binding.layoutSpecialistPrize to binding.tvSpecialistPrizeNumber,
                binding.layoutSpecialPrize to binding.tvSpecialPrizeNumber,
            )
        initView()
        setupSwipeToRefresh()
        setupLotteryInput()
    }

    private fun shareImage(uri: Uri) {
        val intent =
            Intent(Intent.ACTION_SEND).apply {
                type = "image/*"
                putExtra(Intent.EXTRA_STREAM, uri)
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }

        startActivity(
            Intent.createChooser(intent, "分享圖片"),
        )
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
            binding.count.text = ""
            chipScreenshot.visibility = View.GONE
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
                    if (text.length == 3) {
                        count++
                        binding.count.text = getString(R.string.count_invoice, count.toString())
                    }
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

    private fun applyLastThreeHighlight(
        number: String,
        input: String,
        textView: TextView,
    ) {
        val spannable = SpannableString(number)

        if (input.isNotEmpty()) {
            val lastThree = number.takeLast(3)

            if (lastThree.startsWith(input)) {
                val start = number.length - 3
                val end = start + input.length

                spannable.setSpan(
                    ForegroundColorSpan(
                        ContextCompat.getColor(
                            requireContext(),
                            android.R.color.holo_red_dark,
                        ),
                    ),
                    start,
                    end,
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE,
                )
            }
        }

        textView.text = spannable
    }

    private fun updatePrizeVisibility(input: String) {
        val result = checkWinningResult(input, lotteryNumber)

        when (result.state) {
            WinningState.INITIAL,
            WinningState.NONE,
            -> {
                prizeViewMap.forEach { it.visibility = View.VISIBLE }
                bingoContent.forEach { it.visibility = View.GONE }

                // 還原文字（不著色）
                prizeViewMap.forEachIndexed { index, layout ->
                    val textView = prizeNumberViewMap[layout] ?: return@forEachIndexed
                    textView.text = allPrizes[index].number
                }
            }

            WinningState.MAYBE -> {
                bingoContent.forEach { it.visibility = View.GONE }

                prizeViewMap.forEachIndexed { index, layout ->
                    val prize = allPrizes[index]
                    val number = prize.number

                    val lastThree = number.takeLast(3)
                    val isMatch = lastThree.startsWith(input)

                    layout.visibility = if (isMatch) View.VISIBLE else View.GONE

                    val numberTextView =
                        prizeNumberViewMap[layout] ?: return@forEachIndexed

                    applyLastThreeHighlight(number, input, numberTextView)
                }
            }

            WinningState.BINGO -> {
                prizeViewMap.forEach { it.visibility = View.GONE }
                bingoContent.forEach { it.visibility = View.GONE }

                result.winningPrizes.forEach { prize ->
                    when (prize) {
                        is PrizeType.FirstPrize -> {
                            val layout = prizeViewMap[prize.index]
                            layout.visibility = View.VISIBLE

                            val number = allPrizes[prize.index].number
                            val textView =
                                prizeNumberViewMap[layout] ?: return@forEach

                            applyLastThreeHighlight(number, input, textView)

                            bingoContent[1].visibility = View.VISIBLE
                            bingoContent[3].visibility = View.VISIBLE
                        }

                        PrizeType.SpecialistPrize -> {
                            val layout = prizeViewMap[3]
                            layout.visibility = View.VISIBLE

                            val number = allPrizes[3].number
                            val textView =
                                prizeNumberViewMap[layout] ?: return@forEach

                            applyLastThreeHighlight(number, input, textView)

                            bingoContent[0].visibility = View.VISIBLE
                            bingoContent[2].visibility = View.VISIBLE

                            binding.eightNumberHint.visibility = View.VISIBLE
                            binding.eightNumberHint.text =
                                getString(R.string.first_prize_bonus, "1000")
                        }

                        PrizeType.SpecialPrize -> {
                            val layout = prizeViewMap[4]
                            layout.visibility = View.VISIBLE

                            val number = allPrizes[4].number
                            val textView =
                                prizeNumberViewMap[layout] ?: return@forEach

                            applyLastThreeHighlight(number, input, textView)

                            bingoContent[0].visibility = View.VISIBLE
                            bingoContent[2].visibility = View.VISIBLE

                            binding.eightNumberHint.visibility = View.VISIBLE
                            binding.eightNumberHint.text =
                                getString(R.string.first_prize_bonus, "200")
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
