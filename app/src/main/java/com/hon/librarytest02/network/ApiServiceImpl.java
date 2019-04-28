package com.hon.librarytest02.network;

import com.hon.librarytest02.util.Constants;
import com.hon.librarytest02.vo.Stock;

import io.reactivex.Observable;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by Frank_Hon on 4/28/2019.
 * E-mail: v-shhong@microsoft.com
 */
public class ApiServiceImpl {

    private static ApiServiceImpl sInstance;

    private ApiService mApiService;

    private ApiServiceImpl(){
        Retrofit retrofit=new Retrofit.Builder()
                .baseUrl(Constants.JUHE_STOCK_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build();

        mApiService=retrofit.create(ApiService.class);
    }

    public static ApiServiceImpl getInstance(){

        if(sInstance==null){
            synchronized (ApiServiceImpl.class){
                if(sInstance==null){
                    sInstance=new ApiServiceImpl();
                }
            }
        }

        return sInstance;
    }

    public Observable<Stock> getStockInfo(String stockId){
        return mApiService.getStockInfo(stockId,Constants.JUHE_STOCK_KEY);
    }

}
