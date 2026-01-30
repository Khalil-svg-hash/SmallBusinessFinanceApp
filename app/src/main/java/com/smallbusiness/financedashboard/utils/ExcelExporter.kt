package com.smallbusiness.financedashboard.utils

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.core.content.FileProvider
import com.smallbusiness.financedashboard.data.Transaction
import com.smallbusiness.financedashboard.data.TransactionType
import org.apache.poi.ss.usermodel.*
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.*

object ExcelExporter {
    
    fun exportToExcel(
        context: Context,
        transactions: List<Transaction>,
        totalIncome: Double,
        totalExpense: Double
    ): File? {
        try {
            val workbook = XSSFWorkbook()
            
            createSummarySheet(workbook, totalIncome, totalExpense, transactions)
            createTransactionsSheet(workbook, transactions)
            createCategorySheet(workbook, "Income Details",
                transactions.filter { it.type == TransactionType.INCOME })
            createCategorySheet(workbook, "Expense Details",
                transactions.filter { it.type == TransactionType.EXPENSE })
            
            val dateFormat = SimpleDateFormat("yyyy-MM-dd_HH-mm", Locale.getDefault())
            val fileName = "BusinessReport_${dateFormat.format(Date())}.xlsx"
            val file = File(context.getExternalFilesDir(null), fileName)
            
            FileOutputStream(file).use { outputStream ->
                workbook.write(outputStream)
            }
            
            workbook.close()
            return file
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }
    }
    
    private fun createSummarySheet(
        workbook: XSSFWorkbook,
        totalIncome: Double,
        totalExpense: Double,
        transactions: List<Transaction>
    ) {
        val sheet = workbook.createSheet("Dashboard Summary")
        
        val headerStyle = workbook.createCellStyle().apply {
            fillForegroundColor = IndexedColors.DARK_BLUE.index
            fillPattern = FillPatternType.SOLID_FOREGROUND
            val font = workbook.createFont()
            font.color = IndexedColors.WHITE.index
            font.bold = true
            setFont(font)
        }
        
        val profitStyle = workbook.createCellStyle().apply {
            val font = workbook.createFont()
            font.color = IndexedColors.GREEN.index
            font.bold = true
            setFont(font)
        }
        
        val lossStyle = workbook.createCellStyle().apply {
            val font = workbook.createFont()
            font.color = IndexedColors.RED.index
            font.bold = true
            setFont(font)
        }
        
        var rowNum = 0
        
        val titleRow = sheet.createRow(rowNum++)
        titleRow.createCell(0).apply {
            setCellValue("BUSINESS FINANCIAL SUMMARY")
            cellStyle = headerStyle
        }
        
        rowNum++
        
        val metrics = listOf(
            "Total Income" to totalIncome,
            "Total Expenses" to totalExpense,
            "Net Profit/Loss" to (totalIncome - totalExpense),
            "Profit Margin (%)" to if (totalIncome > 0) ((totalIncome - totalExpense) / totalIncome * 100) else 0.0,
            "Total Transactions" to transactions.size.toDouble()
        )
        
        metrics.forEach { (label, value) ->
            val row = sheet.createRow(rowNum++)
            row.createCell(0).setCellValue(label)
            val valueCell = row.createCell(1)
            if (label == "Net Profit/Loss") {
                valueCell.setCellValue(value)
                valueCell.cellStyle = if (value >= 0) profitStyle else lossStyle
            } else {
                valueCell.setCellValue(value)
            }
        }
        
        rowNum++
        sheet.createRow(rowNum++).createCell(0).apply {
            setCellValue("EXPENSE BREAKDOWN BY CATEGORY")
            cellStyle = headerStyle
        }
        
        val expenseByCategory = transactions
            .filter { it.type == TransactionType.EXPENSE }
            .groupBy { it.category }
            .mapValues { it.value.sumOf { t -> t.amount } }
            .toList()
            .sortedByDescending { it.second }
        
        expenseByCategory.forEach { (category, amount) ->
            val row = sheet.createRow(rowNum++)
            row.createCell(0).setCellValue(category)
            row.createCell(1).setCellValue(amount)
            row.createCell(2).setCellValue("${String.format("%.1f", amount / totalExpense * 100)}%")
        }
        
        sheet.setColumnWidth(0, 6000)
        sheet.setColumnWidth(1, 4000)
        sheet.setColumnWidth(2, 3000)
    }
    
    private fun createTransactionsSheet(workbook: XSSFWorkbook, transactions: List<Transaction>) {
        val sheet = workbook.createSheet("All Transactions")
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        
        val headerStyle = workbook.createCellStyle().apply {
            fillForegroundColor = IndexedColors.GREY_25_PERCENT.index
            fillPattern = FillPatternType.SOLID_FOREGROUND
            val font = workbook.createFont()
            font.bold = true
            setFont(font)
        }
        
        val headers = listOf("Date", "Title", "Category", "Type", "Amount", "Notes")
        val headerRow = sheet.createRow(0)
        headers.forEachIndexed { index, header ->
            headerRow.createCell(index).apply {
                setCellValue(header)
                cellStyle = headerStyle
            }
        }
        
        transactions.forEachIndexed { index, transaction ->
            val row = sheet.createRow(index + 1)
            row.createCell(0).setCellValue(dateFormat.format(Date(transaction.date)))
            row.createCell(1).setCellValue(transaction.title)
            row.createCell(2).setCellValue(transaction.category)
            row.createCell(3).setCellValue(transaction.type.name)
            row.createCell(4).setCellValue(
                if (transaction.type == TransactionType.EXPENSE) -transaction.amount
                else transaction.amount
            )
            row.createCell(5).setCellValue(transaction.notes)
        }
        
        for (i in 0..5) {
            sheet.setColumnWidth(i, 4000)
        }
    }
    
    private fun createCategorySheet(
        workbook: XSSFWorkbook,
        sheetName: String,
        transactions: List<Transaction>
    ) {
        val sheet = workbook.createSheet(sheetName)
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        
        val headerStyle = workbook.createCellStyle().apply {
            val font = workbook.createFont()
            font.bold = true
            setFont(font)
        }
        
        val headers = listOf("Date", "Title", "Category", "Amount", "Notes")
        val headerRow = sheet.createRow(0)
        headers.forEachIndexed { index, header ->
            headerRow.createCell(index).apply {
                setCellValue(header)
                cellStyle = headerStyle
            }
        }
        
        transactions.forEachIndexed { index, transaction ->
            val row = sheet.createRow(index + 1)
            row.createCell(0).setCellValue(dateFormat.format(Date(transaction.date)))
            row.createCell(1).setCellValue(transaction.title)
            row.createCell(2).setCellValue(transaction.category)
            row.createCell(3).setCellValue(transaction.amount)
            row.createCell(4).setCellValue(transaction.notes)
        }
        
        for (i in 0..4) {
            sheet.setColumnWidth(i, 4000)
        }
    }
    
    fun shareExcelFile(context: Context, file: File) {
        val uri: Uri = FileProvider.getUriForFile(
            context,
            "${context.packageName}.provider",
            file
        )
        
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"
            putExtra(Intent.EXTRA_STREAM, uri)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        
        context.startActivity(Intent.createChooser(intent, "Share Excel Report"))
    }
}