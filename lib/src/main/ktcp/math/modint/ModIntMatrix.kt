@file:Suppress("unused", "NOTHING_TO_INLINE", "MemberVisibilityCanBePrivate")

package ktcp.math.modint

import ktcp.ds.modarray.*

@JvmInline
value class ModIntMatrix private constructor(
    @PublishedApi
    internal val store: ModIntArray,
) {
    companion object {
        operator fun invoke(rows: Int, cols: Int): ModIntMatrix = ModIntMatrix(
            ModIntArray(rows * cols + 2).apply {
                this[rows * cols - 2] = rows.asModInt()
                this[rows * cols - 1] = cols.asModInt()
            }
        )

        operator fun invoke(
            rows: Int,
            cols: Int,
            init: (row: Int, col: Int) -> ModInt,
        ): ModIntMatrix = invoke(rows, cols).apply {
            for (i in 0..<rows) {
                for (j in 0..<cols) {
                    store[i * cols + j] = init(i, j)
                }
            }
        }

        fun getIdentityMatrix(size: Int): ModIntMatrix =
            invoke(size, size) { row, col ->
                if (row == col) {
                    ModInt(1)
                } else {
                    ModInt(0)
                }
            }
    }

    val rows get() = store[store.size - 2].toInt()
    val cols get() = store[store.size - 1].toInt()

    inline operator fun get(row: Int, col: Int) = store[row * cols + col]
    inline operator fun set(row: Int, col: Int, value: ModInt) {
        store[row * cols + col] = value
    }
}

// exports: ModIntMatrix
// depends: math/modint/ModInt.kt