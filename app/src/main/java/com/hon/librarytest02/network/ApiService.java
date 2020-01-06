package com.hon.librarytest02.network;

import com.hon.librarytest02.vo.Stock;

import io.reactivex.Observable;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by Frank_Hon on 4/28/2019.
 * E-mail: v-shhong@microsoft.com
 */
public interface ApiService {

    @GET("hs")
    Observable<Stock> getStockInfo(@Query("gid") String stockId, @Query("key") String apiKey);

}
