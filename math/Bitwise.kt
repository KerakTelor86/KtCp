package ktcp.math

fun log2Floor(value: Int): Int {
    return 31 - value.countLeadingZeroBits()
}

fun log2Floor(value: Long): Int {
    return 63 - value.countLeadingZeroBits()
}

// exports: log2Floor