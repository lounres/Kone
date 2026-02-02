// SUPPRESS_WARNINGS: PRE_RELEASE_CLASS

package foo.bar

import dev.lounres.kone.suppliedTypes.*


class Foo {
    class Bar {
        @Suppliable
        fun <@Supply Gee> baz(): Gee? = null
    }
}