package com.example.keepaccount

import android.app.Dialog
import android.content.Context.INPUT_METHOD_SERVICE
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.core.graphics.drawable.toDrawable
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.itembook.ui.Adapter.EventListAdapter
import com.example.keepaccount.Entity.Event
import com.example.keepaccount.Entity.Item
import com.example.keepaccount.ViewModels.AddItemViewModel
import com.example.keepaccount.databinding.FragmentAddItemBinding
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.util.*

class AddItemFragment : Fragment() {
    private val viewModel: AddItemViewModel by viewModel()
    private val adapter by lazy { EventListAdapter() }

    private val navigationArgs: AddItemFragmentArgs by navArgs()

    private var _binding: FragmentAddItemBinding? = null
    private val binding get() = _binding!!

    private lateinit var year: String
    private lateinit var month: String
    private lateinit var day: String

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentAddItemBinding.inflate(inflater, container, false)

        initParam()
        binding.chooseAction.setOnClickListener {
            setupRankDialog(viewModel.uiState.value)
        }

        return binding.root
    }

    private fun initParam() {
        viewModel.getAllEvents()
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        super.onViewCreated(view, savedInstanceState)

        val calendar = Calendar.getInstance(TimeZone.getTimeZone("GMT+08:00"))

        year = navigationArgs.year.ifBlank { calendar.get(Calendar.YEAR).toString() }
        month = navigationArgs.month.ifBlank { (calendar.get(Calendar.MONTH) + 1).toString() }
        day = navigationArgs.day.ifBlank { calendar.get(Calendar.DATE).toString() }

        binding.saveAction.setOnClickListener {
            if (binding.itemName.text.isNullOrBlank() ||
                binding.itemPrice.text.isNullOrBlank() ||
                binding.itemColorcode.text.isNullOrBlank()
            ) {
                Toast.makeText(requireContext(), getString(R.string.empty_fields), Toast.LENGTH_SHORT).show()
            } else {
                addNewItem()
            }
        }
    }

    private fun isEntryValid(): Boolean {
        return viewModel.isEntryValid(
            binding.itemName.text.toString(),
            binding.itemPrice.text.toString(),
            binding.itemColorcode.text.toString(),
            year,
            month,
            day,
        )
    }

    private fun addNewItem() {
        if (isEntryValid()) {
            viewModel.addNewItem(
                Item(
                    itemName = binding.itemName.text.toString(),
                    itemPrice = binding.itemPrice.text.toString().toDouble(),
                    itemColorcode = binding.itemColorcode.text.toString(),
                    itemYear = year,
                    itemMonth = month,
                    itemDay = day,
                ),
            )
            findNavController().popBackStack()
        }
    }

    /** 排行榜視窗邏輯 */
    private fun setupRankDialog(events: List<Event>) {
        adapter.items = events
        val dialogBinding = layoutInflater.inflate(R.layout.select_events_dialog, null)
        val dialog =
            Dialog(requireContext()).apply {
                setContentView(dialogBinding)
                setCancelable(true)
                window?.setBackgroundDrawable(Color.TRANSPARENT.toDrawable())
            }

        val recyclerView = dialogBinding.findViewById<RecyclerView>(R.id.eventList)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = adapter
        adapter.onItemClick = { event ->
            binding.itemColorcode.setText(event.eventColorCode)
            binding.itemName.setText(event.eventName)
            dialog.dismiss()
        }
        recyclerView.addItemDecoration(DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL))

        dialog.show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        val inputMethodManager = requireActivity().getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(requireActivity().currentFocus?.windowToken, 0)
        _binding = null
    }
}
