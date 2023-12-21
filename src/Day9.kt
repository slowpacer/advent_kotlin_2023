fun main() {
    Day9().execute()
}

class Day9 : ContestDay<List<List<Int>>, Int>("input/Day09_part1.txt") {
    override fun partOne(input: List<List<Int>>): Int {
        return input.sumOf { predictNextElement(it) }
    }
    
    override fun partTwo(input: List<List<Int>>): Int {
        return input.sumOf { predictPreviousElement(it) }
    }

    fun predictNextElement(history: List<Int>): Int {
        val matrix: MutableList<List<Int>> = buildPredictionMatrix(history)
        var runningPrediction = 0
        for (i in matrix.lastIndex  downTo 1){
            val nextPrediction = matrix[i - 1][matrix[i - 1].lastIndex]
            runningPrediction += nextPrediction
        }
        return runningPrediction
    }

    fun predictPreviousElement(history: List<Int>):Int{
        val matrix: MutableList<List<Int>> = buildPredictionMatrix(history)
        var runningPrediction = 0
        for (i in matrix.lastIndex  downTo 1){
            val previousPrediction = matrix[i - 1][0]
            runningPrediction = previousPrediction - runningPrediction
        }
        return runningPrediction
    }

    private fun buildPredictionMatrix(history: List<Int>): MutableList<List<Int>> {
        val matrix: MutableList<List<Int>> = mutableListOf(history)
        var currentRow = history
        var allZeros = false
        while (!allZeros) {
            var zerosAmount = 0
            val newRow = currentRow.zipWithNext { current, next ->
                val newElement = next - current
                if (newElement == 0) zerosAmount++
                newElement
            }
            matrix.add(newRow)
            currentRow = newRow
            allZeros = zerosAmount == newRow.size
        }
        return matrix
    }

    override fun transformInput(input: List<String>): List<List<Int>> {
        return input.map { numberWithSignRegexp.findAll(it).map { match -> match.value.toInt() }.toList() }
    }
}