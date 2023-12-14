import kotlin.math.*

fun main() {
    Day6().execute()
}

class Day6 : ContestDay<List<String>, Int>("input/Day06_part1.txt") {

    val regexp = "\\D+:\\s+".toRegex()
    val numberRegexp = "\\d+".toRegex()
    val whitespacesRegexp = "\\s+".toRegex()
    override fun partOne(input: List<String>): Int {

        val timePureInput = regexp.replace(input[0], "")
        val distancePureInput = regexp.replace(input[1], "")
        val times = numberRegexp.findAll(timePureInput)
        val distances = numberRegexp.findAll(distancePureInput)
        var runningMargin = 0
        times.zip(distances).forEach { (time, distance) ->
            val ways =
                findWaysToBeatTheRecord(time.value.toInt(), distance.value.toInt()).takeIf { it > 0 } ?: return@forEach
            runningMargin = if (runningMargin == 0) ways else runningMargin * ways
        }


        return runningMargin
    }

    fun findWaysToBeatTheRecord(time: Int, record: Int): Int {
        var waysToBeat = 0
        for (speed in 1..time) {
            val distance = speed * (time - speed)
            if (distance > record) waysToBeat++
        }
        return waysToBeat
    }

    override fun partTwo(input: List<String>): Int {
        val timePureInput = regexp.replace(input[0], "")
        val distancePureInput = regexp.replace(input[1], "")
        val time = whitespacesRegexp.replace(timePureInput, "").toDouble()
        val distance = whitespacesRegexp.replace(distancePureInput, "").toDouble()
        val d = time.pow(2.0) - 4 * distance
        val x1 = floor(-time + sqrt(d)) / 2.0
        val x2 = ceil((((-time - sqrt(d)) / 2.0)) - 1)
        val waysToWin = abs(x2 - x1)
        return waysToWin.toInt()
    }

    override fun transformInput(input: List<String>): List<String> {
        return input
    }
}