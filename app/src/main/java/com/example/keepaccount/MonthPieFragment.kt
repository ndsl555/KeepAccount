package com.example.keepaccount

import MonthPieViewModel
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.keepaccount.Extension.launchAndRepeatWithViewLifecycle
import com.example.keepaccount.ViewModels.VisualSharedViewModel
import com.example.keepaccount.databinding.FragmentPieBinding
import com.github.mikephil.charting.animation.Easing
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.formatter.PercentFormatter
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.util.*

class MonthPieFragment : Fragment() {
    private var _binding: FragmentPieBinding? = null
    private val binding get() = _binding!!
    private val calendar: Calendar = Calendar.getInstance(TimeZone.getTimeZone("GMT+08:00"))
    private val thisyear = calendar.get(Calendar.YEAR).toString()
    private val thismonth = (calendar.get(Calendar.MONTH) + 1).toString()

    private val viewModel: MonthPieViewModel by viewModel()
    private val sharedViewModel: VisualSharedViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentPieBinding.inflate(inflater, container, false)

        initParam()
        initView()

        return binding.root
    }

    private fun initParam() {
        viewModel.loadMonthData(thisyear, thismonth)
    }

    private fun initView() {
        launchAndRepeatWithViewLifecycle {
            launch {
                sharedViewModel.sortType.collect { sortType ->
                    viewModel.sortItems(sortType)
                }
            }

            launch {
                viewModel.uiState.collect { state ->
                    // 更新 PieChart
                    binding.pcChart.apply {
                        setUsePercentValues(true)
                        description.isEnabled = false
                        setDrawHoleEnabled(true)
                        setHoleColor(Color.WHITE)
                        setDrawCenterText(true)
                        rotationAngle = 0f
                        animateY(1400, Easing.EaseInOutQuad)

                        val pieDataSet = PieDataSet(state.pieEntries, "")
                        pieDataSet.colors = state.pieColors
                        pieDataSet.sliceSpace = 3f
                        pieDataSet.selectionShift = 10f

                        val pieData = PieData(pieDataSet)
                        pieData.setValueFormatter(PercentFormatter())
                        pieData.setValueTextSize(12f)
                        pieData.setValueTextColor(Color.BLUE)

                        data = pieData
                        centerText = "${getString(R.string.currency_symbol)}${state.totalCost}"
                        invalidate()
                    }

                    // 更新 RecyclerView
                    binding.recyclerView.apply {
                        adapter = ExampleAdapter(state.todayItems)
                        layoutManager = LinearLayoutManager(context)
                        setHasFixedSize(true)
                    }

                    binding.todayTv.text = getString(R.string.this_month_cost, state.totalCost.toString())
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
