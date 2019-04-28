package com.hon.librarytest02.vo;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by Frank_Hon on 4/28/2019.
 * E-mail: v-shhong@microsoft.com
 */
public class Stock {

    @SerializedName("resultcode")
    public String resultCode;

    @SerializedName("result")
    public List<StockResult> result;

    public static class StockResult{
        @SerializedName("data")
        public StockDetail data;
    }

    public static class StockDetail{

        @SerializedName("name")
        public String name;

        @SerializedName("nowPri")
        public String nowPrice;

        @SerializedName("increPer")
        public String increasePer;

        @SerializedName("increase")
        public String increase;
    }

}
