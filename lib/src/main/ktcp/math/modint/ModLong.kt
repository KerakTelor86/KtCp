@file:Suppress("MemberVisibilityCanBePrivate", "NOTHING_TO_INLINE", "unused")

package ktcp.math.modint

@JvmInline
value class ModLong(val value: Long) {
    operator fun compareTo(other: ModLong): Int =
        this.value.compareTo(other.value)

    operator fun compareTo(other: Long): Int = this.value.compareTo(other)
    operator fun Long.compareTo(other: ModLong) = this.compareTo(other.value)

    override fun toString(): String = value.toString()

    fun toLong(): Long = value
}

fun Long.asModLong(): ModLong = ModLong(this)

class ModFieldLong(val mod: Long) {
    val montgomery = Montgomery(mod.toULong())

    inline operator fun ModLong.plus(other: ModLong): ModLong =
        ModLong((value + other.value) % mod)

    inline operator fun ModLong.minus(other: ModLong): ModLong =
        ModLong(((value - other.value) % mod + mod) % mod)

    inline operator fun ModLong.times(other: ModLong): ModLong {
        val ret = with(montgomery) {
            val aMont = this@times.value.toULong().toMontgomeryInt()
            val bMont = other.value.toULong().toMontgomeryInt()
            (aMont * bMont).toULong()
        }
        return ModLong(ret.toLong())
    }

    inline operator fun ModLong.div(other: ModLong): ModLong =
        this * other.inv()

    infix fun ModLong.pow(exp: Long): ModLong = when {
        exp == 0L -> ModLong(1)
        exp % 2 == 1L -> this * this.pow(exp - 1)
        else -> {
            val temp = this.pow(exp / 2)
            temp * temp
        }
    }

    inline operator fun ModLong.inv(): ModLong = this.pow(mod - 2)

    inline operator fun ModLong.plus(other: Long): ModLong =
        this + ModLong(other)

    inline operator fun ModLong.minus(other: Long): ModLong =
        this - ModLong(other)

    inline operator fun ModLong.times(other: Long): ModLong =
        this * ModLong(other)

    inline operator fun ModLong.div(other: Long): ModLong =
        this / ModLong(other)

    inline operator fun Long.plus(other: ModLong): ModLong =
        ModLong(this) * other

    inline operator fun Long.minus(other: ModLong): ModLong =
        ModLong(this) - other

    inline operator fun Long.times(other: ModLong): ModLong =
        ModLong(this) * other

    inline operator fun Long.div(other: ModLong): ModLong =
        ModLong(this) / other

    inline fun Long.toModLong(): ModLong = ModLong(this % mod)
}

fun <T> withModLong(mod: Long, block: ModFieldLong.() -> T): T {
    return ModFieldLong(mod).block()
}

// exports: ModLong
// exports: ModFieldLong
// exports: withModLong
// exports: toModLong
// exports: asModLong
// depends: math/modint/Montgomery.kt
