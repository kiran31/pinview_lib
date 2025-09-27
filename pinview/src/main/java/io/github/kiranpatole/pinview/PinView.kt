package io.github.kiranpatole.pinview

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.graphics.drawable.Drawable
import android.os.Handler
import android.os.Looper
import android.text.InputType
import android.util.AttributeSet
import android.util.TypedValue
import android.view.KeyEvent
import android.view.MotionEvent
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.inputmethod.BaseInputConnection
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputConnection
import android.view.inputmethod.InputMethodManager

class PinView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    // --- Configurable Attributes with defaults ---
    private var pinCount: Int = 4
    private var pinText: String = ""
    private var pinShape: Int = 0 // 0: square, 1: rounded_square, 2: circle
    private var boxStrokeColor: Int = Color.GRAY
    private var boxStrokeColorFocused: Int = Color.BLACK
    private var boxStrokeColorError: Int = Color.RED
    private var boxStrokeWidth: Float = 2f.dp
    private var boxBackgroundColorFilled: Int = Color.LTGRAY
    private var boxBackgroundColor: Int = Color.TRANSPARENT
    private var pinTextColor: Int = Color.BLACK
    private var pinTextSize: Float = 20f.sp
    private var boxSpacing: Float = 8f.dp
    private var boxCornerRadius: Float = 4f.dp
    private var boxBackgroundDrawable: Drawable? = null
    private var boxWidth: Float = 48f.dp
    private var boxHeight: Float = 48f.dp
    private var pinInputType: Int = 0 // 0: number, 1: text, 2: textCapCharacters
    private var isPassword: Boolean = false
    private var passwordCharacter: String = "●"
    private var showCursor: Boolean = false
    private var cursorColor: Int = Color.BLACK
    private var cursorWidth: Float = 2f.dp


    // --- Internal State ---
    private var currentPin = StringBuilder()
    private val textPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val boxPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val cursorPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val boxRect = RectF()
    private var onPinEnteredListener: ((String) -> Unit)? = null
    private var isErrorState = false
    private val errorHandler = Handler(Looper.getMainLooper())
    private var cursorVisible = false
    private var cursorAnimator: ValueAnimator? = null

    init {
        isFocusable = true
        isFocusableInTouchMode = true
        loadAttributes(attrs)
        setupPaints()
        setupCursorAnimator()
        setOnClickListener {
            requestFocus()
            showKeyboard()
        }
    }

    private fun loadAttributes(attrs: AttributeSet?) {
        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.PinView)
        try {
            pinCount = typedArray.getInt(R.styleable.PinView_pinCount, 4)
            pinText = typedArray.getString(R.styleable.PinView_pinText) ?: ""
            pinShape = typedArray.getInt(R.styleable.PinView_pinShape, 0)
            boxStrokeColor = typedArray.getColor(R.styleable.PinView_boxStrokeColor, Color.GRAY)
            boxStrokeColorFocused =
                typedArray.getColor(R.styleable.PinView_boxStrokeColorFocused, Color.BLACK)
            boxStrokeColorError =
                typedArray.getColor(R.styleable.PinView_boxStrokeColorError, Color.RED)
            boxStrokeWidth = typedArray.getDimension(R.styleable.PinView_boxStrokeWidth, 2f.dp)
            boxBackgroundColorFilled =
                typedArray.getColor(R.styleable.PinView_boxBackgroundColorFilled, Color.LTGRAY)
            boxBackgroundColor =
                typedArray.getColor(R.styleable.PinView_boxBackgroundColor, Color.TRANSPARENT)
            pinTextColor = typedArray.getColor(R.styleable.PinView_pinTextColor, Color.BLACK)
            pinTextSize = typedArray.getDimension(R.styleable.PinView_pinTextSize, 20f.sp)
            boxSpacing = typedArray.getDimension(R.styleable.PinView_boxSpacing, 8f.dp)
            boxCornerRadius = typedArray.getDimension(R.styleable.PinView_boxCornerRadius, 4f.dp)
            boxBackgroundDrawable =
                typedArray.getDrawable(R.styleable.PinView_boxBackgroundDrawable)
            boxWidth = typedArray.getDimension(R.styleable.PinView_boxWidth, 48f.dp)
            boxHeight = typedArray.getDimension(R.styleable.PinView_boxHeight, 48f.dp)
            pinInputType = typedArray.getInt(R.styleable.PinView_pinInputType, 0)
            isPassword = typedArray.getBoolean(R.styleable.PinView_isPassword, false)
            passwordCharacter = typedArray.getString(R.styleable.PinView_passwordCharacter) ?: "●"
            showCursor = typedArray.getBoolean(R.styleable.PinView_showCursor, false)
            cursorColor = typedArray.getColor(R.styleable.PinView_cursorColor, Color.BLACK)
            cursorWidth = typedArray.getDimension(R.styleable.PinView_cursorWidth, 2f.dp)
        } finally {
            typedArray.recycle()
        }

        if (pinText.isNotEmpty()) {
            setPinText(pinText)
        }
    }

    /**
     * Sets the text programmatically. Ideal for autofill functionality.
     */
    fun setPinText(text: String) {
        // 1. Update the internal state
        currentPin.clear()
        currentPin.append(text.take(pinCount))

        // 2. Redraw the view with the new state
        invalidate()

        // *** THIS IS THE CRITICAL FIX ***
        // 3. Force the InputMethodManager to restart the input. This creates a new,
        // fresh InputConnection that is in sync with the view's new state.
        // This solves the "stuck keyboard" and "input not working" issues after autofill.
        val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.restartInput(this)

        // 4. (Enhancement) Check if the new text completes the pin and notify the listener.
        if (currentPin.length == pinCount) {
            onPinEnteredListener?.invoke(currentPin.toString())
        }
    }

    fun getPin(): String = currentPin.toString()

    private fun setupPaints() {
        textPaint.apply {
            color = pinTextColor
            textSize = pinTextSize
            textAlign = Paint.Align.CENTER
        }
        boxPaint.apply {
            strokeWidth = boxStrokeWidth
            style = Paint.Style.STROKE
            color = boxStrokeColor
        }
        cursorPaint.apply {
            strokeWidth = cursorWidth
            style = Paint.Style.STROKE
            color = cursorColor
        }
    }

    private fun setupCursorAnimator() {
        if (showCursor) {
            cursorAnimator = ValueAnimator.ofInt(0, 1).apply {
                duration = 500
                repeatCount = ValueAnimator.INFINITE
                repeatMode = ValueAnimator.REVERSE
                interpolator = AccelerateDecelerateInterpolator()
                addUpdateListener {
                    cursorVisible = it.animatedValue == 1
                    invalidate()
                }
            }
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val totalBoxWidth = (pinCount * boxWidth)
        val totalSpacingWidth = ((pinCount - 1) * boxSpacing)
        val desiredWidth = (totalBoxWidth + totalSpacingWidth + paddingStart + paddingEnd).toInt()
        val desiredHeight = (boxHeight + paddingTop + paddingBottom).toInt()
        setMeasuredDimension(
            resolveSize(desiredWidth, widthMeasureSpec),
            resolveSize(desiredHeight, heightMeasureSpec)
        )
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        for (i in 0 until pinCount) {
            val startX = paddingStart + i * (boxWidth + boxSpacing)
            val endX = startX + boxWidth
            boxRect.set(startX, paddingTop.toFloat(), endX, paddingTop + boxHeight)
            drawBox(canvas, i)
            drawText(canvas, i)
        }
        drawCursor(canvas)
    }

    private fun drawBox(canvas: Canvas, index: Int) {
        if (boxBackgroundDrawable != null) {
            val drawable = boxBackgroundDrawable!!.constantState!!.newDrawable().mutate()
            drawable.setBounds(
                boxRect.left.toInt(),
                boxRect.top.toInt(),
                boxRect.right.toInt(),
                boxRect.bottom.toInt()
            )
            drawable.state = if (isFocused && index == currentPin.length) {
                intArrayOf(android.R.attr.state_focused)
            } else {
                intArrayOf()
            }
            drawable.draw(canvas)
            return
        }

        boxPaint.style = Paint.Style.FILL
        boxPaint.color =
            if (index < currentPin.length) boxBackgroundColorFilled else boxBackgroundColor
        drawShape(canvas, boxRect)

        boxPaint.style = Paint.Style.STROKE
        boxPaint.color = when {
            isErrorState -> boxStrokeColorError
            isFocused && index == currentPin.length -> boxStrokeColorFocused
            else -> boxStrokeColor
        }
        drawShape(canvas, boxRect)
    }

    private fun drawShape(canvas: Canvas, rect: RectF) {
        when (pinShape) {
            0 -> canvas.drawRect(rect, boxPaint)
            1 -> canvas.drawRoundRect(rect, boxCornerRadius, boxCornerRadius, boxPaint)
            2 -> canvas.drawCircle(
                rect.centerX(),
                rect.centerY(),
                rect.width().coerceAtMost(rect.height()) / 2,
                boxPaint
            )
        }
    }

    private fun drawText(canvas: Canvas, index: Int) {
        if (index < currentPin.length) {
            val textToDraw = if (isPassword) passwordCharacter else currentPin[index].toString()
            val yPos = boxRect.centerY() - (textPaint.descent() + textPaint.ascent()) / 2
            canvas.drawText(textToDraw, boxRect.centerX(), yPos, textPaint)
        }
    }

    private fun drawCursor(canvas: Canvas) {
        if (showCursor && isFocused && cursorVisible && currentPin.length < pinCount) {
            val cursorIndex = currentPin.length
            val startX = paddingStart + cursorIndex * (boxWidth + boxSpacing) + (boxWidth / 2)
            val startY = paddingTop + (boxHeight * 0.2f)
            val endY = paddingTop + (boxHeight * 0.8f)
            canvas.drawLine(startX, startY, startX, endY, cursorPaint)
        }
    }

    override fun onFocusChanged(
        gainFocus: Boolean,
        direction: Int,
        previouslyFocusedRect: android.graphics.Rect?
    ) {
        super.onFocusChanged(gainFocus, direction, previouslyFocusedRect)
        if (showCursor) {
            if (gainFocus) cursorAnimator?.start() else cursorAnimator?.cancel()
            cursorVisible = gainFocus
            invalidate()
        }
    }

    override fun onCheckIsTextEditor(): Boolean = true

    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (event.action == MotionEvent.ACTION_DOWN) {
            performClick()
            return true
        }
        return super.onTouchEvent(event)
    }

    private fun showKeyboard() {
        val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.showSoftInput(this, 0)
    }

    private fun hideKeyboard() {
        val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(windowToken, 0)
    }

    override fun onCreateInputConnection(outAttrs: EditorInfo): InputConnection {
        outAttrs.inputType = when (pinInputType) {
            0 -> InputType.TYPE_CLASS_NUMBER
            1 -> InputType.TYPE_CLASS_TEXT
            2 -> InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_FLAG_CAP_CHARACTERS
            else -> InputType.TYPE_CLASS_TEXT
        }
        outAttrs.imeOptions = EditorInfo.IME_ACTION_DONE

        return object : BaseInputConnection(this, false) {
            override fun sendKeyEvent(event: KeyEvent): Boolean {
                if (event.action == KeyEvent.ACTION_DOWN) {
                    if (isErrorState) clearErrorState()

                    if (event.keyCode == KeyEvent.KEYCODE_DEL) {
                        if (currentPin.isNotEmpty()) {
                            currentPin.deleteCharAt(currentPin.length - 1)
                            invalidate()
                        }
                        return true
                    }

                    if (event.unicodeChar != 0) {
                        if (currentPin.length < pinCount) {
                            val ch = event.unicodeChar.toChar()
                            if (pinInputType == 0 && !ch.isDigit()) {
                                return true // Ignore non-digits in number mode
                            }
                            val charToAppend = when (pinInputType) {
                                2 -> ch.uppercaseChar()
                                else -> ch
                            }
                            currentPin.append(charToAppend)
                            invalidate()
                            if (currentPin.length == pinCount) {
                                onPinEnteredListener?.invoke(currentPin.toString())
                                hideKeyboard()
                            }
                        }
                        return true
                    }
                }
                return super.sendKeyEvent(event)
            }

            override fun commitText(text: CharSequence?, newCursorPosition: Int): Boolean {
                if (isErrorState) clearErrorState()
                if (!text.isNullOrEmpty()) {
                    for (rawChar in text.toString()) {
                        if (currentPin.length >= pinCount) break
                        when (pinInputType) {
                            0 -> if (rawChar.isDigit()) currentPin.append(rawChar)
                            2 -> currentPin.append(rawChar.uppercaseChar())
                            else -> currentPin.append(rawChar)
                        }
                    }
                    invalidate()
                    if (currentPin.length == pinCount) {
                        onPinEnteredListener?.invoke(currentPin.toString())
                        hideKeyboard()
                    }
                }
                return true
            }
        }
    }

    fun setOnPinEnteredListener(listener: (String) -> Unit) {
        this.onPinEnteredListener = listener
    }

    fun clearPin() {
        currentPin.clear()
        clearErrorState()
        invalidate()
    }

    fun showErrorState(durationMillis: Long = 1000) {
        isErrorState = true
        invalidate()
        errorHandler.postDelayed({ clearErrorState() }, durationMillis)
    }

    private fun clearErrorState() {
        isErrorState = false
        invalidate()
    }

    private val Float.dp: Float
        get() = TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP, this, resources.displayMetrics
        )

    private val Float.sp: Float
        get() = TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_SP, this, resources.displayMetrics
        )
}