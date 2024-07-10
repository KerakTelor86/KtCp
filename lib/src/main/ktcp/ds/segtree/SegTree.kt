@file:Suppress("unused", "DuplicatedCode", "MemberVisibilityCanBePrivate")

package ktcp.ds.segtree

class SegTree<T> @PublishedApi internal constructor(
    val size: Int,
    private val nilValue: T,
    private val operation: (T, T) -> T,
    private val store: Array<T>,
) {
    companion object {
        inline operator fun <reified T> invoke(
            size: Int,
            nilValue: T,
            noinline operation: (T, T) -> T,
        ): SegTree<T> = SegTree(
            size, nilValue, operation, Array(2 * size) { nilValue }
        )

        inline operator fun <reified T> invoke(
            source: Array<T>,
            nilValue: T,
            noinline operation: (T, T) -> T,
        ): SegTree<T> = invoke(source.size, nilValue, operation).apply {
            buildFrom(source)
        }
    }

    fun buildFrom(source: Array<T>) {
        fun buildInner(idx: Int, l: Int, r: Int): Unit = when {
            l == r -> store[idx] = source[l]
            else -> {
                val (lc, rc, m) = computeSegTreeIndices(idx, l, r)
                buildInner(lc, l, m)
                buildInner(rc, m + 1, r)
                store[idx] = operation(store[lc], store[rc])
            }
        }
        buildInner(0, 0, size - 1)
    }

    fun set(pos: Int, value: T) = set(pos, value, 0, 0, size - 1)
    fun update(pos: Int, value: T) = update(pos, value, 0, 0, size - 1)
    fun query(left: Int, right: Int): T = query(left, right, 0, 0, size - 1)

    private fun set(u: Int, w: T, idx: Int, l: Int, r: Int): Unit = when {
        u !in l..r -> Unit
        u == l && u == r -> store[idx] = w
        else -> {
            val (lc, rc, m) = computeSegTreeIndices(idx, l, r)
            if (u <= m) {
                set(u, w, lc, l, m)
            } else {
                set(u, w, rc, m + 1, r)
            }
            store[idx] = operation(store[lc], store[rc])
        }
    }

    private fun update(u: Int, w: T, idx: Int, l: Int, r: Int): Unit = when {
        u !in l..r -> Unit
        u == l && u == r -> store[idx] = operation(store[idx], w)
        else -> {
            val (lc, rc, m) = computeSegTreeIndices(idx, l, r)
            if (u <= m) {
                update(u, w, lc, l, m)
            } else {
                update(u, w, rc, m + 1, r)
            }
            store[idx] = operation(store[lc], store[rc])
        }
    }

    private fun query(u: Int, v: Int, idx: Int, l: Int, r: Int): T = when {
        u > r || v < l -> nilValue
        u <= l && v >= r -> store[idx]
        else -> {
            val (lc, rc, m) = computeSegTreeIndices(idx, l, r)
            operation(query(u, v, lc, l, m), query(u, v, rc, m + 1, r))
        }
    }
}

// exports: SegTree
// depends: ds/segtree/Util.kt