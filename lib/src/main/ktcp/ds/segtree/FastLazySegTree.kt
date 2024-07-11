@file:Suppress("unused", "DuplicatedCode", "MemberVisibilityCanBePrivate")

package ktcp.ds.segtree

import ktcp.ds.fastarray.*
import ktcp.ds.fastarray.serializer.*

class FastLazySegTree<T, U>(
    valueSerializer: ByteSerializer<T>,
    lazySerializer: ByteSerializer<U>,
    val size: Int,
    private val nilValue: T,
    private val nilLazy: U,
    private val operation: (T, T) -> T,
    private val applyLazy: (value: T, lazy: U, left: Int, right: Int) -> T,
    private val mergeLazy: (U, U) -> U,
) {
    private val storeContext = FastArrayContext(valueSerializer)
    private val lazyStoreContext = FastArrayContext(lazySerializer)

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

    private fun getStore(idx: Int) = with(storeContext) { store[idx] }

    private val lazyStore = with(lazyStoreContext) {
        FastArray(2 * size) { nilLazy }
    }

    private fun setLazy(idx: Int, value: U) = with(lazyStoreContext) {
        lazyStore[idx] = value
    }

    private inline fun transformLazy(idx: Int, transform: (U) -> U) =
        with(lazyStoreContext) {
            lazyStore.transform(idx, transform)
        }

    private fun getLazy(idx: Int) = with(lazyStoreContext) { lazyStore[idx] }

    constructor(
        valueSerializer: ByteSerializer<T>,
        lazySerializer: ByteSerializer<U>,
        source: FastArray,
        nilValue: T,
        nilLazy: U,
        operation: (T, T) -> T,
        applyLazy: (value: T, lazy: U, left: Int, right: Int) -> T,
        mergeLazy: (U, U) -> U,
    ) : this(
        valueSerializer,
        lazySerializer,
        withFastArraySerializer(valueSerializer) { source.size },
        nilValue,
        nilLazy,
        operation,
        applyLazy,
        mergeLazy
    ) {
        buildFrom(source)
    }

    // manually manage context here for speed
    fun buildFrom(source: FastArray) {
        with(lazyStoreContext) {
            for (i in 0..<lazyStore.size) {
                lazyStore[i] = nilLazy
            }
        }
        with(storeContext) {
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
    }

    fun set(pos: Int, value: T) = set(pos, value, 0, 0, size - 1)
    fun query(left: Int, right: Int): T = query(left, right, 0, 0, size - 1)
    fun update(left: Int, right: Int, lazy: U) =
        update(left, right, lazy, 0, 0, size - 1)

    private fun propagate(idx: Int, l: Int, r: Int) {
        val lazy = with(lazyStoreContext) {
            val idxLazy = lazyStore[idx]
            if (idxLazy == nilLazy) {
                return
            }
            if (l != r) {
                val (lc, rc, _) = computeSegTreeIndices(idx, l, r)
                lazyStore.transform(lc) { mergeLazy(it, idxLazy) }
                lazyStore.transform(rc) { mergeLazy(it, idxLazy) }
            }
            lazyStore[idx] = nilLazy
            idxLazy
        }
        with(storeContext) {
            store.transform(idx) {
                applyLazy(it, lazy, l, r)
            }
        }
    }

    private fun set(u: Int, w: T, idx: Int, l: Int, r: Int) {
        propagate(idx, l, r)
        when {
            u !in l..r -> return
            u == l && u == r -> setStore(idx, w)
            else -> {
                val (lc, rc, m) = computeSegTreeIndices(idx, l, r)
                set(u, w, lc, l, m)
                set(u, w, rc, m + 1, r)
                setStore(idx, operation(getStore(lc), getStore(rc)))
            }
        }
    }

    private fun update(u: Int, v: Int, w: U, idx: Int, l: Int, r: Int) {
        propagate(idx, l, r)
        when {
            u > r || v < l -> return
            u <= l && v >= r -> {
                setLazy(idx, w)
                propagate(idx, l, r)
            }

            else -> {
                val (lc, rc, m) = computeSegTreeIndices(idx, l, r)
                update(u, v, w, lc, l, m)
                update(u, v, w, rc, m + 1, r)
                setStore(idx, operation(getStore(lc), getStore(rc)))
            }
        }
    }

    private fun query(u: Int, v: Int, idx: Int, l: Int, r: Int): T {
        propagate(idx, l, r)
        return when {
            u > r || v < l -> nilValue
            u <= l && v >= r -> getStore(idx)
            else -> {
                val (lc, rc, m) = computeSegTreeIndices(idx, l, r)
                operation(query(u, v, lc, l, m), query(u, v, rc, m + 1, r))
            }
        }
    }
}

// exports: FastLazySegTree
// depends: ds/segtree/Util.kt
// depends: ds/fastarray/FastArray.kt
