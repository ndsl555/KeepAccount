package com.example.keepaccount.ui.screens

import android.graphics.Color
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.navigation.NavController
import com.example.keepaccount.R
import com.example.keepaccount.ViewModels.StripViewModel
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.ValueFormatter
import org.koin.androidx.compose.koinViewModel
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StripScreen(
    navController: NavController,
    viewModel: StripViewModel = koinViewModel()
) {
    val context = LocalContext.current
    val calendar = Calendar.getInstance()
    val thisYear = calendar.get(Calendar.YEAR).toString()
    val thisMonth = (calendar.get(Calendar.MONTH) + 1).toString()

    val boardUI by viewModel.boardUI.collectAsState()
    val yearlyCosts by viewModel.yearlyCosts.collectAsState()

    var isHidden by remember { mutableStateOf(false) }
    var showBudgetDialog by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        viewModel.observeItems(thisYear, thisMonth)
        viewModel.getYearlyCosts(thisYear)
    }

    if (showBudgetDialog) {
        BudgetInputDialog(
            onDismiss = { showBudgetDialog = false },
            onConfirm = { value ->
                viewModel.saveBudGet(value, thisYear, thisMonth)
                showBudgetDialog = false
            }
        )
    }

    // 使用 Scaffold 來承載標頭
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text(stringResource(R.string.budget_fragment_title)) },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.primary
                )
            )
        }
    ) { innerPadding ->
        // 內容必須放在 Scaffold 的 lambda 中，並套用 innerPadding
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding) // 重要：避免內容被標頭遮住
                .padding(16.dp), // 原本頁面的邊距
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = stringResource(R.string.month_total_cost, thisMonth),
                style = MaterialTheme.typography.titleLarge
            )

            // Progress Indicator (進度條)
            Box(contentAlignment = Alignment.Center, modifier = Modifier.size(200.dp)) {
                val progress = if (boardUI.budget <= 0) {
                    if (boardUI.cost == 0) 1f else 0f
                } else {
                    (1f - (boardUI.cost.toFloat() / boardUI.budget.toFloat())).coerceIn(0f, 1f)
                }

                CircularProgressIndicator(
                    progress = { progress },
                    modifier = Modifier.fillMaxSize(),
                    strokeWidth = 12.dp,
                    color = MaterialTheme.colorScheme.primary,
                    trackColor = MaterialTheme.colorScheme.surfaceVariant
                )
                Text(
                    text = "${(progress * 100).toInt()}%",
                    style = MaterialTheme.typography.headlineMedium
                )
            }

            // Amount Display (金額顯示)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("支出", style = MaterialTheme.typography.bodyMedium)
                    Text(
                        text = if (isHidden) "****" else context.getString(R.string.cost_with_unit, boardUI.cost),
                        style = MaterialTheme.typography.headlineSmall
                    )
                }
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("預算", style = MaterialTheme.typography.bodyMedium)
                    Text(
                        text = if (isHidden) "****" else context.getString(R.string.budget_with_unit, boardUI.budget),
                        style = MaterialTheme.typography.headlineSmall
                    )
                }
            }

            HorizontalDivider()

            // Controls (按鈕控制)
            Row(
                modifier = Modifier.fillMaxWidth(),
                // 將排列方式改為 SpaceBetween，讓子元件分別推向兩端
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically // 確保兩者垂直居中對齊
            ) {
                FilterChip(
                    selected = isHidden,
                    onClick = { isHidden = !isHidden },
                    label = { Text(if (isHidden) "顯示金額" else "隱藏金額") },
                    leadingIcon = {
                        Icon(
                            imageVector = if (isHidden) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                            contentDescription = null
                        )
                    }
                )

                Button(onClick = { showBudgetDialog = true }) {
                    Text("設定預算")
                }
            }

            // Line Chart (折線圖)
            if (!isHidden && yearlyCosts.isNotEmpty()) {
                AndroidView(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    factory = { ctx ->
                        LineChart(ctx).apply {
                            description.isEnabled = false
                            setTouchEnabled(false)
                            axisRight.isEnabled = false
                            legend.isEnabled = false
                            xAxis.apply {
                                position = XAxis.XAxisPosition.BOTTOM
                                granularity = 1f
                                setLabelCount(12, true)
                                valueFormatter = object : ValueFormatter() {
                                    override fun getFormattedValue(value: Float): String = "${value.toInt()}月"
                                }
                            }
                            axisLeft.apply {
                                axisMinimum = 0f
                            }
                        }
                    },
                    update = { chart ->
                        val entries = yearlyCosts.toSortedMap().map { Entry(it.key.toFloat(), it.value.toFloat()) }
                        val dataSet = LineDataSet(entries, "每月花費").apply {
                            color = Color.BLUE
                            setCircleColor(Color.BLUE)
                            lineWidth = 2.5f
                            circleRadius = 4f
                            setDrawValues(false)
                        }
                        chart.data = LineData(dataSet)
                        chart.animateX(800)
                        chart.invalidate()
                    }
                )
            } else {
                Spacer(modifier = Modifier.weight(1f))
            }
        }
    }
}

@Composable
fun BudgetInputDialog(
    onDismiss: () -> Unit,
    onConfirm: (Int) -> Unit
) {
    var text by remember { mutableStateOf("") }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("設定預算") },
        text = {
            OutlinedTextField(
                leadingIcon = {
                    Icon(painter = painterResource(R.drawable.baseline_attach_money_24), contentDescription = null)
                },
                value = text,
                onValueChange = { text = it },
                label = { Text("輸入預算金額") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )
        },
        confirmButton = {
            TextButton(onClick = {
                val value = text.toIntOrNull()
                if (value != null) onConfirm(value)
            }) {
                Text("確認")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("取消")
            }
        }
    )
}
