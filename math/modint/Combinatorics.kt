@file:Suppress("unused")

package ktcp.math.modint

import ktcp.ds.modarray.*

class Combinatorics internal constructor(
    private val fact: ModIntArray,
    private val invFact: ModIntArray,
) {
    fun fact(n: Int): ModInt = fact[n]

    fun ModFieldInt.perm(n: Int, k: Int): ModInt = fact[n] * invFact[n - k]

    fun ModFieldInt.comb(n: Int, k: Int): ModInt =
        fact[n] * invFact[n - k] * invFact[k]
}

fun <T> ModFieldInt.withCombinatorics(
    maxN: Int,
    block: Combinatorics.() -> T,
): T {
    val fact = (1..maxN).runningFold(ModInt(1)) { acc, it ->
        acc * it
    }.toModIntArray()
    val invFact = (maxN downTo 1).runningFold(fact.last().inv()) { acc, it ->
        acc * it
    }.reversed().toModIntArray()
    return Combinatorics(fact, invFact).block()
}

// exports: Combinatorics
// exports: withCombinatorics
// depends: math/modint/ModInt.kt