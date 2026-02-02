// SUPPRESS_WARNINGS: PRE_RELEASE_CLASS, UNSUPPORTED_FEATURE

package foo.bar

import dev.lounres.kone.suppliedTypes.*


fun foo() {
    @Suppliable
//    context(_: Boolean)
    fun <@Supply Gee : Doo, Doo : List<Gee>> Int.baz(arg: String): Gee? = null
}
