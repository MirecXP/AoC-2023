package mirecxp.aoc23.day03

import java.io.File

//https://adventofcode.com/2023/day/3
class Day03(inputPath: String) {

    private val lines: List<String> = File(inputPath).readLines()

    data class Coord(val r: Int, val c: Int)

    data class Gear(val coord: Coord, val parts: MutableList<Long>)

    fun solve() {
        println("Solving day 3 for ${lines.size} lines")
        var sum = 0L
        var gears = mutableMapOf<Coord, Gear>()

        lines.forEachIndexed { row: Int, line: String ->
            var partNumberStr = ""
            var adjacentCoords = mutableListOf<Coord>()
            line.forEachIndexed { col: Int, c: Char ->
                if (c.isDigit()) {
                    if (partNumberStr.isEmpty()) {
                        //first num digit, check coordinates to the left
                        adjacentCoords.add(Coord(row, col-1))
                        adjacentCoords.add((Coord(row-1, col-1)))
                        adjacentCoords.add((Coord(row+1, col-1)))
                    }
                    adjacentCoords.add((Coord(row-1, col))) //above
                    adjacentCoords.add((Coord(row+1, col))) //below
                    partNumberStr += c
                    if (col == line.length - 1) {
                        //part number is at the end of line
                        extractPartNumber(partNumberStr, adjacentCoords, gears)?.let { partNumber ->
                            sum += partNumber
                        }
                        partNumberStr = ""
                    }
                } else {
                    if (partNumberStr.isNotEmpty()) {
                        //reading number was finished, check coordinates to its right
                        adjacentCoords.add(Coord(row, col))
                        adjacentCoords.add((Coord(row-1, col)))
                        adjacentCoords.add((Coord(row+1, col)))
                        //process num
                        extractPartNumber(partNumberStr, adjacentCoords, gears)?.let { partNumber ->
                            sum += partNumber
                        }
                        partNumberStr = ""
                    }
                }
            }
        }

        val gearSum = gears.filter { it.value.parts.size == 2 }.map {
            it.value.parts[0] * it.value.parts[1]
        }.sum()

        println("Sum is $sum / $gearSum")
    }

    private fun extractPartNumber(
        partNumberStr: String,
        adjacentCoords: MutableList<Coord>,
        gears: MutableMap<Coord, Gear>
    ): Long? {
        var partNumber = partNumberStr.toLong()
        val validCoords = adjacentCoords.filter { (it.r in lines.indices) && (it.c in lines[0].indices) } //filter out coordinates outside the plan
        var isAdjacent = false
        validCoords.forEach { validCoord ->
            val adjacentChar = lines[validCoord.r][validCoord.c]
            if (!adjacentChar.isDigit() && adjacentChar != '.') {
                isAdjacent = true
                if (adjacentChar == '*') {
                    val gear = gears[validCoord]
                    if (gear == null || gear.parts.isEmpty()) {
                        gears[validCoord] = Gear(validCoord, mutableListOf(partNumber))
                    } else {
                        gears[validCoord]!!.parts.add(partNumber)
                    }
                }
            }
        }
        adjacentCoords.clear()
        return if (isAdjacent) {
            println("Valid part number found $partNumber")
            partNumber
        } else {
            println("Invalid part number found $partNumber")
            null
        }
    }

}

fun main(args: Array<String>) {
//    val problem = Day03("/Users/miro/projects/AoC23/input/day03t.txt")
    val problem = Day03("/Users/miro/projects/AoC23/input/day03a.txt")
    problem.solve()
}