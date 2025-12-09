package com.example.keepaccount

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.applandeo.materialcalendarview.EventDay
import com.applandeo.materialcalendarview.listeners.OnCalendarPageChangeListener
import com.applandeo.materialcalendarview.listeners.OnDayClickListener
import com.example.itembook.ui.Adapter.ItemListAdapter
import com.example.keepaccount.Extension.launchAndRepeatWithViewLifecycle
import com.example.keepaccount.ViewModels.ItemListViewModel
import com.example.keepaccount.databinding.ItemListFragmentBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.util.Calendar

class ItemListFragment : Fragment() {
    private var _binding: ItemListFragmentBinding? = null
    private val binding get() = _binding!!

    private val viewModel: ItemListViewModel by viewModel()
    private val adapter by lazy { ItemListAdapter() }

    private var selectedYear = ""
    private var selectedMonth = ""
    private var selectedDay = ""

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = ItemListFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        super.onViewCreated(view, savedInstanceState)

        initCurrentDate()
        initRecyclerView()
        initObservers()
        initCalendarListeners()

        initParam()

        binding.floatingActionButton.setOnClickListener {
            val action =
                ItemListFragmentDirections.actionNavigationItemListFragmentToNavigationAddItemFragment(
                    getString(R.string.add_fragment_title),
                    selectedYear,
                    selectedMonth,
                    selectedDay,
                )
            findNavController().navigate(action)
        }
    }

    /** 取得現在日期 */
    private fun initCurrentDate() {
        val cal = Calendar.getInstance()
        selectedYear = cal.get(Calendar.YEAR).toString()
        selectedMonth = (cal.get(Calendar.MONTH) + 1).toString()
        selectedDay = cal.get(Calendar.DAY_OF_MONTH).toString()
    }

    /** 一開始載入「當月標記」與「今日列表」 */
    private fun initParam() {
        viewModel.getMarkedDays(selectedYear, selectedMonth)
        viewModel.getItemsByDate(selectedYear, selectedMonth, selectedDay)
    }

    /** 初始化 RecyclerView */
    private fun initRecyclerView() {
        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.VERTICAL))
            adapter =
                this@ItemListFragment.adapter.apply {
                    onItemClick = ::onItemClick
                }
        }
    }

    /** Flow 監聽 UI 更新 */
    private fun initObservers() {
        launchAndRepeatWithViewLifecycle {
            // 更新列表
            launch {
                viewModel.uiState.collect { list ->
                    adapter.items = list
                }
            }

            // 更新標記日
            launch {
                viewModel.markedDays.collect { days ->
                    updateCalendarEvents(days)
                }
            }
        }
    }

    /** Calendar 所有事件 */
    private fun initCalendarListeners() {
        // 點擊日期 → 更新列表
        binding.calendarView.setOnDayClickListener(
            object : OnDayClickListener {
                override fun onDayClick(eventDay: EventDay) {
                    val clicked = eventDay.calendar
                    selectedYear = clicked.get(Calendar.YEAR).toString()
                    selectedMonth = (clicked.get(Calendar.MONTH) + 1).toString()
                    selectedDay = clicked.get(Calendar.DAY_OF_MONTH).toString()

                    viewModel.getItemsByDate(selectedYear, selectedMonth, selectedDay)
                }
            },
        )

        // 換到下一月 → 更新標記日（不動列表）
        binding.calendarView.setOnForwardPageChangeListener(
            object : OnCalendarPageChangeListener {
                override fun onChange() {
                    refreshMarkedDaysForCurrentMonth()
                }
            },
        )

        // 換到上一月 → 更新標記日（不動列表）
        binding.calendarView.setOnPreviousPageChangeListener(
            object : OnCalendarPageChangeListener {
                override fun onChange() {
                    refreshMarkedDaysForCurrentMonth()
                }
            },
        )
    }

    /** 更新 Calendar 標記 */
    private fun updateCalendarEvents(days: List<Int>) {
        val events = mutableListOf<EventDay>()
        val cal = Calendar.getInstance()

        cal.set(Calendar.YEAR, selectedYear.toInt())
        cal.set(Calendar.MONTH, selectedMonth.toInt() - 1)

        days.forEach { day ->
            cal.set(Calendar.DAY_OF_MONTH, day)
            events.add(EventDay(cal.clone() as Calendar, R.drawable.sample_three_icons))
        }

        binding.calendarView.setEvents(emptyList())
        binding.calendarView.setEvents(events)
    }

    /** 換月時更新用 */
    private fun refreshMarkedDaysForCurrentMonth() {
        val cal = binding.calendarView.currentPageDate
        selectedYear = cal.get(Calendar.YEAR).toString()
        selectedMonth = (cal.get(Calendar.MONTH) + 1).toString()

        //  這裡只更新標記，不更新列表
        viewModel.getMarkedDays(selectedYear, selectedMonth)
    }

    private fun onItemClick(itemName: String) {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(getString(android.R.string.dialog_alert_title))
            .setMessage(getString(R.string.delete_question))
            .setCancelable(false)
            .setNegativeButton(getString(R.string.no)) { _, _ -> }
            .setPositiveButton(getString(R.string.yes)) { _, _ ->
                viewModel.deleteItem(selectedYear, selectedMonth, selectedDay, itemName)
                // 更新頁面資料
                viewModel.getItemsByDate(selectedYear, selectedMonth, selectedDay)
                viewModel.getMarkedDays(selectedYear, selectedMonth)
            }
            .show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
