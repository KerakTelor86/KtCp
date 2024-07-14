@file:Suppress("MemberVisibilityCanBePrivate", "unused")

package ktcp.ds.list

class IntList(size: Int, init: (Int) -> Int) : List<Int>, Collection<Int> {
    constructor(size: Int) : this(size, { 0 })

    override var size = size
        private set

    private var store = IntArray(size, init)

    val capacity: Int get() = store.size

    private fun reallocate(targetCapacity: Int) {
        store = IntArray(targetCapacity).apply {
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

    fun add(element: Int) {
        if (size == capacity) {
            reallocate(capacity * 2)
        }
        store[size++] = element
    }

    fun addAll(elements: Collection<Int>) {
        reserve(size + elements.size)
        elements.forEach { add(it) }
    }

    fun pop(): Int = store[--size]

    fun removeAt(index: Int): Int {
        val result = store[index]
        --size
        for (i in index..<size) {
            store[i] = store[i + 1]
        }
        return result
    }

    fun remove(element: Int): Boolean {
        val index = indexOf(element)
        if (index == -1) {
            return false
        }
        removeAt(index)
        return true
    }

    override fun contains(element: Int): Boolean {
        for (i in indices) {
            if (store[i] == element) {
                return true
            }
        }
        return false
    }

    override fun containsAll(elements: Collection<Int>): Boolean {
        val set = mutableSetOf<Int>()
        for (i in indices) {
            set.add(store[i])
        }
        return set.containsAll(elements)
    }

    override fun get(index: Int): Int = store[index]

    operator fun set(index: Int, value: Int) {
        store[index] = value
    }

    override fun indexOf(element: Int): Int {
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

    inner class IntListIterator(var index: Int) : ListIterator<Int> {
        override fun hasNext(): Boolean = index < size
        override fun hasPrevious(): Boolean = index > 0
        override fun next(): Int = store[index++]
        override fun nextIndex(): Int = index + 1
        override fun previous(): Int = store[--index]
        override fun previousIndex(): Int = index - 1
    }

    override fun listIterator(): ListIterator<Int> = IntListIterator(0)

    override fun listIterator(index: Int): ListIterator<Int> =
        IntListIterator(index)

    override fun iterator(): Iterator<Int> = IntListIterator(0)

    override fun subList(fromIndex: Int, toIndex: Int): List<Int> =
        IntList(toIndex - fromIndex + 1) { this[it + fromIndex] }

    override fun lastIndexOf(element: Int): Int {
        for (i in size - 1 downTo 0) {
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

fun intListOf(elements: IntArray): IntList = IntList(elements.size) {
    elements[it]
}

@JvmName("intListOfVararg")
fun intListOf(vararg elements: Int): IntList = intListOf(elements)
fun List<Int>.toIntList(): IntList = intListOf(this.toIntArray())

// exports: IntList
// exports: intListOf
// exports: toIntList
