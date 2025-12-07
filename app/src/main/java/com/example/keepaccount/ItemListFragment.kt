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

        val cal = Calendar.getInstance()
        selectedYear = cal.get(Calendar.YEAR).toString()
        selectedMonth = (cal.get(Calendar.MONTH) + 1).toString()
        selectedDay = cal.get(Calendar.DAY_OF_MONTH).toString()

        initView()
        initParam()

        // 點擊日期
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

        // 滑動換月刷新標記
        binding.calendarView.setOnForwardPageChangeListener(
            object : OnCalendarPageChangeListener {
                override fun onChange() {
                    refreshMarkedDaysForCurrentMonth()
                }
            },
        )

        binding.calendarView.setOnPreviousPageChangeListener(
            object : OnCalendarPageChangeListener {
                override fun onChange() {
                    refreshMarkedDaysForCurrentMonth()
                }
            },
        )

        // 新增按鈕
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

    private fun initView() {
        val recyclerView = binding.recyclerView
        recyclerView.addItemDecoration(DividerItemDecoration(recyclerView.context, DividerItemDecoration.VERTICAL))
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = adapter.apply { onItemClick = ::onItemClick }

        launchAndRepeatWithViewLifecycle {
            launch {
                viewModel.uiState.collect { list ->
                    adapter.items = list
                }
            }

            launch {
                viewModel.markedDays.collect { days ->
                    val events = mutableListOf<EventDay>()
                    val calendar = Calendar.getInstance()
                    calendar.set(Calendar.YEAR, selectedYear.toInt())
                    calendar.set(Calendar.MONTH, selectedMonth.toInt() - 1)

                    days.forEach { day ->
                        calendar.set(Calendar.DAY_OF_MONTH, day)
                        events.add(EventDay(calendar.clone() as Calendar, R.drawable.sample_three_icons))
                    }
                    binding.calendarView.setEvents(events)
                }
            }
        }
    }

    private fun initParam() {
        refreshMarkedDaysForCurrentMonth()
        viewModel.getItemsByDate(selectedYear, selectedMonth, selectedDay)
    }

    private fun refreshMarkedDaysForCurrentMonth() {
        val cal = binding.calendarView.currentPageDate
        selectedYear = cal.get(Calendar.YEAR).toString()
        selectedMonth = (cal.get(Calendar.MONTH) + 1).toString()

        viewModel.getMarkedDays(selectedYear, selectedMonth)
        selectedDay = cal.get(Calendar.DAY_OF_MONTH).toString()
        viewModel.getItemsByDate(selectedYear, selectedMonth, selectedDay)
    }

    private fun onItemClick(itemName: String) {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(getString(android.R.string.dialog_alert_title))
            .setMessage(getString(R.string.delete_question))
            .setCancelable(false)
            .setNegativeButton(getString(R.string.no)) { _, _ -> }
            .setPositiveButton(getString(R.string.yes)) { _, _ ->
                viewModel.deleteItem(selectedYear, selectedMonth, selectedDay, itemName)
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
