package mirecxp.aoc23.day10

import mirecxp.aoc23.readInput

//https://adventofcode.com/2023/day/10

/*
| is a vertical pipe connecting north and south.
- is a horizontal pipe connecting east and west.
L is a 90-degree bend connecting north and east.
J is a 90-degree bend connecting north and west.
7 is a 90-degree bend connecting south and west.
F is a 90-degree bend connecting south and east.
. is ground; there is no pipe in this tile.
S is the starting position of the animal; there is a pipe on this tile, but your sketch doesn't show what shape the pipe has.
 */

data class Coord(val row: Int, val col: Int)

sealed class Pipe(
    val coord: Coord,
    val isVertical: Boolean,
    val isHorizontal: Boolean,
    val isCorner: Boolean,
    val dir: String
) {
    abstract fun getNeighbors(): List<Coord>

    fun getOtherNeighbor(me: Pipe): Coord {
        return getNeighbors().first { it != me.coord }
    }

    override fun toString(): String {
        return "${javaClass.canonicalName}(coord=$coord)"
    }

    companion object {
        fun createPipe(c: Char, coord: Coord): Pipe {
            return when (c) {
                '|' -> Vertical(coord)
                '-' -> Horizontal(coord)
                'L' -> NECorner(coord)
                'J' -> NWCorner(coord)
                '7' -> SWCorner(coord)
                'F' -> SECorner(coord)
                '.' -> Ground(coord)
                'S' -> Ground(coord)
                else -> {
                    println("WTF $c : $coord")
                    Ground(coord)
                }
            }
        }
    }

    class Vertical(coord: Coord) : Pipe(
        coord,
        isVertical = true,
        isHorizontal = false,
        isCorner = false,
        dir = "NS"
    ) {
        override fun getNeighbors(): List<Coord> {
            return listOf(
                Coord(coord.row - 1, coord.col),
                Coord(coord.row + 1, coord.col)
            )
        }
    }

    class Horizontal(coord: Coord) : Pipe(
        coord,
        isVertical = false,
        isHorizontal = true,
        isCorner = false,
        dir = "EW"
    ) {
        override fun getNeighbors(): List<Coord> {
            return listOf(
                Coord(coord.row, coord.col - 1),
                Coord(coord.row, coord.col + 1)
            )
        }
    }

    class NECorner(coord: Coord) : Pipe(
        coord,
        isVertical = false,
        isHorizontal = false,
        isCorner = true,
        dir = "NE"
    ) {
        override fun getNeighbors(): List<Coord> {
            return listOf(
                Coord(coord.row - 1, coord.col),
                Coord(coord.row, coord.col + 1)
            )
        }
    }

    class NWCorner(coord: Coord) : Pipe(
        coord,
        isVertical = false,
        isHorizontal = false,
        isCorner = true,
        dir = "NW"
    ) {
        override fun getNeighbors(): List<Coord> {
            return listOf(
                Coord(coord.row - 1, coord.col),
                Coord(coord.row, coord.col - 1)
            )
        }
    }

    class SWCorner(coord: Coord) : Pipe(
        coord,
        isVertical = false,
        isHorizontal = false,
        isCorner = true,
        dir = "SW"
    ) {
        override fun getNeighbors(): List<Coord> {
            return listOf(
                Coord(coord.row, coord.col - 1),
                Coord(coord.row + 1, coord.col)
            )
        }
    }

    class SECorner(coord: Coord) : Pipe(
        coord,
        isVertical = false,
        isHorizontal = false,
        isCorner = true,
        dir = "SE"
    ) {
        override fun getNeighbors(): List<Coord> {
            return listOf(
                Coord(coord.row, coord.col + 1),
                Coord(coord.row + 1, coord.col)
            )
        }
    }

    class Ground(coord: Coord) : Pipe(
        coord,
        isVertical = false,
        isHorizontal = false,
        isCorner = false,
        dir = ""
    ) {
        override fun getNeighbors(): List<Coord> {
            return emptyList()
        }
    }
}

class Day10(private val inputPath: String) {

    private var lines = readInput(inputPath)

    val debug = false

    fun solve(): String {
        println("Solving day 10 for ${lines.size} lines [$inputPath]")

        var map = Array(lines.size) { Array(lines[0].length) { '.' } }
        var pipes = Array(lines.size) { Array(lines[0].length) { Pipe.createPipe('.', Coord(0, 0)) } }

        var start: Coord = Coord(0, 0)
        var startPipe: Pipe? = null
        lines.forEachIndexed { rowIndex, row ->
            row.forEachIndexed { indexCol, c ->
                val coord = Coord(rowIndex, indexCol)
                map[rowIndex][indexCol] = c
                val pipe = Pipe.createPipe(c, coord)
                pipes[rowIndex][indexCol] = pipe
                if (c == 'S') {
                    start = coord
                    startPipe = pipe
                }
            }
        }
        var startDirs = ""
        try {
            if (pipes[start.row - 1][start.col].dir.contains('S')) {
                startDirs += 'N'
            }
        } catch (_: Exception) {}
        try {
            if (pipes[start.row + 1][start.col].dir.contains('N')) {
                startDirs += 'S'
            }
        } catch (_: Exception) {}
        try {
            if (pipes[start.row][start.col + 1].dir.contains('W')) {
                startDirs += 'E'
            }
        } catch (_: Exception) {}
        try {
            if (pipes[start.row][start.col - 1].dir.contains('E')) {
                startDirs += 'W'
            }
        } catch (_: Exception) {}

        startPipe = when (startDirs) {
            "NS" -> Pipe.Vertical(start)
            "EW" -> Pipe.Horizontal(start)
            "NE" -> Pipe.NECorner(start)
            "NW" -> Pipe.NWCorner(start)
            "SW" -> Pipe.SWCorner(start)
            "SE" -> Pipe.SECorner(start)
            else -> {
                println("Error determining start orientation")
                Pipe.Ground(start)
            }
        }
        pipes[start.row][start.col] = startPipe!!

        var result = 0

        val loop = mutableListOf<Pipe>()

        if (startPipe != null) {
            var isLooped: Boolean = false
            var currentPipeCoord = startPipe!!.getNeighbors().first()
            var previousPipe = startPipe!!
            var currentPipe = pipes[currentPipeCoord.row][currentPipeCoord.col]
            loop.add(startPipe!!)
            loop.add(currentPipe)
            while (!isLooped) {
                val nextPipeCoord = currentPipe.getOtherNeighbor(previousPipe)
                val nextPipe = pipes[nextPipeCoord.row][nextPipeCoord.col]
                previousPipe = currentPipe
                currentPipe = nextPipe
                if (currentPipe == startPipe!!) {
                    isLooped = true
                } else {
                    loop.add(currentPipe)
                }
            }
            result = loop.size
        }

        println("Pipe loop is long $result, max distance is ${result / 2}")

        var countInside = 0
        var countPipe = 0 //loop
        var countOutSide = 0
        var ups = 0 // errors
        pipes.forEachIndexed { rowIndex: Int, pipeRow: Array<Pipe> ->
            pipeRow.forEachIndexed { colIndex, pipe ->
                if (!loop.contains(pipe)) {
                    var crossesV = 0
                    var lastCornerDir: String? = null
                    if (pipe.coord.row < pipes.size - 1) {
                        //go down
                        for (r in pipe.coord.row + 1 until pipes.size) {
                            val checkPipe = pipes[r][colIndex]
                            if (loop.contains(checkPipe) && checkPipe.isHorizontal) crossesV++ else {
                                if (loop.contains(checkPipe) && (checkPipe.isCorner)) {
                                    if (lastCornerDir != null) {
                                        if (checkPipe.dir[1] == lastCornerDir[1]) {
                                            lastCornerDir = null
                                        } else {
                                            lastCornerDir = null
                                            crossesV++
                                        }
                                    } else {
                                        lastCornerDir = checkPipe.dir
                                    }
                                }
                            }
                        }
                    }
                    /*
                    //not necessary, just for double check
                    var crossesH = 0
                    if (pipe.coord.col < pipes[0].size - 1) {
                        //go right
                        for (c in pipe.coord.col + 1 until pipes[0].size) {
                            val checkPipe = pipes[rowIndex][c]
                            if (loop.contains(checkPipe) && checkPipe.isVertical) crossesH++ else {
                                if (loop.contains(checkPipe) && (checkPipe.isCorner)) {
                                    if (lastCornerDir != null) {
                                        if (checkPipe.dir[0] == lastCornerDir[0]) {
                                            lastCornerDir = null
                                        } else {
                                            lastCornerDir = null
                                            crossesH++
                                        }
                                    } else {
                                        lastCornerDir = checkPipe.dir
                                    }
                                }
                            }
                        }
                    }
                    */
                    if (crossesV % 2 == 1) { // && crossesV % 2 == 1) {
                        countInside++
                        if (debug) println("Inside: $pipe")
                    } else {
                        countOutSide++
                        if (debug) println("Outside: $pipe")
                    }
                } else countPipe++
            }
        }

        println("Tiles within loop: $countInside")
        println("Tiles outside loop: $countOutSide")
        check(countPipe == result) { "Loop size error" }
        check(ups == 0) { "Inside/Outside recognised errors" }
        check(countPipe + countInside + countOutSide == pipes.size * pipes[0].size) { "Counting error" }
        return "$result"
    }

}

fun main(args: Array<String>) {
    Day10("test/day10t1").solve()  // 4 / 1
    Day10("test/day10t2").solve()  // 8 / 1
    Day10("test/day10t3").solve()  // ? / 8
    Day10("test/day10t4").solve()  // ? / 10

    val problem = Day10("real/day10a")
    problem.solve()
}
