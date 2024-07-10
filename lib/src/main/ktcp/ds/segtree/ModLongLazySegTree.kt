@file:Suppress("unused", "DuplicatedCode", "MemberVisibilityCanBePrivate")

package ktcp.ds.segtree

import ktcp.ds.modarray.*
import ktcp.math.modint.*

class ModLongLazySegTree(
    val size: Int,
    private val nilValue: ModLong = ModLong(0L),
    private val nilLazy: ModLong = ModLong(0L),
    private val operation: (ModLong, ModLong) -> ModLong,
    private val applyLazy: (value: ModLong, lazy: ModLong, left: Int, right: Int) -> ModLong,
    private val mergeLazy: (ModLong, ModLong) -> ModLong,
) {
    private val store = ModLongArray(2 * size) { nilValue }
    private val lazyStore = ModLongArray(2 * size) { nilLazy }

    constructor(
        source: ModLongArray,
        nilValue: ModLong = ModLong(0L),
        nilLazy: ModLong = ModLong(0L),
        operation: (ModLong, ModLong) -> ModLong,
        applyLazy: (value: ModLong, lazy: ModLong, left: Int, right: Int) -> ModLong,
        mergeLazy: (ModLong, ModLong) -> ModLong,
    ) : this(source.size, nilValue, nilLazy, operation, applyLazy, mergeLazy) {
        buildFrom(source)
    }

    fun buildFrom(source: ModLongArray) {
        for (i in lazyStore.indices) {
            lazyStore[i] = nilLazy
        }
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

    fun set(pos: Int, value: ModLong) = set(pos, value, 0, 0, size - 1)
    fun query(left: Int, right: Int): ModLong =
        query(left, right, 0, 0, size - 1)

    fun update(left: Int, right: Int, lazy: ModLong) =
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

    private fun set(u: Int, w: ModLong, idx: Int, l: Int, r: Int) {
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

    private fun update(u: Int, v: Int, w: ModLong, idx: Int, l: Int, r: Int) {
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

    private fun query(u: Int, v: Int, idx: Int, l: Int, r: Int): ModLong {
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

// exports: ModLongLazySegTree
// depends: ds/segtree/Util.kt
// depends: ds/modarray/ModLongArray.kt
// depends: ds/segtree/Util.kt
