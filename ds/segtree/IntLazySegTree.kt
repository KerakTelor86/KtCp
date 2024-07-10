@file:Suppress("unused", "DuplicatedCode", "MemberVisibilityCanBePrivate")

package ktcp.ds.segtree

class IntLazySegTree(
    val size: Int,
    private val nilValue: Int = 0,
    private val nilLazy: Int = 0,
    private val operation: (Int, Int) -> Int,
    private val applyLazy: (value: Int, lazy: Int, left: Int, right: Int) -> Int,
    private val mergeLazy: (Int, Int) -> Int,
) {
    private val store = IntArray(2 * size) { nilValue }
    private val lazyStore = IntArray(2 * size) { nilLazy }

    constructor(
        source: IntArray,
        nilValue: Int = 0,
        nilLazy: Int = 0,
        operation: (Int, Int) -> Int,
        applyLazy: (value: Int, lazy: Int, left: Int, right: Int) -> Int,
        mergeLazy: (Int, Int) -> Int,
    ) : this(source.size, nilValue, nilLazy, operation, applyLazy, mergeLazy) {
        buildFrom(source)
    }

    fun buildFrom(source: IntArray) {
        lazyStore.fill(nilLazy)
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
    fun query(left: Int, right: Int): Int = query(left, right, 0, 0, size - 1)
    fun update(left: Int, right: Int, lazy: Int) =
        update(left, right, lazy, 0, 0, size - 1)

    private fun propagate(idx: Int, l: Int, r: Int) {
        if (lazyStore[idx] == nilLazy) {
            return
        }
        if (l != r) {
            val (lc, rc, _) = computeSegTreeIndices(idx, l, r)
            lazyStore[lc] = mergeLazy(lazyStore[lc], lazyStore[idx])
            lazyStore[rc] = mergeLazy(lazyStore[rc], lazyStore[idx])
        }
        store[idx] = applyLazy(store[idx], lazyStore[idx], l, r)
        lazyStore[idx] = nilLazy
    }

    private fun set(u: Int, w: Int, idx: Int, l: Int, r: Int) {
        propagate(idx, l, r)
        when {
            u !in l..r -> return
            u == l && u == r -> store[idx] = w
            else -> {
                val (lc, rc, m) = computeSegTreeIndices(idx, l, r)
                set(u, w, lc, l, m)
                set(u, w, rc, m + 1, r)
                store[idx] = operation(store[lc], store[rc])
            }
        }
    }

    private fun update(u: Int, v: Int, w: Int, idx: Int, l: Int, r: Int) {
        propagate(idx, l, r)
        when {
            u > r || v < l -> return
            u <= l && v >= r -> {
                lazyStore[idx] = w
                propagate(idx, l, r)
            }

            else -> {
                val (lc, rc, m) = computeSegTreeIndices(idx, l, r)
                update(u, v, w, lc, l, m)
                update(u, v, w, rc, m + 1, r)
                store[idx] = operation(store[lc], store[rc])
            }
        }
    }

    private fun query(u: Int, v: Int, idx: Int, l: Int, r: Int): Int {
        propagate(idx, l, r)
        return when {
            u > r || v < l -> nilValue
            u <= l && v >= r -> store[idx]
            else -> {
                val (lc, rc, m) = computeSegTreeIndices(idx, l, r)
                operation(query(u, v, lc, l, m), query(u, v, rc, m + 1, r))
            }
        }
    }
}

// exports: IntLazySegTree
// depends: ds/segtree/Util.kt
