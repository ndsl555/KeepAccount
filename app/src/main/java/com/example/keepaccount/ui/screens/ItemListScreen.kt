package com.example.keepaccount.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Sort
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.graphics.toColorInt
import androidx.navigation.NavController
import com.applandeo.materialcalendarview.CalendarView
import com.applandeo.materialcalendarview.EventDay
import com.applandeo.materialcalendarview.listeners.OnCalendarPageChangeListener
import com.applandeo.materialcalendarview.listeners.OnDayClickListener
import com.example.keepaccount.R
import com.example.keepaccount.Screen
import com.example.keepaccount.ViewModels.ItemListViewModel
import com.example.keepaccount.ViewModels.ShowItem
import com.example.keepaccount.ViewModels.SortType
import org.koin.androidx.compose.koinViewModel
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ItemListScreen(
    navController: NavController,
    viewModel: ItemListViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val markedDays by viewModel.markedDays.collectAsState()
// 控制排序選單顯示
    var showMenu by remember { mutableStateOf(false) }
    var selectedDate by remember { mutableStateOf(Calendar.getInstance()) }
    // 新增：用來追蹤月曆目前顯示的月份（用於更新標記）
    var displayedMonth by remember { mutableStateOf(Calendar.getInstance()) }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text(stringResource(R.string.list_fragment_title)) },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.primary
                ),
                actions = {
                    Box {
                        IconButton(onClick = { showMenu = true }) {
                            Icon(Icons.Default.Sort, contentDescription = "Sort")
                        }
                        DropdownMenu(
                            expanded = showMenu,
                            onDismissRequest = { showMenu = false }
                        ) {
                            DropdownMenuItem(
                                text = { Text(stringResource(R.string.dscend)) }, // 建議放入 strings.xml
                                onClick = {
                                    viewModel.sortItems(SortType.COST_DESC)
                                    showMenu = false
                                }
                            )
                            DropdownMenuItem(
                                text = { Text(stringResource(R.string.ascend)) },
                                onClick = {
                                    viewModel.sortItems(SortType.COST_ASC)
                                    showMenu = false
                                }
                            )
                            DropdownMenuItem(
                                text = { Text(stringResource(R.string.inital)) },
                                onClick = {
                                    viewModel.sortItems(SortType.NO)
                                    showMenu = false
                                }
                            )
                        }
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = {
                val year = selectedDate.get(Calendar.YEAR).toString()
                val month = (selectedDate.get(Calendar.MONTH) + 1).toString()
                val day = selectedDate.get(Calendar.DAY_OF_MONTH).toString()
                navController.navigate(Screen.AddItem.createRoute(year, month, day))
            }) {
                Icon(Icons.Default.Add, contentDescription = "Add")
            }
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding)) {
            // 橋接原本的 MaterialCalendarView
            AndroidView(
                modifier = Modifier.fillMaxWidth().height(370.dp),
                factory = { context ->
                    CalendarView(context).apply {
                        setOnDayClickListener(object : OnDayClickListener {
                            override fun onDayClick(eventDay: EventDay) {
                                selectedDate = eventDay.calendar
                                viewModel.getItemsByDate(
                                    selectedDate.get(Calendar.YEAR).toString(),
                                    (selectedDate.get(Calendar.MONTH) + 1).toString(),
                                    selectedDate.get(Calendar.DAY_OF_MONTH).toString()
                                )
                            }
                        })

                        // 監聽下個月切換
                        setOnForwardPageChangeListener(object : OnCalendarPageChangeListener {
                            override fun onChange() {
                                displayedMonth = currentPageDate.clone() as Calendar
                                viewModel.getMarkedDays(
                                    displayedMonth.get(Calendar.YEAR).toString(),
                                    (displayedMonth.get(Calendar.MONTH) + 1).toString()
                                )
                            }
                        })

                        // 監聽上個月切換
                        setOnPreviousPageChangeListener(object : OnCalendarPageChangeListener {
                            override fun onChange() {
                                displayedMonth = currentPageDate.clone() as Calendar
                                viewModel.getMarkedDays(
                                    displayedMonth.get(Calendar.YEAR).toString(),
                                    (displayedMonth.get(Calendar.MONTH) + 1).toString()
                                )
                            }
                        })
                    }
                },
                update = { view ->
                    // 根據 markedDays 建立事件，使用 displayedMonth 確保日期設在正確的月份頁面
                    val events = markedDays.map { day ->
                        val cal = displayedMonth.clone() as Calendar
                        cal.set(Calendar.DAY_OF_MONTH, day)
                        EventDay(cal, R.drawable.sample_three_icons)
                    }
                    view.setEvents(events)
                }
            )

            Divider()

            LazyColumn(modifier = Modifier.fillMaxSize()) {
                items(uiState) { item ->
                    ItemRow(item = item, onDelete = {
                        viewModel.deleteItem(
                            selectedDate.get(Calendar.YEAR).toString(),
                            (selectedDate.get(Calendar.MONTH) + 1).toString(),
                            selectedDate.get(Calendar.DAY_OF_MONTH).toString(),
                            item.name
                        )
                    })
                }
            }
        }
    }

    LaunchedEffect(Unit) {
        // 初始載入當天資料
        viewModel.getItemsByDate(
            selectedDate.get(Calendar.YEAR).toString(),
            (selectedDate.get(Calendar.MONTH) + 1).toString(),
            selectedDate.get(Calendar.DAY_OF_MONTH).toString()
        )
        // 初始載入當月標記
        viewModel.getMarkedDays(
            displayedMonth.get(Calendar.YEAR).toString(),
            (displayedMonth.get(Calendar.MONTH) + 1).toString()
        )
    }
}

@Composable
fun ItemRow(item: ShowItem, onDelete: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = item.name,
                    style = MaterialTheme.typography.bodyLarge
                )
                Text(
                    text = "$ ${item.cost}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = try {
                        Color(item.color.toColorInt())
                    } catch (e: Exception) {
                        MaterialTheme.colorScheme.onSurfaceVariant
                    }
                )
            }

            IconButton(onClick = onDelete) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Delete",
                    tint = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}
