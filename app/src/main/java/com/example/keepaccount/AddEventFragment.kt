
package com.example.keepaccount

import android.content.Context.INPUT_METHOD_SERVICE
import android.content.DialogInterface
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.keepaccount.Entity.Event
import com.example.keepaccount.Extension.launchAndRepeatWithViewLifecycle
import com.example.keepaccount.ViewModels.AddEventViewModel
import com.example.keepaccount.databinding.FragmentAddEventBinding
import com.flask.colorpicker.ColorPickerView
import com.flask.colorpicker.builder.ColorPickerDialogBuilder
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel
import kotlin.getValue

/**
 * Fragment to add or update an item in the Inventory database.
 */
class AddEventFragment : Fragment() {
    private val viewModel: AddEventViewModel by viewModel()

    private val navigationArgs: EventDetailFragmentArgs by navArgs()

    lateinit var event: Event

    private var _binding: FragmentAddEventBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        _binding = FragmentAddEventBinding.inflate(inflater, container, false)
        return binding.root
    }

    /**
     * Returns true if the EditTexts are not empty
     */
    private fun isEntryValid(): Boolean {
        return viewModel.isEntryValid(
            binding.itemName.text.toString(),
            binding.eventColorcode.toString(),
        )
    }

    /**
     * Binds views with the passed in [Event] information.
     */
    private fun bind(event: Event) {
        binding.apply {
            itemName.setText(event.eventName, TextView.BufferType.SPANNABLE)
            eventColorcode.setText(event.eventColorCode, TextView.BufferType.SPANNABLE)
            saveAction.setOnClickListener { updateItem() }
            chooseAction.setOnClickListener {
                ColorPickerDialogBuilder
                    .with(context)
                    .setTitle("Choose color")
                    .initialColor(Color.BLUE)
                    .wheelType(ColorPickerView.WHEEL_TYPE.CIRCLE)
                    .density(12)
                    .setOnColorSelectedListener { selectedColor ->
                        println(
                            "onColorSelected: 0x" +
                                Integer.toHexString(
                                    selectedColor,
                                ),
                        )
                    }
                    .setPositiveButton(
                        "ok",
                    ) { dialog, selectedColor, allColors ->
                        binding.eventColorcode.setText(
                            "#" +
                                Integer.toHexString(
                                    selectedColor,
                                ).substring(2),
                        )
                    }
                    .setNegativeButton(
                        "cancel",
                        DialogInterface.OnClickListener { dialog, which -> },
                    )
                    .build()
                    .show()
            }
        }
    }

    /**
     * Inserts the new Item into database and navigates up to list fragment.
     */
    private fun addNewEvent() {
        if (isEntryValid()) {
            viewModel.addNewEvent(
                getNewEventEntry(
                    binding.itemName.text.toString(),
                    binding.eventColorcode.text.toString(),
                ),
            )
            findNavController().popBackStack()
        }
    }

    private fun getNewEventEntry(
        itemName: String,
        itemColorcode: String,
    ): Event {
        return Event(
            eventName = itemName,
            eventColorCode = itemColorcode,
        )
    }

    /**
     * Updates an existing Item in the database and navigates up to list fragment.
     */
    private fun updateItem() {
        if (isEntryValid()) {
            viewModel.updateEvent(
                getUpdatedItemEntry(
                    this.navigationArgs.itemId,
                    this.binding.itemName.text.toString(),
                    this.binding.eventColorcode.text.toString(),
                ),
            )
            findNavController().popBackStack()
        }
    }

    private fun getUpdatedItemEntry(
        itemId: Int,
        itemName: String,
        itemColorcode: String,
    ): Event {
        return Event(
            id = itemId,
            eventName = itemName,
            eventColorCode = itemColorcode,
        )
    }

    /**
     * Called when the view is created.
     * The itemId Navigation argument determines the edit item  or add new item.
     * If the itemId is positive, this method retrieves the information from the database and
     * allows the user to update it.
     */
    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        super.onViewCreated(view, savedInstanceState)

        val id = navigationArgs.itemId
        if (id > 0) {
            viewModel.retrieveItem(id)
            observeUiState()
        } else {
            binding.saveAction.setOnClickListener {
                if (binding.itemName.text.toString().isBlank() ||
                    binding.eventColorcode.text.toString().isBlank()
                ) {
                    Toast.makeText(this.requireContext(), "尚有空白欄位", Toast.LENGTH_SHORT).show()
                } else {
                    addNewEvent()
                }
            }
            binding.chooseAction.setOnClickListener {
                ColorPickerDialogBuilder
                    .with(context)
                    .setTitle("Choose color")
                    .initialColor(Color.BLUE)
                    .wheelType(ColorPickerView.WHEEL_TYPE.CIRCLE)
                    .density(12)
                    .setOnColorSelectedListener { selectedColor ->
                        println(
                            "onColorSelected: 0x" +
                                Integer.toHexString(
                                    selectedColor,
                                ),
                        )
                    }
                    .setPositiveButton(
                        "ok",
                    ) { dialog, selectedColor, allColors ->
                        binding.eventColorcode.setText(
                            "#" +
                                Integer.toHexString(
                                    selectedColor,
                                ).substring(2),
                        )
                    }
                    .setNegativeButton(
                        "cancel",
                        DialogInterface.OnClickListener { dialog, which -> },
                    )
                    .build()
                    .show()
            }
        }
    }

    private fun observeUiState() {
        launchAndRepeatWithViewLifecycle {
            launch {
                viewModel.uiState.collect { item ->
                    bind(item)
                }
            }
        }
    }

    /**
     * Called before fragment is destroyed.
     */
    override fun onDestroyView() {
        super.onDestroyView()
        // Hide keyboard.
        val inputMethodManager =
            requireActivity().getSystemService(INPUT_METHOD_SERVICE) as
                InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(requireActivity().currentFocus?.windowToken, 0)
        _binding = null
    }
}
