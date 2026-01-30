package com.smallbusiness.financedashboard.ui

import android.app.DatePickerDialog
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.smallbusiness.financedashboard.data.Categories
import com.smallbusiness.financedashboard.data.Transaction
import com.smallbusiness.financedashboard.data.TransactionType
import com.smallbusiness.financedashboard.databinding.ActivityAddTransactionBinding
import com.smallbusiness.financedashboard.viewmodel.MainViewModel
import java.text.SimpleDateFormat
import java.util.*

class AddTransactionActivity : AppCompatActivity() {
    
    private lateinit var binding: ActivityAddTransactionBinding
    private val viewModel: MainViewModel by viewModels()
    private var selectedDate: Long = System.currentTimeMillis()
    private var transactionType: TransactionType = TransactionType.EXPENSE
    private val dateFormat = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddTransactionBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Add Transaction"
        
        intent.getStringExtra("type")?.let {
            transactionType = TransactionType.valueOf(it)
        }
        
        setupUI()
        setupClickListeners()
    }
    
    private fun setupUI() {
        binding.tvSelectedDate.text = dateFormat.format(Date(selectedDate))
        
        binding.toggleType.check(
            if (transactionType == TransactionType.INCOME)
                binding.btnIncome.id
            else
                binding.btnExpense.id
        )
        
        updateCategorySpinner()
        
        binding.toggleType.addOnButtonCheckedListener { _, checkedId, isChecked ->
            if (isChecked) {
                transactionType = if (checkedId == binding.btnIncome.id)
                    TransactionType.INCOME
                else
                    TransactionType.EXPENSE
                updateCategorySpinner()
            }
        }
    }
    
    private fun updateCategorySpinner() {
        val categories = if (transactionType == TransactionType.INCOME)
            Categories.incomeCategories
        else
            Categories.expenseCategories
        
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, categories)
        binding.spinnerCategory.adapter = adapter
    }
    
    private fun setupClickListeners() {
        binding.btnSelectDate.setOnClickListener {
            showDatePicker()
        }
        
        binding.btnSave.setOnClickListener {
            saveTransaction()
        }
        
        binding.btnCancel.setOnClickListener {
            finish()
        }
    }
    
    private fun showDatePicker() {
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = selectedDate
        
        DatePickerDialog(
            this,
            { _, year, month, dayOfMonth ->
                calendar.set(year, month, dayOfMonth)
                selectedDate = calendar.timeInMillis
                binding.tvSelectedDate.text = dateFormat.format(Date(selectedDate))
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        ).show()
    }
    
    private fun saveTransaction() {
        val title = binding.etTitle.text.toString().trim()
        val amountText = binding.etAmount.text.toString().trim()
        val notes = binding.etNotes.text.toString().trim()
        val category = binding.spinnerCategory.selectedItem?.toString() ?: ""
        
        if (title.isEmpty()) {
            binding.etTitle.error = "Title is required"
            return
        }
        
        if (amountText.isEmpty()) {
            binding.etAmount.error = "Amount is required"
            return
        }
        
        val amount = amountText.toDoubleOrNull()
        if (amount == null || amount <= 0) {
            binding.etAmount.error = "Enter a valid amount"
            return
        }
        
        val transaction = Transaction(
            title = title,
            amount = amount,
            type = transactionType,
            category = category,
            date = selectedDate,
            notes = notes
        )
        
        viewModel.insert(transaction)
        Toast.makeText(this, "Transaction saved!", Toast.LENGTH_SHORT).show()
        finish()
    }
    
    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }
}