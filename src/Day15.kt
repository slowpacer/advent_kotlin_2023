fun main() {
    Day15().execute()
}

class Day15 : ContestDay<List<List<CharArray>>, Int>("input/Day15_part1_test.txt") {

    override fun partOne(input: List<List<CharArray>>): Int {
        var allLinesSum = 0
        input.forEach {
            var sum = 0
            it.forEach { chunk ->
                var lineSum = 0
                chunk.forEach { char ->
                    val code = char.code
                    lineSum += code
                    lineSum *= 17
                    lineSum %= 256
                }
                sum += lineSum
            }
            allLinesSum += sum
        }
        return allLinesSum
    }

    override fun transformInput(input: List<String>): List<List<CharArray>> {
        return input.map {
            it.split(",").map { chunks -> chunks.toCharArray() }
        }
    }
}