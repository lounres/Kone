package foo.bar

import dev.lounres.kone.suppliedTypes.*


class Foo {
    @Suppliable
    fun <@Supply Gee> baz(): Gee? = null
}