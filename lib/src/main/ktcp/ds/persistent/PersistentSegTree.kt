@file:Suppress("unused", "MemberVisibilityCanBePrivate")

package ktcp.ds.persistent

private class PstSegNode<T>(
    val left: PstSegNode<T>?,
    val right: PstSegNode<T>?,
    val value: T,
)

private fun <T> buildPstSegNodeFrom(
    source: Array<T>,
    operation: (T, T) -> T,
): PstSegNode<T> {
    fun buildInner(
        l: Int,
        r: Int,
    ): PstSegNode<T> = when {
        l == r -> PstSegNode(null, null, source[l])
        else -> {
            val m = l + (r - l) / 2
            val left = buildInner(l, m)
            val right = buildInner(m + 1, r)
            PstSegNode(left, right, operation(left.value, right.value))
        }
    }
    return buildInner(0, source.size - 1)
}

class PersistentSegTree<T> private constructor(
    val size: Int,
    private val nilValue: T,
    private val operation: (T, T) -> T,
    private val root: PstSegNode<T>?,
) {
    constructor(size: Int, nilValue: T, operation: (T, T) -> T) : this(
        size,
        nilValue,
        operation,
        null
    )

    constructor(source: Array<T>, nilValue: T, operation: (T, T) -> T) : this(
        source.size,
        nilValue,
        operation,
        buildPstSegNodeFrom(source, operation)
    )

    fun set(pos: Int, value: T): PersistentSegTree<T> = PersistentSegTree(
        size,
        nilValue,
        operation,
        set(pos, value, root, 0, size - 1)
    )

    fun update(pos: Int, value: T): PersistentSegTree<T> = PersistentSegTree(
        size,
        nilValue,
        operation,
        update(pos, value, root, 0, size - 1)
    )

    fun query(left: Int, right: Int): T = query(left, right, root, 0, size - 1)

    private fun set(
        u: Int,
        w: T,
        cur: PstSegNode<T>?,
        l: Int,
        r: Int,
    ): PstSegNode<T>? = when {
        u !in l..r -> null
        u == l && u == r -> PstSegNode(null, null, w)
        else -> {
            val m = l + (r - l) / 2
            var lc = cur?.left
            var rc = cur?.right
            if (u <= m) {
                lc = set(u, w, cur?.left, l, m)
            } else {
                rc = set(u, w, cur?.right, m + 1, r)
            }
            PstSegNode(
                lc,
                rc,
                operation(lc?.value ?: nilValue, rc?.value ?: nilValue)
            )
        }
    }

    private fun update(
        u: Int,
        w: T,
        cur: PstSegNode<T>?,
        l: Int,
        r: Int,
    ): PstSegNode<T>? = when {
        u !in l..r -> null

        u == l && u == r -> PstSegNode(
            null,
            null,
            operation(cur?.value ?: nilValue, w)
        )

        else -> {
            val m = l + (r - l) / 2
            var lc = cur?.left
            var rc = cur?.right
            if (u <= m) {
                lc = update(u, w, cur?.left, l, m)
            } else {
                rc = update(u, w, cur?.right, m + 1, r)
            }
            PstSegNode(
                lc,
                rc,
                operation(lc?.value ?: nilValue, rc?.value ?: nilValue)
            )
        }
    }

    private fun query(u: Int, v: Int, cur: PstSegNode<T>?, l: Int, r: Int): T =
        when {
            cur == null || u > r || v < l -> nilValue
            u <= l && v >= r -> cur.value
            else -> {
                val m = l + (r - l) / 2
                operation(
                    query(u, v, cur.left, l, m),
                    query(u, v, cur.right, m + 1, r)
                )
            }
        }
}

// exports: PersistentSegTree