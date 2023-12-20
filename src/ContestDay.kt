abstract class ContestDay<Input, Output>(private val inputPath: String) {

    fun execute() {
        val input = readInput(inputPath)
        partOne(transformInput(input)).println()
        partTwo(transformInput(input)).println()
    }

    abstract fun partOne(input: Input): Output
    abstract fun partTwo(input: Input): Output

    internal abstract fun transformInput(input: List<String>): Input
}