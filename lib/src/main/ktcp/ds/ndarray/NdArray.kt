@file:Suppress("unused", "DuplicatedCode")

package ktcp.ds.ndarray

class NdArray<T> @PublishedApi internal constructor(
    private val shape: IntArray,
    private val store: Array<T>,
) {
    companion object {
        inline operator fun <reified T> invoke(
            vararg shape: Int,
            init: (IntArray) -> T,
        ): NdArray<T> {
            val index = IntArray(shape.size)
            val store = Array(shape.reduce { a, b -> a * b }) {
                var idx = it
                for (i in index.indices.reversed()) {
                    index[i] = idx % shape[i]
                    idx /= shape[i]
                }
                init(index)
            }
            return NdArray(shape, store)
        }
    }

    operator fun get(vararg index: Int): T {
        val idx = calculateNdArrayIndex(index, shape)
        return store[idx]
    }

    operator fun set(vararg index: Int, value: T) {
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

// exports: NdArray
// depends: ds/ndarray/Util.kt
