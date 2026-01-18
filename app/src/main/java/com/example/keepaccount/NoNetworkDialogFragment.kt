package com.example.keepaccount

import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.example.keepaccount.databinding.DialogNoNetworkBinding

class NoNetworkDialogFragment : DialogFragment(), View.OnClickListener {
    private var _binding: DialogNoNetworkBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = DialogNoNetworkBinding.inflate(inflater, container, false)
        dialog?.window?.setBackgroundDrawableResource(android.R.color.transparent)
        return binding.root
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        super.onViewCreated(view, savedInstanceState)
        initView()
    }

    private fun initView() {
        binding.apply {
            dialogTitle.text = getString(android.R.string.dialog_alert_title)
            dialogMessage.text = getString(R.string.network_type_no_network)
            okButton.text = getString(R.string.i_know)
            goToSettingButton.text = getString(R.string.go_to_setting)
            okButton.setOnClickListener(this@NoNetworkDialogFragment)
            goToSettingButton.setOnClickListener(this@NoNetworkDialogFragment)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onClick(p0: View) {
        when (p0.id) {
            R.id.ok_button -> {
                dismiss()
            }
            R.id.go_to_setting_button -> {
                Intent(Settings.ACTION_WIRELESS_SETTINGS).apply {
                    startActivity(this)
                }
                dismiss()
            }
        }
    }
}
