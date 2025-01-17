@file:Suppress("unused", "DuplicatedCode")

package ktcp.ds.ndarray

class DoubleNdArray(
    private vararg val shape: Int,
    init: (IntArray) -> Double,
) {
    constructor(shape: IntArray) : this(*shape, init = { 0.0 })

    private val store: DoubleArray

    init {
        val index = IntArray(shape.size)
        store = DoubleArray(shape.reduce { a, b -> a * b }) {
            var idx = it
            for (i in index.indices.reversed()) {
                index[i] = idx % shape[i]
                idx /= shape[i]
            }
            init(index)
        }
    }

    operator fun get(vararg index: Int): Double {
        val idx = calculateNdArrayIndex(index, shape)
        return store[idx]
    }

    operator fun set(vararg index: Int, value: Double) {
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

// exports: DoubleNdArray
// depends: ds/ndarray/Util.kt
