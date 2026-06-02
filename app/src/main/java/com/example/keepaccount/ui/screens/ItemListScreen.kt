package com.example.keepaccount.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
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
import com.applandeo.materialcalendarview.listeners.OnDayClickListener
import com.example.keepaccount.R
import com.example.keepaccount.Screen
import com.example.keepaccount.ViewModels.ItemListViewModel
import com.example.keepaccount.ViewModels.ShowItem
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

    var selectedDate by remember { mutableStateOf(Calendar.getInstance()) }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text(stringResource(R.string.list_fragment_title)) },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.primary
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = {
                val year = selectedDate.get(Calendar.YEAR).toString()
                val month = (selectedDate.get(Calendar.MONTH) + 1).toString()
                val day = selectedDate.get(Calendar.DAY_OF_MONTH).toString()
                // 實作導航邏輯：導向新增頁面並帶入日期參數
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
                    }
                },
                update = { view ->
                    val events = markedDays.map { day ->
                        val cal = selectedDate.clone() as Calendar
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
        viewModel.getItemsByDate(
            selectedDate.get(Calendar.YEAR).toString(),
            (selectedDate.get(Calendar.MONTH) + 1).toString(),
            selectedDate.get(Calendar.DAY_OF_MONTH).toString()
        )
        viewModel.getMarkedDays(
            selectedDate.get(Calendar.YEAR).toString(),
            (selectedDate.get(Calendar.MONTH) + 1).toString()
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
            verticalAlignment = Alignment.CenterVertically // 讓內容垂直居中
        ) {
            Column {
                Text(
                    text = item.name,
                    style = MaterialTheme.typography.bodyLarge
                )
                Text(
                    text = "$ ${item.cost}",
                    style = MaterialTheme.typography.bodyMedium,
                    // 修正顏色報錯：顯式轉換並處理可能的解析錯誤
                    color = try {
                        Color(item.color.toColorInt())
                    } catch (e: Exception) {
                        MaterialTheme.colorScheme.onSurfaceVariant
                    }
                )
            }

            // 修正：使用 IconButton 加上 Icon 來顯示刪除按鈕
            IconButton(onClick = onDelete) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Delete",
                    tint = MaterialTheme.colorScheme.error // 刪除按鈕通常用紅色
                )
            }
        }
    }
}
