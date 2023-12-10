fun main() {
    val input = readInput("input/Day04_part1.txt")
    dayFourPartTwo(input).println()
}

fun dayFourPartOne(input: List<String>): Int {
    var winningPointsSum = 0
    input.forEachIndexed { index, card ->
        val cardPrefixRegex = "Card\\s+${index + 1}:".toRegex()
        val regexNumberPattern = "\\d+".toRegex()
        val (winningNumbersCardPart, actualNumbersCardPart) = card.replace(cardPrefixRegex, "").split(" | ")
        val winningNumbers = regexNumberPattern.findAll(winningNumbersCardPart).mapTo(mutableSetOf()) { it.value }
        val actualNumbers = regexNumberPattern.findAll(actualNumbersCardPart).toSet()
        var actualWins = 0
        actualNumbers.forEach {
            if (winningNumbers.contains(it.value)) {
                actualWins++
            }
        }
        val cardPoints = if (actualWins > 1) 1 shl actualWins - 1 else actualWins
        winningPointsSum += cardPoints
    }
    return winningPointsSum
}

fun dayFourPartTwo(input: List<String>): Int {
    val cardsQueue = ArrayDeque<Card>()
    val cardDeck = mutableMapOf<Int, Card>()

    //building the queue and the card deck
    input.forEachIndexed { index, cardInput ->
        val cardPrefixRegex = "Card\\s+${index + 1}:".toRegex()
        val regexNumberPattern = "\\d+".toRegex()
        val (winningNumbersCardPart, actualNumbersCardPart) = cardInput.replace(cardPrefixRegex, "").split(" | ")
        val winningNumbers = regexNumberPattern.findAll(winningNumbersCardPart).mapTo(mutableSetOf()) { it.value }
        val actualNumbers = regexNumberPattern.findAll(actualNumbersCardPart).toSet()
        var actualWins = 0
        actualNumbers.forEach {
            if (winningNumbers.contains(it.value)) {
                actualWins++
            }
        }
        val card = Card(index, actualWins)
        cardDeck[index] = card
        cardsQueue.add(card)
    }
    var allCardsAmount = cardsQueue.size
    while (cardsQueue.isNotEmpty()){
        val currentCard = cardsQueue.removeFirst()
        for (i in 1..currentCard.winningCombinations){
            cardDeck[currentCard.id + i]?.let {
                cardsQueue.add(it)
                allCardsAmount++
            }
        }
    }
    return allCardsAmount
}

data class Card(
    val id: Int,
    val winningCombinations: Int
)