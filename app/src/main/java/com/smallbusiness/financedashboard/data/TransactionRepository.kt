package com.smallbusiness.financedashboard.data

import androidx.lifecycle.LiveData

class TransactionRepository(private val transactionDao: TransactionDao) {
    
    val allTransactions: LiveData<List<Transaction>> = transactionDao.getAllTransactions()
    val totalIncome: LiveData<Double?> = transactionDao.getTotalIncome()
    val totalExpense: LiveData<Double?> = transactionDao.getTotalExpense()
    val expensesByCategory: LiveData<List<CategoryTotal>> = transactionDao.getExpensesByCategory()
    val incomeByCategory: LiveData<List<CategoryTotal>> = transactionDao.getIncomeByCategory()
    
    suspend fun insert(transaction: Transaction) {
        transactionDao.insert(transaction)
    }
    
    suspend fun update(transaction: Transaction) {
        transactionDao.update(transaction)
    }
    
    suspend fun delete(transaction: Transaction) {
        transactionDao.delete(transaction)
    }
    
    suspend fun getAllTransactionsSync(): List<Transaction> {
        return transactionDao.getAllTransactionsSync()
    }
    
    fun getTransactionsByDateRange(startDate: Long, endDate: Long): LiveData<List<Transaction>> {
        return transactionDao.getTransactionsByDateRange(startDate, endDate)
    }
}