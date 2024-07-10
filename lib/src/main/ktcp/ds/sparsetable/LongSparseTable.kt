@file:Suppress("unused", "DuplicatedCode")

package ktcp.ds.sparsetable

import ktcp.math.*

class LongSparseTable @PublishedApi internal constructor(
    source: LongArray,
    private val nilValue: Long = 0L,
    private val operation: (Long, Long) -> Long,
) {
    private val store = Array(log2Floor(source.size) + 1) {
        if (it == 0) {
            source
        } else {
            LongArray(source.size) { nilValue }
        }
    }

    init {
        for (i in 1..<store.size) {
            for (j in 0..source.size - (1 shl i)) {
                store[i][j] = operation(
                    store[i - 1][j],
                    store[i - 1][j + (1 shl (i - 1))]
                )
            }
        }
    }

    val size: Int = store[0].size

    fun query(l: Int, r: Int): Long {
        val lg = log2Floor(r - l + 1)
        return operation(store[lg][l], store[lg][r - (1 shl lg) + 1])
    }

    fun queryForward(startIdx: Int, numStepsForward: Int): Long {
        var curIdx = startIdx
        var ans = nilValue
        for (i in store.indices.reversed()) {
            if (numStepsForward and i != 0) {
                ans = operation(ans, store[i][curIdx])
                curIdx += 1 shl i
            }
        }
        return ans
    }
}

// exports: LongSparseTable
// depends: math/Bitwise.kt
