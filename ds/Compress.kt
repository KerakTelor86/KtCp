@file:Suppress("MemberVisibilityCanBePrivate", "unused")

package ktcp.ds

class LiveCompressor<T> {
    private val store = hashMapOf<T, Int>()

    fun add(item: T) {
        if (item !in store) {
            store[item] = store.size
        }
    }

    fun addAll(items: Iterable<T>) = items.forEach { add(it) }
    fun compress(item: T): Int = store[item]!!
    fun compress(items: Iterable<T>): List<Int> = items.map { compress(it) }

    val size get(): Int = store.size
}

class DeferredCompressor<T : Comparable<T>>(vararg itemCols: Iterable<T>) {
    constructor(vararg items: T) : this(items.asIterable())

    @PublishedApi
    internal val store = arrayListOf<T>()

    init {
        itemCols.forEach { items -> items.forEach { store.add(it) } }
    }

    fun add(item: T) = store.add(item)
    fun addAll(items: Iterable<T>) = items.forEach { add(it) }
}

class FinalizedCompressor<T : Comparable<T>> @PublishedApi internal constructor(
    val store: Array<T>,
) {
    fun compress(item: T): Int = store.binarySearch(item)
    fun compress(items: Iterable<T>): List<Int> = items.map { compress(it) }
}

inline fun <reified T : Comparable<T>> DeferredCompressor<T>.finalized():
        FinalizedCompressor<T> {
    return FinalizedCompressor(store.distinct().sorted().toTypedArray())
}

// exports: LiveCompressor
// exports: DeferredCompressor
// exports: FinalizedCompressor