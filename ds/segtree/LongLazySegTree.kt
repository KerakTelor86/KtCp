@file:Suppress("unused", "DuplicatedCode", "MemberVisibilityCanBePrivate")

package ktcp.ds.segtree

class LongLazySegTree(
    val size: Int,
    private val nilValue: Long = 0L,
    private val nilLazy: Long = 0L,
    private val operation: (Long, Long) -> Long,
    private val applyLazy: (value: Long, lazy: Long, left: Int, right: Int) -> Long,
    private val mergeLazy: (Long, Long) -> Long,
) {
    private val store = LongArray(2 * size) { nilValue }
    private val lazyStore = LongArray(2 * size) { nilLazy }

    constructor(
        source: LongArray,
        nilValue: Long = 0L,
        nilLazy: Long = 0L,
        operation: (Long, Long) -> Long,
        applyLazy: (value: Long, lazy: Long, left: Int, right: Int) -> Long,
        mergeLazy: (Long, Long) -> Long,
    ) : this(source.size, nilValue, nilLazy, operation, applyLazy, mergeLazy) {
        buildFrom(source)
    }

    fun buildFrom(source: LongArray) {
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

    fun set(pos: Int, value: Long) = set(pos, value, 0, 0, size - 1)
    fun query(left: Int, right: Int): Long = query(left, right, 0, 0, size - 1)
    fun update(left: Int, right: Int, lazy: Long) =
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

    private fun set(u: Int, w: Long, idx: Int, l: Int, r: Int) {
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

    private fun update(u: Int, v: Int, w: Long, idx: Int, l: Int, r: Int) {
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

    private fun query(u: Int, v: Int, idx: Int, l: Int, r: Int): Long {
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

// exports: LongLazySegTree
// depends: ds/segtree/Util.kt
