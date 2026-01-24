package com.example.keepaccount

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.keepaccount.Entity.isReady
import com.example.keepaccount.ViewModels.LotteryCheckViewModel
import com.example.keepaccount.databinding.FragmentLotteryQrScanBinding
import com.example.keepaccount.util.ScreenshotUtil
import com.google.android.material.snackbar.Snackbar
import com.google.zxing.ResultPoint
import com.journeyapps.barcodescanner.BarcodeCallback
import com.journeyapps.barcodescanner.BarcodeResult
import com.journeyapps.barcodescanner.Size
import org.koin.androidx.viewmodel.ext.android.viewModel

class LotteryQRScanFragment : Fragment() {
    private val viewModel: LotteryCheckViewModel by viewModel()
    private var _binding: FragmentLotteryQrScanBinding? = null
    private val binding get() = _binding!!

    private var isScanningPausedByResult = false
    private var count = 0

    private val isLotteryReady: Boolean
        get() = viewModel.lotteryNumber.value.isReady()

    // 取 QR 前 10 碼 → 驗證 → 取後 8 碼
    private fun extractInvoiceLast8FromQr(raw: String): String? {
        if (raw.length < 10) return null
        val head = raw.substring(0, 10)
        val regex = Regex("^[A-Z]{2}\\d{8}$")
        return if (regex.matches(head)) head.takeLast(8) else null
    }

    private val callback =
        object : BarcodeCallback {
            override fun barcodeResult(result: BarcodeResult?) {
                if (!isLotteryReady) {
                    Toast.makeText(requireContext(), "資料尚未準備好，請稍候再掃描", Toast.LENGTH_SHORT).show()
                    return
                }

                if (isScanningPausedByResult) return
                result ?: return

                binding.barcodeScanner.pause()
                isScanningPausedByResult = true

                val invoiceNumber = extractInvoiceLast8FromQr(result.text)
                if (invoiceNumber == null) {
                    Toast.makeText(requireContext(), "無法解析發票號碼", Toast.LENGTH_SHORT).show()
                    isScanningPausedByResult = false
                    binding.barcodeScanner.resume()
                    return
                }

                val winningResult = viewModel.checkWinningByQr(invoiceNumber)

                binding.textView.text = winningResult.money

                // 只在成功時顯示與更新 count
                count++
                binding.count.apply {
                    visibility = View.VISIBLE
                    text = getString(R.string.count_invoice, count)
                }

                binding.chipScreenshot.visibility =
                    if (winningResult.type == QrWinningType.NONE) View.GONE else View.VISIBLE

                Toast.makeText(requireContext(), winningResult.money, Toast.LENGTH_LONG).show()
            }

            override fun possibleResultPoints(resultPoints: List<ResultPoint>) {}
        }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentLotteryQrScanBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        super.onViewCreated(view, savedInstanceState)

        val barcodeView = binding.barcodeScanner.barcodeView
        binding.barcodeScanner.post {
            val width = binding.barcodeScanner.width
            val height = binding.barcodeScanner.height
            barcodeView.framingRectSize =
                Size(
                    (width * 0.7f).toInt(),
                    (height * 0.3f).toInt(),
                )
        }

        binding.barcodeScanner.decodeContinuous(callback)

        binding.chipNextScan.setOnClickListener {
            isScanningPausedByResult = false
            binding.textView.text = ""
            binding.chipScreenshot.visibility = View.GONE
            binding.barcodeScanner.resume()
        }

        binding.chipScreenshot.setOnClickListener {
            ScreenshotUtil.captureAndSave(
                context = requireContext(),
                view = binding.root,
                onSuccess = { uri ->
                    Snackbar.make(binding.root, "截圖已儲存", Snackbar.LENGTH_LONG)
                        .setAction(getString(R.string.share)) {
                            shareImage(uri)
                        }
                        .show()
                },
                onError = {
                    Toast.makeText(requireContext(), "截圖失敗", Toast.LENGTH_SHORT).show()
                },
            )
        }
    }

    private fun shareImage(uri: Uri) {
        val intent =
            Intent(Intent.ACTION_SEND).apply {
                type = "image/*"
                putExtra(Intent.EXTRA_STREAM, uri)
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }
        startActivity(Intent.createChooser(intent, "分享圖片"))
    }

    override fun onResume() {
        super.onResume()
        if (!isScanningPausedByResult) {
            binding.barcodeScanner.resume()
        }
    }

    override fun onPause() {
        super.onPause()
        binding.barcodeScanner.pause()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
