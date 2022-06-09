package com.hon.librarytest02.coroutine

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.hon.librarytest02.R
import kotlinx.android.synthetic.*
import kotlinx.android.synthetic.main.activity_async.*
import kotlinx.coroutines.*

/**
 * Created by Frank Hon on 2020/8/23 8:25 PM.
 * E-mail: frank_hon@foxmail.com
 */
class AsyncActivity : AppCompatActivity() {

    private val TAG = "AsyncActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_async)
        clearFindViewByIdCache()
        Log.d(TAG, "主线程id：${Thread.currentThread().id}")
        btn_hello.setOnClickListener {
            Toast.makeText(this, "hello", Toast.LENGTH_SHORT).show()
//            lifecycleScope.launch {
//                hello()
//            }
//            block()
//            val job = GlobalScope.launch {
//                Log.e(TAG, "协程执行结束 -1- 线程id：${Thread.currentThread().id}")
//                delay(8000)
//                Log.e(TAG, "协程执行结束 -2- 线程id：${Thread.currentThread().id}")
//            }
//            tv_hello.text = "done"
            composeAsyncTask()
        }
        btn_test.setOnClickListener {
            Toast.makeText(this, "test...", Toast.LENGTH_SHORT).show()
        }
    }

    fun block() = runBlocking {
        repeat(8) {
            Log.d(TAG, "协程执行$it 线程id：${Thread.currentThread().id}")
            delay(1000)
        }
    }

    suspend fun hello() {
        Log.e(TAG, "协程执行结束 -1- 线程id：${Thread.currentThread().id}")
        val user = fetchUser()
        Log.e(TAG, "协程执行结束 -3- 线程id：${Thread.currentThread().id}")
        tv_hello.text = user
    }

    suspend fun fetchUser(): String {
        return withContext(Dispatchers.IO) {
            Log.e(TAG, "协程执行结束 -2- 线程id：${Thread.currentThread().id}")
            delay(10 * 1000)
            "Hello Kotlin Coroutine"
        }
    }

    private fun composeAsyncTask() {
        lifecycleScope.launch {
            Log.e(TAG, "协程执行结束 -1- 线程id：${Thread.currentThread().id}")
            val deferreds = listOf(
                    async {
                        Log.e(TAG, "协程执行结束 -2- 线程id：${Thread.currentThread().id}")
                        delay(10 * 1000)
//                        Thread.sleep(8*1000)
                        Log.e(TAG, "协程执行结束 -3- 线程id：${Thread.currentThread().id}")
                        "hello"
                    },
                    async {
                        Log.e(TAG, "协程执行结束 -4- 线程id：${Thread.currentThread().id}")
                        delay(8 * 1000)// no blocking
//                        Thread.sleep(10*1000)//block thread
                        Log.e(TAG, "协程执行结束 -5- 线程id：${Thread.currentThread().id}")
                        "world !"
                    }
            )
            Log.e(TAG, "协程执行结束 -6- 线程id：${Thread.currentThread().id}")
            val results = deferreds.awaitAll()
            Log.d(TAG, "composeAsyncTask: $results")
        }
    }
}