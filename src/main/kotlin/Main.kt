fun main(args: Array<String>) {
    Day1.part2()
    Day2.part1()
    Day2.part2()
    Day3.part1()
    Day3.part2()
    Day4.part1()
}

object Day4 {

    fun part1() {
        val input: List<String> = Util.getResourceFileLines("day4.txt")
        val theNumbers = input[0].split(",").map { it.toInt() }
        val cards = input
            .drop(1)// ignore the first row
            .map { it.trim() }
            .map { it.split("\\s+".toRegex()) }// split a line into 5 values
            .filter { it.size == 5 } // skip empty lines
            .map { it.map { it.toInt() } } // strings to ints
            .withIndex()
            .groupBy { it.index / 5 } // integer division. first 5 is 0. second 5 is 1. third 5 is 2, etc
            .values // we don't need the key
            .map { it.map { it.value } } // unindex
            .map { BingoCard(it) }

        cards.forEach { println(it) }

    }

    data class BingoCard(val fiveByFiveMatrix: List<List<Int>>) {

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
