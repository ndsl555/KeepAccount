package com.example.keepaccount

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.itembook.ui.Adapter.ItemListAdapter
import com.example.keepaccount.Extension.launchAndRepeatWithViewLifecycle
import com.example.keepaccount.ViewModels.ItemListViewModel
import com.example.keepaccount.databinding.ItemListFragmentBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.util.Calendar
import java.util.TimeZone

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

        initView()
        initParam()

        val cal = Calendar.getInstance(TimeZone.getTimeZone("GMT+08:00"))
        selectedYear = cal.get(Calendar.YEAR).toString()
        selectedMonth = (cal.get(Calendar.MONTH) + 1).toString()
        selectedDay = cal.get(Calendar.DAY_OF_MONTH).toString()

        viewModel.getItemsByDate(selectedYear, selectedMonth, selectedDay)
        binding.calendarView.setOnDateChangeListener { _, year, month, dayOfMonth ->
            selectedYear =
                year.toString()
            selectedMonth = (month + 1).toString()
            selectedDay = dayOfMonth.toString()
            viewModel.getItemsByDate(selectedYear, selectedMonth, selectedDay)
        }
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
        recyclerView.addItemDecoration(
            DividerItemDecoration(recyclerView.context, DividerItemDecoration.VERTICAL),
        )
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = adapter.apply { onItemClick = ::onItemClick }
    }

    private fun initParam() {
        launchAndRepeatWithViewLifecycle {
            viewModel.uiState.collect { list ->
                adapter.items = list
            }
        }
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
            }
            .show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
