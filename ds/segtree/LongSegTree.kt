@file:Suppress("unused", "DuplicatedCode", "MemberVisibilityCanBePrivate")

package ktcp.ds.segtree

class LongSegTree(
    val size: Int,
    private val nilValue: Long = 0L,
    private val operation: (Long, Long) -> Long,
) {
    private val store = LongArray(2 * size) { nilValue }

    constructor(
        source: LongArray,
        nilValue: Long = 0L,
        operation: (Long, Long) -> Long,
    ) : this(source.size, nilValue, operation) {
        buildFrom(source)
    }

    fun buildFrom(source: LongArray) {
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

    fun set(pos: Int, value: Long) = set(pos, value, 0, 0, size - 1)
    fun update(pos: Int, value: Long) = update(pos, value, 0, 0, size - 1)
    fun query(left: Int, right: Int): Long = query(left, right, 0, 0, size - 1)

    private fun set(u: Int, w: Long, idx: Int, l: Int, r: Int): Unit = when {
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

    private fun update(u: Int, w: Long, idx: Int, l: Int, r: Int): Unit = when {
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

    private fun query(u: Int, v: Int, idx: Int, l: Int, r: Int): Long = when {
        u > r || v < l -> nilValue
        u <= l && v >= r -> store[idx]
        else -> {
            val (lc, rc, m) = computeSegTreeIndices(idx, l, r)
            operation(query(u, v, lc, l, m), query(u, v, rc, m + 1, r))
        }
    }
}

// exports: LongSegTree
// depends: ds/segtree/Util.kt