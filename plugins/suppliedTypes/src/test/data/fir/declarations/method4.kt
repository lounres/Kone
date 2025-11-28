// SUPPRESS_WARNINGS: PRE_RELEASE_CLASS

package foo.bar

import dev.lounres.kone.suppliedTypes.*


fun foo() {
    class Bar {
        @SuppliedTarget
        fun <@Supplied Gee> baz(): Gee? = null
    }
}