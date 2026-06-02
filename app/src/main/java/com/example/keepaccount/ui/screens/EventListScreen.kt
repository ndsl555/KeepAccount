package com.example.keepaccount.ui.screens

import androidx.compose.animation.core.copy
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.core.graphics.toColorInt
import androidx.navigation.NavController
import com.example.keepaccount.R
import com.example.keepaccount.Screen
import com.example.keepaccount.ViewModels.EventListViewModel
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EventListScreen(
    navController: NavController,
    viewModel: EventListViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    // 每次進入頁面自動刷新資料
    LaunchedEffect(Unit) {
        viewModel.getAllEvents()
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text(stringResource(R.string.event_fragment_title)) },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.primary
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = {
                // 使用 Screen 路由跳轉至新增頁面
                navController.navigate(Screen.AddEvent.createRoute())
            }) {
                Icon(Icons.Default.Add, contentDescription = "Add Event")
            }
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            items(uiState) { event ->
                ListItem(
                    headlineContent = { Text(event.eventName) },
                    // 使用 trailingContent 將內容放置在最右邊
                    trailingContent = {
                        val displayColor = try {
                            // 解析顏色字串，若為 null 或格式錯誤則使用灰色
                            if (event.eventColorCode.isNotBlank()) {
                                Color(event.eventColorCode.toColorInt())
                            } else {
                                Color.LightGray
                            }
                        } catch (e: Exception) {
                            Color.LightGray
                        }

                        // 顯示一個圓形的色塊
                        Box(
                            modifier = Modifier
                                .size(24.dp)
                                .background(displayColor, shape = CircleShape)
                                .border(
                                    1.dp,
                                    Color.Gray.copy(alpha = 0.3f),
                                    CircleShape
                                ) // 加個細邊框更好看
                        )
                    },
                    modifier = Modifier.clickable {
                        // 跳轉至項目詳情頁面
                        navController.navigate(Screen.EventDetail.createRoute(event.id))
                    }
                )
                HorizontalDivider()
            }
        }
    }
}
