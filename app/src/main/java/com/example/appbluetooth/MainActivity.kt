package com.example.appbluetooth

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.WindowManager
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.example.appbluetooth.databinding.ActivityMainBinding


class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var viewModel: BluetoothViewModel
    private var mAddressDevices: ArrayAdapter<String>? = null
    private var mNameDevices: ArrayAdapter<String>? = null

    private val bluetoothPermissions = arrayOf(
        Manifest.permission.BLUETOOTH,
        Manifest.permission.BLUETOOTH_ADMIN,
        Manifest.permission.BLUETOOTH_SCAN,
        Manifest.permission.BLUETOOTH_CONNECT,
        Manifest.permission.ACCESS_FINE_LOCATION
    )
    private val requestBluetoothPermissions = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Inicializar ViewModel
        val repository = BluetoothRepositoryImpl(applicationContext)
        viewModel = ViewModelProvider(this, BluetoothViewModelFactory(repository))
            .get(BluetoothViewModel::class.java)

        mAddressDevices = ArrayAdapter(this, android.R.layout.simple_list_item_1)
        mNameDevices = ArrayAdapter(this, android.R.layout.simple_list_item_1)

        setupObservers()
        setupClickListeners()
        checkBluetoothPermissions()
        cambiarColor()
    }

    private fun setupObservers() {
        viewModel.pairedDevices.observe(this) { devices ->
            mAddressDevices?.clear()
            mNameDevices?.clear()
            devices.forEach { device ->
                mAddressDevices?.add(device.address)
                if (ActivityCompat.checkSelfPermission(
                        this,
                        Manifest.permission.BLUETOOTH_CONNECT
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    return@observe
                }
                mNameDevices?.add(device.name)
            }
            binding.idSpinDisp.adapter = mNameDevices
        }

        viewModel.isConnected.observe(this) { isConnected ->
            if (isConnected) {
                Toast.makeText(this, "CONEXIÓN EXITOSA", Toast.LENGTH_LONG).show()
            } else {
                Toast.makeText(this, "ERROR DE CONEXIÓN", Toast.LENGTH_LONG).show()
            }
        }

        viewModel.message.observe(this) { message ->
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
        }
    }

    private fun setupClickListeners() {
        binding.idBtnOnBT.setOnClickListener {
            viewModel.enableBluetooth()
        }

        binding.idBtnOffBT.setOnClickListener {
            viewModel.disableBluetooth()
        }

        binding.idBtnDispBT.setOnClickListener {
            viewModel.getPairedDevices()
        }

        binding.idBtnConect.setOnClickListener {
            val selectedPosition = binding.idSpinDisp.selectedItemPosition
            val address = mAddressDevices?.getItem(selectedPosition)
            address?.let { viewModel.connectToDevice(it) }
        }

        binding.idBtnLuz1on.setOnClickListener { viewModel.sendCommand("A") }
        binding.idBtnLuz1off.setOnClickListener { viewModel.sendCommand("B") }
        binding.idBtnLuz2on.setOnClickListener { viewModel.sendCommand("C") }
        binding.idBtnLuz2off.setOnClickListener { viewModel.sendCommand("D") }

        binding.idBtnEnviar.setOnClickListener {
            val mensaje = binding.idTextOut.text.toString()
            if (mensaje.isNotEmpty()) {
                viewModel.sendCommand(mensaje)
            } else {
                Toast.makeText(this, "El mensaje no puede estar vacío", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun checkBluetoothPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val missingPermissions = bluetoothPermissions.filter {
                ContextCompat.checkSelfPermission(this, it) != PackageManager.PERMISSION_GRANTED
            }

            if (missingPermissions.isNotEmpty()) {
                ActivityCompat.requestPermissions(this, missingPermissions.toTypedArray(), requestBluetoothPermissions)
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == requestBluetoothPermissions) {
            if (grantResults.all { it == PackageManager.PERMISSION_GRANTED }) {
                // Permisos concedidos, puedes continuar con la funcionalidad de Bluetooth
            } else {
                Toast.makeText(this, "Permisos de Bluetooth denegados", Toast.LENGTH_SHORT).show()
            }
        }
    }
    private  fun cambiarColor(){

            val window=this.window
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            window.statusBarColor=resources.getColor(R.color.colorPantalla)

    }
}
