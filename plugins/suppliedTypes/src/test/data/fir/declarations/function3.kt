// SUPPRESS_WARNINGS: PRE_RELEASE_CLASS, UNSUPPORTED_FEATURE

package foo.bar

import dev.lounres.kone.suppliedTypes.*


fun foo() {
    @SuppliedTarget
//    context(_: Boolean)
    fun <@Supplied Gee : Doo, Doo : List<Gee>> Int.baz(arg: String): Gee? = null
}
