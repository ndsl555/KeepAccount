package com.example.keepaccount

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.itembook.ui.Adapter.EventListAdapter
import com.example.keepaccount.Entity.Event
import com.example.keepaccount.Extension.launchAndRepeatWithViewLifecycle
import com.example.keepaccount.ViewModels.EventListViewModel
import com.example.keepaccount.databinding.EventListFragmentBinding
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.util.*
import kotlin.getValue

/**
 * Main fragment displaying details for all items in the database.
 */
class EventListFragment : Fragment() {
    private var _binding: EventListFragmentBinding? = null
    private val binding get() = _binding!!
    private val viewModel: EventListViewModel by viewModel()
    private val adapter by lazy { EventListAdapter() }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        _binding = EventListFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.getAllEvents()
        initParam()
        initView()

        binding.floatingActionButton.setOnClickListener {
            val action =
                EventListFragmentDirections.actionNavigationEventListFragmentToNavigationAddEventFragment(
                    getString(R.string.add_fragment_title),
                )
            this.findNavController().navigate(action)
        }
    }

    private fun initView() {
//        hideActionBar()
        setupRecyclerView()
    }

    private fun initParam() {
        observeUiState()
    }

    private fun observeUiState() {
        launchAndRepeatWithViewLifecycle {
            viewModel.uiState.collect {
                    e ->
                adapter.items = e
            }
        }
    }

    private fun setupRecyclerView() {
        val recyclerView = binding.recyclerView

        val dividerItemDecoration =
            DividerItemDecoration(
                recyclerView.context,
                DividerItemDecoration.VERTICAL,
            )
        recyclerView.addItemDecoration(dividerItemDecoration)

        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter =
            adapter.apply {
                onItemClick = ::onItemClick
            }
    }

    private fun onItemClick(event: Event) {
        val action =
            EventListFragmentDirections.actionNavigationEventListFragmentToNavigationEventDetailFragment(event.id)
        this.findNavController().navigate(action)
    }
}
