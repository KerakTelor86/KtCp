@file:Suppress("unused")

package ktcp.ds.fastarray.serializer

import ktcp.math.modint.*
import java.nio.ByteBuffer

object ModIntSerializer : ByteSerializer<ModInt> {
    override val bytesRequired = 4

    override fun deserialize(buf: ByteBuffer, bufIdx: Int): ModInt {
        return buf.getInt(bufIdx).asModInt()
    }

    override fun serialize(buf: ByteBuffer, bufIdx: Int, obj: ModInt) {
        buf.putInt(bufIdx, obj.toInt())
    }
}

// exports: ModIntSerializer
// depends: ds/fastarray/serializer/Serializer.kt
// depends: math/modint/ModInt.kt
