package com.example.keepaccount

import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.view.*
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.graphics.drawable.toDrawable
import androidx.fragment.app.Fragment
import com.example.keepaccount.Extension.launchAndRepeatWithViewLifecycle
import com.example.keepaccount.ViewModels.StripViewModel
import com.example.keepaccount.databinding.FragmentStripBinding
import com.example.keepaccount.util.ScreenshotUtil
import com.github.mikephil.charting.components.AxisBase
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.ValueFormatter
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.util.*

class StripFragment : Fragment() {
    private var _binding: FragmentStripBinding? = null
    private val binding get() = _binding!!

    private val calendar = Calendar.getInstance(TimeZone.getTimeZone("GMT+08:00"))
    private val thisYear = calendar.get(Calendar.YEAR).toString()
    private val thisMonth = (calendar.get(Calendar.MONTH) + 1).toString()

    private val viewModel: StripViewModel by viewModel()

    private var currentCost: Int = 0
    private var currentBudget: Int = 0

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentStripBinding.inflate(inflater, container, false)

        binding.monthnameTv.text =
            getString(R.string.month_total_cost, thisMonth)

        initParam()
        initView()
        setupListeners()

        return binding.root
    }

    private fun setupListeners() {
        binding.insertBugetDialogBtn.setOnClickListener {
            setupEnterBudgetDialog()
        }

        binding.visibleChip.setOnCheckedChangeListener { _, isChecked ->
            applyChipState(isChecked)
        }

        binding.chipScreenshot.setOnClickListener {
            ScreenshotUtil.captureAndSave(
                context = requireContext(),
                view = binding.lineChart,
                onSuccess = { uri ->
                    Snackbar.make(binding.root, "截圖已儲存", Snackbar.LENGTH_LONG)
                        .setAction("查看") {
                            openImage(uri)
                        }
                        .show()
                },
                onError = {
                    Toast.makeText(requireContext(), "圖片儲存失敗", Toast.LENGTH_SHORT).show()
                },
            )
        }

        applyChipState(binding.visibleChip.isChecked)
    }

    private fun applyChipState(isChecked: Boolean) {
        binding.visibleChip.text =
            if (isChecked) {
                getString(R.string.text_gone)
            } else {
                getString(R.string.text_show)
            }

        binding.visibleChip.chipIcon =
            if (isChecked) {
                AppCompatResources.getDrawable(
                    requireContext(),
                    R.drawable.outline_visibility_off_24,
                )
            } else {
                AppCompatResources.getDrawable(
                    requireContext(),
                    R.drawable.outline_visibility_24,
                )
            }

        renderAmount()
    }

    private fun initParam() {
        viewModel.observeItems(thisYear, thisMonth)
        viewModel.getYearlyCosts(thisYear)
    }

    private fun initView() {
        launchAndRepeatWithViewLifecycle {
            launch {
                viewModel.boardUI.collect { ui ->
                    updateUI(ui.cost, ui.budget)
                }
            }
            launch {
                viewModel.yearlyCosts.collect { yearlyCosts ->
                    if (yearlyCosts.isNotEmpty()) {
                        setupLineChart(yearlyCosts)
                    }
                }
            }
        }
    }

    private fun updateUI(
        cost: Int,
        budget: Int,
    ) {
        currentCost = cost
        currentBudget = budget

        renderAmount()

        val progress =
            if (budget <= 0) {
                if (cost == 0) 100 else 0
            } else {
                (100 - (cost * 100 / budget)).coerceIn(0, 100)
            }

        binding.progressTv.text = "$progress%"
        binding.circularDeterminativePb.progress = progress
    }

    private fun renderAmount() {
        val isHidden = binding.visibleChip.isChecked

        if (isHidden) {
            binding.totalcostTv.text = "****"
            binding.budTv.text = "****"
        } else {
            binding.totalcostTv.text =
                getString(R.string.cost_with_unit, currentCost)
            binding.budTv.text =
                getString(R.string.budget_with_unit, currentBudget)
        }
    }

    private fun setupEnterBudgetDialog() {
        val dialogView = layoutInflater.inflate(R.layout.dialog_budget, null)
        val dialog =
            Dialog(requireContext()).apply {
                setContentView(dialogView)
                setCancelable(true)
                window?.setBackgroundDrawable(Color.TRANSPARENT.toDrawable())
            }

        dialogView.findViewById<Button>(R.id.buttonlimit).setOnClickListener {
            val text =
                dialogView.findViewById<EditText>(R.id.editTextlimit)
                    .text.toString().trim()

            val value = text.toIntOrNull()

            when {
                text.isBlank() ->
                    Toast.makeText(
                        requireContext(),
                        R.string.no_budget_toast,
                        Toast.LENGTH_SHORT,
                    ).show()

                value == null ->
                    Toast.makeText(
                        requireContext(),
                        R.string.enter_number_toast,
                        Toast.LENGTH_SHORT,
                    ).show()

                else -> {
                    viewModel.saveBudGet(value, thisYear, thisMonth)
                    dialog.dismiss()
                }
            }
        }

        dialog.show()
    }

    private fun setupLineChart(data: Map<Int, Int>) {
        val entries =
            data.toSortedMap().map { Entry(it.key.toFloat(), it.value.toFloat()) }

        val dataSet =
            LineDataSet(entries, "每月花費").apply {
                color = Color.rgb(65, 105, 225)
                setCircleColor(color)
                circleHoleColor = Color.WHITE
                lineWidth = 2.5f
                circleRadius = 4f
                circleHoleRadius = 2f
                setDrawValues(false)
            }

        binding.lineChart.apply {
            this.data = LineData(dataSet)
            description.isEnabled = false
            setTouchEnabled(false)
            axisRight.isEnabled = false
            legend.isEnabled = false

            xAxis.apply {
                position = XAxis.XAxisPosition.BOTTOM
                granularity = 1f
                setLabelCount(12, true)
                valueFormatter =
                    object : ValueFormatter() {
                        override fun getAxisLabel(
                            value: Float,
                            axis: AxisBase?,
                        ): String {
                            return "${value.toInt()}月"
                        }
                    }
            }

            axisLeft.apply {
                axisMinimum = 0f
                valueFormatter =
                    object : ValueFormatter() {
                        override fun getAxisLabel(
                            value: Float,
                            axis: AxisBase?,
                        ): String {
                            return "${value.toInt()}元"
                        }
                    }
            }

            animateX(800)
            invalidate()
        }
    }

    private fun openImage(uri: Uri) {
        val intent =
            Intent(Intent.ACTION_VIEW).apply {
                setDataAndType(uri, "image/*")
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }

        if (intent.resolveActivity(requireContext().packageManager) != null) {
            startActivity(intent)
        } else {
            Toast.makeText(requireContext(), "找不到圖片檢視器", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
