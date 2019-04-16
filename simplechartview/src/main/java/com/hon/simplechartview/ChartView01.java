package com.hon.simplechartview;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathMeasure;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.animation.DecelerateInterpolator;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.Nullable;

/**
 * Created by Frank Hon on 2019/4/14 4:30 PM.
 * E-mail: frank_hon@foxmail.com
 */
public class ChartView01 extends View {

    private List<List<Float>> mChartData=null;

    private int mUnitYDistance=Util.dip2px(getContext(),100);// Y axis unit distance
    private float mTranslateRatio=0.3f;

    private int mUnitXDistance=Util.dip2px(getContext(),50);// X axis unit distance

    private Paint mXAxisPaint;
    private Paint mDataCirclePaint;
    private List<Paint> mDataLinePaintList;

    private int mXAxisStrokeWidth=3;
    private int mDataCircleStrokeWidth=3;
    private int mDataLineStrokeWidth=3;

    private int mYAxisTextSize=Util.sp2px(getContext(),18);// Y axis text size
    private int mYAxisTextMargin=Util.dip2px(getContext(),12);

    private int mXAxisCount=3;// horizontal axis count

    private final String[] texts;
    private final float[] textsWidth;

    private float[] mMaxAndMin;// data max and min;

    private int mDataPointRadius=10;

    private int mLongestSize;
    private float mMaxTextWidth;
    private float mPerDataToPx;
    private float mOriginY;// origin point Y value

    private List<Path> mPathList;
    private List<PathMeasure> mPathMeasureList;

    private ValueAnimator mValueAnimator;
    private long duration = 2500;

    public ChartView01(Context context) {
        this(context,null);
    }

    public ChartView01(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs,0);
    }

    public ChartView01(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        initPaints();

        texts=new String[mXAxisCount];
        textsWidth=new float[mXAxisCount];
    }

    private void initPaints(){
        mXAxisPaint = new Paint();
        mXAxisPaint.setStyle(Paint.Style.STROKE);
        mXAxisPaint.setAntiAlias(true);
        mXAxisPaint.setColor(Color.BLACK);
        mXAxisPaint.setDither(true);
        mXAxisPaint.setStrokeWidth(mXAxisStrokeWidth);
        mXAxisPaint.setTextSize(mYAxisTextSize);

        mDataCirclePaint=new Paint();
        mDataCirclePaint.setStyle(Paint.Style.FILL_AND_STROKE);
        mDataCirclePaint.setAntiAlias(true);
        mDataCirclePaint.setColor(Color.WHITE);
        mDataCirclePaint.setDither(true);
        mDataCirclePaint.setStrokeWidth(mDataCircleStrokeWidth);

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        mLongestSize=Util.getLongestSize(mChartData);
        setMeasuredDimension(mUnitXDistance*mLongestSize,
                (int) (mUnitYDistance*2+mUnitYDistance*mTranslateRatio+3*mYAxisTextSize+3*mDataPointRadius));
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        mOriginY=getHeight()-2*mYAxisTextSize;//在这里getHeight()可以获取到值，在initData()中不行 ? todo

        initPath();

        startAnimation();
    }

    @Override
    protected void onDraw(Canvas canvas) {

        if(mChartData==null)
            return;

        canvas.drawColor(Color.parseColor("#aaaaaa"));// background color

        // X axis and Y axis value
        for(int i=0;i<mXAxisCount;i++){
            float tempY=mOriginY-i*mUnitYDistance;
            canvas.drawText(texts[i],mMaxTextWidth-textsWidth[i]+mYAxisTextMargin,tempY+mYAxisTextSize/2f,mXAxisPaint);
            canvas.drawLine(mMaxTextWidth+mYAxisTextMargin*2,tempY,
                    getWidth(),tempY,mXAxisPaint);
        }

        // data path
        for(int j=0;j<mPathList.size();j++){
            Path path=mPathList.get(j);
            canvas.drawPath(path, mDataLinePaintList.get(j));
        }

        // data point
        for(List<Float> l:mChartData){
            for(int j=0;j<l.size();j++){
                canvas.drawCircle(mMaxTextWidth+mYAxisTextMargin*2+mUnitXDistance*(j+1),
                        mOriginY-(l.get(j)-Float.valueOf(texts[0]))*mPerDataToPx,
                        mDataPointRadius,
                        mDataCirclePaint);
            }
        }

        // X axis value
        for(int k=0;k<mLongestSize;k++){
            canvas.drawText(String.valueOf(k+1),
                    mMaxTextWidth+mYAxisTextMargin*2+mUnitXDistance*(k+1),
                    getHeight()-mYAxisTextSize,
                    mXAxisPaint);
        }


    }

    public void setChartData(List<List<Float>> chartData){
        this.mChartData=chartData;

        initData();

        initDataLinePaints();
    }

    private void initData(){
        mMaxAndMin=Util.getMaxAndMin(mChartData);

        float delta=mMaxAndMin[0]-mMaxAndMin[1];
        float unit=delta/(mXAxisCount-1);

        float maxTextWidth=0;
        for(int i=0;i<mXAxisCount;i++){
            float v=mMaxAndMin[1]-mTranslateRatio*unit+unit*i;
            texts[i]=String.valueOf(Math.round(v*100)/100f);
            float textWidth=mXAxisPaint.measureText(texts[i]);
            textsWidth[i]=textWidth;
            if(textWidth>maxTextWidth)
                maxTextWidth=textWidth;
        }

        mMaxTextWidth=maxTextWidth;
        mPerDataToPx=mUnitYDistance/unit;
    }

    private void initDataLinePaints(){
        mDataLinePaintList=new ArrayList<>();
        for(List<Float> list:mChartData){
            Paint dataLinePaint=new Paint();
            dataLinePaint.setStyle(Paint.Style.STROKE);
            dataLinePaint.setAntiAlias(true);
            dataLinePaint.setColor(Color.BLUE);
            dataLinePaint.setDither(true);
            dataLinePaint.setStrokeWidth(mDataLineStrokeWidth);

            mDataLinePaintList.add(dataLinePaint);
        }
    }

    private void initPath(){
        mPathList=new ArrayList<>();
        mPathMeasureList=new ArrayList<>();

        for(int m=0;m<mChartData.size();m++){
            Path path=new Path();
            path.reset();
            List<Float> list=mChartData.get(m);
            path.moveTo(mMaxTextWidth+mYAxisTextMargin*2+mUnitXDistance,
                    mOriginY-(list.get(0)-Float.valueOf(texts[0]))*mPerDataToPx);
            for(int n=0;n<list.size()-1;n++){
                path.lineTo(mMaxTextWidth+mYAxisTextMargin*2+mUnitXDistance*(n+2),
                        mOriginY-(list.get(n+1)-Float.valueOf(texts[0]))*mPerDataToPx);
            }

            PathMeasure pathMeasure=new PathMeasure(path,false);

            mPathList.add(path);
            mPathMeasureList.add(pathMeasure);
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
}
