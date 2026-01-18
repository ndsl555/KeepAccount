package com.example.keepaccount.UseCase

import android.content.ContentValues
import android.content.Context
import android.provider.MediaStore
import com.example.keepaccount.Entity.Item
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import java.text.SimpleDateFormat
import java.util.*

class ExportMonthlyConsumptionToExcelUseCase(private val context: Context) {
    /**
     * 匯出 Excel，回傳是否成功
     */
    operator fun invoke(items: List<Item>): Boolean {
        val workbook = XSSFWorkbook()
        val sheet = workbook.createSheet("當月花銷明細")

        // Header
        val headerRow = sheet.createRow(0)
        headerRow.createCell(0).setCellValue("日期")
        headerRow.createCell(1).setCellValue("品項")
        headerRow.createCell(2).setCellValue("價錢")

        // Data
        items.forEachIndexed { index, item ->
            val row = sheet.createRow(index + 1)
            row.createCell(0).setCellValue("${item.itemYear}/${item.itemMonth}/${item.itemDay}")
            row.createCell(1).setCellValue(item.itemName)
            row.createCell(2).setCellValue(item.itemPrice.toString())
        }

        val timeStamp = SimpleDateFormat("yyyyMM", Locale.getDefault()).format(Date())
        val fileName = "${timeStamp}花銷明細.xlsx"

        val contentValues =
            ContentValues().apply {
                put(MediaStore.MediaColumns.DISPLAY_NAME, fileName)
                put(MediaStore.MediaColumns.MIME_TYPE, "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")
                put(MediaStore.MediaColumns.RELATIVE_PATH, "Documents/KeepAccount")
            }

        return try {
            val resolver = context.contentResolver
            val uri =
                resolver.insert(MediaStore.Files.getContentUri("external"), contentValues)
                    ?: return false

            resolver.openOutputStream(uri).use { outputStream ->
                workbook.write(outputStream)
            }
            workbook.close()
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }
}
