@file:Suppress("unused")

package ktcp.ds.modarray

import ktcp.math.modint.*

@JvmInline
value class ModLongArray internal constructor(
    private val store: LongArray,
) : Collection<ModLong> {
    companion object {
        operator fun invoke(n: Int) = ModLongArray(LongArray(n))
        operator fun invoke(n: Int, init: (Int) -> ModLong) = ModLongArray(
            LongArray(n) {
                init(it).toLong()
            }
        )
    }

    operator fun get(index: Int): ModLong = ModLong(this.store[index])
    operator fun set(index: Int, value: ModLong) =
        this.store.set(index, value.toLong())

    override val size get() = this.store.size
    override fun isEmpty(): Boolean = this.store.isEmpty()

    override fun iterator(): Iterator<ModLong> = object : Iterator<ModLong> {
        var idx = 0

        override fun hasNext(): Boolean = idx < this@ModLongArray.size
        override fun next(): ModLong = this@ModLongArray[idx++]
    }

    override fun contains(element: ModLong): Boolean =
        element.value in this.store

    override fun containsAll(elements: Collection<ModLong>): Boolean =
        elements.all {
            this.contains(it)
        }
}

fun Collection<ModLong>.toModLongArray(): ModLongArray =
    ModLongArray(this.size).let {
        var idx = 0
        for (i in this) {
            it[idx++] = i
        }
        it
    }

// exports: ModLongArray
// exports: toModLongArray
// depends: math/modint/ModLong.kt