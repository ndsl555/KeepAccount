package com.example.keepaccount.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.keepaccount.Entity.Event
import com.example.keepaccount.R
import com.example.keepaccount.ViewModels.AddEventViewModel
import com.flask.colorpicker.ColorPickerView
import com.flask.colorpicker.builder.ColorPickerDialogBuilder
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEventScreen(
    navController: NavController,
    eventId: Int = -1,
    viewModel: AddEventViewModel = koinViewModel()
) {
    var eventName by remember { mutableStateOf("") }
    var eventColorCode by remember { mutableStateOf("#2196F3") }

    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    LaunchedEffect(eventId) {
        if (eventId > 0) {
            viewModel.retrieveItem(eventId)
        }
    }

    LaunchedEffect(uiState) {
        if (eventId > 0 && uiState.id == eventId) {
            eventName = uiState.eventName
            eventColorCode = uiState.eventColorCode ?: "#2196F3"
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (eventId > 0) "編輯項目" else "新增項目") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
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
                value = eventName,
                onValueChange = { eventName = it },
                label = { Text("項目名稱") },
                modifier = Modifier.fillMaxWidth()
            )

            // 呼叫你原本的 ColorPickerDialogBuilder
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        ColorPickerDialogBuilder
                            .with(context)
                            .setTitle(context.getString(R.string.choose_color_title))
                            .initialColor(android.graphics.Color.parseColor(eventColorCode))
                            .wheelType(ColorPickerView.WHEEL_TYPE.CIRCLE)
                            .density(12)
                            .setPositiveButton(context.getString(R.string.yes)) { _, selectedColor, _ ->
                                eventColorCode = "#" + Integer.toHexString(selectedColor).substring(2).uppercase()
                            }
                            .setNegativeButton(context.getString(R.string.no)) { _, _ -> }
                            .build()
                            .show()
                    }
                    .padding(vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(
                            try {
                                Color(android.graphics.Color.parseColor(eventColorCode))
                            } catch (e: Exception) {
                                Color.Blue
                            }
                        )
                )
                Text(
                    text = "選擇顏色 ($eventColorCode)",
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.weight(1f))

            Button(
                onClick = {
                    if (eventName.isBlank()) {
                        Toast.makeText(context, context.getString(R.string.empty_fields), Toast.LENGTH_SHORT).show()
                    } else {
                        if (eventId > 0) {
                            viewModel.updateEvent(
                                Event(id = eventId, eventName = eventName, eventColorCode = eventColorCode)
                            )
                        } else {
                            viewModel.addNewEvent(
                                Event(eventName = eventName, eventColorCode = eventColorCode)
                            )
                        }
                        navController.popBackStack()
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("儲存")
            }
        }
    }
}
