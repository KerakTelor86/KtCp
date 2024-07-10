@file:Suppress("unused")

package ktcp.misc

import java.util.*

val random by lazy {
    Random(0x594E215C123 * System.nanoTime())
}

// exports: random
