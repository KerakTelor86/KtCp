@file:Suppress("MemberVisibilityCanBePrivate", "unused")

package ktcp.ds.list

import ktcp.ds.modarray.*
import ktcp.math.modint.*

class ModIntList(size: Int, init: (Int) -> ModInt) : List<ModInt>,
    Collection<ModInt> {
    constructor(size: Int) : this(size, { ModInt(0) })

    override var size = size
        private set

    private var store = ModIntArray(size, init)

    val capacity: Int get() = store.size

    private fun reallocate(targetCapacity: Int) {
        store = ModIntArray(targetCapacity).apply {
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

    fun add(element: ModInt) {
        if (size == capacity) {
            reallocate(capacity * 2)
        }
        store[size++] = element
    }

    fun addAll(elements: Collection<ModInt>) {
        reserve(size + elements.size)
        elements.forEach { add(it) }
    }

    fun pop(): ModInt = store[--size]

    fun removeAt(index: Int): ModInt {
        val result = store[index]
        --size
        for (i in index..<size) {
            store[i] = store[i + 1]
        }
        return result
    }

    fun remove(element: ModInt): Boolean {
        val index = indexOf(element)
        if (index == -1) {
            return false
        }
        removeAt(index)
        return true
    }

    override fun contains(element: ModInt): Boolean {
        for (i in indices) {
            if (store[i] == element) {
                return true
            }
        }
        return false
    }

    override fun containsAll(elements: Collection<ModInt>): Boolean {
        val set = mutableSetOf<ModInt>()
        for (i in indices) {
            set.add(store[i])
        }
        return set.containsAll(elements)
    }

    override fun get(index: Int): ModInt = store[index]

    operator fun set(index: Int, value: ModInt) {
        store[index] = value
    }

    override fun indexOf(element: ModInt): Int {
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

    inner class ModIntListIterator(var index: Int) : ListIterator<ModInt> {
        override fun hasNext(): Boolean = index < size
        override fun hasPrevious(): Boolean = index > 0
        override fun next(): ModInt = store[index++]
        override fun nextIndex(): Int = index + 1
        override fun previous(): ModInt = store[--index]
        override fun previousIndex(): Int = index - 1
    }

    override fun listIterator(): ListIterator<ModInt> = ModIntListIterator(0)

    override fun listIterator(index: Int): ListIterator<ModInt> =
        ModIntListIterator(index)

    override fun iterator(): Iterator<ModInt> = ModIntListIterator(0)

    override fun subList(fromIndex: Int, toIndex: Int): List<ModInt> =
        ModIntList(toIndex - fromIndex + 1) { this[it + fromIndex] }

    override fun lastIndexOf(element: ModInt): Int {
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

fun modIntListOf(elements: ModIntArray): ModIntList =
    ModIntList(elements.size) {
        elements[it]
    }

fun List<ModInt>.toModIntList(): ModIntList = modIntListOf(this.toModIntArray())

// exports: ModIntList
// exports: modIntListOf
// exports: toModIntList
// depends: math/modint/ModInt.kt
// depends: ds/modarray/ModIntArray.kt
