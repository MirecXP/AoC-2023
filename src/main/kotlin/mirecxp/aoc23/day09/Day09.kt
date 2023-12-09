package mirecxp.aoc23.day09

import mirecxp.aoc23.day04.toNumList
import mirecxp.aoc23.readInput

//https://adventofcode.com/2023/day/9
class Day09(private val inputPath: String) {

    private var lines: List<List<Long>> = readInput(inputPath).map { it.toNumList() }

    fun solve(part2: Boolean): String {
        println("Solving day 9 for ${lines.size} lines [$inputPath]")

        var result = 0L

        lines.forEach { line: List<Long> ->
            var isZero = false
            var derivatives = mutableListOf<List<Long>>()
            var lastDerivative = line
            derivatives.add(line)
            while (!isZero) {
                isZero = true
                val newDerivative = mutableListOf<Long>()
                lastDerivative.forEachIndexed { index, l ->
                    if (index > 0) {
                        val newDif = l - lastDerivative[index - 1]
                        if (newDif != 0L) {
                            isZero = false
                        }
                        newDerivative.add(newDif)
                    }
                }
                derivatives.add(newDerivative)
                lastDerivative = newDerivative
            }
            var newValue = 0L
            derivatives.reversed().forEachIndexed { index, derivative: List<Long> ->
                if (derivatives.size - index < derivatives.size) {
                    newValue = if (part2) {
                        derivative.first() - newValue
                    } else {
                        newValue + derivative.last()
                    }
                }
            }
            result += newValue
        }

        println(result)
        return "$result"
    }

}

fun main(args: Array<String>) {
    val testProblem = Day09("test/day09t")
    check(testProblem.solve(part2 = false) == "114")
    check(testProblem.solve(part2 = true) == "2")

    val problem = Day09("real/day09a")
    problem.solve(part2 = false)
    problem.solve(part2 = true)
}
