package com.hon.librarytest02.workmanager

import android.content.Context
import android.util.Log
import androidx.work.Worker
import androidx.work.WorkerParameters

/**
 * Created by Frank_Hon on 2/12/2019.
 * E-mail: v-shhong@microsoft.com
 */
class CompressWorker(val context: Context, params:WorkerParameters) :Worker(context,params){

    override fun doWork(): Result {
        myCompress()
        return Result.success();
    }

    private fun myCompress() {
        Thread.sleep(5000)
    }
}