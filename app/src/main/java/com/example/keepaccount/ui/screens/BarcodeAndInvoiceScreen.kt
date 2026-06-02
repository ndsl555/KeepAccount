package com.example.keepaccount.ui.screens

import android.app.Activity
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.keepaccount.R
import com.example.keepaccount.ViewModels.BarcodeViewModel
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BarcodeAndInvoiceScreen(
    navController: NavController
) {
    val titles = listOf(
        stringResource(R.string.bar_fragment_title),
        stringResource(R.string.lottery_check)
    )
    val pagerState = rememberPagerState(pageCount = { titles.size })
    val scope = rememberCoroutineScope()

    // 使用 Scaffold 包裹整個頁面以加入 TopAppBar
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text(stringResource(R.string.barcode_and_invoice_fragment_title)) }, // 這裡可以改用 stringResource
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.primary
                )
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding) // 確保內容不被標頭遮住
        ) {
            TabRow(selectedTabIndex = pagerState.currentPage) {
                titles.forEachIndexed { index, title ->
                    Tab(
                        selected = pagerState.currentPage == index,
                        onClick = { scope.launch { pagerState.animateScrollToPage(index) } },
                        text = { Text(title) }
                    )
                }
            }

            HorizontalPager(
                state = pagerState,
                modifier = Modifier.weight(1f)
            ) { page ->
                when (page) {
                    0 -> BarcodeContent()
                    1 -> LotteryCheckContent(navController)
                }
            }
        }
    }
}

@Composable
fun BarcodeContent(
    viewModel: BarcodeViewModel = koinViewModel()
) {
    val context = LocalContext.current
    val activity = context as? Activity

    val barcodeBitmap by viewModel.barcodeBitmap.observeAsState()
    val barcodeText by viewModel.barcodeText.observeAsState("")
    var showInputDialog by remember { mutableStateOf(false) }
    var isLightOn by remember { mutableStateOf(false) }

    LaunchedEffect(isLightOn) {
        activity?.window?.let { window ->
            val layoutParams = window.attributes
            layoutParams.screenBrightness = if (isLightOn) 1.0f else -1.0f
            window.attributes = layoutParams
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            activity?.window?.let { window ->
                val layoutParams = window.attributes
                layoutParams.screenBrightness = -1.0f
                window.attributes = layoutParams
            }
        }
    }

    LaunchedEffect(Unit) {
        viewModel.loadLatestBarcode()
    }

    if (showInputDialog) {
        BarcodeInputDialog(
            onDismiss = { showInputDialog = false },
            onConfirm = { text ->
                viewModel.saveBarcode(text)
                showInputDialog = false
            }
        )
    }

    // 注意：這裡的 Scaffold 只負責 FAB，不再包含 TopAppBar
    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = { showInputDialog = true }) {
                Icon(Icons.Default.Edit, contentDescription = "Edit Barcode")
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            barcodeBitmap?.let {
                Image(
                    bitmap = it.asImageBitmap(),
                    contentDescription = "Barcode",
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(150.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = barcodeText ?: "",
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier.clickable {
                    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                    val clip = ClipData.newPlainText("barcode", barcodeText)
                    clipboard.setPrimaryClip(clip)
                    Toast.makeText(context, "已複製: $barcodeText", Toast.LENGTH_SHORT).show()
                }
            )

            Spacer(modifier = Modifier.height(32.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.clickable { isLightOn = !isLightOn }
            ) {
                Text(stringResource(R.string.adjust_brightness))
                Spacer(modifier = Modifier.width(8.dp))
                Switch(
                    checked = isLightOn,
                    onCheckedChange = { isLightOn = it }
                )
            }
        }
    }
}

@Composable
fun BarcodeInputDialog(
    onDismiss: () -> Unit,
    onConfirm: (String) -> Unit
) {
    var text by remember { mutableStateOf("") }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.enter_barcode_hint)) },
        text = {
            OutlinedTextField(
                value = text,
                onValueChange = { text = it },
                label = { Text(stringResource(R.string.barcode_fragment_title)) },
                singleLine = true
            )
        },
        confirmButton = {
            TextButton(onClick = { if (text.isNotBlank()) onConfirm(text) }) {
                Text(stringResource(R.string.confirm))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.no))
            }
        }
    )
}

@Composable
fun LotteryCheckContent(navController: NavController) {
    val titles = listOf(
        stringResource(R.string.manual_check),
        stringResource(R.string.qr_code_scan)
    )
    val pagerState = rememberPagerState(pageCount = { titles.size })
    val scope = rememberCoroutineScope()

    Column(modifier = Modifier.fillMaxSize()) {
        TabRow(
            selectedTabIndex = pagerState.currentPage,
            containerColor = MaterialTheme.colorScheme.secondaryContainer
        ) {
            titles.forEachIndexed { index, title ->
                Tab(
                    selected = pagerState.currentPage == index,
                    onClick = { scope.launch { pagerState.animateScrollToPage(index) } },
                    text = { Text(title) }
                )
            }
        }

        HorizontalPager(
            state = pagerState,
            modifier = Modifier.weight(1f)
        ) { page ->
            when (page) {
                0 -> LotteryManualCheckScreen() // 請確保此處有實作對應 Screen
                1 -> LotteryQRScanScreen() // 請確保此處有實作對應 Screen
            }
        }
    }
}
