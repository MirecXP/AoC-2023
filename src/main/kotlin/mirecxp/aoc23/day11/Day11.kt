package mirecxp.aoc23.day11

import mirecxp.aoc23.readInput

data class Coord(val row: Long, val col: Long)

//https://adventofcode.com/2023/day/11
class Day11(private val inputPath: String) {

    private var lines: List<String> = readInput(inputPath)

    fun solve(expansionIndex: Long) {
        println("Solving day 11 for ${lines.size} lines [$inputPath] with expansion=$expansionIndex")

        var spaceWithExpandedRows = mutableListOf<String>()
        var spaceWithExpandedRowsCols = mutableListOf<String>()

        var expandedCols = mutableListOf<Int>() //for part 2
        var expandedRows = mutableListOf<Int>()

        lines.forEachIndexed { index, line ->
            spaceWithExpandedRows.add(line)
            if (line.all { it == '.' }) {
                spaceWithExpandedRows.add(line)
                expandedRows.add(index)
            }
        }

        repeat(spaceWithExpandedRows[0].length) { col ->
            var s: String = ""
            repeat(spaceWithExpandedRows.size) { row ->
                s += spaceWithExpandedRows[row][col]
            }
            spaceWithExpandedRowsCols.add(s)
            if (s.all { it == '.' }) {
                spaceWithExpandedRowsCols.add(s)
                expandedCols.add(col)
            }
        }

        println("Expanded cols: $expandedCols")
        println("Expanded rows: $expandedRows")

        val coords = mutableListOf<Coord>()
        spaceWithExpandedRowsCols.forEachIndexed { colIndex, colStr ->
            colStr.forEachIndexed { rowIndex, c ->
                if (c == '#') {
                    coords.add(Coord(rowIndex.toLong(), colIndex.toLong()))
                }
            }
        }

        val origCoords = mutableListOf<Coord>()
        lines.forEachIndexed { rowIndex, rowStr ->
            rowStr.forEachIndexed { colIndex, c ->
                if (c == '#') {
                    val rowsToAdd = expandedRows.count { it < rowIndex } * (expansionIndex - 1)
                    val colsToAdd = expandedCols.count { it < colIndex } * (expansionIndex - 1)
                    origCoords.add(Coord(rowIndex.toLong() + rowsToAdd, colIndex.toLong() + colsToAdd))
                }
            }
        }

        var distances = 0L
        var countDistances = 0
        coords.forEachIndexed { index1, coord1 ->
            for (index2 in index1 + 1 until coords.size) {
                val coord2 = coords[index2]
                val distanceCol = maxOf(coord1.col, coord2.col) - minOf(coord1.col, coord2.col)
                val distanceRow = maxOf(coord1.row, coord2.row) - minOf(coord1.row, coord2.row)
                val distance = distanceCol + distanceRow
                distances += distance
                countDistances++
            }
        }
        println("Sum of distances for part1: $distances for $countDistances pairs")

        var distances2 = 0L
        var countDistances2 = 0
        origCoords.forEachIndexed { index1, coord1 ->
            for (index2 in index1 + 1 until coords.size) {
                val coord2 = origCoords[index2]
                val distanceCol = maxOf(coord1.col, coord2.col) - minOf(coord1.col, coord2.col)
                val distanceRow = maxOf(coord1.row, coord2.row) - minOf(coord1.row, coord2.row)
                val distance = distanceCol + distanceRow
                distances2 += distance
                countDistances2++
            }
        }
        println("Sum of distances for part2: $distances2 for $countDistances2 pairs\n\n")
    }

}

fun main(args: Array<String>) {
    val testProblem = Day11("test/day11t")
    testProblem.solve(expansionIndex = 2)
    testProblem.solve(expansionIndex = 10)
    testProblem.solve(expansionIndex = 100)

    val problem = Day11("real/day11a")
    problem.solve(expansionIndex = 1000000)
}
