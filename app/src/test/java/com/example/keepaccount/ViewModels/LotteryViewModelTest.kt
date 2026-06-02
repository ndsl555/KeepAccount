package com.example.keepaccount.ViewModels
import com.example.keepaccount.Entity.InvoiceNumber
import com.example.keepaccount.UseCase.LoadInvoiceUseCase
import com.example.keepaccount.UseCase.LotteryCheckUseCase
import com.example.keepaccount.UseCase.SaveInvoiceUseCase
import com.example.keepaccount.Utils.Result
import com.example.keepaccount.Utils.invoke
import com.example.keepaccount.ui.screens.QrWinningType
import io.mockk.*
import io.mockk.impl.annotations.MockK
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

@ExperimentalCoroutinesApi
class LotteryViewModelTest {
    private lateinit var viewModel: LotteryCheckViewModel

    @MockK private lateinit var lotteryCheckUseCase: LotteryCheckUseCase

    @MockK private lateinit var saveInvoiceUseCase: SaveInvoiceUseCase

    @MockK private lateinit var loadInvoiceUseCase: LoadInvoiceUseCase
    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        MockKAnnotations.init(this)
        // 加上這一行：模擬靜態的 Log 類別
        mockkStatic(android.util.Log::class)
        every { android.util.Log.e(any(), any()) } returns 0
        Dispatchers.setMain(testDispatcher)

        val fakeData =
            InvoiceNumber(
                topic = "11月",
                specialistPrize = "12345678",
                specialPrize = "23456789",
                firstPrize = listOf("11111111", "22222222", "33333333")
            )
        coEvery { lotteryCheckUseCase.invoke() } returns
            Result.Success(
                fakeData
            )
        coEvery { saveInvoiceUseCase.invoke(any()) } returns mockk()
        coEvery { loadInvoiceUseCase.invoke() } returns
            Result.Success(
                fakeData
            )

        viewModel = LotteryCheckViewModel(lotteryCheckUseCase, saveInvoiceUseCase, loadInvoiceUseCase)

        testDispatcher.scheduler.advanceUntilIdle()
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain() // 重置主執行緒，若不重置它，這個測試結束後，下一個測試類別可能會受到殘留設定的影響
        unmockkAll()
    }

    @Test
    fun checkWinningByQr() {
        val invoiceNumber = "12345678"
        val result = viewModel.checkWinningByQr(invoiceNumber)
        assertEquals(QrWinningType.SPECIALIST_PRIZE, result.type)

        val invoiceNumber2 = "23456789"
        val result2 = viewModel.checkWinningByQr(invoiceNumber2)
        assertEquals(QrWinningType.SPECIAL_PRIZE, result2.type)

        val invoiceNumber3 = "11111111"
        val result3 = viewModel.checkWinningByQr(invoiceNumber3)
        assertEquals(QrWinningType.FIRST_PRIZE, result3.type)

        val invoiceNumber4 = "66666666"
        val result4 = viewModel.checkWinningByQr(invoiceNumber4)
        assertEquals(QrWinningType.NONE, result4.type)
    }
}
