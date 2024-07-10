@file:Suppress("NOTHING_TO_INLINE", "unused", "MemberVisibilityCanBePrivate")

package ktcp.graph

import ktcp.ds.sparsetable.*

private inline fun encodeLcaToLong(depth: Int, idx: Int): Long =
    (depth.toLong() shl 32) or idx.toLong()

private inline fun decodeLcaFromLong(data: Long): Pair<Int, Int> =
    Pair((data shr 32).toInt(), data.toInt())

class LCA(numVertices: Int, adjList: List<List<Int>>, root: Int) {
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

        val base = LongArray(order.size) {
            val pos = order[it]
            encodeLcaToLong(level[pos], pos)
        }

        LongSparseTable(base) { a, b ->
            val (aDepth, aIdx) = decodeLcaFromLong(a)
            val (bDepth, bIdx) = decodeLcaFromLong(b)
            when {
                aDepth < bDepth -> a
                bDepth < aDepth -> b
                aIdx < bIdx -> a
                else -> b
            }
        }
    }

    fun getLcaDistance(u: Int, v: Int): Pair<Int, Int> {
        val (lcaLevel, lca) = decodeLcaFromLong(sparse.query(tIn[u], tOut[v]))
        return Pair(lca, level[u] + level[v] - lcaLevel)
    }

    fun getLca(u: Int, v: Int): Int {
        return getLcaDistance(u, v).first
    }

    fun getDistance(u: Int, v: Int): Int {
        return getLcaDistance(u, v).second
    }
}

// exports: LCA
// depends: ds/sparsetable/LongSparseTable.kt