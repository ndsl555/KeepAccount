package com.example.keepaccount.DataSource

import com.example.keepaccount.Entity.InvoiceNumber
import com.example.keepaccount.NetWork.LotteryService
import com.example.keepaccount.Utils.Result
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import org.jsoup.Jsoup

class LotteryDataRemoteSource(
    private val lotteryService: LotteryService,
    private val ioDispatcher: CoroutineDispatcher,
) : ILotteryRemoteDataSource {
    override suspend fun getLotteryNumber(): Result<InvoiceNumber> =
        withContext(ioDispatcher) {
            try {
                val response = lotteryService.getLotteryNumber()
                if (response.isSuccessful) {
                    val body = response.body()?.string()
                    if (body != null) {
                        val doc = Jsoup.parse(body)
                        // The class for bolded numbers has changed from "font-weight-bold" to "fw-bold".
                        // Select all elements with the "fw-bold" class and map them to their text content.
                        val numbers = doc.getElementsByClass("fw-bold").map { it.text() }

                        // There should be at least 8 parts to form the numbers.
                        // 1 for special, 1 for specialist, and 3x2 for the first prizes.

                        print(numbers)
                        if (numbers.size >= 8) {
                            val invoiceNumber =
                                InvoiceNumber(
                                    specialistPrize = numbers[0],
                                    specialPrize = numbers[1],
                                    firstPrize =
                                        listOf(
                                            numbers[2] + numbers[3], // Combine the two parts of the first prize number
                                            numbers[4] + numbers[5],
                                            numbers[6] + numbers[7],
                                        ),
                                )
                            Result.Success(invoiceNumber)
                        } else {
                            // If parsing fails, return a specific error.
                            Result.Error(
                                Exception("Error parsing lottery numbers: Not enough numbers found in HTML. Found: ${numbers.size}"),
                            )
                        }
                    } else {
                        Result.Error(Exception("Empty response body"))
                    }
                } else {
                    Result.Error(Exception("Unsuccessful response: ${response.code()}"))
                }
            } catch (e: Exception) {
                // Catch any other exceptions during the process.
                Result.Error(e)
            }
        }
}
