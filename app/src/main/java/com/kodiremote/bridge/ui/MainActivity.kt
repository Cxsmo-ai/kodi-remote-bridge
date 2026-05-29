package com.kodiremote.bridge.ui

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.kodiremote.bridge.R
import com.kodiremote.bridge.api.InputAction
import com.kodiremote.bridge.databinding.ActivityMainBinding
import com.kodiremote.bridge.viewmodel.MainViewModel
import kotlinx.coroutines.launch

/**
 * Main activity for Kodi Remote Bridge
 */
class MainActivity : AppCompatActivity() {
    
    private lateinit var binding: ActivityMainBinding
    private lateinit var viewModel: MainViewModel
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        viewModel = ViewModelProvider(this)[MainViewModel::class.java]
        
        setupUI()
        observeViewModel()
    }
    
    private fun setupUI() {
        // D-pad controls
        binding.remoteControlView.onDirectionPressed = { direction ->
            when (direction) {
                RemoteControlView.Direction.UP -> viewModel.sendInput(InputAction.UP)
                RemoteControlView.Direction.DOWN -> viewModel.sendInput(InputAction.DOWN)
                RemoteControlView.Direction.LEFT -> viewModel.sendInput(InputAction.LEFT)
                RemoteControlView.Direction.RIGHT -> viewModel.sendInput(InputAction.RIGHT)
            }
        }
        
        binding.remoteControlView.onCenterPressed = {
            viewModel.sendInput(InputAction.SELECT)
        }
        
        binding.remoteControlView.onCenterLongPressed = {
            viewModel.sendInput(InputAction.MENU)
        }
        
        // Playback controls
        binding.buttonPlayPause.setOnClickListener {
            viewModel.playPause()
        }
        
        binding.buttonStop.setOnClickListener {
            viewModel.stop()
        }
        
        binding.buttonRewind.setOnClickListener {
            viewModel.seek(com.kodiremote.bridge.api.SeekPosition.SMALL_BACKWARD)
        }
        
        binding.buttonForward.setOnClickListener {
            viewModel.seek(com.kodiremote.bridge.api.SeekPosition.SMALL_FORWARD)
        }
        
        // Volume controls
        binding.volumeSliderView.onVolumeChanged = { volume ->
            viewModel.setVolume(volume)
        }
        
        binding.buttonMute.setOnClickListener {
            viewModel.toggleMute()
        }
        
        // Navigation buttons
        binding.buttonBack.setOnClickListener {
            viewModel.sendInput(InputAction.BACK)
        }
        
        binding.buttonHome.setOnClickListener {
            viewModel.sendInput(InputAction.HOME)
        }
        
        binding.buttonInfo.setOnClickListener {
            viewModel.sendInput(InputAction.INFO)
        }
        
        binding.buttonContext.setOnClickListener {
            viewModel.sendInput(InputAction.MENU)
        }
        
        // System controls
        binding.buttonPower.setOnClickListener {
            viewModel.showPowerMenu()
        }
        
        // Connection status
        binding.buttonConnect.setOnClickListener {
            showConnectionDialog()
        }
        
        // Media library
        binding.buttonMovies.setOnClickListener {
            viewModel.browseMovies()
        }
        
        binding.buttonTvShows.setOnClickListener {
            viewModel.browseTvShows()
        }
    }
    
    private fun observeViewModel() {
        viewModel.connectionState.observe(this) { state ->
            updateConnectionUI(state)
        }
        
        viewModel.playerState.observe(this) { state ->
            updatePlayerUI(state)
        }
        
        viewModel.volume.observe(this) { volume ->
            binding.volumeSliderView.setVolume(volume)
        }
        
        viewModel.error.observe(this) { error ->
            error?.let {
                Toast.makeText(this, it, Toast.LENGTH_SHORT).show()
            }
        }
        
        viewModel.isLoading.observe(this) { loading ->
            binding.progressBar.visibility = if (loading) View.VISIBLE else View.GONE
        }
    }
    
    private fun updateConnectionUI(isConnected: Boolean) {
        if (isConnected) {
            binding.buttonConnect.text = getString(R.string.connected)
            binding.buttonConnect.setBackgroundColor(getColor(R.color.connected))
            binding.remoteControlView.isEnabled = true
            binding.playbackControls.isEnabled = true
        } else {
            binding.buttonConnect.text = getString(R.string.disconnected)
            binding.buttonConnect.setBackgroundColor(getColor(R.color.disconnected))
            binding.remoteControlView.isEnabled = false
            binding.playbackControls.isEnabled = false
        }
    }
    
    private fun updatePlayerUI(state: com.kodiremote.bridge.data.PlayerState?) {
        state?.let { playerState ->
            // Update play/pause button
            binding.buttonPlayPause.setImageResource(
                if (playerState.isPlaying) {
                    R.drawable.ic_pause
                } else {
                    R.drawable.ic_play
                }
            )
            
            // Update time display
            binding.textCurrentTime.text = playerState.formattedCurrentTime
            binding.textTotalTime.text = playerState.formattedTotalTime
            
            // Update progress bar
            binding.progressBarProgress.progress = playerState.progressPercentage
            
            // Update media info
            binding.textMediaTitle.text = playerState.currentMedia?.displayTitle ?: ""
            binding.textMediaSubtitle.text = playerState.currentMedia?.subtitle ?: ""
            
            // Update mute button
            binding.buttonMute.setImageResource(
                if (playerState.isMuted) {
                    R.drawable.ic_volume_off
                } else {
                    R.drawable.ic_volume_on
                }
            )
        }
    }
    
    private fun showConnectionDialog() {
        // Show connection dialog (to be implemented)
        ConnectionDialog().show(supportFragmentManager, "connection_dialog")
    }
    
    override fun onResume() {
        super.onResume()
        viewModel.refreshState()
    }
    
    override fun onPause() {
        super.onPause()
        // Keep connection alive in background
    }
}
