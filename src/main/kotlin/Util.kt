import java.lang.Exception

object Util {
    fun getResourceFileLines(path: String): List<String> {
        val x = this::class.java.getResourceAsStream(path)
        if (x == null)
            throw Exception("java getResourceAsStream returned null for $path")
        else
            return x.bufferedReader().readLines()
    }
}