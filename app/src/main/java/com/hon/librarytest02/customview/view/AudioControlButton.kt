package com.hon.librarytest02.customview.view

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.os.Parcel
import android.os.Parcelable
import android.util.AttributeSet
import android.view.AbsSavedState
import android.view.View
import android.view.animation.DecelerateInterpolator
import android.view.animation.LinearInterpolator
import com.hon.librarytest02.util.dp

/**
 * Created by Frank Hon on 2022/8/11 12:49 下午.
 * E-mail: frank_hon@foxmail.com
 */
class AudioControlButton @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    companion object {
        private const val DEFAULT_PLAY_WIDTH = 6
        private const val DEFAULT_PLAY_HEIGHT = 27
        private const val DEFAULT_PLAY_CENTER_PADDING = 8
        private const val DEFAULT_PAUSE_WIDTH = 25
        private const val DEFAULT_PAUSE_HEIGHT = 30
    }

    private val fillPaint by lazy {
        Paint().apply {
            color = Color.parseColor("#795548")
            style = Paint.Style.FILL
            isAntiAlias = true
        }
    }

    private val strokePaint by lazy {
        Paint().apply {
            color = Color.parseColor("#795548")
            style = Paint.Style.STROKE
            strokeWidth = 2.dp
            isAntiAlias = true
        }
    }

    private var playWidth = DEFAULT_PLAY_WIDTH.dp
    private var playHeight = DEFAULT_PLAY_HEIGHT.dp
    private var playCenterPadding = DEFAULT_PLAY_CENTER_PADDING.dp
    private var pauseWidth = DEFAULT_PAUSE_WIDTH.dp
    private var pauseHeight = DEFAULT_PAUSE_HEIGHT.dp

    private var startAngle = 0f
    private var sweepAngle = 60f

    private val path by lazy { Path() }
    private var playState = PlayState.PLAYING

    private val loadingAnimator by lazy {
        AnimatorSet().apply {
            playTogether(
                ValueAnimator.ofFloat(0f, 1f)
                    .apply {
                        duration = 500
                        repeatCount = ValueAnimator.INFINITE
                        interpolator = LinearInterpolator()
                        repeatMode = ValueAnimator.REVERSE
                        addUpdateListener {
                            val animatedValue = it.animatedValue as Float
                            sweepAngle = 60 + animatedValue * 60
                        }
                    },
                ValueAnimator.ofFloat(0f, 1f)
                    .apply {
                        duration = 1000
                        repeatCount = ValueAnimator.INFINITE
                        interpolator = LinearInterpolator()
                        addUpdateListener {
                            val animatedValue = it.animatedValue as Float
                            startAngle = animatedValue * 360
                            invalidate()
                        }
                    }
            )
        }
    }

    init {
        setPlayState(PlayState.PREPARING)
        setOnClickListener {
            playState = when (playState) {
                PlayState.PLAYING -> {
                    PlayState.PAUSING
                }
                PlayState.PAUSING -> {
                    PlayState.PLAYING
                }
                else -> {
                    return@setOnClickListener
                }
            }
            showSwitchAnimation()
        }
    }

    override fun onDraw(canvas: Canvas) {
        val left = width / 2 - playCenterPadding / 2 - playWidth
        val top = height / 2 - playHeight / 2
        when (playState) {
            PlayState.PLAYING -> drawPlayButton(canvas, left, top)
            PlayState.PAUSING -> drawPauseButton(canvas, left, top)
            PlayState.PREPARING -> drawLoading(canvas, left, top)
        }
    }

    override fun onSaveInstanceState(): Parcelable {
        val superState = super.onSaveInstanceState()
        val state = SavedState(superState)
        state.playState = playState.name
        return state
    }

    override fun onRestoreInstanceState(state: Parcelable) {
        super.onRestoreInstanceState(state)
        (state as? SavedState)?.let {
            this.playState = PlayState.valueOf(it.playState)
        }
    }

    private fun drawPlayButton(canvas: Canvas, left: Float, top: Float) {
        val right = left + playWidth
        val bottom = top + playHeight
        canvas.drawRoundRect(left, top, right, bottom, playWidth / 2, playWidth / 2, fillPaint)
        canvas.drawRoundRect(
            left + playCenterPadding + playWidth,
            top,
            right + playCenterPadding + playWidth,
            bottom,
            playWidth / 2,
            playWidth / 2,
            fillPaint
        )
    }

    private fun drawPauseButton(canvas: Canvas, left: Float, top: Float) {
        path.moveTo(left, top)
        path.lineTo(left + pauseWidth, height / 2f)
        path.lineTo(left, top + pauseHeight)
        path.close()
        canvas.drawPath(path, fillPaint)
    }

    private fun drawLoading(canvas: Canvas, left: Float, top: Float) {
        drawPlayButton(canvas, left, top)
        val centerX = left + playWidth + playCenterPadding / 2
        val centerY = top + playHeight / 2
        val radius = playHeight / 2 + 10.dp
        canvas.drawArc(
            centerX - radius,
            centerY - radius,
            centerX + radius,
            centerY + radius,
            startAngle,
            sweepAngle,
            false,
            strokePaint
        )
    }

    private fun showSwitchAnimation() {
        ObjectAnimator.ofFloat(this, ALPHA, 0.5f, 1.0f)
            .apply {
                duration = 1000
                interpolator = DecelerateInterpolator()
                addUpdateListener {
                    val alpha = it.animatedValue as Float
                    if (alpha == 0.5f) {
                        invalidate()
                    }
                }
            }
            .start()
    }

    fun setPlayState(state: PlayState) {
        if (playState == state) {
            return
        }
        this.playState = state
        if (state == PlayState.PREPARING) {
            loadingAnimator.start()
        } else {
            if (loadingAnimator.isRunning) {
                loadingAnimator.cancel()
            }
            showSwitchAnimation()
        }
    }

    enum class PlayState {
        PLAYING,
        PAUSING,
        PREPARING;
    }

    private class SavedState : AbsSavedState {
        var playState = ""

        constructor(source: Parcelable?) : super(source)

        constructor(parcel: Parcel) : super(parcel) {
            playState = parcel.readString().orEmpty()
        }

        override fun writeToParcel(parcel: Parcel, flags: Int) {
            super.writeToParcel(parcel, flags)
            parcel.writeString(playState)
        }

        override fun describeContents(): Int {
            return 0
        }

        companion object CREATOR : Parcelable.Creator<SavedState> {
            override fun createFromParcel(parcel: Parcel): SavedState {
                return SavedState(parcel)
            }

            override fun newArray(size: Int): Array<SavedState?> {
                return arrayOfNulls(size)
            }
        }
    }

}