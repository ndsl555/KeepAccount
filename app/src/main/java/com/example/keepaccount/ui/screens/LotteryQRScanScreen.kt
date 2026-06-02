package com.example.keepaccount.ui.screens

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.example.keepaccount.Entity.isReady
import com.example.keepaccount.R
import com.example.keepaccount.ViewModels.LotteryCheckViewModel
import com.journeyapps.barcodescanner.BarcodeCallback
import com.journeyapps.barcodescanner.BarcodeResult
import com.journeyapps.barcodescanner.CompoundBarcodeView
import com.journeyapps.barcodescanner.Size
import org.koin.androidx.compose.koinViewModel

@Composable
fun LotteryQRScanScreen(
    viewModel: LotteryCheckViewModel = koinViewModel()
) {
    val context = LocalContext.current
    val lotteryNumber by viewModel.lotteryNumber.collectAsState()
    var scanResultText by remember { mutableStateOf("") }
    var count by remember { mutableStateOf(0) }
    var isScanningPaused by remember { mutableStateOf(false) }

    val barcodeView = remember { CompoundBarcodeView(context) }

    fun extractInvoiceLast8FromQr(raw: String): String? {
        if (raw.length < 10) return null
        val head = raw.substring(0, 10)
        val regex = Regex("^[A-Z]{2}\\d{8}$")
        return if (regex.matches(head)) head.takeLast(8) else null
    }

    DisposableEffect(Unit) {
        barcodeView.resume()
        onDispose {
            barcodeView.pause()
        }
    }

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        ) {
            AndroidView(
                factory = {
                    barcodeView.apply {
                        val callback = object : BarcodeCallback {
                            override fun barcodeResult(result: BarcodeResult?) {
                                if (!lotteryNumber.isReady()) {
                                    Toast.makeText(context, "資料尚未準備好", Toast.LENGTH_SHORT).show()
                                    return
                                }
                                if (isScanningPaused) return
                                result ?: return

                                pause()
                                isScanningPaused = true

                                val invoiceNumber = extractInvoiceLast8FromQr(result.text)
                                if (invoiceNumber == null) {
                                    Toast.makeText(context, "無法解析發票", Toast.LENGTH_SHORT).show()
                                    isScanningPaused = false
                                    resume()
                                    return
                                }

                                val winningResult = viewModel.checkWinningByQr(invoiceNumber)
                                scanResultText = winningResult.money ?: ""
                                count++

                                Toast.makeText(context, winningResult.money, Toast.LENGTH_LONG).show()
                            }
                        }
                        decodeContinuous(callback)
                        // 設定掃描框大小
                        post {
                            this.barcodeView.framingRectSize = Size((width * 0.7f).toInt(), (height * 0.3f).toInt())
                        }
                    }
                },
                modifier = Modifier.fillMaxSize()
            )
        }

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                if (count > 0) {
                    Text(text = stringResource(R.string.count_invoice, count))
                }

                Text(
                    text = scanResultText,
                    style = MaterialTheme.typography.headlineSmall,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(vertical = 8.dp)
                )

                Button(
                    onClick = {
                        isScanningPaused = false
                        scanResultText = ""
                        barcodeView.resume()
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("下一張")
                }
            }
        }
    }
}
sealed class PrizeType {
    data class FirstPrize(val index: Int) : PrizeType() // 第幾組頭獎

    object SpecialistPrize : PrizeType()

    object SpecialPrize : PrizeType()
}

// 表示判斷結果
data class WinningCheckResult(
    val state: WinningState,
    val winningPrizes: List<PrizeType> = emptyList() // 可以中多個獎
)

data class Prize(
    val type: String,
    val number: String,
    val result: String
)

enum class WinningState {
    INITIAL,
    BINGO,
    MAYBE,
    NONE
}

enum class QrWinningType {
    SPECIALIST_PRIZE, // 特別獎
    SPECIAL_PRIZE, // 特獎
    FIRST_PRIZE, // 頭獎
    NONE
}

data class QrWinningResult(
    val type: QrWinningType,
    val money: String? = null
)
