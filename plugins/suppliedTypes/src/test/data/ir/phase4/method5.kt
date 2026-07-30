package foo.bar

import dev.lounres.kone.suppliedTypes.*


interface Foo {
    @Suppliable
    fun <@Supply Gee> baz(): Gee?
}