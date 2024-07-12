@file:Suppress("unused", "MemberVisibilityCanBePrivate", "DuplicatedCode")

package ktcp.ds.persistent

class PersistentList<T> private constructor(
    val size: Int,
    private val store: PersistentArray<T>,
) {
    constructor() : this(0, PersistentArray(0))

    constructor(size: Int, init: (Int) -> T) : this(
        size,
        PersistentArray(size, init)
    )

    val capacity get() = store.size

    fun pop(): PersistentList<T> {
        if (size == 0) {
            throw IndexOutOfBoundsException("List is empty")
        }
        if (size * 3 < capacity) {
            return shrink().pop()
        }
        return PersistentList(size - 1, store.clone())
    }

    fun add(element: T): PersistentList<T> {
        if (size < capacity) {
            return PersistentList(size + 1, store.set(size, element))
        }
        var newStore = PersistentArray<T>(2 * capacity)
        store.forEachIndexed { index, value ->
            newStore = newStore.set(index, value)
        }
        return PersistentList(size + 1, newStore.set(size, element))
    }

    fun addAll(elements: Collection<T>): PersistentList<T> {
        var res = this
        elements.forEach {
            res = res.add(it)
        }
        return res
    }

    fun clone(): PersistentList<T> = PersistentList(size, store)

    fun shrink(): PersistentList<T> = PersistentList(
        size,
        PersistentArray(size) { store[it] }
    )

    fun set(index: Int, value: T): PersistentList<T> {
        if (index >= size) {
            throw IndexOutOfBoundsException(
                "Index $index is out of bounds for size $size"
            )
        }
        return PersistentList(size, store.set(index, value))
    }

    fun transform(index: Int, transform: (T) -> T): PersistentList<T> {
        if (index >= size) {
            throw IndexOutOfBoundsException(
                "Index $index is out of bounds for size $size"
            )
        }
        return PersistentList(size, store.transform(index, transform))
    }

    operator fun get(index: Int): T {
        if (index >= size) {
            throw IndexOutOfBoundsException(
                "Index $index is out of bounds for size $size"
            )
        }
        return store[index]
    }

    fun forEachIndexed(action: (index: Int, value: T) -> Unit) =
        store.forEachIndexed { idx, it ->
            if (idx < size) {
                action(idx, it)
            }
        }

    fun forEach(action: (T) -> Unit) = forEachIndexed { _, it -> action(it) }

    fun <R> mapIndexed(action: (index: Int, value: T) -> R): List<R> {
        val ans = mutableListOf<R>()
        store.forEachIndexed { idx, it ->
            ans.add(action(idx, it))
        }
        return ans
    }

    fun <R> map(action: (T) -> R): List<R> = mapIndexed { _, it -> action(it) }

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

    fun toList(): List<T> = map { it }
    fun first(): T = this[0]
    fun last(): T = this[size - 1]
}

// exports: PersistentList
// depends: ds/persistent/PersistentArray.kt