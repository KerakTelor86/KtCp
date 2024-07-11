@file:Suppress("unused")

package ktcp.ds.fastarray.serializer

import java.nio.ByteBuffer

object IntSerializer : ByteSerializer<Int> {
    override val bytesRequired = 4

    override fun deserialize(buf: ByteBuffer, bufIdx: Int): Int {
        return buf.getInt(bufIdx)
    }

    override fun serialize(buf: ByteBuffer, bufIdx: Int, obj: Int) {
        buf.putInt(bufIdx, obj)
    }
}

// exports: IntSerializer
// depends: ds/fastarray/serializer/Serializer.kt
