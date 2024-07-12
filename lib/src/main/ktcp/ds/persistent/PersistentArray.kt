@file:Suppress("unused", "MemberVisibilityCanBePrivate")

package ktcp.ds.persistent

private open class PstArrNode(val left: PstArrNode?, val right: PstArrNode?)
private class PstArrLeaf<T>(val value: T) : PstArrNode(null, null)

private fun <T> buildPstArrNode(size: Int, init: (Int) -> T): PstArrNode {
    fun inner(l: Int, r: Int): PstArrNode = when {
        l == r -> PstArrLeaf(init(l))
        else -> {
            val m = l + (r - l) / 2
            PstArrNode(inner(l, m), inner(m + 1, r))
        }
    }
    return inner(0, size - 1)
}

class PersistentArray<T> private constructor(
    val size: Int,
    private val root: PstArrNode?,
) {
    constructor(size: Int) : this(size, null)

    constructor(size: Int, init: (Int) -> T) : this(
        size,
        buildPstArrNode(size, init)
    )

    constructor(source: Array<T>) : this(
        source.size,
        buildPstArrNode(source.size) { source[it] }
    )

    fun clone(): PersistentArray<T> = PersistentArray(size, root)

    fun set(index: Int, value: T): PersistentArray<T> =
        PersistentArray(size, set(index, value, root, 0, size - 1))

    fun transform(index: Int, transform: (T) -> T): PersistentArray<T> =
        PersistentArray(size, transform(index, transform, root, 0, size - 1))

    operator fun get(index: Int): T = get(index, root, 0, size - 1)

    private fun set(
        index: Int,
        value: T,
        cur: PstArrNode?,
        l: Int,
        r: Int,
    ): PstArrNode =
        when {
            index !in l..r -> throw IndexOutOfBoundsException(
                "Index $index is out of bounds for size $size"
            )

            l == r -> PstArrLeaf(value)

            else -> {
                val m = l + (r - l) / 2
                if (index <= m) {
                    PstArrNode(
                        set(index, value, cur?.left, l, m),
                        cur?.right
                    )
                } else {
                    PstArrNode(
                        cur?.left,
                        set(index, value, cur?.right, m + 1, r)
                    )
                }
            }
        }

    @Suppress("UNCHECKED_CAST")
    private fun transform(
        index: Int,
        transform: (T) -> T,
        cur: PstArrNode?,
        l: Int,
        r: Int,
    ): PstArrNode = when {
        index !in l..r -> throw IndexOutOfBoundsException(
            "Index $index is out of bounds for size $size"
        )

        l == r -> {
            if (cur == null) {
                throw UninitializedPropertyAccessException(
                    "Value at index is uninitialized"
                )
            }
            val newValue = transform((cur as PstArrLeaf<T>).value)
            PstArrLeaf(newValue)
        }

        else -> {
            val m = l + (r - l) / 2
            if (index <= m) {
                PstArrNode(
                    transform(index, transform, cur?.left, l, m),
                    cur?.right
                )
            } else {
                PstArrNode(
                    cur?.left,
                    transform(index, transform, cur?.right, m + 1, r)
                )
            }
        }
    }

    @Suppress("UNCHECKED_CAST")
    private fun get(index: Int, cur: PstArrNode?, l: Int, r: Int): T = when {
        index !in l..r -> throw IndexOutOfBoundsException(
            "Index $index is out of bounds for size $size"
        )

        l == r -> {
            if (cur == null) {
                throw UninitializedPropertyAccessException(
                    "Value at index is uninitialized"
                )
            }
            (cur as PstArrLeaf<T>).value
        }

        else -> {
            val m = l + (r - l) / 2
            if (index <= m) {
                get(index, cur?.left, l, m)
            } else {
                get(index, cur?.right, m + 1, r)
            }
        }
    }

    @Suppress("UNCHECKED_CAST")
    private fun traverse(
        cur: PstArrNode?,
        l: Int = 0,
        r: Int = size - 1,
        hook: (Int, T) -> Unit,
    ): Unit = when {
        cur == null -> Unit
        l == r -> hook(l, (cur as PstArrLeaf<T>).value)
        else -> {
            val m = l + (r - l) / 2
            traverse(cur.left, l, m, hook)
            traverse(cur.right, m + 1, r, hook)
        }
    }

    fun forEach(action: (T) -> Unit) = traverse(root) { _, it ->
        action(it)
    }

    fun forEachIndexed(action: (index: Int, value: T) -> Unit) =
        traverse(root, hook = action)

    fun <R> map(action: (T) -> R): List<R> {
        val ans = mutableListOf<R>()
        traverse(root) { _, it ->
            ans.add(action(it))
        }
        return ans
    }

    fun <R> mapIndexed(action: (index: Int, value: T) -> R): List<R> {
        val ans = mutableListOf<R>()
        traverse(root) { idx, value ->
            ans.add(action(idx, value))
        }
        return ans
    }

    override fun toString(): String {
        val buf = StringBuilder("[")
        forEach {
            if (buf.length > 1) {
                buf.append(", ")
            }
            buf.append(it)
        }
        buf.append("]")
        return buf.toString()
    }
}

// exports: PersistentArray