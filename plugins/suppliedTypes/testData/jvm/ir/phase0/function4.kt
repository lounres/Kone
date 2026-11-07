// SUPPRESS_WARNINGS: PRE_RELEASE_CLASS, UNSUPPORTED_FEATURE

package foo.bar

import dev.lounres.kone.suppliedTypes.*


@Suppliable
fun <Gee : Doo, @Supply Doo : List<Gee>> Int.baz(arg: String = "57", arg2: Long = 179): Gee? = null
