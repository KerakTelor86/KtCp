@file:Suppress("unused")

package ktcp.ds.modarray

import ktcp.math.modint.*

@JvmInline
value class ModIntArray internal constructor(
    private val store: IntArray,
) : Collection<ModInt> {
    companion object {
        operator fun invoke(n: Int) = ModIntArray(IntArray(n))
        operator fun invoke(n: Int, init: (Int) -> ModInt) = ModIntArray(
            IntArray(n) {
                init(it).toInt()
            }
        )
    }

    operator fun get(index: Int): ModInt = ModInt(this.store[index])
    operator fun set(index: Int, value: ModInt) =
        this.store.set(index, value.toInt())

    override val size get() = this.store.size
    override fun isEmpty(): Boolean = this.store.isEmpty()

    override fun iterator(): Iterator<ModInt> = object : Iterator<ModInt> {
        var idx = 0

        override fun hasNext(): Boolean = idx < this@ModIntArray.size
        override fun next(): ModInt = this@ModIntArray[idx++]
    }

    override fun contains(element: ModInt): Boolean =
        element.value in this.store

    override fun containsAll(elements: Collection<ModInt>): Boolean =
        elements.all {
            this.contains(it)
        }
}

fun Collection<ModInt>.toModIntArray(): ModIntArray =
    ModIntArray(this.size).let {
        var idx = 0
        for (i in this) {
            it[idx++] = i
        }
        it
    }

// exports: ModIntArray
// exports: toModIntArray
// depends: math/modint/ModInt.kt
