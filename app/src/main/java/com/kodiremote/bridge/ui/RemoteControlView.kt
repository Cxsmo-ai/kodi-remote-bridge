package com.kodiremote.bridge.ui

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import androidx.core.content.ContextCompat
import com.kodiremote.bridge.R

/**
 * Custom D-pad remote control view with gesture support
 */
class RemoteControlView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {
    
    private val paint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val path = Path()
    
    private var centerButtonBounds: android.graphics.RectF? = null
    private var upButtonBounds: android.graphics.RectF? = null
    private var downButtonBounds: android.graphics.RectF? = null
    private var leftButtonBounds: android.graphics.RectF? = null
    private var rightButtonBounds: android.graphics.RectF? = null
    
    var onDirectionPressed: ((Direction) -> Unit)? = null
    var onCenterPressed: (() -> Unit)? = null
    var onCenterLongPressed: (() -> Unit)? = null
    
    private var isCenterPressed = false
    private var currentDirection: Direction? = null
    
    enum class Direction {
        UP, DOWN, LEFT, RIGHT
    }
    
    init {
        paint.style = Paint.Style.FILL
        paint.color = ContextCompat.getColor(context, R.color.dpad_button)
    }
    
    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        calculateButtonBounds()
    }
    
    private fun calculateButtonBounds() {
        val centerX = width / 2f
        val centerY = height / 2f
        val buttonSize = width / 5f
        
        centerButtonBounds = android.graphics.RectF(
            centerX - buttonSize / 2,
            centerY - buttonSize / 2,
            centerX + buttonSize / 2,
            centerY + buttonSize / 2
        )
        
        upButtonBounds = android.graphics.RectF(
            centerX - buttonSize / 2,
            centerY - buttonSize * 1.5f,
            centerX + buttonSize / 2,
            centerY - buttonSize / 2
        )
        
        downButtonBounds = android.graphics.RectF(
            centerX - buttonSize / 2,
            centerY + buttonSize / 2,
            centerX + buttonSize / 2,
            centerY + buttonSize * 1.5f
        )
        
        leftButtonBounds = android.graphics.RectF(
            centerX - buttonSize * 1.5f,
            centerY - buttonSize / 2,
            centerX - buttonSize / 2,
            centerY + buttonSize / 2
        )
        
        rightButtonBounds = android.graphics.RectF(
            centerX + buttonSize / 2,
            centerY - buttonSize / 2,
            centerX + buttonSize * 1.5f,
            centerY + buttonSize / 2
        )
    }
    
    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        
        // Draw D-pad buttons
        upButtonBounds?.let { drawButton(canvas, it, currentDirection == Direction.UP) }
        downButtonBounds?.let { drawButton(canvas, it, currentDirection == Direction.DOWN) }
        leftButtonBounds?.let { drawButton(canvas, it, currentDirection == Direction.LEFT) }
        rightButtonBounds?.let { drawButton(canvas, it, currentDirection == Direction.RIGHT) }
        centerButtonBounds?.let { drawButton(canvas, it, isCenterPressed) }
    }
    
    private fun drawButton(canvas: Canvas, bounds: android.graphics.RectF, isPressed: Boolean) {
        paint.color = if (isPressed) {
            ContextCompat.getColor(context, R.color.dpad_button_pressed)
        } else {
            ContextCompat.getColor(context, R.color.dpad_button)
        }
        
        val radius = bounds.width() / 2f
        canvas.drawRoundRect(bounds, radius, radius, paint)
    }
    
    override fun onTouchEvent(event: MotionEvent): Boolean {
        val x = event.x
        val y = event.y
        
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                when {
                    centerButtonBounds?.contains(x, y) == true -> {
                        isCenterPressed = true
                        invalidate()
                        return true
                    }
                    upButtonBounds?.contains(x, y) == true -> {
                        currentDirection = Direction.UP
                        onDirectionPressed?.invoke(Direction.UP)
                        invalidate()
                        return true
                    }
                    downButtonBounds?.contains(x, y) == true -> {
                        currentDirection = Direction.DOWN
                        onDirectionPressed?.invoke(Direction.DOWN)
                        invalidate()
                        return true
                    }
                    leftButtonBounds?.contains(x, y) == true -> {
                        currentDirection = Direction.LEFT
                        onDirectionPressed?.invoke(Direction.LEFT)
                        invalidate()
                        return true
                    }
                    rightButtonBounds?.contains(x, y) == true -> {
                        currentDirection = Direction.RIGHT
                        onDirectionPressed?.invoke(Direction.RIGHT)
                        invalidate()
                        return true
                    }
                }
            }
            MotionEvent.ACTION_UP -> {
                if (isCenterPressed) {
                    isCenterPressed = false
                    onCenterPressed?.invoke()
                    invalidate()
                    return true
                }
                currentDirection?.let {
                    currentDirection = null
                    invalidate()
                    return true
                }
            }
            MotionEvent.ACTION_CANCEL -> {
                isCenterPressed = false
                currentDirection = null
                invalidate()
                return true
            }
        }
        
        return super.onTouchEvent(event)
    }
    
    override fun performLongClick(): Boolean {
        if (isCenterPressed) {
            onCenterLongPressed?.invoke()
            return true
        }
        return super.performLongClick()
    }
}
