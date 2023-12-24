
fun main() {
    Day12().execute()
}

class Day12 : ContestDay<List<SpringsMap>, Long>("input/Day12_part1_test.txt") {

    val suitablePlaceholders = "#?"
    var expansionMultiplier = 1
    override fun partOne(input: List<SpringsMap>): Long? {
        expansionMultiplier = 5
        return input.fold(0L) { acc, springsMap -> acc +
                matchCombination(springsMap) }
    }

    override fun partTwo(input: List<SpringsMap>): Long? {
        return partOne(input)
    }

    fun matchCombination(
        map: SpringsMap,
        schemeIndex: Int = 0,
        damagedSpringIndex: Int = 0,
        springsCoveredIntermediate: Int = 0,
        countedIn: Array<Boolean> = Array(map.damagedScheme.length) { false }
    ): Long {

        if (damagedSpringIndex <= map.damagedSpringsOrder.lastIndex && map.damagedSpringsOrder[damagedSpringIndex] > map.damagedScheme.length - schemeIndex) {
            return 0
        }
        if (damagedSpringIndex > map.damagedSpringsOrder.lastIndex) {
            if (springsCoveredIntermediate == map.defaultDamagedSpringsPlaceholder) {
                return 1
            } else {
                return 0
            }
        }
        if (schemeIndex > map.damagedScheme.lastIndex) {
            return 0L
        }

        var sum = 0L
        val damagedSprings = map.damagedSpringsOrder[damagedSpringIndex]

        for (i in schemeIndex..map.damagedScheme.lastIndex) {
            var fittingPlaceholders = 0
            var containsPlaceholder = 0
            for (j in i..map.damagedScheme.lastIndex) {
                if (map.damagedScheme[j] in suitablePlaceholders) {
                    if (map.damagedScheme[j] == '#') containsPlaceholder++
                    fittingPlaceholders++
                    if (fittingPlaceholders == damagedSprings) {
                        val leftBoundOfDamagedSpringSequence = j - fittingPlaceholders
                        val startOfDamagedSpringsSequence = leftBoundOfDamagedSpringSequence + 1
                        if (suitableSurrondings(
                                leftBoundOfDamagedSpringSequence,
                                j + 1,
                                map
                            ) && !countedIn[startOfDamagedSpringsSequence]
                            && map.damagedSpringsLeftBehind[startOfDamagedSpringsSequence] <= springsCoveredIntermediate
                        ) {
                            countedIn[startOfDamagedSpringsSequence] = true
                            sum += matchCombination(
                                map,
                                j + 2,
                                damagedSpringIndex + 1,
                                springsCoveredIntermediate + containsPlaceholder,
                                countedIn
                            )
                            break
                        }
                        fittingPlaceholders--
                    }
                } else {
                    fittingPlaceholders = 0
                }
            }
            countedIn[i] = false
        }
        return sum
    }


    fun suitableSurrondings(leftIndex: Int, rightIndex: Int, map: SpringsMap): Boolean {
        val leftCheck = if (leftIndex < 0) true else map.damagedScheme[leftIndex] in ".?"
        val rightCheck = if (rightIndex > map.damagedScheme.lastIndex) true else map.damagedScheme[rightIndex] in ".?"
        return leftCheck && rightCheck
    }

    override fun transformInput(input: List<String>): List<SpringsMap> {
        return input.map {
            val (originalScheme, order) = it.split(" ")
            var scheme = originalScheme
            val damagedSpringsOrderOriginal = numberRegexp.findAll(order).map { it.value.toInt() }.toMutableList()
            val damagedSpringsOrder = damagedSpringsOrderOriginal.toMutableList()

            for (i in 1 until expansionMultiplier){
                damagedSpringsOrder.addAll(damagedSpringsOrderOriginal)
                scheme += "?$originalScheme"
            }
            val damagedSpringsLeftBehind = scheme.map { 0 }.toMutableList()
            var leftBehindIndex = 0
            var previousIndexOfDamagedPlaceholder = 0
            var indexOfDamagedPlaceholder = scheme.indexOf('#')
            var defaultPlaceholders = 0
            while (indexOfDamagedPlaceholder != -1) {
                defaultPlaceholders++
                val startRange = previousIndexOfDamagedPlaceholder
                val endRange = indexOfDamagedPlaceholder
                for (i in startRange..endRange) {
                    damagedSpringsLeftBehind[i] = leftBehindIndex
                }
                leftBehindIndex++
                previousIndexOfDamagedPlaceholder = endRange + 1
                indexOfDamagedPlaceholder = scheme.indexOf('#', indexOfDamagedPlaceholder + 1)
            }
            for (i in previousIndexOfDamagedPlaceholder..scheme.lastIndex) {
                damagedSpringsLeftBehind[i] = leftBehindIndex
            }
            SpringsMap(
                scheme,
                damagedSpringsOrder,
                damagedSpringsLeftBehind,
                defaultPlaceholders,
            )
        }
    }
}

data class SpringsMap(
    val damagedScheme: String,
    val damagedSpringsOrder: List<Int>,
    val damagedSpringsLeftBehind: List<Int>,
    val defaultDamagedSpringsPlaceholder: Int = 0,
)

