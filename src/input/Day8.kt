package input

import ContestDay

fun main() {
    Day8().execute()
}

class Day8 : ContestDay<WalkThrough, Int>("input/Day08_part1.txt") {

    private val regexp = "[A-Z]{3}".toRegex()
    override fun partOne(input: WalkThrough): Int {
        var result: String? = null
        var sameNodeIteration = 0
        var node = input.mapping[input.startAt]
        var steps = 0
        while (result != "ZZZ" /*&& sameNodeIteration != 2*/) {
            val (left, right) = requireNotNull(node)
            val instruction = input.instructions[steps % input.instructions.size]
            val direction = left.takeIf { instruction == 'L' } ?: right
            result = direction
            node = input.mapping[direction]
            steps++
        }
        return steps
    }

    override fun partTwo(input: WalkThrough): Int {
        return 1
    }

    override fun transformInput(input: List<String>): WalkThrough {
        val instructions = mutableListOf<Char>()
        var instructionReading = true
        var tree: Tree? = null
        val mapping = mutableMapOf<String, Pair<String, String>>()
        input.forEach {
            if (instructionReading) {
                instructions.addAll(it.toCharArray().toList())
                instructionReading = it.isNotBlank()
            } else {
                val (self, left, right) = regexp.findAll(it).map { match -> match.value }.toList()
                if (tree == null) {
                    tree = Tree(Node(self, Node(reference = left), Node(reference = right)))
                }
                mapping[self] = left to right
            }
        }
        return WalkThrough(
            instructions, /*tree ?: throw IllegalStateException("Tree should be initialized by now")*/
            mapping,
        )
    }
}

data class Tree(val root: Node)

data class Node(val reference: String, var left: Node? = null, var right: Node? = null)

data class WalkThrough(
    val instructions: List<Char>,
    val mapping: Map<String, Pair<String, String>>,
    val startAt: String = "AAA"
)
