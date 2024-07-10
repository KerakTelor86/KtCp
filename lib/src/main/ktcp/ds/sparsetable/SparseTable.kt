@file:Suppress("unused", "DuplicatedCode")

package ktcp.ds.sparsetable

import ktcp.math.*

class SparseTable<T> @PublishedApi internal constructor(
    private val store: Array<Array<T>>,
    private val nilValue: T,
    private val operation: (T, T) -> T,
) {
    companion object {
        inline operator fun <reified T> invoke(
            source: Collection<T>,
            nilValue: T,
            noinline operation: (T, T) -> T,
        ): SparseTable<T> {
            val sparse = Array(log2Floor(source.size) + 1) {
                if (it == 0) {
                    source.toTypedArray()
                } else {
                    Array(source.size) { nilValue }
                }
            }
            for (i in 1..<sparse.size) {
                for (j in 0..source.size - (1 shl i)) {
                    sparse[i][j] = operation(
                        sparse[i - 1][j],
                        sparse[i - 1][j + (1 shl (i - 1))]
                    )
                }
            }
            return SparseTable(sparse, nilValue, operation)
        }
    }

    val size: Int = store[0].size

    fun query(l: Int, r: Int): T {
        val lg = log2Floor(r - l + 1)
        return operation(store[lg][l], store[lg][r - (1 shl lg) + 1])
    }

    fun queryForward(startIdx: Int, numStepsForward: Int): T {
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

// exports: SparseTable
// depends: math/Bitwise.kt
