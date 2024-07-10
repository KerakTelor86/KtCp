@file:Suppress("unused", "DuplicatedCode", "MemberVisibilityCanBePrivate")

package ktcp.ds.segtree

class IntSegTree(
    val size: Int,
    private val nilValue: Int = 0,
    private val operation: (Int, Int) -> Int,
) {
    private val store = IntArray(2 * size) { nilValue }

    constructor(
        source: IntArray,
        nilValue: Int = 0,
        operation: (Int, Int) -> Int,
    ) : this(source.size, nilValue, operation) {
        buildFrom(source)
    }

    fun buildFrom(source: IntArray) {
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

    fun set(pos: Int, value: Int) = set(pos, value, 0, 0, size - 1)
    fun update(pos: Int, value: Int) = update(pos, value, 0, 0, size - 1)
    fun query(left: Int, right: Int): Int = query(left, right, 0, 0, size - 1)

    private fun set(u: Int, w: Int, idx: Int, l: Int, r: Int): Unit = when {
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

    private fun update(u: Int, w: Int, idx: Int, l: Int, r: Int): Unit = when {
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

    private fun query(u: Int, v: Int, idx: Int, l: Int, r: Int): Int = when {
        u > r || v < l -> nilValue
        u <= l && v >= r -> store[idx]
        else -> {
            val (lc, rc, m) = computeSegTreeIndices(idx, l, r)
            operation(query(u, v, lc, l, m), query(u, v, rc, m + 1, r))
        }
    }
}

// exports: IntSegTree
// depends: ds/segtree/Util.kt