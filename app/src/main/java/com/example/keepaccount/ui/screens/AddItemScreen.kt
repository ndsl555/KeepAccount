package com.example.keepaccount.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.core.graphics.toColorInt
import androidx.navigation.NavController
import com.example.keepaccount.Entity.Event
import com.example.keepaccount.Entity.Item
import com.example.keepaccount.R
import com.example.keepaccount.ViewModels.AddItemViewModel
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddItemScreen(
    navController: NavController,
    year: String,
    month: String,
    day: String,
    viewModel: AddItemViewModel = koinViewModel()
) {
    var itemName by remember { mutableStateOf("") }
    var itemPrice by remember { mutableStateOf("") }
    var itemColorCode by remember { mutableStateOf("") }
    var showEventDialog by remember { mutableStateOf(false) }

    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        viewModel.getAllEvents()
    }

    if (showEventDialog) {
        EventSelectionDialog(
            events = uiState,
            onDismiss = { showEventDialog = false },
            onEventSelected = { event ->
                itemName = event.eventName
                itemColorCode = event.eventColorCode ?: ""
                showEventDialog = false
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.add_fragment_title)) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            OutlinedTextField(
                value = itemName,
                onValueChange = { itemName = it },
                label = { Text("項目名稱") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = itemPrice,
                onValueChange = { itemPrice = it },
                label = { Text("金額") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = itemColorCode,
                onValueChange = { itemColorCode = it },
                label = { Text("顏色代碼") },
                modifier = Modifier.fillMaxWidth()
            )

            Button(
                onClick = { showEventDialog = true },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("選擇現有項目")
            }

            Button(
                onClick = {
                    if (itemName.isBlank() || itemPrice.isBlank() || itemColorCode.isBlank()) {
                        Toast.makeText(context, context.getString(R.string.empty_fields), Toast.LENGTH_SHORT).show()
                    } else {
                        val price = itemPrice.toIntOrNull() ?: 0
                        if (viewModel.isEntryValid(itemName, itemPrice, itemColorCode, year, month, day)) {
                            viewModel.addNewItem(
                                Item(
                                    itemName = itemName,
                                    itemPrice = price,
                                    itemColorcode = itemColorCode,
                                    itemYear = year,
                                    itemMonth = month,
                                    itemDay = day
                                )
                            )
                            navController.popBackStack()
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("儲存")
            }
        }
    }
}

@Composable
fun EventSelectionDialog(
    events: List<Event>,
    onDismiss: () -> Unit,
    onEventSelected: (Event) -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(max = 400.dp)
                .padding(16.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "選擇項目",
                    style = MaterialTheme.typography.headlineSmall,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
                LazyColumn {
                    items(events) { event ->
                        ListItem(
                            headlineContent = { Text(event.eventName) },
                            // --- 新增右側顏色顯示 ---
                            trailingContent = {
                                val color = try {
                                    if (event.eventColorCode.isNotBlank()) {
                                        // 解析十六進位顏色字串
                                        Color(event.eventColorCode.toColorInt())
                                    } else {
                                        Color.Transparent
                                    }
                                } catch (e: Exception) {
                                    Color.Gray // 解析失敗時顯示灰色
                                }

                                Box(
                                    modifier = Modifier
                                        .size(24.dp)
                                        .clip(CircleShape)
                                        .background(color)
                                        .border(
                                            0.5.dp,
                                            Color.LightGray,
                                            CircleShape
                                        ) // 增加細邊框避免與背景混淆
                                )
                            },
                            // -----------------------
                            modifier = Modifier.clickable { onEventSelected(event) }
                        )
                        HorizontalDivider()
                    }
                }
            }
        }
    }
}
