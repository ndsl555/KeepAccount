

package com.example.keepaccount

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.keepaccount.Entity.Event
import com.example.keepaccount.Extension.launchAndRepeatWithViewLifecycle
import com.example.keepaccount.ViewModels.EventDetailViewModel
import com.example.keepaccount.databinding.FragmentEventDetailBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel
import kotlin.getValue

/**
 * [EventDetailFragment] displays the details of the selected item.
 */
class EventDetailFragment : Fragment() {
    private val navigationArgs: EventDetailFragmentArgs by navArgs()
    lateinit var event: Event
    private val viewModel: EventDetailViewModel by viewModel()

    private var _binding: FragmentEventDetailBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        _binding = FragmentEventDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    /**
     * Binds views with the passed in item data.
     */
    private fun bind(event: Event) {
        binding.apply {
            eventName.text = event.eventName
            eventColorcode.text = event.eventColorCode
            deleteEvent.setOnClickListener { showConfirmationDialog() }
            eventEditItem.setOnClickListener { editItem() }
        }
    }

    /**
     * Navigate to the Edit item screen.
     */
    private fun editItem() {
        val action =
            EventDetailFragmentDirections.actionNavigationEventDetailFragmentToNavigationAddEventFragment(
                getString(R.string.edit_fragment_title),
                event.id,
            )
        this.findNavController().navigate(action)
    }

    /**
     * Displays an alert dialog to get the user's confirmation before deleting the item.
     */
    private fun showConfirmationDialog() {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(getString(android.R.string.dialog_alert_title))
            .setMessage(getString(R.string.delete_question))
            .setCancelable(false)
            .setNegativeButton(getString(R.string.no)) { _, _ -> }
            .setPositiveButton(getString(R.string.yes)) { _, _ ->
                deleteItem()
            }
            .show()
    }

    /**
     * Deletes the current item and navigates to the list fragment.
     */
    private fun deleteItem() {
        viewModel.deleteItem(event)
        findNavController().navigateUp()
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        super.onViewCreated(view, savedInstanceState)

        val id = navigationArgs.itemId
        if (id > 0) {
            viewModel.retrieveItem(id)
            observeUiState()
        }
    }

    private fun observeUiState() {
        launchAndRepeatWithViewLifecycle {
            launch {
                viewModel.uiState.collect { event ->
                    this@EventDetailFragment.event = event
                    bind(event)
                }
            }
        }
    }

    /**
     * Called when fragment is destroyed.
     */
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
