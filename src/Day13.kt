fun main() {
    Day13().execute()
}

class Day13 : ContestDay<List<String>, Int>("input/Day13_part1_test.txt") {
    override fun transformInput(input: List<String>) = input

    override fun partOne(input: List<String>): Int? {
        val matrices = input.joinToString("\n").split("\n\n").map { it.lines() }
        return matrices.sumOf { 100 * it.findReflection() + it.transposedMatrix().findReflection() }
    }
}

fun List<String>.transposedMatrix() = first().indices.map { columnIndex ->
    buildString {
        for (rowIndex in 0..this@transposedMatrix.lastIndex) {
            this.append(this@transposedMatrix[rowIndex][columnIndex])
        }
    }
}

fun List<String>.findReflection() = zipWithNext { a, b -> a == b }
    .mapIndexedNotNull { index, equalityCheck -> index.takeIf { equalityCheck }?.inc() }
    .firstOrNull { equalPair ->
        hasIdealReflectionAt(equalPair)
    } ?: 0

fun List<String>.hasIdealReflectionAt(meetAtIndex: Int): Boolean {
    var above = meetAtIndex - 1
    var below = meetAtIndex
    while (above >= 0 && below <= lastIndex) {
        if (get(above--) != get(below++)) return false
    }
    return true
}