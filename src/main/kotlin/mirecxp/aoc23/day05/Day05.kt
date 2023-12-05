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

//        checkOverlaps(rangeChain)

        val result = if (part2) {
            //part 2
            var min = Long.MAX_VALUE
            seeds.chunked(2) { chunk: List<Long> ->
                val start = chunk[0]
                val len = chunk[1]
                LongRange(start, start + len - 1)
            }.forEachIndexed { i, rng: LongRange ->
                println("Solving chunk $i, min so far = $min")
                rng.forEach {
                    val output = processSeedToMap(it, rangeChain)
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

/*


    chain (0): 86->88
    chain (1): 88->88
    chain (2): 88->88
    chain (3): 88->81
    chain (4): 81->49
    chain (5): 49->50
    chain (6): 50->50
    chain (7): 50->52
    chain (8): 52->37
    chain (9): 37->26
    chain (10): 26->19
    chain (11): 19->19
    chain (12): 19->20
    chain (13): 20->20
    seed 86 mapped to 20
*/
    fun checkOverlaps(rangeChain: LinkedList<LinkedList<RangeMap>>) {
        //check overlapping
        var isOverlapping = false
        rangeChain.forEachIndexed { indexChain: Int, mapList: MutableList<RangeMap> ->
            mapList.forEachIndexed { indexList, rangeMap ->
                val srcR = rangeMap.getSourceRange()
                val dstR = rangeMap.getTargetRange()
                for (i in indexList+1 until mapList.size) {
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
    problem.solve(part2 = false)
    problem.solve(part2 = true)
}
