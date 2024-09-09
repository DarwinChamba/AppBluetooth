package com.example.appbluetooth

import android.bluetooth.BluetoothDevice

interface BluetoothRepository {
    suspend fun enableBluetooth()
    suspend fun disableBluetooth()
    suspend fun getPairedDevices(): List<BluetoothDevice>
    suspend fun connectToDevice(address: String): Boolean
    suspend fun sendCommand(input: String)
    fun isBluetoothEnabled(): Boolean
    fun isConnected(): Boolean
}