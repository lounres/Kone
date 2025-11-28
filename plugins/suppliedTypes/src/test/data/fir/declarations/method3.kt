// SUPPRESS_WARNINGS: PRE_RELEASE_CLASS

package foo.bar

import dev.lounres.kone.suppliedTypes.*


object Foo {
    object Bar {
        @SuppliedTarget
        fun <@Supplied Gee> baz(): Gee? = null
    }
}