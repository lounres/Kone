// SUPPRESS_WARNINGS: PRE_RELEASE_CLASS

package foo.bar

import dev.lounres.kone.suppliedTypes.*


object Foo {
    object Bar {
        @Suppliable
        fun <@Supply Gee> baz(): Gee? = null
    }
}