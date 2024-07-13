@file:Suppress("unused", "DuplicatedCode")

package ktcp.ds.ndarray

import ktcp.ds.modarray.*
import ktcp.math.modint.*

class ModIntNdArray(
    private vararg val shape: Int,
    init: (IntArray) -> ModInt,
) {
    constructor(vararg shape: Int) : this(*shape, init = { ModInt(0) })

    private val store: ModIntArray

    init {
        val index = IntArray(shape.size)
        store = ModIntArray(shape.reduce { a, b -> a * b }) {
            var idx = it
            for (i in index.indices.reversed()) {
                index[i] = idx % shape[i]
                idx /= shape[i]
            }
            init(index)
        }
    }

    operator fun get(vararg index: Int): ModInt {
        val idx = calculateNdArrayIndex(index, shape)
        return store[idx]
    }

    operator fun set(vararg index: Int, value: ModInt) {
        val idx = calculateNdArrayIndex(index, shape)
        store[idx] = value
    }

    override fun toString(): String {
        val builder = StringBuilder()
        fun appendToBuilder(dimension: Int, index: Int) {
            if (dimension >= shape.size) {
                builder.append(store[index])
            } else {
                val nextBaseIndex = index * shape[dimension]
                builder.append('[')
                for (i in 0..<shape[dimension]) {
                    if (i != 0) {
                        builder.append(", ")
                    }
                    appendToBuilder(dimension + 1, nextBaseIndex + i)
                }
                builder.append(']')
            }
        }
        appendToBuilder(0, 0)
        return builder.toString()
    }
}

// exports: ModIntNdArray
// depends: ds/ndarray/Util.kt
// depends: math/modint/ModInt.kt
// depends: ds/modarray/ModIntArray.kt
