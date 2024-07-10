@file:Suppress("unused")

package ktcp.string

import ktcp.ds.modarray.*
import ktcp.math.modint.*
import ktcp.misc.*

data class RollingHash internal constructor(val hashes: ModIntArray) {
    companion object;

    val len get(): Int = hashes.last().toInt()

    override fun toString(): String {
        val hashesStr = StringBuilder().apply {
            for (i in 0..<hashes.size - 1) {
                if (i != 0) {
                    append(", ")
                }
                append(hashes[i].toString())
            }
        }.toString()
        return "[len = $len | hashes = $hashesStr]"
    }

    operator fun compareTo(other: RollingHash): Int {
        if (hashes.size != other.hashes.size) {
            return hashes.size.compareTo(other.hashes.size)
        }
        for (i in hashes.indices) {
            if (hashes[i] != other.hashes[i]) {
                return hashes[i].compareTo(other.hashes[i])
            }
        }
        return 0
    }

    override fun equals(other: Any?): Boolean {
        if (other !is RollingHash) {
            return false
        }
        for (i in hashes.indices) {
            if (hashes[i] != other.hashes[i]) {
                return false
            }
        }
        return true
    }

    override fun hashCode(): Int =
        hashes.fold(0) { acc, cur -> acc * 31 + cur.toInt() }
}

private const val HASH_MOD = 998244353
private const val HASH_MUL_MIN = 1 shl 15
private const val HASH_MUL_MAX = 1 shl 25

class HashField(maxLen: Int, private val numHashes: Int) {
    private val modField = ModFieldInt(HASH_MOD)

    private val mulPowers = with(modField) {
        Array(numHashes) {
            val mul = random.nextInt(HASH_MUL_MIN, HASH_MUL_MAX)

            ModIntArray(maxLen + 1).apply {
                this[0] = ModInt(1)
                for (i in 1..maxLen) {
                    this[i] = this[i - 1] * mul
                }
            }
        }
    }

    operator fun RollingHash.plus(other: RollingHash): RollingHash =
        with(modField) {
            RollingHash(
                ModIntArray(numHashes + 1).apply {
                    for (i in 0..<numHashes) {
                        val shift = mulPowers[i][other.len]
                        this[i] = hashes[i] * shift + other.hashes[i]
                    }
                    this[numHashes] = (len + other.len).asModInt()
                }
            )
        }

    operator fun RollingHash.minus(other: RollingHash): RollingHash =
        with(modField) {
            RollingHash(
                ModIntArray(numHashes + 1).apply {
                    for (i in 0..<numHashes) {
                        val shift = mulPowers[i][len - other.len]
                        this[i] = hashes[i] - other.hashes[i] * shift
                    }
                    this[numHashes] = (len - other.len).asModInt()
                }
            )
        }

    operator fun RollingHash.Companion.invoke(): RollingHash =
        RollingHash(ModIntArray(numHashes + 1))

    operator fun RollingHash.Companion.invoke(c: Char): RollingHash =
        RollingHash(ModIntArray(numHashes + 1).apply {
            for (i in 0..<numHashes) {
                this[i] = c.code.asModInt()
            }
            this[numHashes] = 1.asModInt()
        })

    operator fun RollingHash.Companion.invoke(str: String): RollingHash =
        str.fold(RollingHash()) { acc, c ->
            acc + RollingHash(c)
        }
}

fun <T> withRollingHash(
    maxLen: Int,
    numHashes: Int,
    block: HashField.() -> T,
): T {
    return HashField(maxLen, numHashes).block()
}

// exports: RollingHash
// exports: HashField
// exports: withRollingHash
// depends: ds/modarray/ModIntArray.kt
// depends: math/modint/ModInt.kt