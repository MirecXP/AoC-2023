package mirecxp.aoc23.day08

import mirecxp.aoc23.readInput

//https://adventofcode.com/2023/day/8
class Day08(private val inputPath: String) {

    private var lines: List<String> = readInput(inputPath).toMutableList()

    val keys = lines[0]
    var keyIndex = 0

    fun getNextKey(): Char {
        val key = keys[keyIndex]
        keyIndex++
        if (keyIndex > keys.length - 1) {
            keyIndex = 0
        }
        return key
    }

    fun solve(part2: Boolean): String {
        println("Solving day 8 for ${lines.size} line [$inputPath]")

        val navMap = mutableMapOf<String, Pair<String, String>>()

        lines.drop(2).forEach { line ->
            line.split("=").apply {
                val key = first().trim()
                get(1).trim().substring(1, 9).split(", ").apply {
                    navMap[key] = get(0) to get(1)
                }
            }
        }

        var countMoves = 0L

        if (!part2) {

            var actualKey = "AAA"
            while (actualKey != "ZZZ") {
                countMoves++
                var actualNav = navMap[actualKey]
                if (actualNav != null) {
                    actualKey = if (getNextKey() == 'L') {
                        actualNav.first
                    } else {
                        actualNav.second
                    }
                } else {
                    println("Error: No nav for $actualKey")
                }
            }

        } else {

            var allDirsCounts = mutableListOf<Long>()

            navMap.keys.filter { it.endsWith("A") }.forEach { key ->
                var actualKey = key
                countMoves = 0
                while (!actualKey.endsWith("Z")) {
                    countMoves++
                    var actualNav = navMap[actualKey]
                    if (actualNav != null) {
                        actualKey = if (getNextKey() == 'L') {
                            actualNav.first
                        } else {
                            actualNav.second
                        }
                    } else {
                        println("Error: No nav for $actualKey")
                    }
                }
                println(countMoves)
                allDirsCounts.add(countMoves)
            }
            countMoves = lcmForList(allDirsCounts)
        }

        val result = countMoves

        val solution = "$result"
        println(solution)
        return solution
    }

}

fun main(args: Array<String>) {
    val testProblem = Day08("test/day08t")
    check(testProblem.solve(part2 = false) == "6")
    check(testProblem.solve(part2 = true) == "6")

    val problem = Day08("real/day08a")
    problem.solve(part2 = false)
    problem.solve(part2 = true)
}

fun lcm(a: Long, b: Long): Long {
    val larger = if (a > b) a else b
    val worst = a * b
    var lcm = larger
    while (lcm <= worst) {
        if (lcm % a == 0L && lcm % b == 0L) {
            return lcm
        }
        lcm += larger
    }
    return worst
}

fun lcmForList(numList: List<Long>): Long {
    var result = numList[0]
    for (i in 1 until numList.size) {
        result = lcm(result, numList[i])
    }
    return result
}