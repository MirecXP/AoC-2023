package mirecxp.aoc23.day06

import mirecxp.aoc23.day04.toNumList
import mirecxp.aoc23.readInput
import java.util.*

//https://adventofcode.com/2023/day/6
class Day06(private val inputPath: String) {

    private var lines: List<String> = readInput(inputPath).toMutableList()

    fun solve(part2: Boolean): String {
        println("Solving day 6 for ${lines.size} lines [$inputPath]")

        if (part2) {
            lines = lines.map { it.replace(" ", "") }
        }

        val times = lines[0].substringAfter("Time:").trim().toNumList().map { it.toInt() }
        val distances = lines[1].substringAfter("Distance:").trim().toNumList()

        var result = 1L

        times.forEachIndexed { index, time ->
            val distance = distances[index]
            result *= numOfWays(time.toLong(), distance)
        }

        val solution = "$result"
        println(solution)
        return solution
    }

    fun numOfWays(time: Long, record: Long): Long {
        var count = 0L
        for (t in 0..time) {
            if (t * (time - t) > record) count++
        }
        return count
    }
}

fun main(args: Array<String>) {
    val testProblem = Day06("test/day06t")
    check(testProblem.solve(part2 = false) == "288")
    check(testProblem.solve(part2 = true) == "71503")

    val problem = Day06("real/day06a")
    problem.solve(part2 = false)
    problem.solve(part2 = true)
}
