package com.madhumarga.app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.madhumarga.app.data.AppDatabase

class AppViewModelFactory(private val database: AppDatabase) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AppViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return AppViewModel(
                database.userDao(),
                database.hiveDao(),
                database.inspectionDao(),
                database.harvestDao()
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
