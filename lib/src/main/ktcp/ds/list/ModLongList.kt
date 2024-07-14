@file:Suppress("MemberVisibilityCanBePrivate", "unused")

package ktcp.ds.list

import ktcp.ds.modarray.*
import ktcp.math.modint.*

class ModLongList(size: Int, init: (Int) -> ModLong) : List<ModLong>,
    Collection<ModLong> {
    constructor(size: Int) : this(size, { ModLong(0) })

    override var size = size
        private set

    private var store = ModLongArray(size, init)

    val capacity: Int get() = store.size

    private fun reallocate(targetCapacity: Int) {
        store = ModLongArray(targetCapacity).apply {
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

    fun add(element: ModLong) {
        if (size == capacity) {
            reallocate(capacity * 2)
        }
        store[size++] = element
    }

    fun addAll(elements: Collection<ModLong>) {
        reserve(size + elements.size)
        elements.forEach { add(it) }
    }

    fun pop(): ModLong = store[--size]

    fun removeAt(index: Int): ModLong {
        val result = store[index]
        --size
        for (i in index..<size) {
            store[i] = store[i + 1]
        }
        return result
    }

    fun remove(element: ModLong): Boolean {
        val index = indexOf(element)
        if (index == -1) {
            return false
        }
        removeAt(index)
        return true
    }

    override fun contains(element: ModLong): Boolean {
        for (i in indices) {
            if (store[i] == element) {
                return true
            }
        }
        return false
    }

    override fun containsAll(elements: Collection<ModLong>): Boolean {
        val set = mutableSetOf<ModLong>()
        for (i in indices) {
            set.add(store[i])
        }
        return set.containsAll(elements)
    }

    override fun get(index: Int): ModLong = store[index]

    operator fun set(index: Int, value: ModLong) {
        store[index] = value
    }

    override fun indexOf(element: ModLong): Int {
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

    inner class ModLongListIterator(var index: Int) : ListIterator<ModLong> {
        override fun hasNext(): Boolean = index < size
        override fun hasPrevious(): Boolean = index > 0
        override fun next(): ModLong = store[index++]
        override fun nextIndex(): Int = index + 1
        override fun previous(): ModLong = store[--index]
        override fun previousIndex(): Int = index - 1
    }

    override fun listIterator(): ListIterator<ModLong> = ModLongListIterator(0)

    override fun listIterator(index: Int): ListIterator<ModLong> =
        ModLongListIterator(index)

    override fun iterator(): Iterator<ModLong> = ModLongListIterator(0)

    override fun subList(fromIndex: Int, toIndex: Int): List<ModLong> =
        ModLongList(toIndex - fromIndex + 1) { this[it + fromIndex] }

    override fun lastIndexOf(element: ModLong): Int {
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

fun modLongListOf(elements: ModLongArray): ModLongList =
    ModLongList(elements.size) {
        elements[it]
    }

fun List<ModLong>.toModLongList(): ModLongList =
    modLongListOf(this.toModLongArray())

// exports: ModLongList
// exports: modLongListOf
// exports: toModLongList
// depends: math/modint/ModLong.kt
// depends: ds/modarray/ModLongArray.kt
