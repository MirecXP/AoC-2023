package mirecxp.aoc23.day07

import mirecxp.aoc23.readInput

//https://adventofcode.com/2023/day/7
class Day07(private val inputPath: String) {

    private var hands: List<Hand> = readInput(inputPath).map { line ->
        with(line.split(" ")) {
            Hand(get(0), get(1).toInt())
        }
    }

    fun mapCard(card: Char, part2: Boolean): Char {
        return when (card) {
            'A' -> 'A'
            'K' -> 'B'
            'Q' -> 'C'
            'J' -> if (part2) 'N' else 'D'
            'T' -> 'E'
            '9' -> 'F'
            '8' -> 'G'
            '7' -> 'H'
            '6' -> 'I'
            '5' -> 'J'
            '4' -> 'K'
            '3' -> 'L'
            '2' -> 'M'
            else -> {
                println("IMPOSSIBLE!")
                'X'
            }
        }
    }

    class Hand(val cards: String, val bid: Int) {

        fun getRegularType(handCards: String): HandType {
            val sorted: Map<Char, List<Char>> = handCards.toList().sorted().groupBy { it }
            return when (sorted.size) {
                1 -> HandType.FIVE

                2 -> {
                    if (sorted.values.maxBy { it.size }.size == 4) {
                        HandType.FOUR
                    } else {
                        HandType.FULLHOUSE
                    }
                }

                3 -> {
                    when (sorted.values.maxBy { it.size }.size) {
                        3 -> HandType.THREE
                        2 -> HandType.TWOPAIR
                        else -> HandType.IMPOSSIBLE
                    }
                }

                4 -> HandType.ONEPAIR

                5 -> HandType.HIGH

                else -> {
                    println("IMPOSSIBLE!")
                    HandType.IMPOSSIBLE
                }
            }
        }

        fun getType(part2: Boolean): HandType {
            if (!part2) {
                return getRegularType(cards)
            } else {
                var regularHandType = getRegularType(cards)

                if (cards.contains('J')) {
                    if (regularHandType == HandType.HIGH) return HandType.ONEPAIR

                    if (regularHandType == HandType.ONEPAIR) {
                        check(
                            cards.count { it == 'J' } in 1..2
                        )
                        return HandType.THREE
                    }

                    if (regularHandType == HandType.TWOPAIR) {
                        return if (cards.count { it == 'J' } == 1) {
                            HandType.FULLHOUSE
                        } else {
                            check(cards.count { it == 'J' } == 2)
                            HandType.FOUR
                        }
                    }

                    if (regularHandType == HandType.THREE) {
                        check(cards.count { it == 'J' } == 1 || cards.count { it == 'J' } == 3)
                        return HandType.FOUR
                    }

                    if (regularHandType == HandType.FULLHOUSE) {
                        check(cards.count { it == 'J' } in 2..3)
                        return HandType.FIVE
                    }

                    if (regularHandType == HandType.FOUR) {
                        return if (cards.count { it == 'J' } == 1) {
                            HandType.FIVE
                        } else {
                            check(cards.count { it == 'J' } == 4)
                            HandType.FIVE
                        }
                    }

                    if (regularHandType == HandType.FIVE) return HandType.FIVE
                }
                return regularHandType
            }
        }
    }

    enum class HandType(val rank: Int) {
        FIVE(6),
        FOUR(5),
        FULLHOUSE(4),
        THREE(3),
        TWOPAIR(2),
        ONEPAIR(1),
        HIGH(0),
        IMPOSSIBLE(-1)
    }

    fun solve(part2: Boolean): String {
        println("Solving day 7 for ${hands.size} hands [$inputPath]")

        val topRank = hands.size

        val groups: Map<HandType, List<Hand>> = hands.sortedBy { it.getType(part2) }.groupBy { it.getType(part2) }

        val handsSorted = mutableListOf<Hand>()
        HandType.values().sortedByDescending { it.rank }.forEach { handType ->
            groups[handType]?.sortedBy { h ->
                h.cards.map { mapCard(it, part2) }.toString()
            }?.let {
                handsSorted.addAll(it)
            }
        }

        var result = 0L

        handsSorted.forEachIndexed { index, hand ->
            val r = hand.bid * (topRank - index)
            result += r
        }

        val solution = "$result"
        println(solution)
        return solution
    }

}

fun main(args: Array<String>) {
    val testProblem = Day07("test/day07t")
    check(testProblem.solve(part2 = false) == "6440")
    check(testProblem.solve(part2 = true) == "5905")

    val problem = Day07("real/day07a")
    problem.solve(part2 = false)
    problem.solve(part2 = true)
}
