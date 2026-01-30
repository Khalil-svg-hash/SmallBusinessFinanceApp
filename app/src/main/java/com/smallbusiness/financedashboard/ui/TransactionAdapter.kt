package com.smallbusiness.financedashboard.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.smallbusiness.financedashboard.R
import com.smallbusiness.financedashboard.data.Transaction
import com.smallbusiness.financedashboard.data.TransactionType
import com.smallbusiness.financedashboard.databinding.ItemTransactionBinding
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*

class TransactionAdapter(
    private val onDeleteClick: (Transaction) -> Unit
) : ListAdapter<Transaction, TransactionAdapter.TransactionViewHolder>(TransactionDiffCallback()) {
    
    private val currencyFormat = NumberFormat.getCurrencyInstance(Locale.US)
    private val dateFormat = SimpleDateFormat("MMM dd", Locale.getDefault())
    
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TransactionViewHolder {
        val binding = ItemTransactionBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return TransactionViewHolder(binding)
    }
    
    override fun onBindViewHolder(holder: TransactionViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
    
    inner class TransactionViewHolder(
        private val binding: ItemTransactionBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        
        fun bind(transaction: Transaction) {
            binding.apply {
                tvTitle.text = transaction.title
                tvCategory.text = transaction.category
                tvDate.text = dateFormat.format(Date(transaction.date))
                
                val isIncome = transaction.type == TransactionType.INCOME
                val amountText = if (isIncome)
                    "+${currencyFormat.format(transaction.amount)}"
                else
                    "-${currencyFormat.format(transaction.amount)}"
                
                tvAmount.text = amountText
                tvAmount.setTextColor(
                    ContextCompat.getColor(
                        itemView.context,
                        if (isIncome) R.color.profit_green else R.color.loss_red
                    )
                )
                
                viewTypeIndicator.setBackgroundColor(
                    ContextCompat.getColor(
                        itemView.context,
                        if (isIncome) R.color.profit_green else R.color.loss_red
                    )
                )
                
                btnDelete.setOnClickListener {
                    onDeleteClick(transaction)
                }
            }
        }
    }
}

class TransactionDiffCallback : DiffUtil.ItemCallback<Transaction>() {
    override fun areItemsTheSame(oldItem: Transaction, newItem: Transaction): Boolean {
        return oldItem.id == newItem.id
    }
    
    override fun areContentsTheSame(oldItem: Transaction, newItem: Transaction): Boolean {
        return oldItem == newItem
    }
}