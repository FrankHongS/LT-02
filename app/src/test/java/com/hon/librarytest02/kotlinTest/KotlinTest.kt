package com.hon.librarytest02.kotlinTest

import org.junit.Test

/**
 * Created by shuaihua on 2021/4/30 11:08 AM
 * Email: shuaihua@staff.sina.com.cn
 */
class KotlinTest {

    @Test
    fun test1() {
        print(getA()?.length)
    }

    private fun getA():String?{
        return A().prop1
    }
}