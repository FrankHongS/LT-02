package com.hon.simplechartview;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PathEffect;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by Frank Hon on 2019/4/14 7:10 PM.
 * E-mail: frank_hon@foxmail.com
 */
public class Util {

    public static float[] getMaxAndMin(List<List<Float>> dataList){

        List<Float> mergedList=new ArrayList<>();

        for(List<Float> list:dataList){
            mergedList.addAll(list);
        }

        return new float[]{Collections.max(mergedList),Collections.min(mergedList)};
    }

    public static int getLongestSize(List<List<Float>> dataList){

        int longest=0;

        for(List<Float> list:dataList){
            int size=list.size();
            if(size>longest)
                longest=size;
        }

        return longest;
    }

    public static Paint generatePaint(int color, int strokeWidth, Paint.Style style,PathEffect effect){

        Paint paint=new Paint();

        paint.setStyle(style);
        paint.setAntiAlias(true);
        paint.setColor(color);
        paint.setDither(true);
        paint.setStrokeWidth(strokeWidth);

        if (effect!=null){
            paint.setPathEffect(effect);
        }

        return paint;
    }

    public static Paint generatePaint(int color, int strokeWidth, Paint.Style style){
        return generatePaint(color,strokeWidth,style,null);
    }

    public static Paint generateTextPaint(int color,int textSize,int strokeWidth){
        Paint paint = new Paint();
        paint.setStyle(Paint.Style.FILL);
        paint.setAntiAlias(true);
        paint.setColor(color);
        paint.setDither(true);
        paint.setStrokeWidth(strokeWidth);
        paint.setTextSize(textSize);

        return paint;
    }

}
