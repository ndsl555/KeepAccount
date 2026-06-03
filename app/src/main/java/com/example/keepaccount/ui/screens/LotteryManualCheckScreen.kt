package com.example.keepaccount.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import com.example.keepaccount.Entity.InvoiceNumber
import com.example.keepaccount.Entity.isReady
import com.example.keepaccount.R
import com.example.keepaccount.ViewModels.LotteryCheckViewModel
import org.koin.androidx.compose.koinViewModel

@Composable
fun LotteryManualCheckScreen(
    viewModel: LotteryCheckViewModel = koinViewModel()
) {
    val lotteryNumber by viewModel.lotteryNumber.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    var inputText by remember { mutableStateOf("") }
    var count by remember { mutableStateOf(0) }
    val focusRequester = remember { FocusRequester() }
    val keyboardController = LocalSoftwareKeyboardController.current

    LaunchedEffect(Unit) {
        viewModel.getLotteryNumberFromDB()
    }

    if (isLoading) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
    } else if (lotteryNumber.isReady()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = lotteryNumber.topic,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.height(16.dp))

            // 偽裝的輸入框
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        focusRequester.requestFocus()
                        keyboardController?.show()
                    },
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                repeat(3) { index ->
                    DigitBox(
                        inputText.getOrNull(index)?.toString() ?: "",
                        onClick = {
                            focusRequester.requestFocus()
                            keyboardController?.show()
                        }
                    )
                    if (index < 2) Spacer(modifier = Modifier.width(8.dp))
                }
            }

            // 隱藏的 TextField 用於接收鍵盤輸入
            Box(modifier = Modifier.size(1.dp).alpha(0f)) {
                BasicTextField(
                    value = inputText,
                    onValueChange = {
                        if (it.length <= 3) {
                            inputText = it
                            if (it.length == 3) {
                                count++
                            }
                        } else {
                            inputText = it.takeLast(1)
                        }
                    },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.focusRequester(focusRequester)
                )
            }

            Text(
                text = stringResource(R.string.count_invoice, count.toString()),
                modifier = Modifier.padding(top = 8.dp)
            )

            Spacer(modifier = Modifier.height(24.dp))

            ResultDisplay(inputText, lotteryNumber)

            Spacer(modifier = Modifier.height(24.dp))

            PrizeList(lotteryNumber, inputText)
        }
    }
}

@Composable
fun DigitBox(char: String, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .size(60.dp)
            .border(2.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(8.dp))
            .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(8.dp))
            .clickable { onClick() }, // 讓方格可被點擊
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = char,
            style = MaterialTheme.typography.headlineLarge,
            color = if (char.isEmpty()) MaterialTheme.colorScheme.outlineVariant else MaterialTheme.colorScheme.onSurface
        )
    }
}

@Composable
fun ResultDisplay(input: String, invoice: InvoiceNumber) {
    val result = checkWinningResult(input, invoice)
    val color = when (result.state) {
        WinningState.BINGO -> Color(0xFFE64A19) // orange_700
        else -> MaterialTheme.colorScheme.onSurface
    }

    val text = when (result.state) {
        WinningState.INITIAL -> stringResource(R.string.hint_enter_numbers)
        WinningState.MAYBE -> ""
        WinningState.NONE -> stringResource(R.string.hint_sorry_try_again)
        WinningState.BINGO -> {
            if (result.winningPrizes.any { it is PrizeType.SpecialistPrize || it is PrizeType.SpecialPrize }) {
                stringResource(R.string.hint_check_numbers)
            } else {
                stringResource(R.string.congrats)
            }
        }
    }

    Text(
        text = text,
        style = MaterialTheme.typography.headlineSmall,
        color = color,
        textAlign = TextAlign.Center
    )
}

@Composable
fun PrizeList(invoice: InvoiceNumber, input: String) {
    val result = checkWinningResult(input, invoice)

    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        if (result.state == WinningState.INITIAL || result.state == WinningState.NONE) {
            PrizeRow(stringResource(R.string.special_prize), invoice.specialistPrize, input)
            PrizeRow(stringResource(R.string.grand_prize), invoice.specialPrize, input)
            invoice.firstPrize.forEach { PrizeRow(stringResource(R.string.first_prize), it, input) }
        } else {
            // 只顯示匹配的獎項
            if (invoice.specialistPrize.takeLast(3).startsWith(input)) {
                PrizeRow(stringResource(R.string.special_prize), invoice.specialistPrize, input)
            }
            if (invoice.specialPrize.takeLast(3).startsWith(input)) {
                PrizeRow(stringResource(R.string.grand_prize), invoice.specialPrize, input)
            }
            invoice.firstPrize.forEach {
                if (it.takeLast(3).startsWith(input)) {
                    PrizeRow(stringResource(R.string.first_prize), it, input)
                }
            }
        }
    }
}

@Composable
fun PrizeRow(label: String, number: String, input: String) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.padding(16.dp).fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(label, fontWeight = FontWeight.Bold)
            Text(
                text = buildAnnotatedString {
                    val lastThree = number.takeLast(3)
                    if (input.isNotEmpty() && lastThree.startsWith(input)) {
                        append(number.dropLast(3))
                        withStyle(style = SpanStyle(color = Color.Red)) {
                            append(lastThree.take(input.length))
                        }
                        append(lastThree.drop(input.length))
                    } else {
                        append(number)
                    }
                },
                style = MaterialTheme.typography.titleLarge
            )
        }
    }
}

fun checkWinningResult(input: String, invoice: InvoiceNumber): WinningCheckResult {
    if (input.isEmpty()) return WinningCheckResult(WinningState.INITIAL)
    val matchedPrizes = mutableListOf<PrizeType>()
    invoice.firstPrize.forEachIndexed { index, number ->
        if (number.takeLast(3).startsWith(input)) {
            if (input.length == 3) matchedPrizes.add(PrizeType.FirstPrize(index))
        }
    }
    if (invoice.specialistPrize.takeLast(3).startsWith(input)) {
        if (input.length == 3) matchedPrizes.add(PrizeType.SpecialistPrize)
    }
    if (invoice.specialPrize.takeLast(3).startsWith(input)) {
        if (input.length == 3) matchedPrizes.add(PrizeType.SpecialPrize)
    }
    return when {
        matchedPrizes.isNotEmpty() -> WinningCheckResult(WinningState.BINGO, matchedPrizes)
        invoice.firstPrize.any { it.takeLast(3).startsWith(input) } ||
            invoice.specialistPrize.takeLast(3).startsWith(input) ||
            invoice.specialPrize.takeLast(3).startsWith(input) -> WinningCheckResult(WinningState.MAYBE)
        else -> WinningCheckResult(WinningState.NONE)
    }
}
