# KtCp

Competitive programming library for Kotlin. Aiming to be [CppCp](https://github.com/KerakTelor86/CppCp) for Kotlin. 

## Example usage: [AtCoder - Range Affine Range Sum](https://atcoder.jp/contests/practice2/tasks/practice2_k)

```kt
@file:Suppress("UnnecessaryOptInAnnotation")
@file:OptIn(ExperimentalStdlibApi::class)
@file:JvmName("K")

package solutions

import ktcp.ds.segtree.*
import ktcp.math.modint.*
import ktcp.misc.*

fun main() = withFastIO {
    val (n, q) = readInts(2)
    val arr = readIntArray(n)

    withModInt(998244353) {
        fun Long.toModInt(): ModInt = this.toInt().toModInt()
        fun Long.toModIntPair(): Pair<ModInt, ModInt> =
            Pair((this shr 32).toModInt(), this.toModInt())

        fun ModInt.toLong(): Long = this.toInt().toLong()
        fun Pair<ModInt, ModInt>.toLong(): Long =
            (first.toLong() shl 32) or second.toLong()

        val seg = LongLazySegTree(
            arr.map { it.toLong() }.toLongArray(),
            nilValue = ModInt(0).toLong(),
            nilLazy = Pair(ModInt(1), ModInt(0)).toLong(),
            operation = { a: Long, b: Long ->
                (a.toModInt() + b.toModInt()).toLong()
            },
            applyLazy = { value: Long, lazy: Long, left: Int, right: Int ->
                val (lazyU, lazyV) = lazy.toModIntPair()
                (value.toModInt() * lazyU + lazyV * (right - left + 1)).toLong()
            },
            mergeLazy = { a: Long, b: Long ->
                val (aU, aV) = a.toModIntPair()
                val (bU, bV) = b.toModIntPair()
                Pair(aU * bU, aV * bU + bV).toLong()
            }
        )

        repeat(q) {
            val t = readInt()
            if (t == 0) {
                val (l, r, b, c) = readInts(4)
                seg.update(l, r - 1, Pair(b.toModInt(), c.toModInt()).toLong())
            } else {
                val (l, r) = readInts(2)
                println(seg.query(l, r - 1))
            }
        }
    }
}
```
