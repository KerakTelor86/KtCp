# KtCp

Competitive programming library for Kotlin. Aims to be [CppCp](https://github.com/KerakTelor86/CppCp) for Kotlin. 

## Example usage: [AtCoder - Range Affine Range Sum](https://atcoder.jp/contests/practice2/tasks/practice2_k)

```kt
@file:Suppress("UnnecessaryOptInAnnotation")
@file:OptIn(ExperimentalStdlibApi::class)
@file:JvmName("K")

import ktcp.ds.fastarray.*
import ktcp.ds.fastarray.serializer.*
import ktcp.ds.segtree.*
import ktcp.math.modint.*
import ktcp.misc.*
import java.nio.ByteBuffer

fun main() = withFastIO {
    val (n, q) = readInts(2)
    val arr = readIntArray(n)

    withModInt(998244353) {
        data class Lazy(val u: ModInt, val v: ModInt)

        val modIntSerializer = ModIntSerializer
        val lazySerializer = object : ByteSerializer<Lazy> {
            override val bytesRequired: Int
                get() = 8

            override fun deserialize(buf: ByteBuffer, bufIdx: Int): Lazy {
                return Lazy(
                    ModIntSerializer.deserialize(buf, bufIdx),
                    ModIntSerializer.deserialize(buf, bufIdx + 4),
                )
            }

            override fun serialize(buf: ByteBuffer, bufIdx: Int, obj: Lazy) {
                ModIntSerializer.serialize(buf, bufIdx, obj.u)
                ModIntSerializer.serialize(buf, bufIdx + 4, obj.v)
            }
        }

        val base = withFastArraySerializer(modIntSerializer) {
            FastArray(arr.size) { arr[it].asModInt() }
        }

        val seg = FastLazySegTree(
            modIntSerializer,
            lazySerializer,
            base,
            0.asModInt(),
            Lazy(1.asModInt(), 0.asModInt()),
            { a, b -> a + b },
            { value, lazy, left, right ->
                value * lazy.u + lazy.v * (right - left + 1)
            },
            { a, b ->
                Lazy(a.u * b.u, a.v * b.u + b.v)
            }
        )

        repeat(q) {
            val t = readInt()
            if (t == 0) {
                val (l, r, b, c) = readInts(4)
                seg.update(l, r - 1, Lazy(b.asModInt(), c.asModInt()))
            } else {
                val (l, r) = readInts(2)
                println(seg.query(l, r - 1))
            }
        }
    }
}
```
