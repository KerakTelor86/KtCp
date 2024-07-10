@file:Suppress("unused", "DuplicatedCode", "MemberVisibilityCanBePrivate")

package ktcp.ds.segtree

class LazySegTree<T, U> @PublishedApi internal constructor(
    val size: Int,
    private val nilValue: T,
    private val nilLazy: U,
    private val operation: (T, T) -> T,
    private val applyLazy: (T, U, Int, Int) -> T,
    private val mergeLazy: (U, U) -> U,
    private val store: Array<T>,
    private val lazyStore: Array<U>,
) {
    companion object {
        inline operator fun <reified T, reified U> invoke(
            size: Int,
            nilValue: T,
            nilLazy: U,
            noinline operation: (T, T) -> T,
            noinline applyLazy: (value: T, lazy: U, left: Int, right: Int) -> T,
            noinline mergeLazy: (U, U) -> U,
        ): LazySegTree<T, U> = LazySegTree(
            size,
            nilValue,
            nilLazy,
            operation,
            applyLazy,
            mergeLazy,
            Array(2 * size) { nilValue },
            Array(2 * size) { nilLazy }
        )

        inline operator fun <reified T, reified U> invoke(
            source: Array<T>,
            nilValue: T,
            nilLazy: U,
            noinline operation: (T, T) -> T,
            noinline applyLazy: (value: T, lazy: U, left: Int, right: Int) -> T,
            noinline mergeLazy: (U, U) -> U,
        ): LazySegTree<T, U> = invoke(
            source.size,
            nilValue,
            nilLazy,
            operation,
            applyLazy,
            mergeLazy
        ).apply {
            buildFrom(source)
        }
    }

    fun buildFrom(source: Array<T>) {
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

    fun set(pos: Int, value: T) = set(pos, value, 0, 0, size - 1)
    fun query(left: Int, right: Int): T = query(left, right, 0, 0, size - 1)
    fun update(left: Int, right: Int, lazy: U) =
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

    private fun set(u: Int, w: T, idx: Int, l: Int, r: Int) {
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

    private fun update(u: Int, v: Int, w: U, idx: Int, l: Int, r: Int) {
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

    private fun query(u: Int, v: Int, idx: Int, l: Int, r: Int): T {
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

// exports: LazySegTree
// depends: ds/segtree/Util.kt
