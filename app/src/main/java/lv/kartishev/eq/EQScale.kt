package lv.kartishev.eq

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View


/**
 * TODO: document your custom view class.
 */
class EQScale : View {

    private var scalePaint = Paint()
    private var cursorPaint = Paint()
    private var cursorPositionX = -1.0f
    private var hz = 1130L

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

        a.recycle()

        // Set up a default TextPaint object
        scalePaint.flags = Paint.ANTI_ALIAS_FLAG
        scalePaint.color = scaleColor
        scalePaint.textAlign = Paint.Align.CENTER
        scalePaint.textSize = 10 * resources.displayMetrics.density

        cursorPaint.flags = Paint.ANTI_ALIAS_FLAG
        cursorPaint.color = cursorColor
        cursorPaint.strokeWidth = 2 * resources.displayMetrics.density
        cursorPaint.textAlign = Paint.Align.CENTER
        cursorPaint.textSize = 12 * resources.displayMetrics.density

    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        val x = event.x

        val n = x *  9.0 / width;
        hz = Math.round(25.0 * Math.pow(2.0, n + 1))

        when (event.action) {
            MotionEvent.ACTION_DOWN, MotionEvent.ACTION_MOVE -> {
                cursorPositionX = x;
                invalidate()
            }
        }
        return true
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        if (cursorPositionX < 0) {
            cursorPositionX = width / 2.0f
        }

        val bounds = Rect()
        cursorPaint.getTextBounds("1000Hz", 0, "1000Hz".length, bounds)
        val curTextHeight: Int = bounds.height()

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

        cursorPaint.let {
            canvas.drawLine(cursorPositionX, 0.0f, cursorPositionX, (height.toFloat() / 2) - (curTextHeight / 2) - (curTextHeight / 4), it)
            canvas.drawLine(cursorPositionX, (height.toFloat() / 2) + (curTextHeight / 2) + (curTextHeight / 4), cursorPositionX, height.toFloat(), it)

            canvas.drawText(hz.toString() + "Hz", cursorPositionX, (height.toFloat() / 2) + (curTextHeight / 2), it)
        }
    }
}
