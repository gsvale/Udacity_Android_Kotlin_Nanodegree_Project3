package com.udacity

import android.animation.Animator
import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import androidx.core.content.ContextCompat
import androidx.core.content.withStyledAttributes
import kotlin.properties.Delegates


class LoadingButton @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {


     // View width and height
    private var widthSize = 0
    private var heightSize = 0

    // Button color, download button color and text color
    private var buttonColor = 0
    private var loadingColor = 0
    private var textColor = 0


    private val valueAnimator = ValueAnimator()


    // Loading Rect used
    private val loadingRect = Rect()

    // Arc RectF used
    private val arcRect = RectF()

    // Init loading value
    private var loadingValue: Int = 0


    // Chek current/new button state
    private var buttonState: ButtonState by Delegates.observable<ButtonState>(ButtonState.Completed) { p, old, new ->
        when (new) {
            ButtonState.Clicked -> {
                // On click, loads animation
                loading()
            }
            ButtonState.Loading -> {
                // When loading, set view disabled
                isEnabled = false
            }
            ButtonState.Completed -> {
                // After loading is complete, reset button
                isEnabled = true
                loadingValue = 0
                invalidate()
            }
        }
    }

    // Paint used for canvas
    private val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
        textAlign = Paint.Align.CENTER
        textSize = 50.0f
    }


    // Init Custom view
    init {
        loadingValue = 0
        isEnabled = true
        isClickable = true


        // Get colors from attrs values
        context.withStyledAttributes(attrs, R.styleable.LoadingButton) {
            buttonColor = getColor(R.styleable.LoadingButton_buttonColor, 0)
            loadingColor = getColor(R.styleable.LoadingButton_loadingColor, 0)
            textColor = getColor(R.styleable.LoadingButton_textColor, 0)
        }
    }

    // Custom View Click
    override fun performClick(): Boolean {
        buttonState = ButtonState.Clicked
        if (super.performClick()) return true
        return true
    }

    // Method OnDraw
    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)

        // Set button color
        paint.color = buttonColor
        canvas?.drawPaint(paint)


        //  Create loading rect when downloading
        // width is defined with right value of rect
        // with current loadingValue
        loadingRect.left = 0
        loadingRect.top = top
        loadingRect.right = loadingValue
        loadingRect.bottom = 0

        paint.color = loadingColor
        canvas?.drawRect(
            loadingRect,
            paint
        )

        // Set correct text due to button state
        paint.color = textColor
        var text = resources.getString(R.string.button_name)
        if (buttonState == ButtonState.Loading) {
            text = resources.getString(R.string.button_loading)
        }

        canvas?.drawText(
            text,
            (widthSize / 2).toFloat(),
            (((heightSize / 2) - (paint.descent() + paint.ascent()) / 2).toInt()).toFloat(),
            paint
        )

        // Measure text width, to put arc/circle on the right
        val textWidth = paint.measureText(text)


        // Loading arc/circle position and size
        var xPosition = ((widthSize / 2) + (textWidth / 2) + 50)
        var yPosition = (((heightSize / 2)).toFloat())
        val size = 70

        // Arc drawn color
        paint.color = ContextCompat.getColor(context, R.color.colorAccent)

        xPosition -= size / 2
        yPosition -= size / 2

        // Create arc rectF
        arcRect.left = xPosition
        arcRect.top = yPosition
        arcRect.right = (xPosition + size)
        arcRect.bottom = (yPosition + size)

        // Set angle due to loadingValue
        val angle = (loadingValue * 360) / widthSize

        // Draw arc from 0 to correct degree.
        canvas?.drawArc(
            arcRect,
            0F,
            angle.toFloat(),
            true,
            paint
        )


    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val minw: Int = paddingLeft + paddingRight + suggestedMinimumWidth
        val w: Int = resolveSizeAndState(minw, widthMeasureSpec, 1)
        val h: Int = resolveSizeAndState(
            MeasureSpec.getSize(w),
            heightMeasureSpec,
            0
        )
        widthSize = w
        heightSize = h
        setMeasuredDimension(w, h)
    }

    // Loading Method
    private fun loading() {

        // Start and End values
        val startValue = 0
        val endValue = widthSize

        // Create animator to update loading value
        // and then update canvas correctly
        valueAnimator.setIntValues(startValue, endValue)
        valueAnimator.addUpdateListener { animator ->
            loadingValue = animator.animatedValue.toString().toInt()
            invalidate()
        }
        valueAnimator.addListener(object : Animator.AnimatorListener {
            override fun onAnimationStart(p0: Animator?) {
            }

            override fun onAnimationEnd(p0: Animator?) {
                buttonState = ButtonState.Completed
            }

            override fun onAnimationCancel(p0: Animator?) {
            }

            override fun onAnimationRepeat(p0: Animator?) {

            }

        })

        // animation duration, and don't repeat
        valueAnimator.duration = 5000
        valueAnimator.repeatCount = 0
        valueAnimator.repeatMode = ObjectAnimator.REVERSE
        buttonState = ButtonState.Loading
        valueAnimator.start()
    }


    // Call when download is complete, to acelerate loading to finish animation quicker
    fun setDownloadComplete() {
        if (buttonState == ButtonState.Loading) {
            val timeLeft = 5000 - valueAnimator.currentPlayTime
            valueAnimator.duration = (timeLeft * 0.3).toLong()
        }
    }

}