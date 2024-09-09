package com.example.appbluetooth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class BluetoothViewModelFactory(private val repository: BluetoothRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(BluetoothViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return BluetoothViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}