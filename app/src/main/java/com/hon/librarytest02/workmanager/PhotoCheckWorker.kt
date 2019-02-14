package com.hon.librarytest02.workmanager

import android.content.Context
import android.util.Log
import androidx.work.Worker
import androidx.work.WorkerParameters

/**
 * Created by Frank_Hon on 2/12/2019.
 * E-mail: v-shhong@microsoft.com
 */
class PhotoCheckWorker(context:Context,parameters: WorkerParameters):Worker(context,parameters) {

    override fun doWork(): Result {
//        Thread.sleep(2000)
        Log.d("PhotoCheckWorker", "========================================")
        return Result.success()
    }
}