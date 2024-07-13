@file:Suppress("unused", "DuplicatedCode")

package ktcp.ds.sparsetable

import ktcp.ds.fastarray.*
import ktcp.ds.fastarray.serializer.*
import ktcp.math.*

class FastSparseTable<T>(
    serializer: ByteSerializer<T>,
    source: FastArray,
    private val nilValue: T,
    private val operation: (T, T) -> T,
) {
    private val storeContext = FastArrayContext(serializer)
    private val lgSize = with(storeContext) {
        log2Floor(source.size) + 1
    }
    private val store = with(storeContext) {
        val temp = Array(lgSize) {
            if (it == 0) {
                source
            } else {
                FastArray(source.size)
            }
        }
        for (i in 1..<lgSize) {
            for (j in 0..source.size - (1 shl i)) {
                temp[i][j] = operation(
                    temp[i - 1][j],
                    temp[i - 1][j + (1 shl (i - 1))]
                )
            }
        }
        temp
    }

    fun query(l: Int, r: Int): T = with(storeContext) {
        val lg = log2Floor(r - l + 1)
        return operation(store[lg][l], store[lg][r - (1 shl lg) + 1])
    }

    fun queryForward(startIdx: Int, numStepsForward: Int): T =
        with(storeContext) {
            var curIdx = startIdx
            var ans = nilValue
            for (i in lgSize - 1 downTo 0) {
                if ((numStepsForward and (1 shl i)) != 0) {
                    ans = operation(ans, store[i][curIdx])
                    curIdx += 1 shl i
                }
            }
            return ans
        }
}

// exports: IntSparseTable
// depends: math/Bitwise.kt
// depends: ds/fastarray/FastArray.kt
