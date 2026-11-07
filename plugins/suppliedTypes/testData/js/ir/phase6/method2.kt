package foo.bar

import dev.lounres.kone.suppliedTypes.*


class Foo {
    class Bar {
        @Suppliable
        fun <@Supply Gee> baz(): Gee? = null
    }
}