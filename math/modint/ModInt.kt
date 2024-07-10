@file:Suppress("MemberVisibilityCanBePrivate", "NOTHING_TO_INLINE", "unused")

package ktcp.math.modint

@JvmInline
value class ModInt(val value: Int) {
    operator fun compareTo(other: ModInt): Int =
        this.value.compareTo(other.value)

    operator fun compareTo(other: Int): Int = this.value.compareTo(other)
    operator fun Int.compareTo(other: ModInt) = this.compareTo(other.value)

    override fun toString(): String = value.toString()

    fun toInt(): Int = value
}

fun Int.asModInt(): ModInt = ModInt(this)

@JvmInline
value class ModFieldInt(val mod: Int) {
    inline operator fun ModInt.plus(other: ModInt): ModInt =
        ModInt((value + other.value) % mod)

    inline operator fun ModInt.minus(other: ModInt): ModInt =
        ModInt(((value - other.value) % mod + mod) % mod)

    inline operator fun ModInt.times(other: ModInt): ModInt =
        ModInt(((value.toLong() * other.value) % mod).toInt())

    inline operator fun ModInt.div(other: ModInt): ModInt =
        this * other.inv()

    infix fun ModInt.pow(exp: Int): ModInt = when {
        exp == 0 -> ModInt(1)
        exp % 2 == 1 -> this * this.pow(exp - 1)
        else -> {
            val temp = this.pow(exp / 2)
            temp * temp
        }
    }

    inline operator fun ModInt.inv(): ModInt = this.pow(mod - 2)

    inline operator fun ModInt.plus(other: Int): ModInt =
        this + ModInt(other)

    inline operator fun ModInt.minus(other: Int): ModInt =
        this - ModInt(other)

    inline operator fun ModInt.times(other: Int): ModInt =
        this * ModInt(other)

    inline operator fun ModInt.div(other: Int): ModInt =
        this / ModInt(other)

    inline operator fun Int.plus(other: ModInt): ModInt =
        ModInt(this) * other

    inline operator fun Int.minus(other: ModInt): ModInt =
        ModInt(this) - other

    inline operator fun Int.times(other: ModInt): ModInt =
        ModInt(this) * other

    inline operator fun Int.div(other: ModInt): ModInt =
        ModInt(this) / other

    inline fun Int.toModInt(): ModInt = ModInt(this % mod)
}

fun <T> withModInt(mod: Int, block: ModFieldInt.() -> T): T {
    return ModFieldInt(mod).block()
}

// exports: ModInt
// exports: ModFieldInt
// exports: withModInt
// exports: toModInt
// exports: asModInt