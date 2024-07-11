@file:Suppress("unused", "DuplicatedCode")

package ktcp.ds.sparsetable

import ktcp.ds.ndarray.*
import ktcp.math.*

class SparseTable<T> @PublishedApi internal constructor(
    private val store: NdArray<T>,
    private val lgSize: Int,
    private val nilValue: T,
    private val operation: (T, T) -> T,
) {
    companion object {
        inline operator fun <reified T> invoke(
            source: List<T>,
            nilValue: T,
            noinline operation: (T, T) -> T,
        ): SparseTable<T> {
            val lgSize = log2Floor(source.size) + 1
            val sparse = NdArray(lgSize, source.size) { (i, j) ->
                if (i == 0) {
                    source[j]
                } else {
                    nilValue
                }
            }
            for (i in 1..<lgSize) {
                for (j in 0..source.size - (1 shl i)) {
                    sparse[i, j] = operation(
                        sparse[i - 1, j],
                        sparse[i - 1, j + (1 shl (i - 1))]
                    )
                }
            }
            return SparseTable(sparse, lgSize, nilValue, operation)
        }
    }

    fun query(l: Int, r: Int): T {
        val lg = log2Floor(r - l + 1)
        return operation(store[lg, l], store[lg, r - (1 shl lg) + 1])
    }

    fun queryForward(startIdx: Int, numStepsForward: Int): T {
        var curIdx = startIdx
        var ans = nilValue
        for (i in lgSize - 1 downTo 0) {
            if (numStepsForward and i != 0) {
                ans = operation(ans, store[i, curIdx])
                curIdx += 1 shl i
            }
        }
        return ans
    }
}

// exports: SparseTable
// depends: math/Bitwise.kt
// depends: ds/ndarray/NdArray.kt
