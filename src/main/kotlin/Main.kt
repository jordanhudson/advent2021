fun main(args: Array<String>) {
//    Day1.part2()
//    Day2.part1()
//    Day2.part2()
//    Day3.part1()
//    Day3.part2()
//    Day4.part1()
//    Day4.part2()
    Day5.part1()
}

object Day5 {

    data class Point(val x: Int, val y: Int)

    data class Line(val p1: Point, val p2: Point) {
        fun isHorizontal(): Boolean =
            p1.y == p2.y

        fun isVertical(): Boolean =
            p1.x == p2.x

        fun allPoints(): List<Point> {
            var points = listOf<Point>()
            if (isVertical()) {
                val (start, end) = if (p1.y < p2.y) Pair(p1, p2) else Pair(p2, p1)
                points = ((start.y)..(end.y)).map { Point(start.x, it) }
            } else if (isHorizontal()) {
                val (start, end) = if (p1.x < p2.x) Pair(p1, p2) else Pair(p2, p1)
                points = ((start.x)..(end.x)).map { Point(it, start.y) }
            }
            return points
        }
    }

    data class Grid(val width: Int, val height: Int) {
        private val cells: List<IntArray> = (0 until width).map { IntArray(height) }

        fun draw(line: Line) {
            line.allPoints().forEach {
                cells[it.x][it.y]++
            }
        }

        fun countCellsDrawnTwiceOrMore(): Int {
            return cells.sumOf { it.count { it > 1 } }
        }

        fun repr(): String {
            val builder = StringBuilder()
            for (y in 0 until height) {
                for (x in 0 until width) {
                    builder.append(cells[x][y])
                }
                builder.append("\n")
            }
            return builder.toString()
        }
    }

    fun parse(rawLines: List<String>): List<Line> {
        val regex = Regex("(\\d+),(\\d+) -> (\\d+),(\\d+)")
        return rawLines.map {
            val (x1, y1, x2, y2) = regex.find(it)!!.destructured
            Line(Point(x1.toInt(), y1.toInt()), Point(x2.toInt(), y2.toInt()))
        }
    }

    fun part1() {
        val lines = parse(Util.getResourceFileLines("day5.txt"))
        val grid = Grid(1000, 1000)
        lines
            .filter { it.isVertical() or it.isHorizontal() }
            .forEach { grid.draw(it) }

        println("cells drawn twice or more: ${grid.countCellsDrawnTwiceOrMore()}")
        //println(grid.repr())  don't run this unless the dataset is small
    }
}

object Day4 {

    data class BingoCard(val numbers: List<List<Int>>) {
        data class CardSquare(val number: Int) {
            var marked = false
        }

        private val rows: List<List<CardSquare>> = numbers.map { row -> row.map { CardSquare(it) } }

        private fun columns(): List<List<CardSquare>> {
            return (rows.indices).map { colNum -> rows.map { row -> row[colNum] } }
        }

        fun call(number: Int): Int {
            rows.forEach { row ->
                row.forEach { square ->
                    if (square.number == number)
                        square.marked = true
                }
            }
            return if (hasWon()) number * sumOfUnmarked() else -1
        }

        fun hasWon(): Boolean {
            var won = false
            rows.forEach { row ->
                if (row.all { it.marked })
                    won = true
            }
            columns().forEach { col ->
                if (col.all { it.marked })
                    won = true
            }
            return won
        }

        private fun sumOfUnmarked(): Int {
            var unmarkedSum: Int = 0
            rows.forEach { it.forEach { if (!it.marked) unmarkedSum += it.number } }
            return unmarkedSum
        }
    }

    fun buildNumbers(input: List<String>): List<Int> =
        input[0].split(",").map { it.toInt() }

    fun buildCards(input: List<String>): List<BingoCard> =
        input
            .drop(1)// ignore the first row
            .map { it.trim() }
            .map { it.split("\\s+".toRegex()) } // split string into list
            .filter { it.size == 5 } // skip empty lines
            .map { it.map { it.toInt() } } // strings to ints
            .withIndex()
            .groupBy { it.index / 5 } // integer division. first 5 is 0. second 5 is 1. third 5 is 2, etc
            .values // we don't need the key
            .map { it.map { it.value } } // unwrap what withIndex did
            .map { BingoCard(it) }

    fun part1() {
        val input: List<String> = Util.getResourceFileLines("day4.txt")
        val theNumbers: List<Int> = buildNumbers(input)
        val cards = buildCards(input)

        // play bingo!
        var someoneWon = false
        for (number in theNumbers.withIndex()) {
            println("Call round ${number.index}: ${number.value}")
            cards.forEachIndexed { index, card ->
                val score = card.call(number.value)
                if (score > -1) {
                    println("Card $index won. Score: $score")
                    someoneWon = true
                }
            }
            if (someoneWon) break
        }
    }

    fun part2() {
        val input: List<String> = Util.getResourceFileLines("day4.txt")
        val theNumbers: List<Int> = buildNumbers(input)
        val cards = buildCards(input)

        // play reverse bingo (find which card wins last)
        println("Loser bingo")
        for (number in theNumbers.withIndex()) {
            println("Call round ${number.index}: ${number.value}")
            cards.forEachIndexed { index, card ->
                if (!card.hasWon()) {
                    val score = card.call(number.value)
                    if (score > -1) {
                        println("Card $index won. Score: $score")
                    }
                }
            }
            // was it the last card to win?  (has everyone card now won?)
            if (cards.all { it.hasWon() }) {
                println("Every card is now a winner!")
                break
            }
        }
    }
}

object Day3 {
    fun part1() {
        val diagReport: List<String> = Util.getResourceFileLines("day3.txt")
        val gamma: List<Char> = (0 until 12).map { mostCommonValueAtIndex(diagReport, it) }
        val epsilon: List<Char> = (0 until 12).map { leastCommonValueAtIndex(diagReport, it) }
        println("Power Consumption: ${gamma.joinToString("").toInt(2) * epsilon.joinToString("").toInt(2)}")
    }

    fun part2() {
        val diagReport: List<String> = Util.getResourceFileLines("day3.txt")

        var oxygenRating = diagReport
        for (i in 0 until 12) {
            val columnSlice = oxygenRating.map { it[i] }
            val onePercentage = percentageOfValuesThatAre1(columnSlice)
            val mostCommonChar = if (onePercentage >= 0.5) '1' else '0' // exactly .5 is key here
            oxygenRating = oxygenRating.filter { it[i] == mostCommonChar }
            if (oxygenRating.size == 1) break
        }
        var co2Rating = diagReport
        for (i in 0 until 12) {
            val columnSlice = co2Rating.map { it[i] }
            val onePercentage = percentageOfValuesThatAre1(columnSlice)
            val leastCommonChar = if (onePercentage >= 0.5) '0' else '1' // exactly .5 is key here
            co2Rating = co2Rating.filter { it[i] == leastCommonChar }
            if (co2Rating.size == 1) break
        }
        println("Life Support Rating: ${oxygenRating.first().toLong(2) * co2Rating.first().toLong(2)}")
    }

    private fun percentageOfValuesThatAre1(list: List<Char>): Double {
        return list.filter { it == '1' }.size.toDouble() / list.size.toDouble()
    }

    private fun mostCommonValueAtIndex(list: List<String>, index: Int): Char {
        return list
            .map { it[index] }
            .groupBy { it }
            .maxBy { it.value.size }
            .key
    }

    private fun leastCommonValueAtIndex(list: List<String>, index: Int): Char {
        return list
            .map { it[index] }
            .groupBy { it }
            .minBy { it.value.size }
            .key
    }
}

object Day2 {
    data class MutablePoint(var x: Int, var y: Int)

    fun part1() {
        val point = MutablePoint(0, 0)
        val cmds = Util.getResourceFileLines("day2.txt")
        cmds.forEach { rawCmd ->
            val cmdParts = rawCmd.split(" ")
            val direction = cmdParts[0]
            val amount = cmdParts[1].toInt()
            when (direction) {
                "forward" -> point.x += amount
                "down" -> point.y += amount
                "up" -> point.y -= amount
                else -> throw Exception("Unknown direction: $direction")
            }
        }
        println("Final location: $point. Coordinate product: ${point.x * point.y}")
    }

    fun part2() {
        val point = MutablePoint(0, 0)
        var aim: Int = 0
        val cmds = Util.getResourceFileLines("day2.txt")
        cmds.forEach { rawCmd ->
            val cmdParts = rawCmd.split(" ")
            val direction = cmdParts[0]
            val amount = cmdParts[1].toInt()
            when (direction) {
                "forward" -> {
                    point.x += amount
                    point.y += (amount * aim)
                }

                "down" ->
                    aim += amount

                "up" ->
                    aim -= amount

                else ->
                    throw Exception("Unknown direction: $direction")
            }
        }
        println("Final location: $point. Coordinate product: ${point.x * point.y}")
    }
}

object Day1 {
    fun part2() {
        val day01Depths = Util.getResourceFileLines("day1.txt").map { it.toInt() }
        var increases = 0
        for (i in 3 until day01Depths.size) {
            val prevWindow = day01Depths.get(i - 3) + day01Depths.get(i - 2) + day01Depths.get(i - 1)
            val currWindow = day01Depths.get(i - 2) + day01Depths.get(i - 1) + day01Depths.get(i)
            if (currWindow > prevWindow)
                increases++
        }
        println("Increases: $increases")
    }
}
