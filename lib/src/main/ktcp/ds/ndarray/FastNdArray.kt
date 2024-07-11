@file:Suppress("unused", "DuplicatedCode")

package ktcp.ds.ndarray

import ktcp.ds.fastarray.*
import ktcp.ds.fastarray.serializer.*

class FastNdArray<T>(
    serializer: ByteSerializer<T>,
    private vararg val shape: Int,
    init: (IntArray) -> T,
) {
    private val storeContext = FastArrayContext(serializer)
    private val store = run {
        val index = IntArray(shape.size)
        with(storeContext) {
            FastArray(shape.reduce { a, b -> a * b }) {
                var idx = it
                for (i in index.indices.reversed()) {
                    index[i] = idx % shape[i]
                    idx /= shape[i]
                }
                init(index)
            }
        }
    }

    operator fun get(vararg index: Int): T = with(storeContext) {
        val idx = index.foldIndexed(0) { dimension, acc, dimensionIdx ->
            acc * shape[dimension] + dimensionIdx
        }
        return store[idx]
    }

    operator fun set(vararg index: Int, value: T) = with(storeContext) {
        val idx = index.foldIndexed(0) { dimension, acc, dimensionIdx ->
            acc * shape[dimension] + dimensionIdx
        }
        store[idx] = value
    }

    override fun toString(): String = with(storeContext) {
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

// exports: FastNdArray
// depends: ds/fastarray/FastArray.kt