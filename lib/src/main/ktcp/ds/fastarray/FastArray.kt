@file:Suppress("NOTHING_TO_INLINE", "unused")

package ktcp.ds.fastarray

import ktcp.ds.fastarray.serializer.*
import java.nio.ByteBuffer


@JvmInline
value class FastArray @PublishedApi internal constructor(
    @PublishedApi internal val buffer: ByteBuffer,
) {
    companion object;
}

class FastList @PublishedApi internal constructor(
    @PublishedApi internal var buffer: ByteBuffer,
) {
    companion object;

    val size: Int
        get() = buffer.getInt(0)

    @PublishedApi
    internal inline fun setSize(value: Int) {
        buffer.putInt(0, value)
    }
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

    inline val FastList.capacity
        get() = (buffer.capacity() - 4) / serializer.bytesRequired

    operator fun FastList.Companion.invoke(size: Int): FastList =
        FastList(
            ByteBuffer.allocate(4 + size * serializer.bytesRequired)
        ).also {
            it.setSize(size)
        }

    inline operator fun FastList.Companion.invoke(
        size: Int,
        init: (Int) -> T,
    ): FastList {
        val arr = FastList(size)
        for (i in 0..<size) {
            arr[i] = init(i)
        }
        return arr
    }

    inline operator fun FastList.get(index: Int): T =
        serializer.deserialize(buffer, 4 + index * serializer.bytesRequired)

    inline operator fun FastList.set(index: Int, value: T) =
        serializer.serialize(
            buffer,
            4 + index * serializer.bytesRequired,
            value
        )

    inline fun FastList.transform(index: Int, transform: (T) -> T) {
        val bufIndex = 4 + index * serializer.bytesRequired
        val res = transform(serializer.deserialize(buffer, bufIndex))
        serializer.serialize(buffer, bufIndex, res)
    }

    inline fun FastList.add(value: T) {
        if (size == capacity) {
            reallocate(2 * capacity)
        }
        this[size] = value
        setSize(size + 1)
    }

    inline fun FastList.addAll(elements: Collection<T>) {
        reserve(size + elements.size)
        elements.forEach { add(it) }
    }

    inline fun FastList.reallocate(targetCapacity: Int) {
        buffer = ByteBuffer
            .allocate(
                4 + targetCapacity * serializer.bytesRequired
            )
            .rewind()
            .put(buffer.rewind())
    }

    inline fun FastList.reserve(minCapacity: Int) {
        if (capacity < minCapacity) {
            reallocate(minCapacity)
        }
    }

    inline fun FastList.pop() {
        if (size == 0) {
            throw NoSuchElementException("Cannot pop an empty list")
        }
        setSize(size - 1)
    }

    inline fun FastList.clear() = setSize(0)
}

// exports: FastArray
// exports: FastList
// exports: FastArrayContext
// depends: ds/fastarray/serializer/Serializer.kt
