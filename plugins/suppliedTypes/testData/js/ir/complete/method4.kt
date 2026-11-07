package foo.bar

import dev.lounres.kone.suppliedTypes.*


fun foo() {
    class Bar {
        @Suppliable
        fun <@Supply Gee> baz(): Gee? = null
    }
}