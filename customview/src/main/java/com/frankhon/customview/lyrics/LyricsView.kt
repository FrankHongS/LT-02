package com.frankhon.customview.lyrics

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View
import android.widget.Scroller
import com.frankhon.customview.R
import com.frankhon.customview.util.dp2px
import java.lang.Integer.min
import kotlin.math.max

/**
 * Created by Frank Hon on 2022/12/1 7:08 下午.
 * E-mail: frank_hon@foxmail.com
 */
@SuppressLint("ClickableViewAccessibility")
class LyricsView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private val lyricTextSize: Float
    private val highlightLyricTextRatio: Float
    private val lineHeightRatio: Float
    private val lineHeight: Float
    private val lineHorizontalMargin: Float

    // @ColorInt
    private val lyricColor: Int
    private val highlightLyricColor: Int

    private var lyrics: List<Pair<Long, List<String>>>? = null
    private var totalLineCount = 0
    private var maxScrollHeight = 0

    // 当前播放的歌词index
    private var curIndex = -1

    private val lyricPaint: Paint
    private val highlightLyricPaint: Paint

    private val scroller by lazy { Scroller(context) }
    private val gestureDetector =
        GestureDetector(context, object : GestureDetector.SimpleOnGestureListener() {
            override fun onFling(
                e1: MotionEvent, e2: MotionEvent, velocityX: Float, velocityY: Float
            ): Boolean {
                scroller.fling(
                    scrollX, scrollY, 0, (-velocityY).toInt(), 0, 0,
                    0, maxScrollHeight
                )
                return true
            }

            override fun onScroll(
                e1: MotionEvent, e2: MotionEvent, distanceX: Float, distanceY: Float
            ): Boolean {
                if (distanceY < 0 && scrollY <= 0) {
                    return true
                }
                if (distanceY > 0 && scrollY >= maxScrollHeight) {
                    return true
                }
                val distance = if (distanceY + scrollY > maxScrollHeight) {
                    maxScrollHeight - scrollY
                } else if (distanceY + scrollY < 0) {
                    -scrollY
                } else {
                    distanceY
                }
                scrollBy(0, distance.toInt())
                return true
            }

            override fun onSingleTapUp(e: MotionEvent): Boolean {
                onLyricsClickListener?.invoke(this@LyricsView)
                return true
            }

        })

    // 是否手动点击View
    private var isTouching = false
    private var onLyricsClickListener: ((View) -> Unit)? = null

    init {
        val typedArray = getContext().obtainStyledAttributes(
            attrs, R.styleable.LyricsView
        )
        lyricTextSize = typedArray.getDimension(R.styleable.LyricsView_textSize, dp2px(20))
        lyricColor = typedArray.getColor(R.styleable.LyricsView_textColor, Color.BLACK)
        highlightLyricTextRatio =
            typedArray.getFloat(R.styleable.LyricsView_highlightTextRatio, 1.1f)
        highlightLyricColor =
            typedArray.getColor(R.styleable.LyricsView_highlightTextColor, Color.BLUE)
        lineHeightRatio = typedArray.getFloat(R.styleable.LyricsView_lineHeightRatio, 1.8f)
        lineHorizontalMargin =
            typedArray.getDimension(R.styleable.LyricsView_lineHorizontalMargin, dp2px(32))
        typedArray.recycle()

        lineHeight = lyricTextSize * lineHeightRatio
        lyricPaint = Paint().apply {
            strokeWidth = dp2px(3)
            textSize = lyricTextSize
            color = lyricColor
            textAlign = Paint.Align.CENTER
        }
        highlightLyricPaint = Paint().apply {
            strokeWidth = dp2px(6)
            textSize = lyricTextSize * highlightLyricTextRatio
            color = highlightLyricColor
            textAlign = Paint.Align.CENTER
        }

        isClickable = true
        setOnTouchListener { _, event ->
            when (event.actionMasked) {
                MotionEvent.ACTION_DOWN -> isTouching = true
                MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> isTouching = false
            }
            gestureDetector.onTouchEvent(event)
        }
    }

    override fun onDraw(canvas: Canvas) {
        val startX = width / 2f
        val startY = paddingTop
        var lineCount = 0
        lyrics?.forEachIndexed { index, (_, lyricLines) ->
            val paint = if (curIndex == index) highlightLyricPaint else lyricPaint
            lyricLines.forEach {
                canvas.drawText(it, startX, startY + lineHeight * lineCount, paint)
                lineCount++
            }
        }
    }

    override fun computeScroll() {
        if (scroller.computeScrollOffset()) {
            scrollTo(scroller.currX, scroller.currY)
            postInvalidate()
        }
    }

    fun reset() {
        lyrics = null
        totalLineCount = 0
        maxScrollHeight = 0
        curIndex = -1
        if (scrollY == 0) {
            invalidate()
        } else {
            scrollTo(0, 0)
        }
    }

    /**
     * 设置歌词列表，并将长歌词换行，转换成多行歌词;
     * Note: post是为了获取width
     */
    fun setLyrics(list: List<Pair<Long, String>>, action: (() -> Unit)? = null) {
        post {
            lyrics = transformLyrics(list)
            maxScrollHeight = max(0, ((lyrics!!.size - 1) * lineHeight).toInt())
            invalidate()
            action?.invoke()
        }
    }

    /**
     * 更新当前播放歌词
     */
    fun updateCurLyricTime(curLyricTime: Long, shouldSmooth: Boolean = true) {
        lyrics?.takeIf { it.isNotEmpty() }?.let {
            val (lastLyricsTime, _) = it.last()
            if (curLyricTime >= lastLyricsTime) {
                if (curIndex != it.size - 1) {
                    curIndex = it.size - 1
                    scrollToLine(totalLineCount - 1, shouldSmooth)
                }
            } else {
                var lineCount = 0
                it.forEachIndexed { index, (lyricTime, lyrics) ->
                    if (curLyricTime < lyricTime) {
                        if (curIndex != index - 1) {
                            curIndex = index - 1
                            scrollToLine(lineCount - 1, shouldSmooth)
                        }
                        return
                    } else {
                        lineCount += lyrics.size
                    }
                }
            }
        }
    }

    fun setOnLyricsClickListener(listener: (View) -> Unit) {
        this.onLyricsClickListener = listener
    }

    fun smoothScrollToNextLine() {
        scroller.startScroll(0, scrollY, 0, lineHeight.toInt(), 800)
        invalidate()
    }

    /**
     * 从空格（' '）处换行，防止英文单词从中间拆开
     */
    private fun transformLyrics(list: List<Pair<Long, String>>): List<Pair<Long, List<String>>> {
        val tempLyrics = mutableListOf<Pair<Long, List<String>>>()
        list.forEach { (lyricTime, lyric) ->
            val newLyric = lyric.trim()
            val lyricWidth = lyricPaint.measureText(newLyric)
            val singleCharWidth = lyricWidth / newLyric.length
            val maxLyricWidth = width - lineHorizontalMargin * 2
            if (lyricWidth > maxLyricWidth) {
                val lineCharCount = (maxLyricWidth / singleCharWidth).toInt()
                val lines = mutableListOf<String>()
                var start = 0
                var end = lineCharCount
                var subStr: String
                var blankIndex: Int
                while (start < newLyric.length) {
                    subStr = if (end - start < lineCharCount) {
                        newLyric.substring(start, end)
                    } else {
                        newLyric.substring(start, end).run {
                            blankIndex = lastIndexOf(' ')
                            if (blankIndex == -1) {
                                this
                            } else {
                                substring(0, blankIndex + 1)
                            }
                        }
                    }
                    start += subStr.length
                    end = min(start + lineCharCount, newLyric.length)
                    lines.add(subStr.trim())
                    totalLineCount++
                }
                tempLyrics.add(lyricTime to lines)
            } else {
                tempLyrics.add(lyricTime to listOf(newLyric))
                totalLineCount++
            }
        }
        return tempLyrics
    }

    private fun scrollToLine(lineIndex: Int, shouldSmooth: Boolean) {
        if (lineIndex < 0) {
            return
        }
        if (!isTouching) {
            if (shouldSmooth) {
                val dy = lineIndex * lineHeight - scrollY
                scroller.startScroll(0, scrollY, 0, dy.toInt(), 800)
                invalidate()
            } else {
                scrollTo(0, (lineIndex * lineHeight).toInt())
            }
        } else {
            invalidate()
        }
    }
}