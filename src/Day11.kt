import kotlin.math.abs

fun main() {
    Day11().execute()
}

class Day11 : ContestDay<Galaxy, Long>("input/Day11_part1.txt") {

    private var expansionRate = 2L
    override fun partOne(input: Galaxy): Long {
        // pretty much permutations
//        1 2 3 4 5 6 7 8 9
//        1  * * * * * * * *
//        2    * * * * * * *
//        3      * * * * * *
//        4        * * * * *
//        5          * * * *
//        6            * * *
//        7              * *
//        8                *
//        9
        var shortestPathSum: Long = 0
        for (i in 0..input.starts.lastIndex) {
            for (j in i + 1..input.starts.lastIndex) {
                val verticalDistance = abs(input.starts[j].location.first - input.starts[i].location.first)
                val horizontalDistance = abs(input.starts[j].location.second - input.starts[i].location.second)
                shortestPathSum += verticalDistance + horizontalDistance
            }
        }
        expansionRate = 1_000_000L
        return shortestPathSum
    }

    override fun partTwo(input: Galaxy): Long {
        return partOne(input)
    }

    override fun transformInput(input: List<String>): Galaxy {
        val columnEmptySpots = Array(input.size) { 0L }
        val rowEmptySpots = Array(input[0].length) { 0L }
        val starts = mutableListOf<Star>()
        iterateOverGalaxy(input, { it == '.' }) { (i, j) ->
            rowEmptySpots[i]++
            columnEmptySpots[j]++
        }
        val verticalExpansionMap = shiftExpansionValues(columnEmptySpots, input.size)
        val horizontalExpansionMap = shiftExpansionValues(rowEmptySpots, input.size)
        iterateOverGalaxy(input, { it == '#' }) { (i, j) ->
            starts.add(Star(i.toLong() + horizontalExpansionMap[i] to j.toLong() + verticalExpansionMap[j]))

        }
        return Galaxy(starts)
    }

    private fun shiftExpansionValues(expansion: Array<Long>, reachableGalaxy: Int): List<Long> {
        var shift = 0L
        return expansion.map {
            if (it == reachableGalaxy * 1L) {
                shift += expansionRate - 1

            }
            shift
        }
    }

    private fun iterateOverGalaxy(
        input: List<String>,
        matchCondition: (Char) -> Boolean,
        onMatch: (Pair<Int, Int>) -> Unit,
    ) {
        for (i in 0..input.lastIndex) {
            for (j in 0..input[i].lastIndex) {
                val spot = input[i][j]
                if (matchCondition(spot)) {
                    onMatch(i to j)
                }
            }
        }
    }
}

data class Galaxy(
    val starts: List<Star>,
)

data class Star(val location: Pair<Long, Long> /*val distances: List<Int>*/)