import Direction.*
import Pipes.entry
import Pipes.ground
import Pipes.horizontal
import Pipes.northEast
import Pipes.northWest
import Pipes.southEast
import Pipes.southWest
import Pipes.vertical
import java.lang.IllegalStateException

fun main() {
    Day10().execute()
}

//| is a vertical pipe connecting north and south.
//- is a horizontal pipe connecting east and west.
//L is a 90-degree bend connecting north and east.
//J is a 90-degree bend connecting north and west.
//7 is a 90-degree bend connecting south and west.
//F is a 90-degree bend connecting south and east.
//. is ground; there is no pipe in this tile.
//S is the starting position of the animal; there is a pipe on this tile, but your sketch doesn't show what shape the pipe has.

object Pipes {
    val entry = 'S'
    val ground = '.'
    val horizontal = '-'
    val vertical = '|'
    val northEast = 'L'
    val northWest = 'J'
    val southWest = '7'
    val southEast = 'F'
}

enum class Direction {
    West {
        override fun moveTowards(point: Pair<Int, Int>): Pair<Int, Int> {
            return point.first to point.second - 1
        }
    },
    North {
        override fun moveTowards(point: Pair<Int, Int>): Pair<Int, Int> {
            return point.first - 1 to point.second
        }
    },
    East {
        override fun moveTowards(point: Pair<Int, Int>): Pair<Int, Int> {
            return point.first to point.second + 1
        }
    },
    South {
        override fun moveTowards(point: Pair<Int, Int>): Pair<Int, Int> {
            return point.first + 1 to point.second
        }
    },
    NA;

    open fun moveTowards(point: Pair<Int, Int>): Pair<Int, Int> = point
}

data class Connection(val directions: Pair<Direction, Direction>) {
    fun moveToNext(cameFrom: Pair<Int, Int>, standsAt: Pair<Int, Int>): Pair<Int, Int> {
        val enteredFrom = comingFrom(cameFrom, standsAt)
        val nextStep = if (enteredFrom == directions.first) directions.second else directions.first
        return nextStep.moveTowards(standsAt)
    }
}


fun Pair<Direction, Direction>.has(direction: Direction): Boolean {
    return first == direction || second == direction
}

val connections = mapOf(
    horizontal to Connection(West to East),
    vertical to Connection(South to North),
    northEast to Connection(North to East),
    northWest to Connection(North to West),
    southEast to Connection(South to East),
    southWest to Connection(South to West),
    ground to Connection(NA to NA)
)


fun comingFrom(cameFrom: Pair<Int, Int>, standsAt: Pair<Int, Int>): Direction {
    return when {
        cameFrom.first - standsAt.first < 0 -> North
        cameFrom.first - standsAt.first > 0 -> South
        cameFrom.second - standsAt.second < 0 -> West
        cameFrom.second - standsAt.second > 0 -> East
        else -> throw IllegalStateException("We only handle this directions")
    }

}


class Day10 : ContestDay<Landscape, Int>("input/Day10_part1.txt") {
    override fun partOne(input: Landscape): Int {
        var steps = 0
        val (firstClockwiseStep, firstCounterClockwiseStep) = input.findMovementDirections(input.startingCoordinates)
        steps++
        var (clockwiseMovementCameFrom, clockwiseMovement) = firstClockwiseStep.first to firstClockwiseStep.second.moveToNext(
            input.startingCoordinates,
            firstClockwiseStep.first
        )
        var (counterClockwiseMovementCameFrom, counterClockwiseMovement) = firstCounterClockwiseStep.first to firstCounterClockwiseStep.second.moveToNext(
            input.startingCoordinates,
            firstCounterClockwiseStep.first
        )
        steps++

        while (clockwiseMovement != counterClockwiseMovement) {
            val clockwiseConnection = input.matrix.findConnection(clockwiseMovement)
            var cameFromBackup = clockwiseMovement
            clockwiseMovement =
                clockwiseConnection.moveToNext(clockwiseMovementCameFrom, clockwiseMovement)
            clockwiseMovementCameFrom = cameFromBackup
            val counterClockwiseConnection = input.matrix.findConnection(counterClockwiseMovement)
            cameFromBackup = counterClockwiseMovement
            counterClockwiseMovement =
                counterClockwiseConnection.moveToNext(counterClockwiseMovementCameFrom, counterClockwiseMovement)
            counterClockwiseMovementCameFrom = cameFromBackup
            steps++

        }
        return steps
    }

    fun Landscape.findMovementDirections(startingPoint: Pair<Int, Int>): List<Pair<Pair<Int, Int>, Connection>> {
        val southConnection =
            South.moveTowards(startingPoint).takeIf { it.isWithin(this) }
                ?.let { it to matrix.findConnection(it) }?.takeIf { it.second.directions.has(North) }
        val northConnection =
            North.moveTowards(startingPoint).takeIf { it.isWithin(this) }
                ?.let { it to matrix.findConnection(it) }?.takeIf { it.second.directions.has(South) }
        val eastConnection =
            East.moveTowards(startingPoint).takeIf { it.isWithin(this) }
                ?.let { it to matrix.findConnection(it) }?.takeIf { it.second.directions.has(West) }
        val westConnection =
            West.moveTowards(startingPoint).takeIf { it.isWithin(this) }
                ?.let { it to matrix.findConnection(it) }?.takeIf { it.second.directions.has(West) }
        return listOfNotNull(northConnection, eastConnection, southConnection, westConnection)
    }

    fun List<CharArray>.findConnection(point: Pair<Int, Int>): Connection {
        val c = this[point.first][point.second]
        return connections[c] ?: throw IllegalStateException("Unknown connection $c")
    }

    fun Pair<Int, Int>.isWithin(landscape: Landscape): Boolean {
        return first in 0..landscape.matrix.lastIndex && second in 0..landscape.matrix[first].lastIndex

    }

    override fun partTwo(input: Landscape): Int {
        return 1
    }

    override fun transformInput(input: List<String>): Landscape {
        val matrix = mutableListOf<CharArray>()
        var start: Pair<Int, Int>? = null
        input.forEachIndexed { index, line ->
            val j = line.indexOf(entry)
            if (j != -1) {
                start = index to j
            }
            matrix.add(line.toCharArray())
        }
        return Landscape(matrix, start ?: throw IllegalStateException("The start should be somewhere here!"))
    }
}

data class Landscape(val matrix: List<CharArray>, val startingCoordinates: Pair<Int, Int>)