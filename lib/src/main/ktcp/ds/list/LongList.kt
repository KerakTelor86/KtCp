@file:Suppress("MemberVisibilityCanBePrivate", "unused")

package ktcp.ds.list

class LongList(size: Int, init: (Int) -> Long) : List<Long>, Collection<Long> {
    constructor(size: Int) : this(size, { 0L })

    override var size = size
        private set

    private var store = LongArray(size, init)

    val capacity: Int get() = store.size

    private fun reallocate(targetCapacity: Int) {
        store = LongArray(targetCapacity).apply {
            for (i in 0..<capacity) {
                this[i] = store[i]
            }
        }
    }

    fun reserve(minCapacity: Int) {
        if (capacity < minCapacity) {
            reallocate(minCapacity)
        }
    }

    fun add(element: Long) {
        if (size == capacity) {
            reallocate(capacity * 2)
        }
        store[size++] = element
    }

    fun addAll(elements: Collection<Long>) {
        reserve(size + elements.size)
        elements.forEach { add(it) }
    }

    fun pop(): Long = store[--size]

    fun removeAt(index: Int): Long {
        val result = store[index]
        --size
        for (i in index..<size) {
            store[i] = store[i + 1]
        }
        return result
    }

    fun remove(element: Long): Boolean {
        val index = indexOf(element)
        if (index == -1) {
            return false
        }
        removeAt(index)
        return true
    }

    override fun contains(element: Long): Boolean {
        for (i in indices) {
            if (store[i] == element) {
                return true
            }
        }
        return false
    }

    override fun containsAll(elements: Collection<Long>): Boolean {
        val set = mutableSetOf<Long>()
        for (i in indices) {
            set.add(store[i])
        }
        return set.containsAll(elements)
    }

    override fun get(index: Int): Long = store[index]

    operator fun set(index: Int, value: Long) {
        store[index] = value
    }

    override fun indexOf(element: Long): Int {
        for (i in indices) {
            if (store[i] == element) {
                return i
            }
        }
        return -1
    }

    @Suppress("ReplaceSizeZeroCheckWithIsEmpty")
    override fun isEmpty(): Boolean {
        return size == 0
    }

    inner class LongListIterator(var index: Int) : ListIterator<Long> {
        override fun hasNext(): Boolean = index < size
        override fun hasPrevious(): Boolean = index > 0
        override fun next(): Long = store[index++]
        override fun nextIndex(): Int = index + 1
        override fun previous(): Long = store[--index]
        override fun previousIndex(): Int = index - 1
    }

    override fun listIterator(): ListIterator<Long> = LongListIterator(0)

    override fun listIterator(index: Int): ListIterator<Long> =
        LongListIterator(index)

    override fun iterator(): Iterator<Long> = LongListIterator(0)

    override fun subList(fromIndex: Int, toIndex: Int): List<Long> =
        LongList(toIndex - fromIndex + 1) { this[it + fromIndex] }

    override fun lastIndexOf(element: Long): Int {
        for (i in indices.reversed()) {
            if (store[i] == element) {
                return i
            }
        }
        return -1
    }

    override fun toString(): String {
        return joinToString(", ", "[", "]")
    }
}

fun longListOf(elements: LongArray): LongList = LongList(elements.size) {
    elements[it]
}

fun longListOf(vararg elements: Long): LongList = longListOf(elements)
fun List<Long>.toLongList(): LongList = longListOf(this.toLongArray())

// exports: LongList
// exports: longListOf
// exports: toLongList
