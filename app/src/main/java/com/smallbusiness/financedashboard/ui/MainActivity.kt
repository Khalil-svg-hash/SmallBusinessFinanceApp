package com.smallbusiness.financedashboard.ui

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.github.mikephil.charting.data.*
import com.github.mikephil.charting.utils.ColorTemplate
import com.smallbusiness.financedashboard.R
import com.smallbusiness.financedashboard.data.Transaction
import com.smallbusiness.financedashboard.data.TransactionType
import com.smallbusiness.financedashboard.databinding.ActivityMainBinding
import com.smallbusiness.financedashboard.utils.ExcelExporter
import com.smallbusiness.financedashboard.viewmodel.MainViewModel
import kotlinx.coroutines.launch
import java.text.NumberFormat
import java.util.*

class MainActivity : AppCompatActivity() {
    
    private lateinit var binding: ActivityMainBinding
    private val viewModel: MainViewModel by viewModels()
    private lateinit var transactionAdapter: TransactionAdapter
    private val currencyFormat = NumberFormat.getCurrencyInstance(Locale.US)
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        setSupportActionBar(binding.toolbar)
        supportActionBar?.title = "Business Dashboard"
        
        setupRecyclerView()
        setupObservers()
        setupClickListeners()
    }
    
    private fun setupRecyclerView() {
        transactionAdapter = TransactionAdapter(
            onDeleteClick = { transaction ->
                viewModel.delete(transaction)
                Toast.makeText(this, "Transaction deleted", Toast.LENGTH_SHORT).show()
            }
        )
        
        binding.recyclerViewTransactions.apply {
            layoutManager = LinearLayoutManager(this@MainActivity)
            adapter = transactionAdapter
        }
    }
    
    private fun setupObservers() {
        viewModel.totalIncome.observe(this) { income ->
            val value = income ?: 0.0
            binding.tvTotalIncome.text = currencyFormat.format(value)
            updateChart()
        }
        
        viewModel.totalExpense.observe(this) { expense ->
            val value = expense ?: 0.0
            binding.tvTotalExpense.text = currencyFormat.format(value)
            updateChart()
        }
        
        viewModel.profitLoss.observe(this) { profit ->
            binding.tvNetProfit.text = currencyFormat.format(profit)
            binding.tvNetProfit.setTextColor(
                if (profit >= 0) getColor(R.color.profit_green)
                else getColor(R.color.loss_red)
            )
            binding.tvProfitLabel.text = if (profit >= 0) "Net Profit" else "Net Loss"
        }
        
        viewModel.allTransactions.observe(this) { transactions ->
            transactionAdapter.submitList(transactions.take(10))
            binding.tvTransactionCount.text = "Total: ${transactions.size} transactions"
        }
        
        viewModel.expensesByCategory.observe(this) { categories ->
            setupPieChart(categories.map { it.category to it.total })
        }
    }
    
    private fun updateChart() {
        val income = viewModel.totalIncome.value ?: 0.0
        val expense = viewModel.totalExpense.value ?: 0.0
        
        val entries = listOf(
            BarEntry(0f, income.toFloat()),
            BarEntry(1f, expense.toFloat())
        )
        
        val dataSet = BarDataSet(entries, "Income vs Expense").apply {
            colors = listOf(
                getColor(R.color.profit_green),
                getColor(R.color.loss_red)
            )
            valueTextSize = 12f
        }
        
        binding.barChart.apply {
            data = BarData(dataSet)
            description.isEnabled = false
            xAxis.setDrawLabels(true)
            xAxis.valueFormatter = object : com.github.mikephil.charting.formatter.ValueFormatter() {
                override fun getFormattedValue(value: Float): String {
                    return if (value == 0f) "Income" else "Expense"
                }
            }
            animateY(1000)
            invalidate()
        }
    }
    
    private fun setupPieChart(categories: List<Pair<String, Double>>) {
        if (categories.isEmpty()) return
        
        val entries = categories.map { PieEntry(it.second.toFloat(), it.first) }
        val dataSet = PieDataSet(entries, "Expenses").apply {
            colors = ColorTemplate.MATERIAL_COLORS.toList()
            valueTextSize = 10f
            sliceSpace = 2f
        }
        
        binding.pieChart.apply {
            data = PieData(dataSet)
            description.isEnabled = false
            isDrawHoleEnabled = true
            holeRadius = 40f
            centerText = "Expenses\nby Category"
            animateY(1000)
            invalidate()
        }
    }
    
    private fun setupClickListeners() {
        binding.fabAddTransaction.setOnClickListener {
            startActivity(Intent(this, AddTransactionActivity::class.java))
        }
        
        binding.btnAddIncome.setOnClickListener {
            startActivity(Intent(this, AddTransactionActivity::class.java).apply {
                putExtra("type", TransactionType.INCOME.name)
            })
        }
        
        binding.btnAddExpense.setOnClickListener {
            startActivity(Intent(this, AddTransactionActivity::class.java).apply {
                putExtra("type", TransactionType.EXPENSE.name)
            })
        }
        
        binding.btnExportExcel.setOnClickListener {
            exportToExcel()
        }
    }
    
    private fun exportToExcel() {
        lifecycleScope.launch {
            val transactions = viewModel.getAllTransactionsForExport()
            val income = viewModel.totalIncome.value ?: 0.0
            val expense = viewModel.totalExpense.value ?: 0.0
            
            val file = ExcelExporter.exportToExcel(
                this@MainActivity, transactions, income, expense
            )
            
            if (file != null) {
                Toast.makeText(this@MainActivity, "Excel exported: ${file.name}", Toast.LENGTH_LONG).show()
                ExcelExporter.shareExcelFile(this@MainActivity, file)
            } else {
                Toast.makeText(this@MainActivity, "Export failed", Toast.LENGTH_SHORT).show()
            }
        }
    }
    
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return true
    }
    
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_reports -> {
                startActivity(Intent(this, ReportsActivity::class.java))
                true
            }
            R.id.action_export -> {
                exportToExcel()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}