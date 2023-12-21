fun main() {
    Day5().execute()
}

class Day5 : ContestDay<SeedToLocationPath, Long>("input/Day05_part1.txt") {
    override fun partOne(input: SeedToLocationPath): Long {
        val locations = input.seedsToLocations(input.seeds.asSequence())
        return locations.min()
    }

    private fun SeedToLocationPath.seedsToLocations(seedsSequence: Sequence<Long>): List<Long> {
        return seedsSequence.map { findDestination(it, seedToSoilStages) }
            .map { findDestination(it, soilToFertilizerStages) }
            .map { findDestination(it, fertilizerToWaterStages) }
            .map { findDestination(it, waterToLightStages) }
            .map { findDestination(it, lightToTemperatureStages) }
            .map { findDestination(it, temperatureToHumidityStages) }
            .map {
                findDestination(it, humidityToLocationStages)
            }.toList()
    }

    private fun SeedToLocationPath.seedToLocations(seed: Long): Long {
        return findDestination(seed, seedToSoilStages).let {
            findDestination(it, soilToFertilizerStages)
        }.let {
            findDestination(it, fertilizerToWaterStages)
        }.let {
            findDestination(it, waterToLightStages)
        }.let {
            findDestination(it, lightToTemperatureStages)
        }.let {
            findDestination(it, temperatureToHumidityStages)
        }.let {
            findDestination(it, humidityToLocationStages)
        }
    }

    // runs around  two mins, as it iterates over all seed, soils and so on.
    // different approach should be applied, applying soil filters on seeds and so on
    // TODO: rebuild
    override fun partTwo(input: SeedToLocationPath): Long {
        var minLocation = Long.MAX_VALUE
        for (i in 0..input.seeds.lastIndex step 2) {
            val seedRangeStart = input.seeds[i]
            val seedRangeEnd = input.seeds[i] + input.seeds[i + 1]
            for (seed in seedRangeStart until seedRangeEnd) {
                val seedLocation = input.seedToLocations(seed)
                if (seedLocation < minLocation) minLocation = seedLocation
            }
        }
        return minLocation
    }

    override fun transformInput(input: List<String>): SeedToLocationPath {
        val stages = StageBlock.entries
        var stageIndex = 0
        var currentStage: StageBlock = stages[stageIndex]
        val seeds = input[0].removePrefix(currentStage.key).split(" ").map { it.toLong() }
        var lineIndex = 0
        while (++lineIndex <= input.lastIndex) {
            val line = input[lineIndex]
            val breakBeforeNextStage = line.isEmpty()
            if (breakBeforeNextStage) {
                stageIndex++
                lineIndex++
                currentStage = stages[stageIndex]
            } else {
                currentStage.stages.add(buildSourceToDestMap(line))
            }
        }


        return SeedToLocationPath(
            seeds,
            StageBlock.SeedToSoil.convertStagesToIntervalTree(),
            StageBlock.SoilToFertilizer.convertStagesToIntervalTree(),
            StageBlock.FertilizerToWater.convertStagesToIntervalTree(),
            StageBlock.WaterToLight.convertStagesToIntervalTree(),
            StageBlock.LightToTemperature.convertStagesToIntervalTree(),
            StageBlock.TemperatureToHumidity.convertStagesToIntervalTree(),
            StageBlock.HumidityToLocation.convertStagesToIntervalTree()
        )
    }

    private fun buildSourceToDestMap(line: String): Stage {
        val lineMappings = numberRegexp.findAll(line).mapTo(mutableListOf()) { it.value.toLong() }
        val (destination, source, range) = lineMappings
        return Stage(destination, source, range)
    }

    private fun findDestination(source: Long, intervalTree: IntervalTree): Long {
        val rangeNode = intervalTree.search(source) ?: return source
        return source + rangeNode.range.destinationShift
    }
}

data class Stage(
    val destination: Long,
    val source: Long,
    val range: Long,
)

data class SeedToLocationPath(
    val seeds: List<Long>,
    val seedToSoilStages: IntervalTree,
    val soilToFertilizerStages: IntervalTree,
    val fertilizerToWaterStages: IntervalTree,
    val waterToLightStages: IntervalTree,
    val lightToTemperatureStages: IntervalTree,
    val temperatureToHumidityStages: IntervalTree,
    val humidityToLocationStages: IntervalTree,
)

enum class StageBlock(val key: String, val stages: MutableList<Stage> = mutableListOf()) {
    SeedsGathering("seeds: "),
    SeedToSoil("seed-to-soil map: "),
    SoilToFertilizer("soil-to-fertilizer map: "),
    FertilizerToWater("fertilizer-to-water map: "),
    WaterToLight("water-to-light map: "),
    LightToTemperature("light-to-temperature map: "),
    TemperatureToHumidity("temperature-to-humidity map: "),
    HumidityToLocation("humidity-to-location map: "), ;

    fun convertStagesToIntervalTree(): IntervalTree {
        val ranges = stages.map {
            Range(
                sourceRange = it.source..it.source + it.range,
                destinationShift = it.destination - it.source
            )
        }
        return IntervalTree(ranges)
    }
}

data class Range(
    val sourceRange: LongRange,
    val destinationShift: Long
)

class RangeNode(val range: Range, var max: Long) {
    var left: RangeNode? = null
    var right: RangeNode? = null
}

class IntervalTree(ranges: List<Range>) {

    private val root: RangeNode?

    init {
        root = constructFromList(ranges.sortedBy { it.sourceRange.first }, 0, ranges.size)
    }

    private fun constructFromList(sortedRanges: List<Range>, start: Int, end: Int): RangeNode? {
        if (start >= end) return null
        val midIndex = start + (end - start) / 2
        val midRange = sortedRanges[midIndex]

        val node = RangeNode(midRange, midRange.sourceRange.last)
        node.left = constructFromList(sortedRanges, start, midIndex)
        node.right = constructFromList(sortedRanges, midIndex + 1, end)

        node.max = maxOf(node.max, node.left?.max ?: Long.MIN_VALUE, node.right?.max ?: Long.MIN_VALUE)

        return node
    }

    fun search(number: Long): RangeNode? {
        return search(root, number)
    }

    private fun search(node: RangeNode?, number: Long): RangeNode? {
        if (node == null) return null
        if (number in node.range.sourceRange) return node

        node.left?.let {
            if (it.max > number) {
                return search(it, number)
            }
        }
        return search(node.right, number)
    }

}