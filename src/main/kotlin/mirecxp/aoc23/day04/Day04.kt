package mirecxp.aoc23.day04

import mirecxp.aoc23.readInput

//https://adventofcode.com/2023/day/4
class Day04(private val inputPath: String) {

    private val lines: List<String> = readInput(inputPath)

    class Card(val id: Int, val wins: Int)

    val allCards = mutableMapOf<Int, MutableList<Card>>()

    fun solve(): String {
        println("Solving day 4 for ${lines.size} cards [$inputPath]")
        var sumPoints = 0L //part 1
        lines.forEach { line ->
            line.split(":").apply {
                val id = get(0).substringAfter("Card ").trim().toInt()
                val cardSplit = get(1).split(" | ")
                val winningNumbers = cardSplit[0].toNumList()
                val numbersToCheck = cardSplit[1].toNumList()
                val matchedNumbers = numbersToCheck.intersect(winningNumbers)
                val winCount = matchedNumbers.size
                val points = when (winCount) {
                    0 -> 0
                    else -> 1 shl (winCount - 1)
                }
                println("$id : $points points ($winCount wins)")
                val card = Card(id, winCount)
                allCards[id] = mutableListOf(card)
                sumPoints += points
            }
        }

        var cardSum = 0L //part 2
        allCards.forEach { cardGroup ->
            cardGroup.value.forEach { card ->
                repeat(card.wins) { num ->
                    val i = card.id + num + 1
                    allCards[i]?.let { group ->
                        group.add(group.first())
                    }
                }
            }
        }
        allCards.forEach {
            cardSum += it.value.size
        }

        val solution = "$sumPoints / $cardSum"
        println(solution)
        return solution
    }

}

fun String.toNumList() = split(" ")
    .filter { it.isNotEmpty() }
    .map { it.toLong() }

fun main(args: Array<String>) {
    val testProblem = Day04("test/day04t")
    check(testProblem.solve() == "13 / 30")

    val problem = Day04("real/day04a")
    problem.solve()
}
