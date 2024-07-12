@file:Suppress("unused")

package ktcp.math

tailrec fun gcd(x: Long, y: Long): Long {
    if (y == 0L) {
        return x
    }
    return gcd(y, x % y)
}

fun lcm(x: Long, y: Long): Long {
    return x * y / gcd(x, y)
}

// exports: gcd
// exports: lcm