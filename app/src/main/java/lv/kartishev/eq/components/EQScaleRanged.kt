package lv.kartishev.eq.components

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.text.TextPaint
import android.text.TextUtils
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import androidx.core.graphics.ColorUtils
import lv.kartishev.eq.R
import lv.kartishev.eq.fragment.Position
import lv.kartishev.eq.fragment.Range
import kotlin.math.ln
import kotlin.random.Random


/**
 * TODO: document your custom view class.
 */
class EQScaleRanged : View {

    private var scalePaint = Paint()
    private var rangePaint = Paint()
    private var cursorPaint = Paint()
    private var textPaint = TextPaint()

    private var textHeight = 0

    private var onRangeChangedListener: (Range) -> Unit = {}

    fun setOnRangeChangedListener(l: (Range) -> Unit) {
        onRangeChangedListener = l
    }


    private var ranges: List<Range> = listOf()


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
            attrs, R.styleable.EQScaleRanged, defStyle, 0
        )

        val scaleColor = a.getColor(
            R.styleable.EQScaleRanged_scaleRangedColor,
            Color.BLUE
        )

        a.recycle()

        val rangeColor = Color.GREEN

        // Set up a default TextPaint object
        scalePaint.flags = Paint.ANTI_ALIAS_FLAG
        scalePaint.color = scaleColor
        scalePaint.textAlign = Paint.Align.CENTER
        scalePaint.textSize = 12 * resources.displayMetrics.density

        rangePaint.flags = Paint.ANTI_ALIAS_FLAG
        rangePaint.color = rangeColor
        rangePaint.textAlign = Paint.Align.CENTER
        rangePaint.textSize = 12 * resources.displayMetrics.density
        rangePaint.alpha = 120

        cursorPaint.flags = Paint.ANTI_ALIAS_FLAG
        cursorPaint.color = Color.WHITE
        cursorPaint.strokeWidth = 2 * resources.displayMetrics.density
        cursorPaint.textAlign = Paint.Align.CENTER
        cursorPaint.textSize = 14 * resources.displayMetrics.density

        val bounds = Rect()
        cursorPaint.getTextBounds("1000Hz", 0, "1000Hz".length, bounds)
        textHeight = bounds.height()

        textPaint = TextPaint()
        textPaint.color = Color.WHITE
        textPaint.style = Paint.Style.FILL
        textPaint.isAntiAlias = true
        textPaint.textSize = 14 * resources.displayMetrics.density
        textPaint.textAlign = Paint.Align.CENTER
        textPaint.isLinearText = true

    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        val selectedFrequency = positionToFrequency(event.x)

        when (event.action) {
            MotionEvent.ACTION_DOWN, MotionEvent.ACTION_MOVE -> {
                val range = ranges.find { it.low <= selectedFrequency && it.high >= selectedFrequency && (it.position == Position.FULL || (it.position == Position.HIGH && event.y <= height / 2) || (it.position == Position.LOW && event.y > height / 2))}
                range?.let {
                    ranges.forEach { r -> r.selected = false }
                    onRangeChangedListener.invoke(it)
                    it.selected = true
                }
                invalidate()
            }
        }
        return true
    }


    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        scalePaint.let {
            canvas.drawLine(width / 10 * 1.0f, 0.0f, width / 10 * 1.0f, height.toFloat() - (it.fontMetrics.descent - it.fontMetrics.ascent), it)
            canvas.drawLine(width / 10 * 2.0f, 0.0f, width / 10 * 2.0f, height.toFloat() - (it.fontMetrics.descent - it.fontMetrics.ascent), it)
            canvas.drawLine(width / 10 * 3.0f, 0.0f, width / 10 * 3.0f, height.toFloat() - (it.fontMetrics.descent - it.fontMetrics.ascent), it)
            canvas.drawLine(width / 10 * 4.0f, 0.0f, width / 10 * 4.0f,height.toFloat() - (it.fontMetrics.descent - it.fontMetrics.ascent), it)
            canvas.drawLine(width / 10 * 5.0f, 0.0f, width / 10 * 5.0f, height.toFloat() - (it.fontMetrics.descent - it.fontMetrics.ascent), it)
            canvas.drawLine(width / 10 * 6.0f, 0.0f, width / 10 * 6.0f, height.toFloat() - (it.fontMetrics.descent - it.fontMetrics.ascent), it)
            canvas.drawLine(width / 10 * 7.0f, 0.0f, width / 10 * 7.0f, height.toFloat() - (it.fontMetrics.descent - it.fontMetrics.ascent), it)
            canvas.drawLine(width / 10 * 8.0f, 0.0f, width / 10 * 8.0f, height.toFloat() - (it.fontMetrics.descent - it.fontMetrics.ascent), it)
            canvas.drawLine(width / 10 * 9.0f, 0.0f, width / 10 * 9.0f, height.toFloat() - (it.fontMetrics.descent - it.fontMetrics.ascent), it)

            canvas.drawText("50Hz", width / 10 * 1.0f, height.toFloat(), it)
            canvas.drawText("100Hz", width / 10 * 2.0f, height.toFloat(), it)
            canvas.drawText("200Hz", width / 10 * 3.0f, height.toFloat(), it)
            canvas.drawText("400Hz", width / 10 * 4.0f, height.toFloat(), it)
            canvas.drawText("800Hz", width / 10 * 5.0f, height.toFloat(), it)
            canvas.drawText("1.6kHz", width / 10 * 6.0f, height.toFloat(), it)
            canvas.drawText("3.2kHz", width / 10 * 7.0f, height.toFloat(), it)
            canvas.drawText("6.4kHz", width / 10 * 8.0f, height.toFloat(), it)
            canvas.drawText("12.8kHz", width / 10 * 9.0f, height.toFloat(), it)
        }

        for (range in ranges) {
            if (range.point != 0L) {
                val pointPosition = frequencyToPosition(range.point)
                val lowPosition = pointPosition - width / 40
                range.low = positionToFrequency(lowPosition)
                val highPosition = pointPosition + width / 40
                range.high = positionToFrequency(highPosition)
            }
        }

        for ((index, range) in ranges.withIndex()) {
            range.position = Position.FULL

            if (index == 0 && ranges[index + 1].low < range.high) {
                range.position = Position.LOW
            }

            if (index > 0 && index < ranges.size - 1 && ranges[index + 1].low < range.high) {
                range.position = Position.LOW
            }

            if (index > 0 && ranges[index - 1].high > range.low && ranges[index - 1].position == Position.LOW) {
                range.position = Position.HIGH
            }
            if (index > 0 && ranges[index - 1].high > range.low && ranges[index - 1].position == Position.HIGH) {
                range.position = Position.LOW
            }
        }

        for (range in ranges) {
            if (range.selected) {
                rangePaint.color = ColorUtils.setAlphaComponent(range.color, 180)
            } else {
                rangePaint.color = range.color
            }

            val lowPosition = frequencyToPosition(if (range.low <= 25) 25 else range.low)
            val highPosition = frequencyToPosition(if (range.high >= 20000) 25600 else range.high)

            if (range.position == Position.FULL) {
                canvas.drawRect(lowPosition, 0.0f, highPosition, height.toFloat(), rangePaint)

                val txt = TextUtils.ellipsize(range.title, textPaint, highPosition - lowPosition, TextUtils.TruncateAt.END)
                canvas.drawText(txt.toString(), lowPosition + (highPosition - lowPosition) / 2, (height.toFloat() / 2) + (textHeight / 2), textPaint)
            }
            if (range.position == Position.HIGH) {
                canvas.drawRect(lowPosition, 0.0f, highPosition, height.toFloat() / 2, rangePaint)

                val txt = TextUtils.ellipsize(range.title, textPaint, highPosition - lowPosition, TextUtils.TruncateAt.END)
                canvas.drawText(txt.toString(), lowPosition + (highPosition - lowPosition) / 2, ((height.toFloat() / 2) - (height.toFloat() / 4)) + (textHeight / 2), textPaint)
            }
            if (range.position == Position.LOW) {
                canvas.drawRect(lowPosition, height.toFloat() / 2, highPosition, height.toFloat(), rangePaint)

                val txt = TextUtils.ellipsize(range.title, textPaint, highPosition - lowPosition, TextUtils.TruncateAt.END)
                canvas.drawText(txt.toString(), lowPosition + (highPosition - lowPosition) / 2, ((height.toFloat() / 2) + (height.toFloat() / 4)) + (textHeight / 2), textPaint)
            }

        }

    }

    fun setRanges(ranges: List<Range>) {
        this.ranges = ranges
        this.ranges.forEach {
            it.color = Color.argb(120, Random.nextInt(256), Random.nextInt(256), Random.nextInt(256))
            it.selected = false
        }
        invalidate()
    }

    private fun frequencyToPosition(frequency: Long): Float {
        val n = ln(frequency / 25.0) / ln(2.0)
        return (n * width).toFloat() / 10.0f
    }

    private fun positionToFrequency(position: Float): Long {
        val n = position * 10.0 / width;
        return Math.round(25.0 * Math.pow(2.0, n))
    }

}



