package com.example.appbluetooth

import android.bluetooth.BluetoothDevice
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class BluetoothViewModel(private val repository: BluetoothRepository) : ViewModel() {
    private val _pairedDevices = MutableLiveData<List<BluetoothDevice>>()
    val pairedDevices: LiveData<List<BluetoothDevice>> = _pairedDevices

    private val _isConnected = MutableLiveData<Boolean>()
    val isConnected: LiveData<Boolean> = _isConnected

    private val _message = MutableLiveData<String>()
    val message: LiveData<String> = _message

    fun enableBluetooth() {

        viewModelScope.launch {
            try {
                repository.enableBluetooth()
                _message.value = "Bluetooth activado"
            } catch (e: Exception) {
                _message.value = "Error al activar Bluetooth: ${e.message}"
            }
        }
    }

    fun disableBluetooth() {
        viewModelScope.launch {
            try {
                repository.disableBluetooth()
                _message.value = "Bluetooth desactivado"
            } catch (e: Exception) {
                _message.value = "Error al desactivar Bluetooth: ${e.message}"
            }
        }
    }

    fun getPairedDevices() {
        viewModelScope.launch {
            try {
                _pairedDevices.value = repository.getPairedDevices()
            } catch (e: Exception) {
                _message.value = "Error al obtener dispositivos emparejados: ${e.message}"
                println("error  para emparajar $e")
            }
        }
    }

    fun connectToDevice(address: String) {
        viewModelScope.launch {
            try {
                val result = repository.connectToDevice(address)
                _isConnected.value = result
                _message.value = if (result) "Conexión exitosa" else "Error de conexión"
            } catch (e: Exception) {
                _message.value = "Error al conectar: ${e.message}"
            }
        }
    }

    fun sendCommand(input: String) {
        viewModelScope.launch {
            try {
                repository.sendCommand(input)
                //_message.value = "Comando enviado"
            } catch (e: Exception) {
              //  _message.value = "Error al enviar comando: ${e.message}"
            }
        }
    }

    fun isBluetoothEnabled() = repository.isBluetoothEnabled()
}