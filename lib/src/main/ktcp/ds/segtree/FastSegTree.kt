@file:Suppress("unused", "DuplicatedCode", "MemberVisibilityCanBePrivate")

package ktcp.ds.segtree

import ktcp.ds.fastarray.*

class FastSegTree<T>(
    serializer: ByteSerializer<T>,
    val size: Int,
    private val nilValue: T,
    private val operation: (T, T) -> T,
) {
    private val storeContext = FastArrayContext(serializer)
    private val store = with(storeContext) {
        FastArray(2 * size) { nilValue }
    }

    private fun setStore(idx: Int, value: T) = with(storeContext) {
        store[idx] = value
    }

    private inline fun transformStore(idx: Int, transform: (T) -> T) =
        with(storeContext) {
            store.transform(idx, transform)
        }

    private fun getStore(idx: Int): T = with(storeContext) {
        store[idx]
    }

    constructor(
        serializer: ByteSerializer<T>,
        source: FastArray,
        nilValue: T,
        operation: (T, T) -> T,
    ) : this(
        serializer,
        withFastArraySerializer(serializer) { source.size },
        nilValue,
        operation
    ) {
        buildFrom(source)
    }

    // manually manage context here for speed
    fun buildFrom(source: FastArray) = with(storeContext) {
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
        u == l && u == r -> setStore(idx, w)
        else -> {
            val (lc, rc, m) = computeSegTreeIndices(idx, l, r)
            if (u <= m) {
                set(u, w, lc, l, m)
            } else {
                set(u, w, rc, m + 1, r)
            }
            setStore(idx, operation(getStore(lc), getStore(rc)))
        }
    }

    private fun update(u: Int, w: T, idx: Int, l: Int, r: Int): Unit = when {
        u !in l..r -> Unit
        u == l && u == r -> transformStore(idx) {
            operation(it, w)
        }

        else -> {
            val (lc, rc, m) = computeSegTreeIndices(idx, l, r)
            if (u <= m) {
                update(u, w, lc, l, m)
            } else {
                update(u, w, rc, m + 1, r)
            }
            setStore(idx, operation(getStore(lc), getStore(rc)))
        }
    }

    private fun query(u: Int, v: Int, idx: Int, l: Int, r: Int): T = when {
        u > r || v < l -> nilValue
        u <= l && v >= r -> getStore(idx)
        else -> {
            val (lc, rc, m) = computeSegTreeIndices(idx, l, r)
            operation(query(u, v, lc, l, m), query(u, v, rc, m + 1, r))
        }
    }
}

// exports: FastSegTree
// depends: ds/segtree/Util.kt
// depends: ds/fastarray/FastArray.k5 7
1 2 3 4 5
1 0 5
0 2 4 100 101
1 0 3
0 1 3 102 103
1 2 5
0 2 5 104 105
1 0 5
t