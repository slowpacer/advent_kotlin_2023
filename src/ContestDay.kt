abstract class ContestDay<Input, Output>(private val inputPath: String) {

    fun execute() {
        val input = readInput(inputPath)
        val transformedInput = transformInput(input)
        partOne(transformedInput).println()
        partTwo(transformedInput).println()
    }

    abstract fun partOne(input: Input): Output
    abstract fun partTwo(input: Input): Output

    internal abstract fun transformInput(input: List<String>): Input
}