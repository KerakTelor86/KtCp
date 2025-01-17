@file:Suppress("unused", "DuplicatedCode")

package ktcp.ds.ndarray

class BooleanNdArray(
    private vararg val shape: Int,
    init: (IntArray) -> Boolean,
) {
    constructor(vararg shape: Int) : this(*shape, init = { false })

    private val store: BooleanArray

    init {
        val index = IntArray(shape.size)
        store = BooleanArray(shape.reduce { a, b -> a * b }) {
            var idx = it
            for (i in index.indices.reversed()) {
                index[i] = idx % shape[i]
                idx /= shape[i]
            }
            init(index)
        }
    }

    operator fun get(vararg index: Int): Boolean {
        val idx = index.foldIndexed(0) { dimension, acc, dimensionIdx ->
            acc * shape[dimension] + dimensionIdx
        }
        return store[idx]
    }

    operator fun set(vararg index: Int, value: Boolean) {
        val idx = index.foldIndexed(0) { dimension, acc, dimensionIdx ->
            acc * shape[dimension] + dimensionIdx
        }
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

// exports: BooleanNdArray
