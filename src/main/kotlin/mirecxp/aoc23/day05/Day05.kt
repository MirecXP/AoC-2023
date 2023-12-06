package mirecxp.aoc23.day05

import mirecxp.aoc23.day04.toNumList
import mirecxp.aoc23.readInput
import java.util.*

//https://adventofcode.com/2023/day/5
class Day05(private val inputPath: String) {

    private val lines: List<String> = readInput(inputPath)

    class RangeMap(val target: Long, val source: Long, val length: Long) {
        fun getSourceRange() = LongRange(source, source + length - 1)
        fun getTargetRange() = LongRange(target, target + length - 1)
    }

    // https://www.reddit.com/r/adventofcode/comments/18b4b0r/comment/kc2hh66/?utm_source=share&utm_medium=web3x&utm_name=web3xcss&utm_term=1&utm_content=share_button
    fun solve2() {
        val seeds: List<LongRange> = lines.first().substringAfter(" ").split(" ").map { it.toLong() }.chunked(2)
            .map { it.first()..<it.first() + it.last() }
        val maps: List<Map<LongRange, LongRange>> = lines.drop(2).joinToString("\n").split("\n\n").map { section ->
            section.lines().drop(1).associate {
                it.split(" ").map { it.toLong() }.let { (dest, source, length) ->
                    source..(source + length) to dest..(dest + length)
                }
            }
        }
        val res = seeds.flatMap { seedsRange: LongRange ->
            maps.fold(listOf(seedsRange)) { aac: List<LongRange>, map: Map<LongRange, LongRange> ->
                aac.flatMap {
                    map.entries.mapNotNull { (source, dest) ->
                        (maxOf(source.first, it.first) to minOf(source.last, it.last)).let { (start, end) ->
                            if (start <= end) (dest.first - source.first).let { (start + it)..(end + it) } else null
                        }
                    }
                }
            }
        }.minOf { it.first }
        println(res)
    }

    fun solve(part2: Boolean): String {
        println("Solving day 5 for ${lines.size} lines [$inputPath]")

        val rangeChain = LinkedList<LinkedList<RangeMap>>()
        var seeds = lines.first().substringAfter("seeds: ").toNumList()

        var rangeMapList = LinkedList<RangeMap>()

        lines.subList(2, lines.size - 1).forEach { line ->
            if (line.contains(":")) {
                // new mapping stage
                if (rangeMapList.isNotEmpty()) rangeChain.add(rangeMapList)
                rangeMapList = LinkedList<RangeMap>()
            } else if (line.isEmpty()) {
                //ignore
            } else {
                line.toNumList().apply {
                    rangeMapList.add(
                        RangeMap(target = get(0), source = get(1), length = get(2))
                    )
                }
            }
        }
        rangeChain.add(rangeMapList) //last one

//        checkOverlaps(rangeChain)  // OOM :(

        val result = if (part2) {
            //part 2
            var min = Long.MAX_VALUE
            seeds.chunked(2) { chunk: List<Long> ->
                val start = chunk[0]
                val len = chunk[1]
                LongRange(start, start + len - 1)
            }.forEachIndexed { i, rng: LongRange ->
                println("Solving chunk $i, min so far = $min")
                var current: Long = 0
                rng.forEachIndexed { index, seed ->
                    val output = processSeedToMap(seed, rangeChain)
                    val diff = seed - output
                    if (current != diff) {
                        println("It changed: $current -> $diff [$index]")
                        current = diff
                    }
                    if (output < min) {
                        min = output
                    }
                }
            }
            min
        } else {
            //part 1
            seeds.minOfOrNull { seed -> processSeedToMap(seed, rangeChain) }
        }

        val solution = "$result"
        println(solution)
        return solution
    }

    fun processSeedToMap(seed: Long, rangeChain: LinkedList<LinkedList<RangeMap>>): Long {
        var output: Long = seed
        rangeChain.forEachIndexed { chainIndex, rangeList ->
            val r: RangeMap? = rangeList.firstOrNull {
                output >= it.source && output <= it.source + it.length - 1 // it.getSourceRange().contains(output)
            }
            // print("chain ($chainIndex): $output->")
            r?.let {
                val i = output - r.source
                output = r.target + i
            } // if no range, output is not modified
            // println(output)
        }
        // println("seed $seed mapped to $output")
        return output
    }

    fun checkOverlaps(rangeChain: LinkedList<LinkedList<RangeMap>>) {
        //check overlapping
        var isOverlapping = false
        rangeChain.forEachIndexed { indexChain: Int, mapList: MutableList<RangeMap> ->
            mapList.forEachIndexed { indexList, rangeMap ->
                val srcR = rangeMap.getSourceRange()
                val dstR = rangeMap.getTargetRange()
                for (i in indexList + 1 until mapList.size) {
                    val srcR1 = mapList[i].getSourceRange()
                    val dstR1 = mapList[i].getTargetRange()
                    if (srcR.intersect(srcR1).isNotEmpty()) {
                        println("Overlap found: source chain $indexChain, range $indexList with range $i")
                        isOverlapping = true
                    }
                    if (dstR.intersect(dstR1).isNotEmpty()) {
                        println("Overlap found: target chain $indexChain, range $indexList with range $i")
                        isOverlapping = true
                    }
                }
            }
        }
        if (isOverlapping) println("OVERLAP FOUND")
    }
}

fun main(args: Array<String>) {
    val testProblem = Day05("test/day05t")
    check(testProblem.solve(part2 = false) == "35")
    check(testProblem.solve(part2 = true) == "46")

    val problem = Day05("real/day05a")
//    problem.solve2()
    problem.solve(part2 = false)
    problem.solve(part2 = true)
}
