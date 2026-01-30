package com.smallbusiness.financedashboard.viewmodel

import android.app.Application
import androidx.lifecycle.*
import com.smallbusiness.financedashboard.data.*
import kotlinx.coroutines.launch

class MainViewModel(application: Application) : AndroidViewModel(application) {
    
    private val repository: TransactionRepository
    val allTransactions: LiveData<List<Transaction>>
    val totalIncome: LiveData<Double?>
    val totalExpense: LiveData<Double?>
    val expensesByCategory: LiveData<List<CategoryTotal>>
    val incomeByCategory: LiveData<List<CategoryTotal>>
    
    val profitLoss: LiveData<Double> = MediatorLiveData<Double>().apply {
        var income = 0.0
        var expense = 0.0
        
        addSource(totalIncome) {
            income = it ?: 0.0
            value = income - expense
        }
        
        addSource(totalExpense) {
            expense = it ?: 0.0
            value = income - expense
        }
    }
    
    init {
        val database = AppDatabase.getDatabase(application)
        val transactionDao = database.transactionDao()
        repository = TransactionRepository(transactionDao)
        
        allTransactions = repository.allTransactions
        totalIncome = repository.totalIncome
        totalExpense = repository.totalExpense
        expensesByCategory = repository.expensesByCategory
        incomeByCategory = repository.incomeByCategory
    }
    
    fun insert(transaction: Transaction) = viewModelScope.launch {
        repository.insert(transaction)
    }
    
    fun update(transaction: Transaction) = viewModelScope.launch {
        repository.update(transaction)
    }
    
    fun delete(transaction: Transaction) = viewModelScope.launch {
        repository.delete(transaction)
    }
    
    suspend fun getAllTransactionsForExport(): List<Transaction> {
        return repository.getAllTransactionsSync()
    }
}