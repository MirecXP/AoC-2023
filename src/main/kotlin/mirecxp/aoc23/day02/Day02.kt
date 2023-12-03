package mirecxp.aoc23.day02

import java.io.File

//https://adventofcode.com/2023/day/2
class Day02(inputPath: String) {

    private val gameInputs: List<String> =
        File(inputPath).readLines()

    data class Game(
        val id: Long,
        val draws: List<Draw>
    ) {
        fun getMinimumDraw() = Draw(
            draws.maxOf { it.red },
            draws.maxOf { it.green },
            draws.maxOf { it.blue }
        )
    }

    data class Draw(val red: Long, val green: Long, val blue: Long) {
        fun getPower() = red * green * blue

        fun isPossible(minDraw: Draw) =
            red <= minDraw.red && green <= minDraw.green && blue <= minDraw.blue
    }

    fun solve() {
        println("Solving day 2 for ${gameInputs.size} games")
        var sum = 0L
        var sumPowers = 0L
        val games = mutableListOf<Game>()
        gameInputs.forEach { gameInput ->
            gameInput.split(":").apply {
                val id = get(0).split(" ")[1].toLong()
                val draws = mutableListOf<Draw>()
                var possibleGame = true
                get(1).split(";").forEach { singleGame ->
                    var red = 0L
                    var blue = 0L
                    var green = 0L
                    singleGame.split(",").forEach { gems ->
                        gems.trim().split(" ").apply {
                            val num = get(0).toLong()
                            when (val color = get(1)) {
                                "red" -> red = num
                                "green" -> green = num
                                "blue" -> blue = num
                            }
                        }
                        val draw = Draw(red, green, blue)
                        draws.add(draw)
                        if (!draw.isPossible(minDraw = Draw(red = 12, green = 13, blue = 14))) {
                            possibleGame = false
                        }
                    }
                }
                if (possibleGame) {
                    sum += id
                }
                val game = Game(id, draws)
                games.add(game)
                sumPowers += game.getMinimumDraw().getPower()
            }
        }
        println("Sum of possible game IDs: $sum")
        println("Sum of all powers: $sumPowers")
    }

}

fun main(args: Array<String>) {
//    val problem = Day02("/Users/miro/projects/AoC23/input/day02t.txt")
    val problem = Day02("/Users/miro/projects/AoC23/input/day02a.txt")
    problem.solve()
}