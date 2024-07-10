@file:Suppress("unused", "DuplicatedCode", "MemberVisibilityCanBePrivate")

package ktcp.ds.segtree

import ktcp.ds.modarray.*
import ktcp.math.modint.*

class ModIntLazySegTree(
    val size: Int,
    private val nilValue: ModInt = ModInt(0),
    private val nilLazy: ModInt = ModInt(0),
    private val operation: (ModInt, ModInt) -> ModInt,
    private val applyLazy: (value: ModInt, lazy: ModInt, left: Int, right: Int) -> ModInt,
    private val mergeLazy: (ModInt, ModInt) -> ModInt,
) {
    private val store = ModIntArray(2 * size) { nilValue }
    private val lazyStore = ModIntArray(2 * size) { nilLazy }

    constructor(
        source: ModIntArray,
        nilValue: ModInt = ModInt(0),
        nilLazy: ModInt = ModInt(0),
        operation: (ModInt, ModInt) -> ModInt,
        applyLazy: (value: ModInt, lazy: ModInt, left: Int, right: Int) -> ModInt,
        mergeLazy: (ModInt, ModInt) -> ModInt,
    ) : this(source.size, nilValue, nilLazy, operation, applyLazy, mergeLazy) {
        buildFrom(source)
    }

    fun buildFrom(source: ModIntArray) {
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

    fun set(pos: Int, value: ModInt) = set(pos, value, 0, 0, size - 1)
    fun query(left: Int, right: Int): ModInt =
        query(left, right, 0, 0, size - 1)

    fun update(left: Int, right: Int, lazy: ModInt) =
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

    private fun set(u: Int, w: ModInt, idx: Int, l: Int, r: Int) {
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

    private fun update(u: Int, v: Int, w: ModInt, idx: Int, l: Int, r: Int) {
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

    private fun query(u: Int, v: Int, idx: Int, l: Int, r: Int): ModInt {
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

// exports: ModIntLazySegTree
// depends: ds/segtree/Util.kt
// depends: ds/modarray/ModIntArray.kt
// depends: ds/segtree/Util.kt
