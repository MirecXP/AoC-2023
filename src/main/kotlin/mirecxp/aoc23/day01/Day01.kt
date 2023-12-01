package mirecxp.aoc23.day01

import java.io.File
import java.util.*

//https://adventofcode.com/2023/day/1
class Day01(inputPath: String) {

    private val calibrations: List<String> =
        File(inputPath).readLines().map { it.lowercase(Locale.getDefault()) }

    fun solveA() {
        println("Solving day 1A for ${calibrations.size} lines")
        var sum = 0L
        calibrations.forEach { line ->
            val first = line.first { it.isDigit() }.digitToInt()
            val last = line.last { it.isDigit() }.digitToInt()
            val cal = 10 * first + last
            println("calibration value is $cal")
            sum += cal
        }
        println("Sum is $sum")
    }

    //eightwo = 82
    fun solveB() {
        println("Solving day 1B for ${calibrations.size} lines")
        var sum = 0L
        calibrations.forEach { line ->
            val first = firstTextOrNumericDigit(line)
            val last = lastTextOrNumericDigit(line)
            val cal = 10 * first + last
            println("calibration value is $cal")
            sum += cal
        }
        println("sum is $sum")
    }

    private fun firstTextOrNumericDigit(line: String): Int {
        var digit: Int = 0
        var index: Int = Int.MAX_VALUE
        val first = line.first { it.isDigit() }.digitToInt()
        digit = first
        index = line.indexOfFirst { it.isDigit() }
        stringDigits.forEach { (sDigit: String, nDigit: Int) ->
            val i = line.indexOf(sDigit)
            if (i in 0 until index) {
                index = i
                digit = nDigit
            }
        }
        return digit
    }

    private fun lastTextOrNumericDigit(line: String): Int {
        var digit: Int = 0
        var index: Int = -1
        val last = line.last { it.isDigit() }.digitToInt()
        digit = last
        index = line.indexOfLast { it.isDigit() }
        stringDigits.forEach { (sDigit: String, nDigit: Int) ->
            val i = line.lastIndexOf(sDigit)
            if (i > index) {
                index = i
                digit = nDigit
            }
        }
        return digit
    }

    private val stringDigits: List<Pair<String, Int>> = listOf(
        "zero" to 0,
        "one" to 1,
        "two" to 2,
        "three" to 3,
        "four" to 4,
        "five" to 5,
        "six" to 6,
        "seven" to 7,
        "eight" to 8,
        "nine" to 9
    )
}

fun main(args: Array<String>) {
    val problem = Day01("/Users/miro/projects/AoC23/input/day01a.txt")
    problem.solveA()
    problem.solveB()
}