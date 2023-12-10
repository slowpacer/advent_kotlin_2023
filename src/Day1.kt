fun main() {
    val input = readInput("Day01_part2")
    part2(input).println()
}

const val minWrittenDigitLength = 3
val wordsToDigit = mapOf(
    "one" to 1,
    "two" to 2,
    "three" to 3,
    "four" to 4,
    "five" to 5,
    "six" to 6,
    "seven" to 7,
    "eight" to 8,
    "nine" to 9,
)

fun part1(input: List<String>): Int {
    val sum = input.sumOf { line ->
        val firstDigit = line.first { it.isDigit() } - '0'
        val secondDigit = line.last { it.isDigit() } - '0'
        firstDigit * 10 + secondDigit
    }
    return sum
}

@Suppress("Duplicates")
fun part2(input: List<String>): Int {
    return input.sumOf { line ->
        val firstNumericDigitIndex = line.indexOfFirst { it.isDigit() }
        val lastNumericDigitIndex = line.indexOfLast { it.isDigit() }
        val firstWrittenDigitMaxLength = (firstNumericDigitIndex + 1).takeIf { it != 0 } ?: line.length
        val lastWrittenDigitMaxLength =
            (line.length - lastNumericDigitIndex).takeIf { it in 0..line.length } ?: line.length
        val firstResultingDigit =
            line.findWrittenDigitWithinBoundaries(firstNumericDigitIndex, firstWrittenDigitMaxLength, false)
        val lastResultingDigit =
            line.findWrittenDigitWithinBoundaries(lastNumericDigitIndex, lastWrittenDigitMaxLength, true)
        firstResultingDigit * 10 + lastResultingDigit
    }
}

fun String.findWrittenDigitWithinBoundaries(
    boundaryIndex: Int,
    writtenDigitMaxLength: Int,
    iterateFromEnd: Boolean = false
): Int {
    val targetNumericDigit = if (boundaryIndex != -1) this[boundaryIndex] - '0' else null
    val targetWrittenDigit = if (writtenDigitMaxLength >= minWrittenDigitLength) {
        var anchoredWrittenDigitMatchIndex = if (iterateFromEnd) Int.MIN_VALUE else Int.MAX_VALUE
        var matchingWord: String? = null
        wordsToDigit.keys.forEach { writtenDigit ->
            if (writtenDigit.length <= writtenDigitMaxLength) {
                val startIndex = if (iterateFromEnd) length else 0
                val endIndex = if (iterateFromEnd) length - writtenDigitMaxLength else writtenDigitMaxLength - 1
                val runningWrittenDigitIndex =
                    bidirectionalIndexOf(writtenDigit, startIndex, endIndex, true, iterateFromEnd).takeIf { it != -1 }
                        ?: return@forEach
                // define comparison condition logic for both iteration directions
                if ((iterateFromEnd && runningWrittenDigitIndex > anchoredWrittenDigitMatchIndex) || (!iterateFromEnd && runningWrittenDigitIndex < anchoredWrittenDigitMatchIndex)) {
                    anchoredWrittenDigitMatchIndex = runningWrittenDigitIndex
                    matchingWord = writtenDigit
                }
            }
        }
        wordsToDigit[matchingWord]
    } else null
    return targetWrittenDigit ?: targetNumericDigit ?: -1
}

private fun String.bidirectionalIndexOf(
    other: String,
    startIndex: Int,
    endIndex: Int,
    ignoreCase: Boolean,
    last: Boolean = false
): Int {
    val indices = if (!last)
        startIndex.coerceAtLeast(0)..endIndex.coerceAtMost(length)
    else
        startIndex.coerceAtMost(lastIndex) downTo endIndex.coerceAtLeast(0)

    for (index in indices) {
        if (other.regionMatches(0, this, index, other.length, ignoreCase))
            return index
    }
    return -1
}

@Suppress("Unused", "Duplicates")
fun part2StraightForwardBackup(input: List<String>): Int {
    return input.sumOf { line ->
        val firstNumericDigitIndex = line.indexOfFirst { it.isDigit() }
        val lastNumericDigitIndex = line.indexOfLast { it.isDigit() }
        val firstWrittenDigitMaxLength = (firstNumericDigitIndex + 1).takeIf { it != 0 } ?: line.length
        val lastWrittenDigitMaxLength =
            (line.length - lastNumericDigitIndex).takeIf { it in 0..line.length } ?: line.length
        val firstNumericDigit = if (firstNumericDigitIndex != -1) line[firstNumericDigitIndex] - '0' else null
        val firstWrittenDigit = if (firstWrittenDigitMaxLength >= 3) {
            var smallestIndex = Int.MAX_VALUE
            var digitKey: String? = null
            wordsToDigit.keys.forEach { writtenDigit ->
                if (writtenDigit.length <= firstWrittenDigitMaxLength) {
                    val tmpSmallestIndex =
                        line.bidirectionalIndexOf(writtenDigit, 0, firstWrittenDigitMaxLength - 1, true)
                    if (tmpSmallestIndex != -1 && tmpSmallestIndex < smallestIndex) {
                        smallestIndex = tmpSmallestIndex
                        digitKey = writtenDigit
                    }
                }
            }
            wordsToDigit[digitKey]
        } else null
        val firstResultingDigit = firstWrittenDigit ?: firstNumericDigit ?: -1

        val lastNumericDigit = if (lastNumericDigitIndex != -1) line[lastNumericDigitIndex] - '0' else null
        val lastWrittenDigit = if (lastWrittenDigitMaxLength >= 3) {
            var biggestIndex = Int.MIN_VALUE
            var digitKey: String? = null
            wordsToDigit.keys.forEach { writtenDigit ->
                if (writtenDigit.length <= lastWrittenDigitMaxLength) {
                    val tmpBiggest =
                        line.bidirectionalIndexOf(
                            writtenDigit,
                            line.length,
                            line.length - lastWrittenDigitMaxLength,
                            ignoreCase = true,
                            last = true
                        )
                    if (tmpBiggest != -1 && tmpBiggest > biggestIndex) {
                        biggestIndex = tmpBiggest
                        digitKey = writtenDigit
                    }
                }
            }
            wordsToDigit[digitKey]
        } else null
        val lastResultingDigit = lastWrittenDigit ?: lastNumericDigit ?: -1
        firstResultingDigit * 10 + lastResultingDigit
    }
}
