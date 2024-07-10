@file:Suppress("MemberVisibilityCanBePrivate", "unused")

package ktcp.math.modint

@JvmInline
private value class UInt128(private val value: Pair<ULong, ULong>) {
    constructor(hi: ULong, lo: ULong) : this(Pair(hi, lo))

    val hi get() = value.first
    val lo get() = value.second
}

private infix fun ULong.mul128(other: ULong): UInt128 {
    val a = (this shr 32).toUInt()
    val b = this.toUInt()

    val c = (other shr 32).toUInt()
    val d = other.toUInt()

    val ac = a.toULong() * c
    val ad = a.toULong() * d
    val bc = b.toULong() * c
    val bd = b.toULong() * d

    val carry = ad.toUInt().toULong() + bc.toUInt().toULong() + (bd shr 32)

    return UInt128(
        ac + (ad shr 32) + (bc shr 32) + (carry shr 32),
        (ad shl 32) + (bc shl 32) + bd
    )
}

class Montgomery private constructor(private val mod: ULong) {
    companion object {
        private val initialized = hashMapOf<ULong, Montgomery>()

        operator fun invoke(mod: ULong): Montgomery =
            initialized.getOrPut(mod) { Montgomery(mod) }
    }

    private val inv = run {
        var temp = 1UL
        repeat(7) {
            temp *= 2UL - mod * temp
        }
        temp
    }

    private val r2 = run {
        var temp = (0UL - mod) % mod
        repeat(2) {
            temp = (temp shl 1) % mod
        }
        var tempMont = MontgomeryInt(temp)
        repeat(5) {
            tempMont *= tempMont
        }
        tempMont
    }

    private fun reduce(value: UInt128): ULong {
        val q = value.lo * inv
        var a = value.hi - (q mul128 mod).hi
        if (a.toLong() < 0) {
            a += mod
        }
        return a
    }

    @JvmInline
    value class MontgomeryInt internal constructor(val value: ULong)

    operator fun MontgomeryInt.times(other: MontgomeryInt): MontgomeryInt =
        MontgomeryInt(reduce(this.value mul128 other.value))

    fun ULong.toMontgomeryInt(): MontgomeryInt = MontgomeryInt(this) * r2
    fun MontgomeryInt.toULong(): ULong = reduce(UInt128(0UL, this.value))
}

// exports: Montgomery