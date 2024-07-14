@file:Suppress("unused")

package ktcp.ds.fastarray.serializer

import java.nio.ByteBuffer

object DoubleSerializer : ByteSerializer<Double> {
    override val bytesRequired = 8

    override fun deserialize(buf: ByteBuffer, bufIdx: Int): Double {
        return buf.getDouble(bufIdx)
    }

    override fun serialize(buf: ByteBuffer, bufIdx: Int, obj: Double) {
        buf.putDouble(bufIdx, obj)
    }
}

// exports: DoubleSerializer
// depends: ds/fastarray/serializer/Serializer.kt
