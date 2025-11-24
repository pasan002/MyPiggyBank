package com.example.mypiggybank.data.repository

import com.example.mypiggybank.data.Transaction
import com.example.mypiggybank.data.TransactionType
import com.example.mypiggybank.data.dao.TransactionDao
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton
import android.util.Log

private const val TAG = "TransactionRepository"

@Singleton
class TransactionRepository @Inject constructor(
    private val transactionDao: TransactionDao
) {
    private val gson = Gson()

    fun getTransactionsForMonth(month: Int, year: Int): Flow<List<Transaction>> {
        return transactionDao.getTransactionsForMonth(month, year)
    }

    fun getAllTransactions(): Flow<List<Transaction>> {
        return transactionDao.getAllTransactions()
            .catch { e ->
                Log.e(TAG, "Error getting transactions", e)
                emit(emptyList())
            }
            .map { transactions ->
                Log.d(TAG, "Retrieved ${transactions.size} transactions")
                transactions
            }
    }

    suspend fun insertTransaction(transaction: Transaction) {
        try {
            transactionDao.insertTransaction(transaction)
            Log.d(TAG, "Successfully inserted transaction: ${transaction.description}")
        } catch (e: Exception) {
            Log.e(TAG, "Error inserting transaction", e)
            throw e
        }
    }

    suspend fun updateTransaction(transaction: Transaction) {
        try {
            transactionDao.updateTransaction(transaction)
            Log.d(TAG, "Successfully updated transaction: ${transaction.description}")
        } catch (e: Exception) {
            Log.e(TAG, "Error updating transaction", e)
            throw e
        }
    }

    suspend fun deleteTransaction(transaction: Transaction) {
        try {
            transactionDao.deleteTransaction(transaction)
            Log.d(TAG, "Successfully deleted transaction: ${transaction.description}")
        } catch (e: Exception) {
            Log.e(TAG, "Error deleting transaction", e)
            throw e
        }
    }

    suspend fun deleteAllTransactions() {
        try {
            transactionDao.deleteAllTransactions()
            Log.d(TAG, "Successfully deleted all transactions")
        } catch (e: Exception) {
            Log.e(TAG, "Error deleting all transactions", e)
            throw e
        }
    }

    suspend fun insertTransactions(transactions: List<Transaction>) {
        try {
            transactions.forEach { transaction ->
                transactionDao.insertTransaction(transaction)
            }
            Log.d(TAG, "Successfully inserted ${transactions.size} transactions")
        } catch (e: Exception) {
            Log.e(TAG, "Error inserting transactions", e)
            throw e
        }
    }

    fun serializeTransactions(transactions: List<Transaction>): String {
        return gson.toJson(transactions)
    }

    fun deserializeTransactions(jsonString: String): List<Transaction> {
        val type = object : TypeToken<List<Transaction>>() {}.type
        return gson.fromJson(jsonString, type)
    }
} 