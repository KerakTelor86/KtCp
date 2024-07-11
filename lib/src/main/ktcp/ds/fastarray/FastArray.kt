@file:Suppress("NOTHING_TO_INLINE", "unused")

package ktcp.ds.fastarray

import java.nio.ByteBuffer

interface ByteSerializer<T> {
    val bytesRequired: Int

    fun deserialize(buf: ByteBuffer, bufIdx: Int): T
    fun serialize(buf: ByteBuffer, bufIdx: Int, obj: T)
}

@JvmInline
value class FastArray @PublishedApi internal constructor(
    @PublishedApi internal val buffer: ByteBuffer,
) {
    companion object;
}

@JvmInline
value class FastArrayContext<T>(
    @PublishedApi internal val serializer: ByteSerializer<T>,
) {
    inline val FastArray.size
        get() = buffer.capacity() / serializer.bytesRequired

    inline operator fun FastArray.Companion.invoke(size: Int): FastArray =
        FastArray(ByteBuffer.allocate(size * serializer.bytesRequired))

    inline operator fun FastArray.Companion.invoke(
        size: Int,
        init: (Int) -> T,
    ): FastArray {
        val arr = FastArray(size)
        for (i in 0..<size) {
            arr[i] = init(i)
        }
        return arr
    }

    inline operator fun FastArray.get(index: Int): T =
        serializer.deserialize(buffer, index * serializer.bytesRequired)

    inline operator fun FastArray.set(index: Int, value: T) =
        serializer.serialize(buffer, index * serializer.bytesRequired, value)

    inline fun FastArray.transform(index: Int, transform: (T) -> T) {
        val bufIndex = index * serializer.bytesRequired
        val res = transform(serializer.deserialize(buffer, bufIndex))
        serializer.serialize(buffer, bufIndex, res)
    }
}

inline fun <T, R> withFastArraySerializer(
    serializer: ByteSerializer<T>,
    block: FastArrayContext<T>.() -> R,
): R = FastArrayContext(serializer).block()

// exports: ByteSerializer
// exports: FastArrayContext
// exports: withFastArraySerializer