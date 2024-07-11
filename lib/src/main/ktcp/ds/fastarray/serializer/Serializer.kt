package ktcp.ds.fastarray.serializer

import ktcp.ds.fastarray.*
import java.nio.ByteBuffer

interface ByteSerializer<T> {
    val bytesRequired: Int

    fun deserialize(buf: ByteBuffer, bufIdx: Int): T
    fun serialize(buf: ByteBuffer, bufIdx: Int, obj: T)
}

inline fun <T, R> withFastArraySerializer(
    serializer: ByteSerializer<T>,
    block: FastArrayContext<T>.() -> R,
): R = FastArrayContext(serializer).block()
