// SUPPRESS_WARNINGS: PRE_RELEASE_CLASS

package foo.bar

import dev.lounres.kone.suppliedTypes.*


@SuppliedTarget
fun foo() {
    TODO()
}

@SuppliedTarget
class Foo {
    @SuppliedTarget
    class Bar
    
    @SuppliedTarget
    fun baz() {
        TODO()
    }
}