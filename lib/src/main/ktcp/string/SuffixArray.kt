@file:Suppress("unused")

package ktcp.string

import ktcp.math.*
import kotlin.math.max

fun getOrderedCycleShifts(s: IntArray): IntArray {
    val n = s.size

    var numKeys = s.max() + 1
    val keyEndIdx = IntArray(max(numKeys, s.size))

    val order = s.indices.sortedBy { s[it] }.toIntArray()
    val scratch = IntArray(n)

    fun updateOrderByKey(key: IntArray) {
        keyEndIdx.fill(0, 0, numKeys)
        for (i in key) {
            ++keyEndIdx[i]
        }
        for (i in 1..<numKeys) {
            keyEndIdx[i] += keyEndIdx[i - 1]
        }
        // scratch is revOrder
        for (i in order.indices) {
            scratch[i] = order[n - i - 1]
        }
        for (idx in scratch) {
            order[--keyEndIdx[key[idx]]] = idx
        }
    }

    val keyNxt = IntArray(n)
    val keyCur = s.clone()

    for (k in 0..log2Floor(n)) {
        val offset = 1 shl k

        for (idx in 0..<n) {
            val curIdx = order[idx]
            val prvIdx = (curIdx - offset).let {
                when {
                    it < 0 -> when {
                        it + n < 0 -> it + n + n
                        else -> it + n
                    }

                    else -> it
                }
            }
            keyNxt[prvIdx] = keyCur[curIdx]
        }

        updateOrderByKey(keyNxt)
        updateOrderByKey(keyCur)

        // scratch is keyCur
        for (i in order.indices) {
            scratch[i] = keyCur[i]
        }

        numKeys = 0
        keyCur[order[0]] = 0
        for (i in 1..<n) {
            val idx = order[i]
            val prvIdx = order[i - 1]
            if (
                scratch[idx] != scratch[prvIdx]
                || keyNxt[idx] != keyNxt[prvIdx]
            ) {
                ++numKeys
            }
            keyCur[idx] = numKeys
        }
        ++numKeys
    }

    return order
}

fun getOrderedCycleShifts(s: String): IntArray =
    getOrderedCycleShifts(s.map { it.code }.toIntArray())

fun getSuffixArray(s: String): IntArray =
    getOrderedCycleShifts("$s$").toMutableList().apply {
        remove(s.length)
    }.toIntArray()

fun getLcpArray(s: String, suffixArray: IntArray): IntArray {
    val n = s.length
    val rank = IntArray(n).apply {
        for (i in 0..<n) {
            this[suffixArray[i]] = i
        }
    }
    var k = 0
    val lcp = IntArray(n)
    for (i in 0..<n) {
        if (rank[i] == n - 1) {
            k = 0
            continue
        }
        val j = suffixArray[rank[i] + 1]
        while (i + k < n && j + k < n && s[i + k] == s[j + k]) {
            ++k
        }
        lcp[rank[i]] = k
        k = max(0, k - 1)
    }
    return lcp
}

// exports: getOrderedCycleShifts
// exports: getSuffixArray
// exports: getLcpArray
// depends: math/Bitwise.kt
