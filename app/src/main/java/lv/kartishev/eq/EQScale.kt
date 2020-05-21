package lv.kartishev.eq

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import kotlin.math.ln


/**
 * TODO: document your custom view class.
 */
class EQScale : View {

    private var scalePaint = Paint()
    private var cursorPaint = Paint()
    private var correctCursorPaint = Paint()
    private var rangePaint = Paint()
    private var cursorPositionX = -999.0f
    private var correctCursorPositionX = -999.0f
    private var selectedFrequency = 1130L
    private var correctFrequency = 1130L
    private var showCorrect = false
    private var rangeMultiplier = 1.0f

    private var textHeight = 0

    constructor(context: Context) : super(context) {
        init(null, 0)
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init(attrs, 0)
    }

    constructor(context: Context, attrs: AttributeSet, defStyle: Int) : super(
        context,
        attrs,
        defStyle
    ) {
        init(attrs, defStyle)
    }

    private fun init(attrs: AttributeSet?, defStyle: Int) {
        // Load attributes
        val a = context.obtainStyledAttributes(
            attrs, R.styleable.EQScale, defStyle, 0
        )

        val scaleColor = a.getColor(
            R.styleable.EQScale_scaleColor,
            Color.BLUE
        )

        val cursorColor = a.getColor(
            R.styleable.EQScale_cursorColor,
            Color.RED
        )

        val correctCursorColor = a.getColor(
            R.styleable.EQScale_correctCursorColor,
            Color.GREEN
        )

        val rangeColor = a.getColor(
            R.styleable.EQScale_rangeColor,
            Color.LTGRAY
        )

        a.recycle()

        // Set up a default TextPaint object
        scalePaint.flags = Paint.ANTI_ALIAS_FLAG
        scalePaint.color = scaleColor
        scalePaint.textAlign = Paint.Align.CENTER
        scalePaint.textSize = 12 * resources.displayMetrics.density

        cursorPaint.flags = Paint.ANTI_ALIAS_FLAG
        cursorPaint.color = cursorColor
        cursorPaint.strokeWidth = 2 * resources.displayMetrics.density
        cursorPaint.textAlign = Paint.Align.CENTER
        cursorPaint.textSize = 14 * resources.displayMetrics.density

        val bounds = Rect()
        cursorPaint.getTextBounds("1000Hz", 0, "1000Hz".length, bounds)
        textHeight = bounds.height()

        correctCursorPaint.flags = Paint.ANTI_ALIAS_FLAG
        correctCursorPaint.color = correctCursorColor
        correctCursorPaint.strokeWidth = 2 * resources.displayMetrics.density
        correctCursorPaint.textAlign = Paint.Align.CENTER
        correctCursorPaint.textSize = 14 * resources.displayMetrics.density

        rangePaint.flags = Paint.ANTI_ALIAS_FLAG
        rangePaint.color = rangeColor
        rangePaint.alpha = 120

    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        selectedFrequency = positionToFrequency(event.x)

        when (event.action) {
            MotionEvent.ACTION_DOWN, MotionEvent.ACTION_MOVE -> {
                cursorPositionX = event.x;
                invalidate()
            }
        }
        return true
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        if (cursorPositionX < -100) {
            cursorPositionX = width / 2.0f
        }

        scalePaint.let {
            canvas.drawLine(width / 9 * 1.0f, 0.0f, width / 9 * 1.0f, height.toFloat() - (it.fontMetrics.descent - it.fontMetrics.ascent), it)
            canvas.drawLine(width / 9 * 2.0f, 0.0f, width / 9 * 2.0f, height.toFloat() - (it.fontMetrics.descent - it.fontMetrics.ascent), it)
            canvas.drawLine(width / 9 * 3.0f, 0.0f, width / 9 * 3.0f, height.toFloat() - (it.fontMetrics.descent - it.fontMetrics.ascent), it)
            canvas.drawLine(width / 9 * 4.0f, 0.0f, width / 9 * 4.0f, height.toFloat() - (it.fontMetrics.descent - it.fontMetrics.ascent), it)
            canvas.drawLine(width / 9 * 5.0f, 0.0f, width / 9 * 5.0f, height.toFloat() - (it.fontMetrics.descent - it.fontMetrics.ascent), it)
            canvas.drawLine(width / 9 * 6.0f, 0.0f, width / 9 * 6.0f, height.toFloat() - (it.fontMetrics.descent - it.fontMetrics.ascent), it)
            canvas.drawLine(width / 9 * 7.0f, 0.0f, width / 9 * 7.0f, height.toFloat() - (it.fontMetrics.descent - it.fontMetrics.ascent), it)
            canvas.drawLine(width / 9 * 8.0f, 0.0f, width / 9 * 8.0f, height.toFloat() - (it.fontMetrics.descent - it.fontMetrics.ascent), it)

            canvas.drawText("100Hz", width / 9 * 1.0f, height.toFloat(), it)
            canvas.drawText("200Hz", width / 9 * 2.0f, height.toFloat(), it)
            canvas.drawText("400Hz", width / 9 * 3.0f, height.toFloat(), it)
            canvas.drawText("800Hz", width / 9 * 4.0f, height.toFloat(), it)
            canvas.drawText("1.6kHz", width / 9 * 5.0f, height.toFloat(), it)
            canvas.drawText("3.2kHz", width / 9 * 6.0f, height.toFloat(), it)
            canvas.drawText("6.4kHz", width / 9 * 7.0f, height.toFloat(), it)
            canvas.drawText("12.8kHz", width / 9 * 8.0f, height.toFloat(), it)
        }

        rangePaint.let {
            canvas.drawRect(cursorPositionX - (width * rangeMultiplier / 9), 0.0f, cursorPositionX + (width * rangeMultiplier / 9), height.toFloat(), it)
        }

        cursorPaint.let {
            if (!showCorrect) {
                canvas.drawLine(cursorPositionX, 0.0f, cursorPositionX, (height.toFloat() / 2) - (textHeight / 2) - (textHeight / 4), it)
                canvas.drawLine(cursorPositionX, (height.toFloat() / 2) + (textHeight / 2) + (textHeight / 4), cursorPositionX, height.toFloat(), it)

                canvas.drawText(selectedFrequency.toString() + "Hz", cursorPositionX, (height.toFloat() / 2) + (textHeight / 2), it)
            }
        }

        correctCursorPaint.let {
            if (showCorrect) {
                canvas.drawLine(correctCursorPositionX, 0.0f, correctCursorPositionX, (height.toFloat() / 2) - (textHeight / 2) - (textHeight / 4), it)
                canvas.drawLine(correctCursorPositionX, (height.toFloat() / 2) + (textHeight / 2) + (textHeight / 4), correctCursorPositionX, height.toFloat(), it)

                canvas.drawText(correctFrequency.toString() + "Hz", correctCursorPositionX, (height.toFloat() / 2) + (textHeight / 2), it)
            }
        }
    }

    private fun frequencyToPosition(frequency: Long): Float {
        val n = ln(frequency / 50.0) / ln(2.0)
        return (n * width).toFloat() / 9.0f
    }

    private fun positionToFrequency(position: Float): Long {
        val n = position *  9.0 / width;
        return Math.round(50.0 * Math.pow(2.0, n))
    }

    fun getBottomRangeFrequency(): Long {
        return positionToFrequency(cursorPositionX - (width * rangeMultiplier / 9))
    }

    fun getTopRangeFrequency(): Long {
        return positionToFrequency(cursorPositionX + (width * rangeMultiplier / 9))
    }

    fun showCorrect(frequency: Long) {
        correctFrequency = frequency
        showCorrect = true

        correctCursorPositionX = frequencyToPosition(frequency)

        invalidate()
    }

    fun stopShowingCorrect() {
        showCorrect = false
        invalidate()
    }


    fun setLevel(level: Level) {
        when(level) {
            Level.EASY -> {
                rangeMultiplier = 1.0f
            }
            Level.MID -> {
                rangeMultiplier = 0.5f
            }
            Level.HARD -> {
                rangeMultiplier = 0.33f
            }
        }

        invalidate()
    }
}

enum class Level {
    EASY, MID, HARD
}
