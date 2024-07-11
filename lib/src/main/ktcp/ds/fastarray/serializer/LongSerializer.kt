@file:Suppress("unused")

package ktcp.ds.fastarray.serializer

import java.nio.ByteBuffer

object LongSerializer : ByteSerializer<Long> {
    override val bytesRequired = 8

    override fun deserialize(buf: ByteBuffer, bufIdx: Int): Long {
        return buf.getLong(bufIdx)
    }

    override fun serialize(buf: ByteBuffer, bufIdx: Int, obj: Long) {
        buf.putLong(bufIdx, obj)
    }
}

// exports: LongSerializer
// depends: ds/fastarray/serializer/Serializer.kt
