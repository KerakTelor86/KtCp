@file:Suppress("unused", "DuplicatedCode", "MemberVisibilityCanBePrivate")

package ktcp.ds.segtree

import ktcp.ds.modarray.*
import ktcp.math.modint.*

class ModIntSegTree(
    val size: Int,
    private val nilValue: ModInt = ModInt(0),
    private val operation: (ModInt, ModInt) -> ModInt,
) {
    private val store = ModIntArray(2 * size) { nilValue }

    constructor(
        source: ModIntArray,
        nilValue: ModInt = ModInt(0),
        operation: (ModInt, ModInt) -> ModInt,
    ) : this(source.size, nilValue, operation) {
        buildFrom(source)
    }

    fun buildFrom(source: ModIntArray) {
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
    fun update(pos: Int, value: ModInt) = update(pos, value, 0, 0, size - 1)
    fun query(left: Int, right: Int): ModInt =
        query(left, right, 0, 0, size - 1)

    private fun set(u: Int, w: ModInt, idx: Int, l: Int, r: Int): Unit = when {
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

    private fun update(u: Int, w: ModInt, idx: Int, l: Int, r: Int): Unit =
        when {
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

    private fun query(u: Int, v: Int, idx: Int, l: Int, r: Int): ModInt = when {
        u > r || v < l -> nilValue
        u <= l && v >= r -> store[idx]
        else -> {
            val (lc, rc, m) = computeSegTreeIndices(idx, l, r)
            operation(query(u, v, lc, l, m), query(u, v, rc, m + 1, r))
        }
    }
}

// exports: ModIntSegTree
// depends: math/modint/ModInt.kt
// depends: ds/modarray/ModIntArray.kt
// depends: ds/segtree/Util.kt