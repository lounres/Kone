// SUPPRESS_WARNINGS: PRE_RELEASE_CLASS

package foo.bar

import dev.lounres.kone.suppliedTypes.*


class Foo {
    @SuppliedTarget
    fun <@Supplied Gee> baz(): Gee? = null
}