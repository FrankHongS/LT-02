package com.hon.simplechartview;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathMeasure;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

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

    private int mXAxisStrokeWidth=3;
    private int mDataCircleStrokeWidth=3;

    private int mYAxisTextSize=Util.sp2px(getContext(),18);// Y axis text size
    private int mYAxisTextMargin=Util.dip2px(getContext(),12);

    private int mXAxisCount=3;// horizontal axis count

    private final String[] texts;
    private final float[] textsWidth;

    private float[] mMaxAndMin;// data max and min;

    private int mDataPointRadius=10;

    private int mLongestSize;

    private List<Path> mPathList;
    private List<PathMeasure> mPathMeasureList;

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

        mDataCirclePaint=new Paint();
        mDataCirclePaint.setStyle(Paint.Style.STROKE);
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
    protected void onDraw(Canvas canvas) {

        if(mChartData==null)
            return;

        canvas.drawColor(Color.parseColor("#aaaaaa"));// background color

        // X axis
        float delta=mMaxAndMin[0]-mMaxAndMin[1];
        float unit=delta/(mXAxisCount-1);

        mXAxisPaint.setTextSize(mYAxisTextSize);
        float maxTextWidth=0;
        for(int i=0;i<mXAxisCount;i++){
            float v=mMaxAndMin[1]-mTranslateRatio*unit+unit*i;
            texts[i]=String.valueOf(Math.round(v*100)/100f);
            float textWidth=mXAxisPaint.measureText(texts[i]);
            textsWidth[i]=textWidth;
            if(textWidth>maxTextWidth)
                maxTextWidth=textWidth;
        }

        float originY=getHeight()-2*mYAxisTextSize;
        for(int i=0;i<mXAxisCount;i++){
            float tempY=originY-i*mUnitYDistance;
            canvas.drawText(texts[i],maxTextWidth-textsWidth[i]+mYAxisTextMargin,tempY+mYAxisTextSize/2f,mXAxisPaint);
            canvas.drawLine(maxTextWidth+mYAxisTextMargin*2,tempY,
                    getWidth(),tempY,mXAxisPaint);
        }

        // data
        for(List<Float> l:mChartData){
            for(int j=0;j<l.size();j++){
                canvas.drawCircle(maxTextWidth+mYAxisTextMargin*2+mUnitXDistance*(j+1),
                        originY-(l.get(j)-Float.valueOf(texts[0]))*(mUnitYDistance/unit),
                        mDataPointRadius,
                        mDataCirclePaint);
            }
        }

        for(int k=0;k<mLongestSize;k++){
            canvas.drawText(String.valueOf(k+1),
                    maxTextWidth+mYAxisTextMargin*2+mUnitXDistance*(k+1),
                    getHeight()-mYAxisTextSize,
                    mXAxisPaint);
        }

    }

    public void setChartData(List<List<Float>> chartData){
        this.mChartData=chartData;
        mMaxAndMin=Util.getMaxAndMin(mChartData);

        mPathList=new ArrayList<>();
        mPathMeasureList=new ArrayList<>();

        for(int m=0;m<mChartData.size();m++){
            Path path=mPathList.get(m);
            path.reset();
            List<Float> list=mChartData.get(m);
            path.moveTo(maxTextWidth+mYAxisTextMargin*2+mUnitXDistance,
                    originY-(list.get(0)-Float.valueOf(texts[0]))*(mUnitYDistance/unit));
            for(int n=0;n<list.size()-1;n++){
                path.lineTo(maxTextWidth+mYAxisTextMargin*2+mUnitXDistance*(n+2),
                        originY-(list.get(n+1)-Float.valueOf(texts[0]))*(mUnitYDistance/unit));
            }
        }
    }
}
