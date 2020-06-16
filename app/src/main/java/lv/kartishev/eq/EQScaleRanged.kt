package lv.kartishev.eq

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import androidx.core.graphics.ColorUtils
import kotlin.math.ln
import kotlin.random.Random


/**
 * TODO: document your custom view class.
 */
class EQScaleRanged : View {

    private var scalePaint = Paint()
    private var rangePaint = Paint()
    private var cursorPaint = Paint()

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

    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        val selectedFrequency = positionToFrequency(event.x)

        when (event.action) {
            MotionEvent.ACTION_DOWN, MotionEvent.ACTION_MOVE -> {
                val range = ranges.find { it.low <= selectedFrequency && it.high >= selectedFrequency }
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
            canvas.drawLine(
                width / 10 * 1.0f,
                0.0f,
                width / 10 * 1.0f,
                height.toFloat() - (it.fontMetrics.descent - it.fontMetrics.ascent),
                it
            )
            canvas.drawLine(
                width / 10 * 2.0f,
                0.0f,
                width / 10 * 2.0f,
                height.toFloat() - (it.fontMetrics.descent - it.fontMetrics.ascent),
                it
            )
            canvas.drawLine(
                width / 10 * 3.0f,
                0.0f,
                width / 10 * 3.0f,
                height.toFloat() - (it.fontMetrics.descent - it.fontMetrics.ascent),
                it
            )
            canvas.drawLine(
                width / 10 * 4.0f,
                0.0f,
                width / 10 * 4.0f,
                height.toFloat() - (it.fontMetrics.descent - it.fontMetrics.ascent),
                it
            )
            canvas.drawLine(
                width / 10 * 5.0f,
                0.0f,
                width / 10 * 5.0f,
                height.toFloat() - (it.fontMetrics.descent - it.fontMetrics.ascent),
                it
            )
            canvas.drawLine(
                width / 10 * 6.0f,
                0.0f,
                width / 10 * 6.0f,
                height.toFloat() - (it.fontMetrics.descent - it.fontMetrics.ascent),
                it
            )
            canvas.drawLine(
                width / 10 * 7.0f,
                0.0f,
                width / 10 * 7.0f,
                height.toFloat() - (it.fontMetrics.descent - it.fontMetrics.ascent),
                it
            )
            canvas.drawLine(
                width / 10 * 8.0f,
                0.0f,
                width / 10 * 8.0f,
                height.toFloat() - (it.fontMetrics.descent - it.fontMetrics.ascent),
                it
            )
            canvas.drawLine(
                width / 10 * 9.0f,
                0.0f,
                width / 10 * 9.0f,
                height.toFloat() - (it.fontMetrics.descent - it.fontMetrics.ascent),
                it
            )

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
            if (range.selected) {
                rangePaint.color = ColorUtils.setAlphaComponent(range.color, 180)
            } else {
                rangePaint.color = range.color
            }

            val lowPosition = frequencyToPosition(range.low)
            val highPosition = frequencyToPosition(range.high)
            canvas.drawRect(lowPosition, 0.0f, highPosition, height.toFloat(), rangePaint)

            if (range.name.contains("\n")) {
                canvas.drawText(range.name.split("\n")[0], lowPosition + (highPosition - lowPosition) / 2, (height.toFloat() / 2) + (textHeight / 2) - textHeight, cursorPaint)
                canvas.drawText(range.name.split("\n")[1], lowPosition + (highPosition - lowPosition) / 2, (height.toFloat() / 2) + (textHeight / 2) + textHeight, cursorPaint)
            } else {
                canvas.drawText(range.name, lowPosition + (highPosition - lowPosition) / 2, (height.toFloat() / 2) + (textHeight / 2), cursorPaint)
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



