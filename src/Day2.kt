import java.lang.IllegalArgumentException

fun main() {
    val input = readInput("input/Day02_part2.txt")
    dayTwoPart2(input).println()
}

val cubeAllowance = mapOf("red" to 12, "green" to 13, "blue" to 14)
val minCubesNeededForGame = mutableMapOf("red" to 0, "green" to 0, "blue" to 0)

fun dayTwoPart1(input: List<String>): Int {
    var validGames = 0
    input.forEachIndexed { index, line ->
        val gameNumber = index + 1
        val sets = line.replace("Game $gameNumber: ", "").replace(",", "").split(";")
        sets.forEach { set ->
            val cubesInBag = set.trim().split(" ")
            for (cubeColorIndex in 1..cubesInBag.lastIndex step 2) {
                val cubeQuantityIndex = cubeColorIndex - 1
                val cubeColor = cubesInBag[cubeColorIndex]
                val cubeOfParticularColor = cubeAllowance[cubeColor]
                    ?: throw IllegalArgumentException("We deal only with RGB cubes")
                if (cubeOfParticularColor < cubesInBag[cubeQuantityIndex].toInt()) {
                    return@forEachIndexed
                }
            }
        }
        validGames += gameNumber
    }
    return validGames
}

fun dayTwoPart2(input: List<String>): Int {
    var sumOfAllSets = 0
    input.forEachIndexed { index, line ->
        //reset cube map
        minCubesNeededForGame.replaceAll { _, _ -> 0 }
        val gameNumber = index + 1
        val sets = line.replace("Game $gameNumber: ", "").replace(",", "").split(";")
        sets.forEach { set ->
            val cubesInBag = set.trim().split(" ")
            for (cubeColorIndex in 1..cubesInBag.lastIndex step 2) {
                val cubeQuantityIndex = cubeColorIndex - 1
                val cubeColor = cubesInBag[cubeColorIndex]
                val maxAmountOfCubes =
                    minCubesNeededForGame[cubeColor] ?: throw IllegalArgumentException("We deal only with RGB cubes")
                val desiredAmountOfCubes = cubesInBag[cubeQuantityIndex].toInt()
                if (maxAmountOfCubes < desiredAmountOfCubes) {
                    minCubesNeededForGame[cubeColor] = desiredAmountOfCubes
                }
            }
        }
        val power = minCubesNeededForGame.values.reduce { acc, i -> acc * i }
        sumOfAllSets += power
    }
    return sumOfAllSets
}