package com.hon.librarytest02

import org.junit.Test

import org.junit.Assert.*

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {
    @Test
    fun addition_isCorrect() {
        assertEquals(4, 2 + 2)
    }

    @Test
    // Kotlin
    fun check() {
        val isHighPaid = paidMore(100000)

        val Smithers = Employee("Fred", 120000)
        val Homer = Employee("Homer", 80000)

        println(isHighPaid(Smithers))
        println(isHighPaid(Homer))

    }

    data class Employee(val name: String, val salary: Int)

    fun paidMore(amount: Int): (Employee) -> Boolean {
        return { employee -> employee.salary > amount }
    }

    @Test
    //Kotlin
    fun test() {
        val numbers = listOf(1, 2, 3, 4, 5, 6)
        numbers.map { n -> n * n }
                .filter { n -> n > 10 }
                .forEach { n -> println(n) }
    }

}
