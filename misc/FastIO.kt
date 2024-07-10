@file:Suppress("unused", "MemberVisibilityCanBePrivate")

package ktcp.misc

import java.io.PrintWriter
import java.util.*

@JvmInline
value class FastIO(val isLocal: Boolean) {
    companion object {
        private val input = System.`in`!!
        private val output = System.out!!

        private val reader = input.bufferedReader()
        private var tokenizer = StringTokenizer("")
        private val writer = PrintWriter(output)

        private var debugStackCnt = 0
    }

    fun <T> withWriter(block: PrintWriter.() -> T) = with(writer) {
        block().also { debugFlush() }
    }

    fun print(x: Any) = withWriter { print(x) }
    fun println(x: Any) = withWriter { println(x) }
    fun printf(x: String, vararg y: Any) = withWriter { printf(x, *y) }!!
    fun flush() = writer.flush()

    fun debugFlush() = if (isLocal) flush() else Unit
    fun debug(block: FastIO.() -> Unit) = if (isLocal) {
        this.run {
            if (debugStackCnt == 0) {
                print("\u001b[32m")
            }
            ++debugStackCnt
            block()
            --debugStackCnt
            if (debugStackCnt == 0) {
                print("\u001b[0m")
            }
        }
    } else Unit

    fun read(): String {
        while (tokenizer.hasMoreTokens().not()) {
            tokenizer = StringTokenizer(
                reader.readLine() ?: return "", " "
            )
        }
        return tokenizer.nextToken()
    }

    fun readLine(): String? = reader.readLine()
    fun readLn() = reader.readLine()!!
    fun readInt() = read().toInt()
    fun readIntPair() = readInts(2).let { (a, b) -> a to b }
    fun readDouble() = read().toDouble()
    fun readLong() = read().toLong()
    fun readStrings(n: Int) = List(n) { read() }
    fun readLines(n: Int) = List(n) { readLn() }
    fun readInts(n: Int) = List(n) { read().toInt() }
    fun readIntArray(n: Int) = IntArray(n) { read().toInt() }
    fun readDoubles(n: Int) = List(n) { read().toDouble() }
    fun readDoubleArray(n: Int) = DoubleArray(n) { read().toDouble() }
    fun readLongs(n: Int) = List(n) { read().toLong() }
    fun readLongArray(n: Int) = LongArray(n) { read().toLong() }
}

fun withFastIO(isLocal: Boolean = true, block: FastIO.() -> Unit) =
    with(FastIO(isLocal)) {
        block().also { flush() }
    }

// exports: withFastIO
// exports: FastIO