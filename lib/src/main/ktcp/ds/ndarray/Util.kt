@file:Suppress("NOTHING_TO_INLINE")

package ktcp.ds.ndarray

internal inline fun calculateNdArrayIndex(
    index: IntArray,
    shape: IntArray,
): Int {
    var res = 0
    for (i in index.indices) {
        res = res * shape[i] + index[i]
    }
    return res
}