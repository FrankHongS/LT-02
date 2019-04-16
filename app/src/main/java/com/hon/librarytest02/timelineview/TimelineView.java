package com.hon.librarytest02.timelineview;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import com.hon.librarytest02.R;

import androidx.annotation.Nullable;

/**
 * Created by Frank_Hon on 3/5/2019.
 * E-mail: v-shhong@microsoft.com
 */
@SuppressWarnings("all")
public class TimelineView extends View {

    private static final String TAG = TimelineView.class.getSimpleName();

    private int mNodeCount=3;
    private int mBigRadius=dp2px(8);
    private float mDistance=dp2px(100);
    private int mStrokeWidth=dp2px(5);
    private int mPaintColor=Color.BLACK;

    private int mSelectedNode=0;
    private int mSelectedPaintColor=Color.GREEN;

    private Paint mPaint;
    private Paint mSelectedPaint;

    private OnClickListener mListener;

    public TimelineView(Context context) {
        this(context,null);
    }

    public TimelineView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs,0);
    }

    public TimelineView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        initAttrs(attrs);

        initPaint();
    }

    private void initAttrs(AttributeSet attrs) {
        final TypedArray a = getContext().obtainStyledAttributes(
                attrs, R.styleable.TimelineView);

        mNodeCount=a.getInteger(R.styleable.TimelineView_nodeCount,mNodeCount);
        mStrokeWidth=a.getDimensionPixelSize(R.styleable.TimelineView_lineWidth,mStrokeWidth);
        mBigRadius=a.getDimensionPixelOffset(R.styleable.TimelineView_circleRadius,dp2px(5.5f))
                +mStrokeWidth/2;
        mPaintColor=a.getColor(R.styleable.TimelineView_paintColor,mPaintColor);

        mSelectedNode=a.getInteger(R.styleable.TimelineView_selectedNode,mSelectedNode);
        mSelectedPaintColor=a.getColor(R.styleable.TimelineView_selectedNodeColor,mSelectedPaintColor);

        a.recycle();
    }


    private void initPaint(){
        mPaint=new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(mStrokeWidth);
        mPaint.setColor(mPaintColor);

        mSelectedPaint=new Paint();
        mSelectedPaint.setAntiAlias(true);
        mSelectedPaint.setColor(mSelectedPaintColor);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int height=2*mBigRadius+mStrokeWidth;

        setMeasuredDimension(MeasureSpec.getSize(widthMeasureSpec),height);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        mDistance=(getWidth()-(mBigRadius*2*mNodeCount+mStrokeWidth))*1.0f/(mNodeCount-1);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        for(int i=0;i<mNodeCount;i++){
            // draw circle
            float radius;
            Paint tempPaint;
            if(i==mSelectedNode){
                radius=mBigRadius+mStrokeWidth/2f;
                tempPaint=mSelectedPaint;
            }else {
                radius=mBigRadius;
                tempPaint=mPaint;
            }
            canvas.drawCircle(mBigRadius+i*(2*mBigRadius+mDistance)+mStrokeWidth/2f,
                    mBigRadius+mStrokeWidth/2f,radius,tempPaint);

            // draw line
            if(i!=mNodeCount-1){
                float startX;
                if(i==mSelectedNode){
                    startX=2*mBigRadius+i*(2*mBigRadius+mDistance)+mStrokeWidth;
                }else{
                    startX=2*mBigRadius+i*(2*mBigRadius+mDistance)+mStrokeWidth/2f;
                }

                canvas.drawLine(startX,
                        mBigRadius+mStrokeWidth/2f,
                        2*mBigRadius+mDistance+i*(2*mBigRadius+mDistance)+mStrokeWidth/2f,
                        mBigRadius+mStrokeWidth/2f,
                        mPaint);
            }
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent event) {

        int width=getWidth();
        int blockCount=(mNodeCount-1)*2;
        float blockLength=width*1.0f/blockCount;

        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN:
                float x=event.getX();
                int selectedBlock=(int)(x/blockLength);
                mSelectedNode=(selectedBlock+1)/2;

                invalidate();

                if(mListener!=null){
                    mListener.onClick(mSelectedNode);
                }

                return true;
            default:
                break;
        }

        return super.onTouchEvent(event);
    }

    public void setOnNodeClickListener(OnClickListener listener){
        this.mListener=listener;
    }

    public int dp2px(float value) {
        final float scale = Resources.getSystem().getDisplayMetrics().densityDpi;
        return (int) (value * (scale / 160) + 0.5f);
    }

    public interface OnClickListener{
        void onClick(int position);
    }
}
