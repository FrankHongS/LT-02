package com.hon.simplechartview;

import java.util.List;

/**
 * Created by Frank_Hon on 4/18/2019.
 * E-mail: v-shhong@microsoft.com
 */
public class ChartEntity {

    private List<Float> dataList;

    private int lineColor;

    private int dotColor;

    public ChartEntity(List<Float> dataList, int lineColor, int dotColor) {
        this.dataList = dataList;
        this.lineColor = lineColor;
        this.dotColor = dotColor;
    }

    public List<Float> getDataList() {
        return dataList;
    }

    public int getLineColor() {
        return lineColor;
    }

    public int getDotColor() {
        return dotColor;
    }
}
