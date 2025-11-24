package com.example.mypiggybank.utils

import android.content.Context
import android.util.Log
import com.example.mypiggybank.data.Transaction
import com.example.mypiggybank.data.repository.TransactionRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BackupManager @Inject constructor(
    @ApplicationContext private val context: Context,
    private val transactionRepository: TransactionRepository
) {
    private var backupData: List<Transaction>? = null

    suspend fun createBackup(): Boolean {
        return try {
            Log.d("BackupManager", "Creating backup...")
            backupData = transactionRepository.getAllTransactions().first()
            Log.d("BackupManager", "Backup created successfully with ${backupData?.size} transactions")
            true
        } catch (e: Exception) {
            Log.e("BackupManager", "Error creating backup", e)
            false
        }
    }

    suspend fun restoreFromBackup(): Boolean {
        return try {
            Log.d("BackupManager", "Attempting to restore from backup...")
            val backup = backupData ?: run {
                Log.e("BackupManager", "No backup data available")
                return false
            }
            
            Log.d("BackupManager", "Deleting existing transactions...")
            transactionRepository.deleteAllTransactions()
            
            Log.d("BackupManager", "Restoring ${backup.size} transactions...")
            transactionRepository.insertTransactions(backup)
            
            Log.d("BackupManager", "Restore completed successfully")
            true
        } catch (e: Exception) {
            Log.e("BackupManager", "Error restoring from backup", e)
            false
        }
    }

    suspend fun createBackupBeforeDelete(transaction: Transaction) {
        withContext(Dispatchers.IO) {
            createBackup()
        }
    }
} 