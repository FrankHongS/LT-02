package com.hon.librarytest02.util;

/**
 * Created by Frank_Hon on 4/28/2019.
 * E-mail: v-shhong@microsoft.com
 */
public interface Constants {

    String JUHE_STOCK_KEY="3272d51138a05d8f3895c6d7381fa0a7";

    String JUHE_STOCK_URL="http://web.juhe.cn:8080/finance/stock/";

    interface StockNotification{
        String STOCK_CHANNEL_ID = "stock";

        String STOCK_WATCH_OUT_CHANNEL_ID = "stock_watch_out";
    }

}
