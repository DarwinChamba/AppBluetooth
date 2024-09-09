package com.example.appbluetooth

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothManager
import android.bluetooth.BluetoothSocket
import android.content.Context
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.IOException
import java.util.*

class BluetoothRepositoryImpl(private val context: Context) : BluetoothRepository {
    private val bluetoothAdapter: BluetoothAdapter by lazy {
        (context.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager).adapter
    }

    private var bluetoothSocket: BluetoothSocket? = null
    private val uuid: UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB")

    override suspend fun enableBluetooth() {
        withContext(Dispatchers.IO) {
            if (!bluetoothAdapter.isEnabled) {
                bluetoothAdapter.enable()
            }
        }
    }

    override suspend fun disableBluetooth() {
        withContext(Dispatchers.IO) {
            if (bluetoothAdapter.isEnabled) {
                bluetoothAdapter.disable()
            }
        }
    }

    override suspend fun getPairedDevices(): List<BluetoothDevice> = withContext(Dispatchers.IO) {
        bluetoothAdapter.bondedDevices.toList()
    }

    override suspend fun connectToDevice(address: String): Boolean = withContext(Dispatchers.IO) {
        try {
            val device = bluetoothAdapter.getRemoteDevice(address)
            bluetoothSocket = device.createRfcommSocketToServiceRecord(uuid)
            bluetoothSocket?.connect()
            true
        } catch (e: IOException) {
            false
        }
    }

    override suspend fun sendCommand(input: String) {
        withContext(Dispatchers.IO) {
            try {
                bluetoothSocket?.outputStream?.write(input.toByteArray())
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }

    override fun isBluetoothEnabled(): Boolean = bluetoothAdapter.isEnabled

    override fun isConnected(): Boolean = bluetoothSocket?.isConnected == true
}