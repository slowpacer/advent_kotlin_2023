fun main() {
    val matrix = readInput("input/Day03_part1.txt").map { it.toCharArray() }
    dayThreePart2(matrix).println()
}

// region part 1
const val excludedSymbol = '.'
val partOneConnectivityCheck: (Char, Pair<Int, Int>) -> Boolean = { char, _ -> char != excludedSymbol }
fun dayThreePart1(matrix: List<CharArray>): Int {
    var sumOfVehicleParts = 0
    for (i in 0..matrix.lastIndex) {
        var currentPartNumber = 0
        var isConnected = false
        for (j in 0..matrix[i].lastIndex) {
            if (matrix[i][j].isDigit()) {
                val adjacencyArea = if (currentPartNumber == 0) AdjacencyArea.START else AdjacencyArea.CENTER
                if (!isConnected) {
                    isConnected = checkIfConnected(matrix, i, j, adjacencyArea)
                }
                currentPartNumber *= 10
                currentPartNumber += matrix[i][j] - '0'
            } else if (currentPartNumber != 0) {
                if (!isConnected) {
                    isConnected = checkIfConnected(matrix, i, j - 1, AdjacencyArea.END)
                }
                if (isConnected) {
                    sumOfVehicleParts += currentPartNumber
                }
                currentPartNumber = 0
                isConnected = false
            }
        }
        // last number in row check
        if (isConnected) {
            sumOfVehicleParts += currentPartNumber
        }
    }

    return sumOfVehicleParts
}

fun checkIfConnected(
    matrix: List<CharArray>,
    i: Int,
    j: Int,
    adjacencyArea: AdjacencyArea,
    connectivityCheck: (Char, Pair<Int, Int>) -> Boolean = partOneConnectivityCheck
) = when (adjacencyArea) {
    AdjacencyArea.START -> matrix.checkFirstCharAdjacencyConnection(i, j, connectivityCheck)
    AdjacencyArea.CENTER -> matrix.checkMiddleCharAdjacencyConnection(i, j, connectivityCheck)
    AdjacencyArea.END -> matrix.checkLastCharAdjacencyConnection(i, j, connectivityCheck)
}

private fun List<CharArray>.checkMiddleCharAdjacencyConnection(
    i: Int,
    j: Int,
    connectivityCheck: (Char, Pair<Int, Int>) -> Boolean
): Boolean {
    val upperConnection = checkUpperConnection(i, j, connectivityCheck)
    val bottomConnection = checkBottomConnection(i, j, connectivityCheck)
    return upperConnection || bottomConnection
}

private fun List<CharArray>.checkBottomConnection(
    i: Int,
    j: Int,
    connectivityCheck: (Char, Pair<Int, Int>) -> Boolean
) =
    if (i + 1 > lastIndex) false else connectivityCheck(this[i + 1][j], i + 1 to j)

private fun List<CharArray>.checkUpperConnection(i: Int, j: Int, connectivityCheck: (Char, Pair<Int, Int>) -> Boolean) =
    if (i - 1 < 0) false else connectivityCheck(this[i - 1][j], i - 1 to j)

fun List<CharArray>.checkFirstCharAdjacencyConnection(
    i: Int,
    j: Int,
    connectivityCheck: (Char, Pair<Int, Int>) -> Boolean
): Boolean {
    val centerSelfCheck = checkIfConnected(this, i, j, AdjacencyArea.CENTER, connectivityCheck)
    val leftCenterCheck = if (j - 1 < 0) false else checkIfConnected(
        this, i, j - 1, AdjacencyArea.CENTER, connectivityCheck
    ) || connectivityCheck(this[i][j - 1], i to j - 1)
    return centerSelfCheck || leftCenterCheck

}

fun List<CharArray>.checkLastCharAdjacencyConnection(
    i: Int,
    j: Int,
    connectivityCheck: (Char, Pair<Int, Int>) -> Boolean
): Boolean {
    // we don't need to do a self center check as that was already done
    val rightCenterCheck = if (j + 1 > this[i].lastIndex) false else checkIfConnected(
        this, i, j + 1, AdjacencyArea.CENTER, connectivityCheck
    ) || connectivityCheck(this[i][j + 1], i to j + 1)
    return rightCenterCheck
}

enum class AdjacencyArea {
    START, CENTER, END
}

//endregion
// region part 2
const val gear = '*'

// might have been easier just to find the gears and then discover numbers that are adjacent by using dfs
fun dayThreePart2(matrix: List<CharArray>): Int {
    val partsNumberHitMap = mutableMapOf<Pair<Int, Int>, Int>()
    val partsConnectedByGear = mutableListOf<Int>()
    val partsDuplicationCheck = mutableSetOf<Int>()
    val gears = mutableListOf<Pair<Int, Int>>()
    // actual check if two parts are connected by a gear
    val partsConnectivityCheck: (Char, Pair<Int, Int>) -> Boolean = { char, coordinates ->
        (char.isDigit()).also {
            if (it) {
                partsNumberHitMap[coordinates]?.let { part ->
                    if (!partsDuplicationCheck.contains(part)) {
                        partsConnectedByGear.add(part)
                        partsDuplicationCheck.add(part)
                    }
                }
            }
        }
    }

    // iteration through the board to find all parts and gears
    for (i in 0..matrix.lastIndex) {
        var currentPartNumber = 0
        var partNumberDigitsAmount = 0
        for (j in 0..matrix[i].lastIndex) {
            if (matrix[i][j].isDigit()) {
                currentPartNumber *= 10
                currentPartNumber += matrix[i][j] - '0'
                partNumberDigitsAmount++
            } else {
                if (currentPartNumber != 0) {
                    addNumberToHitMap(partNumberDigitsAmount, partsNumberHitMap, i, j - 1, currentPartNumber)
                }
                currentPartNumber = 0
                partNumberDigitsAmount = 0
                if (matrix[i][j] == gear) gears.add(i to j)
            }
        }
        //don't forget the edge case
        if (currentPartNumber != 0) {
            addNumberToHitMap(partNumberDigitsAmount, partsNumberHitMap, i, matrix[i].lastIndex, currentPartNumber)
        }
    }
    // iterating through gears to understand which parts are connected
    gears.forEach {
        matrix.verifyGear(it.first, it.second, partsConnectivityCheck)
        // in case that's just one connection just remove it, as we'll always have two as per req
        if (partsConnectedByGear.size % 2 == 1) partsConnectedByGear.removeAt(partsConnectedByGear.lastIndex)
        partsDuplicationCheck.clear()
    }
    // finally calculate the actual sum
    var gearRatioSum = 0
    for (i in 1..partsConnectedByGear.lastIndex step 2) {
        gearRatioSum += partsConnectedByGear[i - 1] * partsConnectedByGear[i]
    }

    return gearRatioSum
}

private fun addNumberToHitMap(
    partNumberDigitsAmount: Int, map: MutableMap<Pair<Int, Int>, Int>, i: Int, j: Int, currentPartNumber: Int
) {
    for (digitsInNumber in 0 until partNumberDigitsAmount) {
        map[i to j - digitsInNumber] = currentPartNumber
    }
}

// check surroundings
fun List<CharArray>.verifyGear(i: Int, j: Int, partTwoConnectivityCheck: (Char, Pair<Int, Int>) -> Boolean) {
    // just a trick because I don't want to rewrite the  part 1adjacency check
    checkIfConnected(this, i, j, AdjacencyArea.START, partTwoConnectivityCheck)
    checkIfConnected(this, i, j, AdjacencyArea.END, partTwoConnectivityCheck)

}
// endregion

