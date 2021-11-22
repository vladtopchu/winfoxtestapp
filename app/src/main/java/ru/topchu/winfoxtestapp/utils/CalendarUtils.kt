package ru.topchu.winfoxtestapp.utils

import java.util.*

fun getToday(): List<Int> {
    lateinit var result: List<Int>
    Calendar.getInstance().apply {
        result = listOf(this.get(Calendar.DAY_OF_MONTH), this.get(Calendar.MONTH), this.get(Calendar.YEAR))
    }
    return result
}

fun makeDateString(day: Int, month: Int, year: Int) = "$day ${month + 1} $year"

fun fromDateString(date: String): List<Int> {
    val arr = date.split(" ").map { it.toInt() }.toMutableList()
    arr[1] = arr[1] - 1
    return arr
}