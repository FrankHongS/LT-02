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
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.animation.DecelerateInterpolator;
import android.widget.OverScroller;
import android.widget.Scroller;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.Nullable;

/**
 * Created by Frank Hon on 2019/4/14 4:30 PM.
 * E-mail: frank_hon@foxmail.com
 */
public class ChartView01 extends View {

    private static final int DEFAULT_STROKE_WIDTH=3;

    private List<ChartEntity> mChartEntityList;

    private List<List<Float>> mChartData=new ArrayList<>();
    private List<String> mLineColorList=new ArrayList<>();
    private List<String> mDotColorList=new ArrayList<>();
    private List<String> mXAxisLabelList=new ArrayList<>();

    private int mUnitXDistance;// X axis unit distance
    private int mUnitYDistance;// Y axis unit distance
    private float mTranslateRatio;// 折线图向上平移的距离占单位Y距离的百分比


    private Paint mXAxisPaint;
    private Paint mYLabelPaint;
    private Paint mXLabelPaint;
    private List<Paint> mDataLinePaintList=new ArrayList<>();
    private List<Paint> mDotPaintList=new ArrayList<>();

    private int mXAxisStrokeWidth=3;
    private int mDataLineStrokeWidth=3;

    private int mXLabelTextSize=Util.sp2px(getContext(),18);
    private int mYLabelTextSize=Util.sp2px(getContext(),18);// Y axis text size
    private int mYAxisTextMargin=Util.dip2px(getContext(),12);

    private int mXAxisCount=3;// horizontal axis count

    private final String[] mYLabelArray;
    private final float[] mYLabelWidthArray;

    private float[] mMaxAndMin;// data max and min;

    private int mDataPointRadius=10;

    private int mLongestSize;
    private int mMaxTextWidth;
    private float mPerDataToPx;
    private float mOriginY;// origin point Y value

    private List<Path> mPathList;
    private List<PathMeasure> mPathMeasureList;

    private List<Path> mXAxisPathList;

    private ValueAnimator mValueAnimator;
    private long duration = 2500;

    private int mContentWidth;

    private Scroller mScroller;

    private int mMarginLeft;// 绘制第一个点距离左边界距离

    public ChartView01(Context context) {
        this(context,null);
    }

    public ChartView01(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs,0);
    }

    public ChartView01(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        setClickable(true);

        initAttrs(attrs);
        initPaints();

        mYLabelArray=new String[mXAxisCount];
        mYLabelWidthArray=new float[mXAxisCount];

        mScroller=new Scroller(getContext());


        final GestureDetector detector=new GestureDetector(getContext(),new GestureDetector.SimpleOnGestureListener(){
            @Override
            public boolean onDoubleTap(MotionEvent e) {
                Toast.makeText(getContext(),"onDoubleTap",Toast.LENGTH_SHORT).show();
                return super.onDoubleTap(e);
            }

            @Override
            public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
                Log.d("ChartView1", "onFling: "+velocityX);
                mScroller.fling(getScrollX(),0,-(int)velocityX,0,
                        0, mContentWidth-getWidth(),
                        0,0);
                return true;
            }

            @Override
            public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
                Log.d("ChartView1", "onScroll: "+distanceX);

                if(Math.abs(distanceX)>=Math.abs(distanceY)){
                    getParent().requestDisallowInterceptTouchEvent(true);
                }else {
                    return false;
                }

                int distance= (int) distanceX;
                if(-distance>getScrollX()){
                    distance=-getScrollX();
                }else if(distance+getScrollX()>mContentWidth-getWidth()){
                    distance=mContentWidth-getWidth()-getScrollX();
                }

                scrollBy(distance,0);
                return true;
            }
        });

        setOnTouchListener(new OnTouchListener() {
            @SuppressLint("ClickableViewAccessibility")
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return detector.onTouchEvent(event);
            }
        });
    }

    private void initAttrs(AttributeSet attrs) {
        final TypedArray a = getContext().obtainStyledAttributes(
                attrs, R.styleable.ChartView);

        mUnitXDistance=a.getDimensionPixelSize(R.styleable.ChartView_x_unit_distance,200);
        mUnitYDistance=a.getDimensionPixelSize(R.styleable.ChartView_y_unit_distance,300);

        mTranslateRatio=a.getFloat(R.styleable.ChartView_vertical_translate,0.4f);

        a.recycle();
    }

    @Override
    public void computeScroll() {
        if(mScroller.computeScrollOffset()){
            scrollTo(mScroller.getCurrX(),mScroller.getCurrY());
            postInvalidate();
        }
    }

    private void initPaints(){
        mXAxisPaint = new Paint();
        mXAxisPaint.setStyle(Paint.Style.STROKE);
        mXAxisPaint.setAntiAlias(true);
        mXAxisPaint.setColor(Color.parseColor("#666666"));
        mXAxisPaint.setDither(true);
        mXAxisPaint.setStrokeWidth(mXAxisStrokeWidth);
        mXAxisPaint.setPathEffect(new DashPathEffect(new float[]{10,10},10));

        mXLabelPaint=generateTextPaint("#555555",mXLabelTextSize);
        mYLabelPaint=generateTextPaint("#555555",mYLabelTextSize);

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        mLongestSize=Util.getLongestSize(mChartData);
        int width=MeasureSpec.getSize(widthMeasureSpec);

        mContentWidth=mUnitXDistance*mLongestSize+mMarginLeft;
        setMeasuredDimension(width,
                (int) (mUnitYDistance*2+mUnitYDistance*mTranslateRatio+3*mXLabelTextSize+3*mDataPointRadius));
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        mOriginY=getHeight()-2*mXLabelTextSize;//在这里getHeight()可以获取到值，在initData()中不行 ? todo

        initPath();

        startAnimation();
    }

    @Override
    protected void onDraw(Canvas canvas) {

        if(mChartEntityList==null)
            return;

        // X axis and Y axis label
        for(int i=0;i<mXAxisCount;i++){
            float tempY=mOriginY-i*mUnitYDistance;
            canvas.drawText(mYLabelArray[i],mMaxTextWidth-mYLabelWidthArray[i]+mYAxisTextMargin,tempY+mYLabelTextSize/2f,
                    mYLabelPaint);
            canvas.drawPath(mXAxisPathList.get(i),mXAxisPaint);
        }

        // data path
        for(int j=0;j<mPathList.size();j++){
            Path path=mPathList.get(j);
            canvas.drawPath(path, mDataLinePaintList.get(j));
        }

        // data point
        for(int i=0;i<mChartData.size();i++){

            List<Float> dataList=mChartData.get(i);
            Paint dotPaint=mDotPaintList.get(i);

            for(int j=0;j<dataList.size();j++){
                canvas.drawCircle(mMarginLeft+mUnitXDistance*j,
                        mOriginY-(dataList.get(j)-Float.valueOf(mYLabelArray[0]))*mPerDataToPx,
                        mDataPointRadius,
                        dotPaint);
            }
        }

        // X axis label
        for(int k=0;k<mLongestSize;k++){
            String text=k<mXAxisLabelList.size()?mXAxisLabelList.get(k):"";
            float tempTextWidth=mXLabelPaint.measureText(text);
            canvas.drawText(text,
                    mMarginLeft+mUnitXDistance*k-tempTextWidth/2,
                    getHeight()-mXLabelTextSize,
                    mXLabelPaint);
        }

    }

    public void setChartData(List<ChartEntity> chartEntityList,List<String> labelList){
        this.mChartEntityList=chartEntityList;
        this.mXAxisLabelList=labelList;

        initData();

        initDataPaints();
    }

    private void initData(){

        for(ChartEntity entity:mChartEntityList){
            mChartData.add(entity.getDataList());
            mLineColorList.add(entity.getLineColor());
            mDotColorList.add(entity.getDotColor());
        }

        mMaxAndMin=Util.getMaxAndMin(mChartData);

        float delta=mMaxAndMin[0]-mMaxAndMin[1];
        float unit=delta/(mXAxisCount-1);

        float maxTextWidth=0;
        for(int i=0;i<mXAxisCount;i++){
            float v=mMaxAndMin[1]-mTranslateRatio*unit+unit*i;
            mYLabelArray[i]=String.valueOf(Math.round(v));
            float textWidth=mYLabelPaint.measureText(mYLabelArray[i]);
            mYLabelWidthArray[i]=textWidth;
            if(textWidth>maxTextWidth)
                maxTextWidth=textWidth;
        }

        mMaxTextWidth= (int) (maxTextWidth+0.5f);
        mMarginLeft=mMaxTextWidth+mYAxisTextMargin*4;

        mPerDataToPx=mUnitYDistance/unit;
    }

    private void initDataPaints(){
        for(String colorStr:mLineColorList){
            Paint dataLinePaint;
            if(!TextUtils.isEmpty(colorStr))
                dataLinePaint=generatePaint(colorStr,mDataLineStrokeWidth, Paint.Style.STROKE);
            else
                dataLinePaint=generatePaint("#0000ff",mDataLineStrokeWidth,Paint.Style.STROKE);

            mDataLinePaintList.add(dataLinePaint);
        }

        for(String dotColor:mDotColorList){
            Paint dotPaint;
            if (!TextUtils.isEmpty(dotColor))
                dotPaint=generatePaint(dotColor,DEFAULT_STROKE_WIDTH, Paint.Style.FILL);
            else
                dotPaint=generatePaint("#000000",DEFAULT_STROKE_WIDTH, Paint.Style.FILL);
            mDotPaintList.add(dotPaint);
        }
    }

    private void initPath(){
        mPathList=new ArrayList<>();
        mPathMeasureList=new ArrayList<>();

        for(List<Float> list:mChartData){
            Path path=new Path();
            path.moveTo(mMarginLeft,
                    mOriginY-(list.get(0)-Float.valueOf(mYLabelArray[0]))*mPerDataToPx);
            for(int n=0;n<list.size()-1;n++){
                path.lineTo(mMarginLeft+mUnitXDistance*(n+1),
                        mOriginY-(list.get(n+1)-Float.valueOf(mYLabelArray[0]))*mPerDataToPx);
            }

            PathMeasure pathMeasure=new PathMeasure(path,false);

            mPathList.add(path);
            mPathMeasureList.add(pathMeasure);
        }

        mXAxisPathList=new ArrayList<>();

        for(int i=0;i<mXAxisCount;i++){
            Path path=new Path();
            float tempY=mOriginY-i*mUnitYDistance;
            path.moveTo(mMaxTextWidth+mYAxisTextMargin*2,tempY);
            path.lineTo(mContentWidth,tempY);

            mXAxisPathList.add(path);
        }
    }

    private void startAnimation() {

        if (mValueAnimator != null) {
            mValueAnimator.cancel();
        }

        mValueAnimator = ValueAnimator.ofFloat(1, 0);
        mValueAnimator.setDuration(duration);
        mValueAnimator.setInterpolator(new DecelerateInterpolator());
        mValueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {

                float fraction = (Float) animation.getAnimatedValue();

                for(int i=0;i<mPathMeasureList.size();i++){

                    PathMeasure pathMeasure=mPathMeasureList.get(i);

                    float length=pathMeasure.getLength();
                    DashPathEffect effect=new DashPathEffect(
                            new float[]{length,length},
                            fraction*length
                    );
                    mDataLinePaintList.get(i).setPathEffect(effect);
                }

                invalidate();
            }
        });

        mValueAnimator.start();
    }

    private Paint generatePaint(String colorStr,int strokeWidth,Paint.Style style){

        Paint paint=new Paint();

        paint.setStyle(style);
        paint.setAntiAlias(true);
        paint.setColor(Color.parseColor(colorStr));
        paint.setDither(true);
        paint.setStrokeWidth(strokeWidth);

        return paint;
    }

    private Paint generateTextPaint(String colorStr,int textSize){
        Paint paint = new Paint();
        paint.setStyle(Paint.Style.FILL);
        paint.setAntiAlias(true);
        paint.setColor(Color.parseColor(colorStr));
        paint.setDither(true);
        paint.setStrokeWidth(DEFAULT_STROKE_WIDTH);
        paint.setTextSize(textSize);

        return paint;
    }
}
