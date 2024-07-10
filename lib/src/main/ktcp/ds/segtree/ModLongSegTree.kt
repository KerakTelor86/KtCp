@file:Suppress("unused", "DuplicatedCode", "MemberVisibilityCanBePrivate")

package ktcp.ds.segtree

import ktcp.ds.modarray.*
import ktcp.math.modint.*

class ModLongSegTree(
    val size: Int,
    private val nilValue: ModLong = ModLong(0L),
    private val operation: (ModLong, ModLong) -> ModLong,
) {
    private val store = ModLongArray(2 * size) { nilValue }

    constructor(
        source: ModLongArray,
        nilValue: ModLong = ModLong(0L),
        operation: (ModLong, ModLong) -> ModLong,
    ) : this(source.size, nilValue, operation) {
        buildFrom(source)
    }

    fun buildFrom(source: ModLongArray) {
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
    fun update(pos: Int, value: ModLong) = update(pos, value, 0, 0, size - 1)
    fun query(left: Int, right: Int): ModLong =
        query(left, right, 0, 0, size - 1)

    private fun set(u: Int, w: ModLong, idx: Int, l: Int, r: Int): Unit = when {
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

    private fun update(u: Int, w: ModLong, idx: Int, l: Int, r: Int): Unit =
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

    private fun query(u: Int, v: Int, idx: Int, l: Int, r: Int): ModLong =
        when {
            u > r || v < l -> nilValue
            u <= l && v >= r -> store[idx]
            else -> {
                val (lc, rc, m) = computeSegTreeIndices(idx, l, r)
                operation(query(u, v, lc, l, m), query(u, v, rc, m + 1, r))
            }
        }
}

// exports: ModLongSegTree
// depends: math/modint/ModLong.kt
// depends: ds/modarray/ModLongArray.kt
// depends: ds/segtree/Util.kt
