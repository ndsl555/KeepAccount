package com.example.keepaccount

import android.app.AlertDialog
import android.os.Bundle
import android.view.*
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.keepaccount.ViewModels.BarcodeViewModel
import com.example.keepaccount.databinding.FragmentBarcodeBinding
import org.koin.androidx.viewmodel.ext.android.viewModel
import kotlin.getValue
import kotlin.text.isNotBlank

class BarCodeFragment : Fragment() {
    private var _binding: FragmentBarcodeBinding? = null
    private val binding get() = _binding!!
    private val viewModel: BarcodeViewModel by viewModel()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentBarcodeBinding.inflate(inflater, container, false)

        setupListeners()
        observeViewModel()
        viewModel.loadLatestBarcode()

        return binding.root
    }

    private fun setupListeners() {
        binding.floatingActionButton.setOnClickListener {
            showInputDialog()
        }

        binding.light.setOnCheckedChangeListener { _, isChecked ->
            adjustBrightness(isChecked)
        }
    }

    private fun observeViewModel() {
        viewModel.barcodeBitmap.observe(viewLifecycleOwner) { bitmap ->
            binding.imageBarcode.setImageBitmap(bitmap)
        }

        viewModel.barcodeText.observe(viewLifecycleOwner) { text ->
            binding.codeNum.text = text
        }
    }

    private fun adjustBrightness(enable: Boolean) {
        val layoutParams = requireActivity().window.attributes
        layoutParams.screenBrightness = if (enable) 1.0f else -1.0f
        requireActivity().window.attributes = layoutParams
    }

    private fun showInputDialog() {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle(getString(R.string.enter_string))

        val input = EditText(requireContext())
        builder.setView(input)

        builder.setPositiveButton(getString(R.string.yes)) { _, _ ->
            val text = input.text.toString()
            if (text.isNotBlank()) {
                viewModel.saveBarcode(text)
                Toast.makeText(requireContext(), getString(R.string.saved), Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(requireContext(), getString(R.string.not_entered), Toast.LENGTH_SHORT).show()
            }
        }

        builder.setNegativeButton(getString(R.string.no)) { dialog, _ -> dialog.dismiss() }

        builder.show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
