package com.example.keepaccount

import android.app.Dialog
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.core.graphics.drawable.toDrawable
import androidx.fragment.app.Fragment
import com.example.keepaccount.Extension.launchAndRepeatWithViewLifecycle
import com.example.keepaccount.ViewModels.StripViewModel
import com.example.keepaccount.databinding.FragmentStripBinding
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.util.*

class StripFragment : Fragment() {
    private var _binding: FragmentStripBinding? = null
    private val binding get() = _binding!!

    private val calendar: Calendar = Calendar.getInstance(TimeZone.getTimeZone("GMT+08:00"))
    private val thisyear = calendar.get(Calendar.YEAR).toString()
    private val thismonth = (calendar.get(Calendar.MONTH) + 1).toString()

    private val viewModel: StripViewModel by viewModel()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentStripBinding.inflate(inflater, container, false)

        binding.monthnameTv.text = getString(R.string.month_total_cost, thismonth)

        initParam()
        initView()

        binding.insertBugetDialogBtn.setOnClickListener {
            setupEnterBudgetDialog()
        }

        return binding.root
    }

    private fun initView() {
        launchAndRepeatWithViewLifecycle {
            launch {
                viewModel.boardUI.collect { ui ->
                    updateUI(ui.cost, ui.budget)
                }
            }
        }
    }

    private fun initParam() {
        viewModel.observeItems(thisyear, thismonth)
    }

    private fun updateUI(
        cost: Int,
        budget: Int,
    ) {
        binding.totalcostTv.text = getString(R.string.cost_with_unit, cost)
        binding.budTv.text = getString(R.string.budget_with_unit, budget)

        val progress =
            if (budget <= 0) {
                // 尚未設定預算：若 cost 也為 0 → 完全 100%
                // 若 cost > 0 → 沒預算就代表沒比例 → 0%
                if (cost == 0) 100 else 0
            } else {
                (100 - (cost * 100 / budget))
                    .coerceIn(0, 100)
            }

        binding.progressTv.text = "$progress%"
        binding.circularDeterminativePb.progress = progress
    }

    private fun setupEnterBudgetDialog() {
        val dialogBinding = layoutInflater.inflate(R.layout.dialog_budget, null)
        val dialog =
            Dialog(requireContext()).apply {
                setContentView(dialogBinding)
                setCancelable(true)
                window?.setBackgroundDrawable(Color.TRANSPARENT.toDrawable())
            }

        dialogBinding.findViewById<Button>(R.id.buttonlimit).setOnClickListener {
            val num = dialogBinding.findViewById<EditText>(R.id.editTextlimit).text.toString()
            val numStr = num.trim() // 去除首尾空白
            val numInt = numStr.toIntOrNull() // 嘗試轉成 Int，如果失敗會返回 null
            if (numStr.isBlank()) {
                Toast.makeText(requireContext(), getString(R.string.no_budget_toast), Toast.LENGTH_SHORT).show()
            } else if (numInt == null) {
                Toast.makeText(requireContext(), getString(R.string.enter_number_toast), Toast.LENGTH_SHORT).show()
            } else {
                viewModel.saveBudGet(num.toInt(), thisyear, thismonth)
                dialog.dismiss()
            }
        }
        dialog.show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
