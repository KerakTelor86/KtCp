@file:Suppress("unused")

package ktcp.math

import ktcp.math.modint.*
import ktcp.misc.*
import kotlin.math.abs

private object MillerRabin {
    val PRIMES = intArrayOf(2, 3, 5, 7, 11, 13, 17, 19, 23, 29, 31, 37)

    fun millerRabin(n: Long, a: Long, d: Long, s: Int): Boolean {
        return withModLong(n) {
            var x = ModLong(a) pow d
            if (x.value == 1L || x.value == n - 1) {
                return@withModLong false
            }
            for (i in 0..<s) {
                x *= x
                if (x.value == n - 1) {
                    return@withModLong false
                }
            }
            true
        }
    }
}

private object PollardRho {
    fun f(x: Long, b: Long, n: Long): Long = withModLong(n) {
        x.toModLong() * x + b
    }.toLong()

    fun rho(n: Long): Long {
        if (n % 2 == 0L) {
            return 2L
        }
        val b = random.nextLong(Long.MAX_VALUE)
        var x = random.nextLong(Long.MAX_VALUE)
        var y = x
        while (true) {
            x = f(x, b, n)
            y = f(f(y, b, n), b, n)
            val d = gcd(abs(x - y), n)
            if (d != 1L) {
                return d
            }
        }
    }

    fun pollardRho(n: Long, res: ArrayList<Long>): Unit = when {
        n == 1L -> Unit
        isPrime(n) -> {
            res.add(n)
            Unit
        }

        else -> {
            val d = rho(n)
            pollardRho(d, res)
            pollardRho(n / d, res)
        }
    }
}

fun isPrime(x: Long): Boolean {
    if (x < 2) {
        return false
    }
    var r = 0
    var d = x - 1
    while (d % 2 == 0L) {
        d /= 2
        ++r
    }
    for (prime in MillerRabin.PRIMES) {
        if (x == prime.toLong()) {
            return true
        }
        if (MillerRabin.millerRabin(x, prime.toLong(), d, r)) {
            return false
        }
    }
    return true
}

fun factorize(n: Long): List<Long> {
    val res = arrayListOf<Long>()
    PollardRho.pollardRho(n, res)
    return res.sorted()
}

// exports: isPrime
// exports: factorize
// depends: math/modint/ModLong.kt
// depends: math/Misc.kt
// depends: misc/Random.kt
