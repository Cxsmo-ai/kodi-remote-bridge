package com.kodiremote.bridge.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.kodiremote.bridge.data.KodiConnection
import com.kodiremote.bridge.databinding.DialogConnectionBinding
import com.kodiremote.bridge.viewmodel.MainViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch

/**
 * Dialog for managing Kodi connections
 */
class ConnectionDialog : DialogFragment() {
    
    private var _binding: DialogConnectionBinding? = null
    private val binding get() = _binding!!
    
    private lateinit var viewModel: MainViewModel
    private val savedConnections = mutableListOf<KodiConnection>()
    
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        _binding = DialogConnectionBinding.inflate(LayoutInflater.from(context))
        viewModel = ViewModelProvider(requireActivity())[MainViewModel::class.java]
        
        setupUI()
        loadSavedConnections()
        
        return MaterialAlertDialogBuilder(requireContext())
            .setTitle("Connect to Kodi")
            .setView(binding.root)
            .setPositiveButton("Connect") { _, _ ->
                connectToKodi()
            }
            .setNegativeButton("Cancel", null)
            .create()
    }
    
    private fun setupUI() {
        // Saved connections spinner
        val adapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_item,
            savedConnections.map { it.name }
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerSavedConnections.adapter = adapter
        
        // Add new connection button
        binding.buttonAddConnection.setOnClickListener {
            showAddConnectionDialog()
        }
        
        // Edit connection button
        binding.buttonEditConnection.setOnClickListener {
            showEditConnectionDialog()
        }
        
        // Delete connection button
        binding.buttonDeleteConnection.setOnClickListener {
            deleteConnection()
        }
        
        // Test connection button
        binding.buttonTestConnection.setOnClickListener {
            testConnection()
        }
    }
    
    private fun loadSavedConnections() {
        // Load saved connections from preferences
        // This would be implemented with SharedPreferences or DataStore
        // For now, add a sample connection
        savedConnections.add(
            KodiConnection.createDefault(
                "Living Room Kodi",
                "192.168.1.100"
            )
        )
        
        val adapter = binding.spinnerSavedConnections.adapter as ArrayAdapter<String>
        adapter.clear()
        adapter.addAll(savedConnections.map { it.name })
        adapter.notifyDataSetChanged()
    }
    
    private fun connectToKodi() {
        val selectedPosition = binding.spinnerSavedConnections.selectedItemPosition
        if (selectedPosition >= 0 && selectedPosition < savedConnections.size) {
            val connection = savedConnections[selectedPosition]
            viewModel.connect(connection)
            dismiss()
        }
    }
    
    private fun showAddConnectionDialog() {
        // For now, just show a simple alert dialog
        // In a full implementation, this would show the add connection form
        val dialog = MaterialAlertDialogBuilder(requireContext())
            .setTitle("Add New Connection")
            .setMessage("Connection management will be implemented in a future update.")
            .setPositiveButton("OK", null)
            .create()
        dialog.show()
    }
    
    private fun showEditConnectionDialog() {
        val selectedPosition = binding.spinnerSavedConnections.selectedItemPosition
        if (selectedPosition >= 0 && selectedPosition < savedConnections.size) {
            val connection = savedConnections[selectedPosition]
            // Show edit dialog with connection details
        }
    }
    
    private fun deleteConnection() {
        val selectedPosition = binding.spinnerSavedConnections.selectedItemPosition
        if (selectedPosition >= 0 && selectedPosition < savedConnections.size) {
            savedConnections.removeAt(selectedPosition)
            val adapter = binding.spinnerSavedConnections.adapter as ArrayAdapter<String>
            adapter.removeAt(selectedPosition)
        }
    }
    
    private fun testConnection() {
        val selectedPosition = binding.spinnerSavedConnections.selectedItemPosition
        if (selectedPosition >= 0 && selectedPosition < savedConnections.size) {
            val connection = savedConnections[selectedPosition]
            lifecycleScope.launch {
                try {
                    val success = viewModel.kodiApi.jsonRpcClient.connect(connection.host, connection.port)
                    if (success) {
                        viewModel.kodiApi.jsonRpcClient.disconnect()
                        Toast.makeText(
                            requireContext(),
                            "Connection successful!",
                            Toast.LENGTH_SHORT
                        ).show()
                    } else {
                        Toast.makeText(
                            requireContext(),
                            "Connection failed",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                } catch (e: Exception) {
                    Toast.makeText(
                        requireContext(),
                        "Connection error: ${e.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }
    
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
