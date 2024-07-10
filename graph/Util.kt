@file:Suppress("unused")

package ktcp.graph

@JvmName("edgeListToAdjList")
fun toAdjList(
    numVertices: Int,
    edges: Iterable<Pair<Int, Int>>,
    directed: Boolean = false,
): Array<out List<Int>> {
    return Array(numVertices) { arrayListOf<Int>() }.apply {
        for ((u, v) in edges) {
            this[u].add(v)
            if (!directed) {
                this[v].add(u)
            }
        }
    }
}

@JvmName("edgeListToAdjListWeighted")
fun toAdjList(
    numVertices: Int,
    edges: Iterable<Triple<Int, Int, Int>>,
    directed: Boolean = false,
): Array<out List<Pair<Int, Int>>> {
    return Array(numVertices) { arrayListOf<Pair<Int, Int>>() }.apply {
        for ((u, v, w) in edges) {
            this[u].add(Pair(v, w))
            if (!directed) {
                this[v].add(Pair(u, w))
            }
        }
    }
}

// exports: toAdjList