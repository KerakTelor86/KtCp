@file:Suppress("MemberVisibilityCanBePrivate", "unused")

package ktcp.ds

class DisjointSet(size: Int) {
    private val parent = IntArray(size) { it }
    private val counts = IntArray(size) { 1 }

    fun getRoot(x: Int): Int = when {
        parent[x] == x -> x
        else -> {
            parent[x] = getRoot(parent[x])
            parent[x]
        }
    }

    fun makeRoot(x: Int) {
        val y = getRoot(x)
        if (x != y) {
            parent[x] = x
            counts[x] = counts[y]
            parent[y] = x
        }
    }

    fun join(x: Int, y: Int) {
        val a = getRoot(x)
        val b = getRoot(y)
        if (a == b) {
            return
        }
        val (lo, hi) = if (counts[a] < counts[b]) {
            a to b
        } else {
            b to a
        }
        counts[hi] += counts[lo]
        parent[lo] = hi
    }

    fun getCount(x: Int): Int = counts[x]
    val size get() = parent.size
}

// exports: DisjointSet