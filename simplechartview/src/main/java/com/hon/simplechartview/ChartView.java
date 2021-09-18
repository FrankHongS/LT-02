package com.hon.simplechartview;

import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathMeasure;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.Scroller;

import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Frank Hon on 2019/4/14 4:30 PM.
 * E-mail: frank_hon@foxmail.com
 */
public class ChartView extends View {

    private static final int DEFAULT_STROKE_WIDTH = 3;


    private int mUnitXDistance;// X axis unit distance
    private int mUnitYDistance;// Y axis unit distance
    private float mTranslateRatio;// the Y direction translate distance divide the Y unit distance( translate/unit )

    private int mXAxisStrokeWidth;
    private int mDataLineStrokeWidth;

    private int mXLabelTextSize;
    private int mYLabelTextSize;
    private int mUnitLeftPadding;
    private int mXLabelMargin;

    private int mXLabelColor;
    private int mYLabelColor;
    private int mXAxisColor;

    private int mXAxisCount;// horizontal axis count
    private int mDataDotRadius;
    private long mAnimationDuration;

    private Paint mXAxisPaint;
    private Paint mYLabelPaint;
    private Paint mXLabelPaint;

    private List<Paint> mDataLinePaintList = new ArrayList<>();
    private List<Paint> mDotPaintList = new ArrayList<>();

    private List<List<Float>> mChartData = new ArrayList<>();
    private List<Integer> mLineColorList = new ArrayList<>();
    private List<Integer> mDotColorList = new ArrayList<>();

    private List<ChartEntity> mChartEntityList;
    private List<String> mXAxisLabelList;

    private String[] mYLabelArray;
    private float[] mYLabelWidthArray;

    private int mLongestSize;
    private int mMaxTextWidth;
    private float mPerDataToPx;
    private float mOriginY;// origin point Y value

    private List<Path> mPathList;
    private List<PathMeasure> mPathMeasureList;

    private List<Path> mXAxisPathList;

    private ValueAnimator mValueAnimator;

    private int mContentWidth;

    private Scroller mScroller;

    private int mMarginLeft;// the distance between left border with first data dot

    private int mHeight;

    public ChartView(Context context) {
        this(context, null);
    }

    public ChartView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ChartView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        setClickable(true);

        initAttrs(attrs);

        initPaints();

        solveScrollConflict();

    }

    private void initAttrs(AttributeSet attrs) {
        final TypedArray a = getContext().obtainStyledAttributes(
                attrs, R.styleable.ChartView);

        mUnitXDistance = a.getDimensionPixelSize(R.styleable.ChartView_x_unit_distance, 200);
        mUnitYDistance = a.getDimensionPixelSize(R.styleable.ChartView_y_unit_distance, 300);

        mTranslateRatio = a.getFloat(R.styleable.ChartView_vertical_translate, 0.4f);

        mXLabelTextSize = a.getDimensionPixelSize(R.styleable.ChartView_x_label_text_size, 60);
        mYLabelTextSize = a.getDimensionPixelSize(R.styleable.ChartView_y_label_text_size, 60);
        mUnitLeftPadding = a.getDimensionPixelSize(R.styleable.ChartView_unit_left_padding, 50);

        mXAxisStrokeWidth = a.getInteger(R.styleable.ChartView_x_axis_stroke_width, 3);
        mDataLineStrokeWidth = a.getInteger(R.styleable.ChartView_data_line_stroke_width, 4);

        mXAxisCount = a.getInteger(R.styleable.ChartView_x_axis_count, 4);
        mDataDotRadius = a.getInteger(R.styleable.ChartView_data_dot_radius, 15);

        mXLabelColor = a.getColor(R.styleable.ChartView_x_label_text_color, Color.parseColor("#555555"));
        mYLabelColor = a.getColor(R.styleable.ChartView_y_label_text_color, Color.parseColor("#555555"));
        mXAxisColor = a.getColor(R.styleable.ChartView_x_axis_color, Color.parseColor("#666666"));

        mAnimationDuration = (long) (a.getFloat(R.styleable.ChartView_animation_duration, 2.5f) * 1000);

        mXLabelMargin = a.getDimensionPixelSize(R.styleable.ChartView_x_label_margin_top, 28);

        a.recycle();
    }

    private void solveScrollConflict() {

        mScroller = new Scroller(getContext());

        final GestureDetector detector = new GestureDetector(getContext(), new GestureDetector.SimpleOnGestureListener() {

            @Override
            public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
                if (velocityX < 0 && getScrollX() >= mContentWidth - getWidth()) {
                    return false;
                }

                if (velocityX > 0 && getScrollX() <= 0) {
                    return false;
                }

                mScroller.fling(getScrollX(), 0, -(int) velocityX, 0,
                        0, mContentWidth - getWidth(),
                        0, 0);
                return true;
            }

            @Override
            public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {

                if (Math.abs(distanceX) >= Math.abs(distanceY)) {
                    if (distanceX > 0 && getScrollX() >= mContentWidth - getWidth()) {
                        getParent().requestDisallowInterceptTouchEvent(false);
                        return false;
                    }
                    if (distanceX < 0 && getScrollX() <= 0) {
                        getParent().requestDisallowInterceptTouchEvent(false);
                        return false;
                    }

                } else {
                    return false;
                }

                int distance = (int) distanceX;
                if (-distance > getScrollX()) {
                    distance = -getScrollX();
                } else if (distance + getScrollX() > mContentWidth - getWidth()) {
                    distance = mContentWidth - getWidth() - getScrollX();
                }

                scrollBy(distance, 0);
                return true;
            }
        });

        setOnTouchListener(new OnTouchListener() {
            float startX;
            float startY;

            @SuppressLint("ClickableViewAccessibility")
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        startX = event.getX();
                        startY = event.getY();
                        break;
                    case MotionEvent.ACTION_MOVE:
                        float deltaX = event.getX() - startX;
                        float deltaY = event.getY() - startY;
                        getParent().requestDisallowInterceptTouchEvent(Math.abs(deltaX) >= Math.abs(deltaY));
                        startX = event.getX();
                        startY = event.getY();
                        break;

                }
                return detector.onTouchEvent(event);
            }
        });

    }

    @Override
    public void computeScroll() {
        if (mScroller.computeScrollOffset()) {
            scrollTo(mScroller.getCurrX(), mScroller.getCurrY());
            postInvalidate();
        }
    }

    private void initPaints() {
        mXAxisPaint = Util.generatePaint(mXAxisColor, mXAxisStrokeWidth, Paint.Style.STROKE, new DashPathEffect(new float[]{10, 10}, 10));
        mXLabelPaint = Util.generateTextPaint(mXLabelColor, mXLabelTextSize, DEFAULT_STROKE_WIDTH);
        mYLabelPaint = Util.generateTextPaint(mYLabelColor, mYLabelTextSize, DEFAULT_STROKE_WIDTH);

    }

    public void setChartData(List<ChartEntity> chartEntityList, List<String> labelList) {
        this.mChartEntityList = chartEntityList;
        this.mXAxisLabelList = labelList;

        initData();

        mLongestSize = Util.getLongestSize(mChartData);
        mContentWidth = mUnitXDistance * mLongestSize + mMarginLeft;

        mHeight = (int) (mUnitYDistance * (mXAxisCount - 1) + mUnitYDistance * mTranslateRatio + 3 * mXLabelTextSize + 3 * mDataDotRadius + mXLabelMargin);
        mOriginY = mHeight - 2 * mXLabelTextSize - mXLabelMargin;

        initDataPaints();

        initPath();

        startAnimation();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int width = MeasureSpec.getSize(widthMeasureSpec);

        setMeasuredDimension(width, mHeight);

    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        // getHeight()==h!=0

    }

    @Override
    protected void onDraw(Canvas canvas) {

        if (mChartEntityList == null)
            return;

        // X axis and Y axis label
        for (int i = 0; i < mXAxisCount; i++) {
            float tempY = mOriginY - i * mUnitYDistance;
            canvas.drawText(mYLabelArray[i], mMaxTextWidth - mYLabelWidthArray[i] + mUnitLeftPadding, tempY + mYLabelTextSize / 2f,
                    mYLabelPaint);
            canvas.drawPath(mXAxisPathList.get(i), mXAxisPaint);
        }

        // data path
        for (int j = 0; j < mPathList.size(); j++) {
            Path path = mPathList.get(j);
            canvas.drawPath(path, mDataLinePaintList.get(j));
        }

        // data point
        for (int i = 0; i < mChartData.size(); i++) {

            List<Float> dataList = mChartData.get(i);
            Paint dotPaint = mDotPaintList.get(i);

            for (int j = 0; j < dataList.size(); j++) {
                canvas.drawCircle(mMarginLeft + mUnitXDistance * j,
                        mOriginY - (dataList.get(j) - Float.parseFloat(mYLabelArray[0])) * mPerDataToPx,
                        mDataDotRadius,
                        dotPaint);
            }
        }

        // X axis label
        for (int k = 0; k < mLongestSize; k++) {
            String text = k < mXAxisLabelList.size() ? mXAxisLabelList.get(k) : "";
            float tempTextWidth = mXLabelPaint.measureText(text);
            canvas.drawText(text,
                    mMarginLeft + mUnitXDistance * k - tempTextWidth / 2,
                    getHeight() - mXLabelTextSize,
                    mXLabelPaint);
        }

    }

    private void initData() {

        mYLabelArray = new String[mXAxisCount];
        mYLabelWidthArray = new float[mXAxisCount];

        // is necessary
        clearData();

        for (ChartEntity entity : mChartEntityList) {
            mChartData.add(entity.getDataList());
            mLineColorList.add(entity.getLineColor());
            mDotColorList.add(entity.getDotColor());
        }

        float[] maxAndMin = Util.getMaxAndMin(mChartData);

        float delta = maxAndMin[0] - maxAndMin[1];
        float unit = delta / (mXAxisCount - 1);

        float maxTextWidth = 0;
        for (int i = 0; i < mXAxisCount; i++) {
            float v = maxAndMin[1] - mTranslateRatio * unit + unit * i;
            mYLabelArray[i] = String.valueOf(Math.round(v));
            float textWidth = mYLabelPaint.measureText(mYLabelArray[i]);
            mYLabelWidthArray[i] = textWidth;
            if (textWidth > maxTextWidth)
                maxTextWidth = textWidth;
        }

        mMaxTextWidth = (int) (maxTextWidth + 0.5f);
        mMarginLeft = mMaxTextWidth + mUnitLeftPadding * 4;

        mPerDataToPx = mUnitYDistance / unit;
    }

    private void initDataPaints() {
        for (int color : mLineColorList) {
            Paint dataLinePaint;
            if (color != 0)
                dataLinePaint = Util.generatePaint(color, mDataLineStrokeWidth, Paint.Style.STROKE);
            else
                dataLinePaint = Util.generatePaint(Color.parseColor("#0000ff"), mDataLineStrokeWidth, Paint.Style.STROKE);

            mDataLinePaintList.add(dataLinePaint);
        }

        for (int color : mDotColorList) {
            Paint dotPaint;
            if (color != 0)
                dotPaint = Util.generatePaint(color, DEFAULT_STROKE_WIDTH, Paint.Style.FILL);
            else
                dotPaint = Util.generatePaint(Color.parseColor("#000000"), DEFAULT_STROKE_WIDTH, Paint.Style.FILL);

            mDotPaintList.add(dotPaint);
        }
    }

    private void initPath() {
        mPathList = new ArrayList<>();
        mPathMeasureList = new ArrayList<>();

        for (List<Float> list : mChartData) {
            Path path = new Path();
            path.moveTo(mMarginLeft,
                    mOriginY - (list.get(0) - Float.parseFloat(mYLabelArray[0])) * mPerDataToPx);
            for (int n = 0; n < list.size() - 1; n++) {
                path.lineTo(mMarginLeft + mUnitXDistance * (n + 1),
                        mOriginY - (list.get(n + 1) - Float.parseFloat(mYLabelArray[0])) * mPerDataToPx);
            }

            PathMeasure pathMeasure = new PathMeasure(path, false);

            mPathList.add(path);
            mPathMeasureList.add(pathMeasure);
        }

        mXAxisPathList = new ArrayList<>();

        for (int i = 0; i < mXAxisCount; i++) {
            Path path = new Path();
            float tempY = mOriginY - i * mUnitYDistance;
            path.moveTo(mMaxTextWidth + mUnitLeftPadding * 2, tempY);
            path.lineTo(mContentWidth, tempY);

            mXAxisPathList.add(path);
        }
    }

    private void clearData() {
        mChartData.clear();
        mLineColorList.clear();
        mDotColorList.clear();

        mDataLinePaintList.clear();
        mDotPaintList.clear();
    }

    private void startAnimation() {

        if (mValueAnimator != null) {
            mValueAnimator.cancel();
        }

        mValueAnimator = ValueAnimator.ofFloat(1, 0);
        mValueAnimator.setDuration(mAnimationDuration);
        mValueAnimator.setInterpolator(new DecelerateInterpolator());
        mValueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {

                float fraction = (Float) animation.getAnimatedValue();

                for (int i = 0; i < mPathMeasureList.size(); i++) {

                    PathMeasure pathMeasure = mPathMeasureList.get(i);

                    float length = pathMeasure.getLength();
                    DashPathEffect effect = new DashPathEffect(
                            new float[]{length, length},
                            fraction * length
                    );
                    mDataLinePaintList.get(i).setPathEffect(effect);
                }

                invalidate();
            }
        });

        mValueAnimator.start();
    }

}
