package com.example.keepaccount

import android.app.Dialog
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.core.graphics.drawable.toDrawable
import androidx.core.graphics.toColorInt
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.keepaccount.Extension.launchAndRepeatWithViewLifecycle
import com.example.keepaccount.ViewModels.Example2Item
import com.example.keepaccount.ViewModels.StripViewModel
import com.example.keepaccount.databinding.FragmentStripBinding
import com.example.keepaccount.ui.adapter.RankItemListAdapter
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.time.YearMonth
import java.util.*

class StripFragment : Fragment() {
    private var _binding: FragmentStripBinding? = null
    private val binding get() = _binding!!

    private val calendar: Calendar = Calendar.getInstance(TimeZone.getTimeZone("GMT+08:00"))
    private val thisyear = calendar.get(Calendar.YEAR).toString()
    private val thismonth = (calendar.get(Calendar.MONTH) + 1).toString()

    private val viewModel: StripViewModel by viewModel()

    //  Adapter 只初始化一次
    private lateinit var rankAdapter: RankItemListAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentStripBinding.inflate(inflater, container, false)

        binding.monthnameTv.text = getString(R.string.month_total_cost, thismonth)

        initRecyclerView() // 初始化 RecyclerView
        initParam()
        initView()
        updateRemainingDays()

        binding.insertBugetDialogBtn.setOnClickListener {
            setupEnterBudgetDialog()
        }

        return binding.root
    }

    /** 初始化 RecyclerView，只做一次 */
    private fun initRecyclerView() {
        rankAdapter = RankItemListAdapter()

        binding.recyclerView.apply {
            adapter = rankAdapter
            layoutManager = LinearLayoutManager(context)
            setHasFixedSize(true)
        }
    }

    /** 觀察 ViewModel Flow 更新 UI */
    private fun initView() {
        launchAndRepeatWithViewLifecycle {
            launch {
                viewModel.boardUI.collect { ui ->
                    updateUI(ui.cost, ui.budget, ui.rankList)
                }
            }
        }
    }

    /** 初次載入資料 */
    private fun initParam() {
        viewModel.observeItems(thisyear, thismonth)
    }

    /** 更新 UI: progress, cost, balance */
    private fun updateUI(
        cost: Double,
        budget: Int,
        rankList: List<Example2Item>,
    ) {
        binding.totalcostTv.text = getString(R.string.cost_with_unit, cost)
        binding.budTv.text = getString(R.string.budget_with_unit, budget)

        val balance = budget - cost
        binding.progressBar.max = budget
        binding.progressBar.progress = cost.toInt()

        when {
            balance >= 1000 -> { // 正常
                binding.situationTv.text = getString(R.string.remaining_balance, balance)
                binding.situationTv.setTextColor(Color.GREEN)
            }
            balance in 0.0..999.0 -> { // 快沒錢了
                binding.situationTv.text = getString(R.string.remaining_balance, balance)
                binding.situationTv.setTextColor(Color.GREEN)
                binding.progressBar.progressTintList =
                    ColorStateList.valueOf("#FFA500".toColorInt())
            }
            else -> { // 超支
                binding.situationTv.text = getString(R.string.overspending, -balance)
                binding.situationTv.setTextColor(Color.RED)
                binding.progressBar.progressTintList = ColorStateList.valueOf(Color.RED)
            }
        }

        //  更新 Adapter 資料，不重建
        rankAdapter.submitList(rankList)
        binding.todayTv.text = getString(R.string.this_month_cost, cost)
    }

    /** 預算輸入對話框 */
    private fun setupEnterBudgetDialog() {
        val dialogBinding = layoutInflater.inflate(R.layout.fragment_budget, null)
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

    /** 計算距離下個月剩餘天數 */
    private fun updateRemainingDays() {
        val dayPass = YearMonth.now().lengthOfMonth() - calendar.get(Calendar.DAY_OF_MONTH) + 1
        binding.leastdayTv.text = getString(R.string.remaining_days, dayPass)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
