package foo.bar

import dev.lounres.kone.suppliedTypes.*


object Foo {
    object Bar {
        @Suppliable
        fun <@Supply Gee> baz(): Gee? = null
    }
}