package ktcp.ds.segtree

internal fun computeSegTreeIndices(
    idx: Int,
    l: Int,
    r: Int,
): Triple<Int, Int, Int> {
    val m = l + (r - l) / 2
    val lc = idx + 1
    val rc = idx + (m - l + 1) * 2
    return Triple(lc, rc, m)
}
