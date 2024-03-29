package com.frankhon.customview.lyrics

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View
import android.widget.Scroller
import com.frankhon.customview.R
import com.frankhon.customview.util.dp
import java.lang.Integer.min
import kotlin.math.max

/**
 * 歌词View，支持上下滚动，上下边缘支持渐出和渐入
 *
 * Created by Frank Hon on 2022/12/1 7:08 下午.
 * E-mail: frank_hon@foxmail.com
 */
@SuppressLint("ClickableViewAccessibility")
class RangeLyricsView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private val lyricTextSize: Float
    private val lineHeightRatio: Float
    private val lineHeight: Float
    private val lineHorizontalPadding: Float

    // @ColorInt
    private val lyricColor: Int
    private val highlightLyricColor: Int

    private var lyrics: List<Pair<Long, List<String>>>? = null
    private var totalLineCount = 0
    private var maxScrollHeight = 0

    // 当前选择的歌词范围
    private var curRange = -1 to -1

    private val normalLyricPaint: Paint
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
                scroller.forceFinished(true)
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
                onLyricsClickListener?.invoke(this@RangeLyricsView)
                return true
            }

        })

    // 是否手动点击View
    private var isTouching = false
    private var onLyricsClickListener: ((View) -> Unit)? = null

    init {
        val typedArray = getContext().obtainStyledAttributes(
            attrs, R.styleable.RangeLyricsView
        )
        lyricTextSize = typedArray.getDimension(R.styleable.RangeLyricsView_rangeTextSize, 20.dp)
        lyricColor = typedArray.getColor(R.styleable.RangeLyricsView_rangeTextColor, Color.BLACK)
        highlightLyricColor =
            typedArray.getColor(R.styleable.RangeLyricsView_rangeHighlightTextColor, Color.BLUE)
        lineHeightRatio =
            typedArray.getFloat(R.styleable.RangeLyricsView_rangeLineHeightRatio, 1.8f)
        lineHorizontalPadding =
            typedArray.getDimension(R.styleable.RangeLyricsView_rangeLineHorizontalPadding, 32.dp)
        typedArray.recycle()

        lineHeight = lyricTextSize * lineHeightRatio
        normalLyricPaint = Paint().apply {
            strokeWidth = 3.dp
            textSize = lyricTextSize
            color = lyricColor
            textAlign = Paint.Align.CENTER
        }
        highlightLyricPaint = Paint().apply {
            strokeWidth = 6.dp
            textSize = lyricTextSize
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
            val isHighlightLyric = index in curRange.first..curRange.second
            lyricLines.forEach {
                val positionY = startY + lineHeight * lineCount
                canvas.drawText(
                    it, startX, positionY,
                    if (isHighlightLyric) highlightLyricPaint else normalLyricPaint
                )
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
        curRange = -1 to -1
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
            maxScrollHeight = max(0, ((totalLineCount - 1) * lineHeight).toInt())
            invalidate()
            action?.invoke()
        }
    }

    fun updateCurLyricRangeStart(startLyricTime: Long) {
        getLineInfoByLyricTime(startLyricTime)?.let { (curIndex, lineIndex) ->
            val (curStartIndex, curEndIndex) = curRange
            if (curStartIndex != curIndex) {
                curRange = curIndex to curEndIndex
                scrollToLine(lineIndex)
            }
        }
    }

    fun updateCurLyricRangeEnd(endLyricTime: Long) {
        getLineInfoByLyricTime(endLyricTime)?.let { (curIndex, lineIndex) ->
            val (curStartIndex, curEndIndex) = curRange
            if (curEndIndex != curIndex) {
                curRange = curStartIndex to curIndex
                scrollToLine(lineIndex)
            }
        }
    }

    private fun getLineInfoByLyricTime(curLyricTime: Long): Pair<Int, Int>? {
        lyrics?.takeIf { it.isNotEmpty() }?.let {
            val (lastLyricsTime, _) = it.last()
            if (curLyricTime >= lastLyricsTime) {
                return it.size - 1 to totalLineCount - 1
            } else {
                var lineCount = 0
                it.forEachIndexed { index, (lyricTime, lyrics) ->
                    if (curLyricTime == lyricTime) {
                        return index to lineCount
                    } else if (curLyricTime < lyricTime) {
                        return index - 1 to lineCount - 1
                    } else {
                        lineCount += lyrics.size
                    }
                }
            }
        }
        return null
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
            val lyricWidth = normalLyricPaint.measureText(newLyric)
            val singleCharWidth = lyricWidth / newLyric.length
            val maxLyricWidth = width - lineHorizontalPadding * 2
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

    private fun scrollToLine(lineIndex: Int) {
        val tempLineIndex = if (lineIndex < 0) 0 else lineIndex
        if (!isTouching) {
            val dy = tempLineIndex * lineHeight - scrollY
            scroller.startScroll(0, scrollY, 0, dy.toInt(), 800)
            invalidate()
        } else {
            invalidate()
        }
    }
}