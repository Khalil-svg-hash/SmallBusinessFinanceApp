package com.smallbusiness.financedashboard.ui

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.github.mikephil.charting.data.*
import com.github.mikephil.charting.utils.ColorTemplate
import com.smallbusiness.financedashboard.databinding.ActivityReportsBinding
import com.smallbusiness.financedashboard.viewmodel.MainViewModel
import java.text.NumberFormat
import java.util.*

class ReportsActivity : AppCompatActivity() {
    
    private lateinit var binding: ActivityReportsBinding
    private val viewModel: MainViewModel by viewModels()
    private val currencyFormat = NumberFormat.getCurrencyInstance(Locale.US)
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityReportsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Financial Reports"
        
        setupObservers()
    }
    
    private fun setupObservers() {
        viewModel.totalIncome.observe(this) { income ->
            binding.tvReportIncome.text = "Total Income: ${currencyFormat.format(income ?: 0.0)}"
        }
        
        viewModel.totalExpense.observe(this) { expense ->
            binding.tvReportExpense.text = "Total Expense: ${currencyFormat.format(expense ?: 0.0)}"
        }
        
        viewModel.profitLoss.observe(this) { profit ->
            binding.tvReportProfit.text = "Net Profit/Loss: ${currencyFormat.format(profit)}"
        }
        
        viewModel.incomeByCategory.observe(this) { categories ->
            setupIncomePieChart(categories.map { it.category to it.total })
        }
        
        viewModel.expensesByCategory.observe(this) { categories ->
            setupExpensePieChart(categories.map { it.category to it.total })
        }
    }
    
    private fun setupIncomePieChart(categories: List<Pair<String, Double>>) {
        if (categories.isEmpty()) return
        
        val entries = categories.map { PieEntry(it.second.toFloat(), it.first) }
        val dataSet = PieDataSet(entries, "Income Sources").apply {
            colors = ColorTemplate.COLORFUL_COLORS.toList()
            valueTextSize = 11f
        }
        
        binding.pieChartIncome.apply {
            data = PieData(dataSet)
            description.text = "Income by Category"
            centerText = "Income"
            animateY(1000)
            invalidate()
        }
    }
    
    private fun setupExpensePieChart(categories: List<Pair<String, Double>>) {
        if (categories.isEmpty()) return
        
        val entries = categories.map { PieEntry(it.second.toFloat(), it.first) }
        val dataSet = PieDataSet(entries, "Expense Categories").apply {
            colors = ColorTemplate.MATERIAL_COLORS.toList()
            valueTextSize = 11f
        }
        
        binding.pieChartExpense.apply {
            data = PieData(dataSet)
            description.text = "Expenses by Category"
            centerText = "Expenses"
            animateY(1000)
            invalidate()
        }
    }
    
    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }
}