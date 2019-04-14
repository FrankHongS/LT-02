package com.hon.simplechartview;

import android.content.Context;

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

    public static int getLongestSize(List<List<Float>> dataLiist){

        int longest=0;

        for(List<Float> list:dataLiist){
            int size=list.size();
            if(size>longest)
                longest=size;
        }

        return longest;
    }

    public static int dip2px(Context context, float dipValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dipValue * scale + 0.5f);
    }

    public static int sp2px(Context context, float spValue) {
        final float scale = context.getResources().getDisplayMetrics().scaledDensity;
        return (int) (spValue * scale + 0.5f);
    }

}
