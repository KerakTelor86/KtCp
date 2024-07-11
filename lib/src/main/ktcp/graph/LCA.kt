@file:Suppress("unused", "MemberVisibilityCanBePrivate")

package ktcp.graph

import ktcp.ds.fastarray.*
import ktcp.ds.sparsetable.*
import ktcp.misc.*
import java.nio.ByteBuffer

object LCASerializer : ByteSerializer<Pair<Int, Int>> {
    override val bytesRequired = 8

    override fun deserialize(buf: ByteBuffer, bufIdx: Int): Pair<Int, Int> {
        val temp = buf.asLongBuffer().get(bufIdx shr 3)
        return (temp shr 32).toInt() to temp.toInt()
    }

    override fun serialize(buf: ByteBuffer, bufIdx: Int, obj: Pair<Int, Int>) {
        buf.asLongBuffer().put(
            bufIdx shr 3,
            (obj.first.toLong() shl 32) or obj.second.toLong()
        )
    }
}

class LCA(numVertices: Int, adjList: Array<out List<Int>>, root: Int) {
    private val tIn = IntArray(numVertices)
    private val tOut = IntArray(numVertices)
    private val level = IntArray(numVertices)

    private val sparse = run {
        val order = arrayListOf<Int>()

        fun dfs(pos: Int, last: Int, depth: Int) {
            tIn[pos] = order.size
            tOut[pos] = order.size
            level[pos] = depth
            order.add(pos)

            for (child in adjList[pos]) {
                if (child == last) {
                    continue
                }
                dfs(child, pos, depth + 1)

                tOut[pos] = order.size
                order.add(pos)
            }
        }
        dfs(root, -1, 0)

        withFastArraySerializer(LCASerializer) {
            val base = FastArray(order.size) {
                val pos = order[it]
                Pair(level[pos], pos)
            }

            FastSparseTable(LCASerializer, base, Pair(0, 0)) { a, b ->
                val (aDepth, aIdx) = a
                val (bDepth, bIdx) = b
                when {
                    aDepth < bDepth -> a
                    bDepth < aDepth -> b
                    aIdx < bIdx -> a
                    else -> b
                }
            }
        }
    }

    fun getLcaDistance(u: Int, v: Int): Pair<Int, Int> {
        val (l, r) = minMax(tIn[u], tOut[v])
        val (lcaLevel, lca) = sparse.query(l, r)
        return Pair(lca, level[u] + level[v] - lcaLevel * 2)
    }

    fun getLca(u: Int, v: Int): Int {
        return getLcaDistance(u, v).first
    }

    fun getDistance(u: Int, v: Int): Int {
        return getLcaDistance(u, v).second
    }
}

// exports: LCA
// depends: ds/sparsetable/FastSparseTable.kt
// depends: ds/fastarray/FastArray.kt
// depends: misc/Shortcuts.kt