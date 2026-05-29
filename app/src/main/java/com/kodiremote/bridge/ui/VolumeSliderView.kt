package com.kodiremote.bridge.ui

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import androidx.core.content.ContextCompat
import com.kodiremote.bridge.R
import kotlin.math.max
import kotlin.math.min

/**
 * Custom volume slider view
 */
class VolumeSliderView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {
    
    private val backgroundPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val progressPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val thumbPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    
    private var trackBounds: RectF? = null
    private var thumbBounds: RectF? = null
    
    private var volume = 100
    private var isDragging = false
    
    var onVolumeChanged: ((Int) -> Unit)? = null
    
    init {
        backgroundPaint.style = Paint.Style.FILL
        backgroundPaint.color = ContextCompat.getColor(context, R.color.volume_track_background)
        
        progressPaint.style = Paint.Style.FILL
        progressPaint.color = ContextCompat.getColor(context, R.color.volume_track_progress)
        
        thumbPaint.style = Paint.Style.FILL
        thumbPaint.color = ContextCompat.getColor(context, R.color.volume_thumb)
    }
    
    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        calculateBounds()
    }
    
    private fun calculateBounds() {
        val padding = 16f
        val trackHeight = 8f
        val thumbSize = 24f
        
        trackBounds = RectF(
            padding,
            height / 2f - trackHeight / 2,
            width - padding,
            height / 2f + trackHeight / 2
        )
        
        val progressWidth = (trackBounds?.width() ?: 0f) * (volume / 100f)
        thumbBounds = RectF(
            padding + progressWidth - thumbSize / 2,
            height / 2f - thumbSize / 2,
            padding + progressWidth + thumbSize / 2,
            height / 2f + thumbSize / 2
        )
    }
    
    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        
        // Draw background track
        trackBounds?.let {
            canvas.drawRoundRect(it, 4f, 4f, backgroundPaint)
        }
        
        // Draw progress track
        val progressWidth = (trackBounds?.width() ?: 0f) * (volume / 100f)
        if (progressWidth > 0) {
            val progressBounds = RectF(
                trackBounds?.left ?: 0f,
                trackBounds?.top ?: 0f,
                (trackBounds?.left ?: 0f) + progressWidth,
                trackBounds?.bottom ?: 0f
            )
            canvas.drawRoundRect(progressBounds, 4f, 4f, progressPaint)
        }
        
        // Draw thumb
        thumbBounds?.let {
            canvas.drawRoundRect(it, 12f, 12f, thumbPaint)
        }
    }
    
    override fun onTouchEvent(event: MotionEvent): Boolean {
        val x = event.x
        
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                thumbBounds?.let { bounds ->
                    if (x >= bounds.left - 20 && x <= bounds.right + 20) {
                        isDragging = true
                        updateVolume(x)
                        return true
                    }
                }
            }
            MotionEvent.ACTION_MOVE -> {
                if (isDragging) {
                    updateVolume(x)
                    return true
                }
            }
            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                isDragging = false
                return true
            }
        }
        
        return super.onTouchEvent(event)
    }
    
    private fun updateVolume(x: Float) {
        val trackWidth = trackBounds?.width() ?: return
        val startX = trackBounds?.left ?: return
        
        val relativeX = x - startX
        val newVolume = ((relativeX / trackWidth) * 100).toInt()
        
        volume = max(0, min(100, newVolume))
        onVolumeChanged?.invoke(volume)
        
        calculateBounds()
        invalidate()
    }
    
    fun setVolume(volume: Int) {
        this.volume = max(0, min(100, volume))
        calculateBounds()
        invalidate()
    }
    
    fun getVolume(): Int = volume
}
