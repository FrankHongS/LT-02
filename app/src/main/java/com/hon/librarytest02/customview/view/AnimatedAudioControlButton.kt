package com.hon.librarytest02.customview.view

import android.animation.AnimatorSet
import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.drawable.AnimatedVectorDrawable
import android.os.Parcel
import android.os.Parcelable
import android.util.AttributeSet
import android.util.Log
import android.view.AbsSavedState
import android.view.LayoutInflater
import android.view.View
import android.view.animation.LinearInterpolator
import android.widget.FrameLayout
import android.widget.ImageButton
import com.hon.librarytest02.R
import com.hon.librarytest02.util.dp

/**
 * Created by Frank Hon on 2022/8/14 12:21 下午.
 * E-mail: frank_hon@foxmail.com
 */
class AnimatedAudioControlButton @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : FrameLayout(context, attrs) {

    //region draw loadingBar
    private val loadingBarPadding = 8.dp
    private val strokePaint by lazy {
        Paint().apply {
            color = Color.parseColor("#795548")
            style = Paint.Style.STROKE
            strokeWidth = 2.dp
            isAntiAlias = true
        }
    }
    private var startAngle = 0f
    private var sweepAngle = 60f
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
    //endregion

    private var playState = PlayState.PAUSING
    private val imageButton: ImageButton

    init {
        View.inflate(context, R.layout.layout_audio_control, this)
        imageButton = findViewById<ImageButton>(R.id.btn_audio_control).apply {
            setOnClickListener {
                when (playState) {
                    PlayState.PLAYING -> {
                        setPlayState(PlayState.PAUSING)
                    }
                    PlayState.PAUSING -> {
                        setPlayState(PlayState.PLAYING)
                    }
                    else -> {}
                }
            }
        }
        //无背景时，需要调用此方法ViewGroup才会绘制自身，调用onDraw()
        setWillNotDraw(false)
    }

    override fun onDraw(canvas: Canvas) {
        if (playState == PlayState.PREPARING) {
            drawLoading(canvas)
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

    private fun drawLoading(canvas: Canvas) {
        val centerX = width / 2
        val centerY = height / 2
        val radius = imageButton.width /2 + loadingBarPadding
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

    private fun switchState() {
        if (playState != PlayState.PREPARING && loadingAnimator.isRunning) {
            loadingAnimator.cancel()
        }
        imageButton.run {
            when (playState) {
                PlayState.PLAYING -> {
                    setImageResource(R.drawable.pause_to_play_vector_drawable)
                }
                PlayState.PAUSING -> {
                    setImageResource(R.drawable.play_to_pause_vector_drawable)
                }
                PlayState.PREPARING -> {
                    imageButton.setImageResource(R.drawable.ic_play_song)
                    loadingAnimator.start()
                    return
                }
            }
            (drawable as? AnimatedVectorDrawable)?.start()
        }
    }

    fun setPlayState(state: PlayState) {
        if (playState == state) {
            return
        }
        this.playState = state
        switchState()
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