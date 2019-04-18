package com.hon.simplechartview;

import java.util.List;

/**
 * Created by Frank_Hon on 4/18/2019.
 * E-mail: v-shhong@microsoft.com
 */
public class ChartEntity {

    private List<Float> dataList;

    private String lineColor;

    private String dotColor;

    public ChartEntity(List<Float> dataList, String lineColor, String dotColor) {
        this.dataList = dataList;
        this.lineColor = lineColor;
        this.dotColor = dotColor;
    }

    public List<Float> getDataList() {
        return dataList;
    }

    public String getLineColor() {
        return lineColor;
    }

    public String getDotColor() {
        return dotColor;
    }
}
