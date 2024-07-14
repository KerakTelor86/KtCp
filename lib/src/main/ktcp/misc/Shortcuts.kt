@file:Suppress("unused")

package ktcp.misc

fun <T : Comparable<T>> minMax(a: T, b: T): Pair<T, T> = when (a < b) {
    true -> Pair(a, b)
    false -> Pair(b, a)
}

fun <T> minMax(a: T, b: T, comparator: Comparator<T>): Pair<T, T> =
    if (comparator.compare(a, b) <= 0) {
        Pair(a, b)
    } else {
        Pair(b, a)
    }

fun Boolean.toInt(): Int = if (this) 1 else 0
fun Int.toBoolean(): Boolean = this != 0
fun Long.toBoolean(): Boolean = this != 0L

fun <T> List<T>.toPair(): Pair<T, T> {
    if (size != 2) {
        throw IllegalArgumentException(
            "Cannot convert List of size != 2 to pair"
        )
    }
    return Pair(this[0], this[1])
}

inline fun <T> forC(
    init: () -> T,
    check: (T) -> Boolean,
    post: (T) -> T,
    block: (T) -> Unit,
) {
    var state = init()
    while (check(state)) {
        block(state)
        state = post(state)
    }
}

// exports: minMax
// exports: toInt
// exports: toBoolean
// exports: forC
