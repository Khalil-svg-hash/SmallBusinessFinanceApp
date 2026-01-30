package com.smallbusiness.financedashboard.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "transactions")
data class Transaction(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val amount: Double,
    val type: TransactionType,
    val category: String,
    val date: Long,
    val notes: String = ""
)

enum class TransactionType {
    INCOME, EXPENSE
}

object Categories {
    val incomeCategories = listOf(
        "Sales", "Services", "Consulting", "Investments",
        "Refunds", "Other Income"
    )
    
    val expenseCategories = listOf(
        "Inventory", "Salaries", "Rent", "Utilities",
        "Marketing", "Equipment", "Supplies", "Transport",
        "Insurance", "Taxes", "Maintenance", "Other Expense"
    )
}