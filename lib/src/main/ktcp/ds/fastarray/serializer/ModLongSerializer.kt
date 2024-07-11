@file:Suppress("unused")

package ktcp.ds.fastarray.serializer

import ktcp.math.modint.*
import java.nio.ByteBuffer

object ModLongSerializer : ByteSerializer<ModLong> {
    override val bytesRequired = 8

    override fun deserialize(buf: ByteBuffer, bufIdx: Int): ModLong {
        return buf.getLong(bufIdx).asModLong()
    }

    override fun serialize(buf: ByteBuffer, bufIdx: Int, obj: ModLong) {
        buf.putLong(bufIdx, obj.toLong())
    }
}

// exports: ModLongSerializer
// depends: ds/fastarray/serializer/Serializer.kt
// depends: math/modint/ModLong.kt
