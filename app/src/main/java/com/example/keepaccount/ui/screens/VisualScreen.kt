package com.example.keepaccount.ui.screens

import android.graphics.Color
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Sort
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.example.keepaccount.R
import com.example.keepaccount.ViewModels.DayPieViewModel
import com.example.keepaccount.ViewModels.MonthPieViewModel
import com.example.keepaccount.ViewModels.ShowItem
import com.example.keepaccount.ViewModels.SortType
import com.example.keepaccount.ViewModels.VisualSharedViewModel
import com.example.keepaccount.ui.components.ItemRow
import com.github.mikephil.charting.animation.Easing
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.formatter.PercentFormatter
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VisualScreen(
    sharedViewModel: VisualSharedViewModel = koinViewModel()
) {
    val titles = listOf(
        stringResource(R.string.today_consumption_status),
        stringResource(R.string.monthly_consumption_status)
    )
    val pagerState = rememberPagerState(pageCount = { titles.size })
    val scope = rememberCoroutineScope()

    // 控制排序選單顯示
    var showMenu by remember { mutableStateOf(false) }

    Column(modifier = Modifier.fillMaxSize()) {
        // --- 頂部欄：包含標題與排序按鈕 ---
        CenterAlignedTopAppBar(
            title = { Text(stringResource(R.string.visual_fragment_title)) },
            colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer,
                titleContentColor = MaterialTheme.colorScheme.primary
            ),
            actions = {
                Box {
                    IconButton(onClick = { showMenu = true }) {
                        Icon(
                            imageVector = Icons.Default.Sort,
                            contentDescription = "Sort"
                        )
                    }

                    // 排序選單
                    DropdownMenu(
                        expanded = showMenu,
                        onDismissRequest = { showMenu = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text("金額由高到低") }, // 建議放入 strings.xml
                            onClick = {
                                sharedViewModel.setSort(SortType.COST_DESC)
                                showMenu = false
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("金額由低到高") },
                            onClick = {
                                sharedViewModel.setSort(SortType.COST_ASC)
                                showMenu = false
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("預設排序") },
                            onClick = {
                                sharedViewModel.setSort(SortType.NO)
                                showMenu = false
                            }
                        )
                    }
                }
            }
        )

        // 分頁切換欄
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
                0 -> DayPieContent(sharedViewModel) // 內部會監聽 sortType
                1 -> MonthPieContent(sharedViewModel) // 內部會監聽 sortType
            }
        }
    }
}

@Composable
fun DayPieContent(
    sharedViewModel: VisualSharedViewModel,
    viewModel: DayPieViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val sortType by sharedViewModel.sortType.collectAsState()
    val context = LocalContext.current

    LaunchedEffect(sortType) {
        viewModel.sortItems(sortType)
    }

    LaunchedEffect(Unit) {
        val calendar = Calendar.getInstance()
        viewModel.loadTodayData(
            calendar.get(Calendar.YEAR).toString(),
            (calendar.get(Calendar.MONTH) + 1).toString(),
            calendar.get(Calendar.DAY_OF_MONTH).toString()
        )
    }

    Column(modifier = Modifier.fillMaxSize()) {
        AndroidView(
            modifier = Modifier
                .fillMaxWidth()
                .height(300.dp),
            factory = { ctx ->
                PieChart(ctx).apply {
                    setUsePercentValues(true)
                    description.isEnabled = false
                    setDrawHoleEnabled(true)
                    setHoleColor(Color.WHITE)
                    animateY(1400, Easing.EaseInOutQuad)
                }
            },
            update = { chart ->
                val pieDataSet = PieDataSet(uiState.pieEntries, "").apply {
                    colors = uiState.pieColors
                    sliceSpace = 3f
                    selectionShift = 10f
                }
                chart.data = PieData(pieDataSet).apply {
                    setValueFormatter(PercentFormatter())
                    setValueTextSize(12f)
                    setValueTextColor(Color.BLUE)
                }
                chart.centerText = "${context.getString(R.string.currency_symbol)}${uiState.totalCost}"
                chart.invalidate()
            }
        )

        HorizontalDivider()

        Text(
            text = stringResource(R.string.today_total_cost, uiState.totalCost),
            modifier = Modifier.padding(16.dp),
            style = MaterialTheme.typography.headlineSmall
        )

        LazyColumn {
            items(uiState.todayItems) { item ->
                ItemRow(
                    item = ShowItem(
                        name = item.itemName,
                        cost = item.itemPrice,
                        color = item.itemColor
                    )
                )
            }
        }
    }
}

@Composable
fun MonthPieContent(
    sharedViewModel: VisualSharedViewModel,
    viewModel: MonthPieViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val sortType by sharedViewModel.sortType.collectAsState()
    val context = LocalContext.current

    LaunchedEffect(sortType) {
        viewModel.sortItems(sortType)
    }

    LaunchedEffect(Unit) {
        val calendar = Calendar.getInstance()
        viewModel.loadMonthData(
            calendar.get(Calendar.YEAR).toString(),
            (calendar.get(Calendar.MONTH) + 1).toString()
        )
    }

    Column(modifier = Modifier.fillMaxSize()) {
        AndroidView(
            modifier = Modifier
                .fillMaxWidth()
                .height(300.dp),
            factory = { ctx ->
                PieChart(ctx).apply {
                    setUsePercentValues(true)
                    description.isEnabled = false
                    setDrawHoleEnabled(true)
                    setHoleColor(Color.WHITE)
                    animateY(1400, Easing.EaseInOutQuad)
                }
            },
            update = { chart ->
                val pieDataSet = PieDataSet(uiState.pieEntries, "").apply {
                    colors = uiState.pieColors
                    sliceSpace = 3f
                    selectionShift = 10f
                }
                chart.data = PieData(pieDataSet).apply {
                    setValueFormatter(PercentFormatter())
                    setValueTextSize(12f)
                    setValueTextColor(Color.BLUE)
                }
                chart.centerText = "${context.getString(R.string.currency_symbol)}${uiState.totalCost}"
                chart.invalidate()
            }
        )

        HorizontalDivider()

        Text(
            text = stringResource(R.string.this_month_cost, uiState.totalCost),
            modifier = Modifier.padding(16.dp),
            style = MaterialTheme.typography.headlineSmall
        )

        LazyColumn {
            items(uiState.todayItems) { item ->
                ItemRow(
                    item = ShowItem(
                        name = item.itemName,
                        cost = item.itemPrice,
                        color = item.itemColor
                    )
                )
            }
        }
    }
}
