import java.util.PriorityQueue

fun main() {
    Day7().execute()
}

private var gameWithJoker = false
private val cardsPriority: Map<Char, Int>
    get() = if (gameWithJoker) cardsPriorityWithJoker else cardsPriorityWithoutJoker

private val cardsPriorityWithoutJoker = "23456789TJQKA".toCharArray().withIndex()
    .associate { it.value to it.index }

private val cardsPriorityWithJoker = "J23456789TQKA".toCharArray().withIndex().associate { it.value to it.index }

class Day7 : ContestDay<List<Hand>, Int>("input/Day07_part1.txt") {
    override fun partOne(input: List<Hand>): Int {
        val priorityQueue = PriorityQueue<Hand>(input.size) { first, second -> second.compareTo(first) }
        input.forEach {
            priorityQueue.add(it)
        }

        var sum = 0
        while (priorityQueue.isNotEmpty()) {
            sum += priorityQueue.size * priorityQueue.poll().bid
        }
        gameWithJoker = true
        return sum
    }

    override fun partTwo(input: List<Hand>): Int {
        return partOne(input)
    }


    override fun transformInput(input: List<String>): List<Hand> {
        val result = input.map {
            val (hand, bid) = it.split(" ")
            Hand(hand, bid.toInt())
        }
        return result
    }
}

enum class HandType {
    FiveOfAKind {
        override fun isOfKind(hand: Hand) =
            hand.mapping.size == 1

    },
    FourOfAKind {
        override fun isOfKind(hand: Hand): Boolean {
            return hand.mapping.size == 2 && hand.mapping.values.any { it == 4 }
        }
    },
    FullHouse {
        override fun isOfKind(hand: Hand): Boolean {
            return hand.mapping.size == 2 && hand.mapping.values.any { it == 3 }
        }
    },
    ThreeOfAKind {
        override fun isOfKind(hand: Hand): Boolean {
            return hand.mapping.size == 3 && hand.mapping.values.any { it == 3 }
        }
    },
    TwoPair {
        override fun isOfKind(hand: Hand): Boolean {
            return hand.mapping.size == 3 && hand.mapping.values.filter { it == 2 }.size == 2
        }
    },
    OnePair {
        override fun isOfKind(hand: Hand): Boolean {
            return hand.mapping.size == 4 && hand.mapping.values.any { it == 2 }
        }
    },
    HighCard {
        override fun isOfKind(hand: Hand): Boolean {
            return hand.mapping.size == 5
        }
    };

    abstract fun isOfKind(hand: Hand): Boolean
}

data class Hand(
    val cards: CharSequence,
    val bid: Int,
) : Comparable<Hand> {

    val mapping: MutableMap<Char, Int> = mutableMapOf()
    private val type: HandType

    init {
        cards.forEach { card ->
            mapping.putIfAbsent(card, 1)?.let {
                mapping[card] = it + 1
            }
        }
        if (gameWithJoker) {
            val wildcardCards = mapping['J'] ?: 0
            if (wildcardCards in 1..4) {
                mapping.remove('J')
                val maxEntry = mapping.maxBy { it.value }
                mapping[maxEntry.key] = maxEntry.value + wildcardCards
            }
        }
        type = HandType.entries.first { it.isOfKind(this) }
    }

    override fun compareTo(other: Hand): Int {
        val comparison = other.type.ordinal - type.ordinal
        return comparison.takeIf { it != 0 } ?: cardByCardComparison(other.cards)
    }

    private fun cardByCardComparison(otherCards: CharSequence): Int {
        cards.forEachIndexed { index, currentCard ->
            val otherCard = otherCards[index]
            val comparison = cardsPriority[currentCard]?.compareTo(cardsPriority[otherCard] ?: return 0) ?: return 0
            if (comparison != 0) return comparison
        }
        return 0
    }
}
