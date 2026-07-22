// SUPPRESS_WARNINGS: PRE_RELEASE_CLASS

package foo.bar

import dev.lounres.kone.contexts.*


class Work(private val n: Int) {
    fun work(): Int = n
}

context(work: Work)
fun work(): Int = work.work()

class Foo(n: Int) : KoneContext {
    @KoneContextInclude
    val foo: Work = Work(n)
}

context(foo: Foo)
fun makeItFoo(): Int = foo.foo.work()

interface Bar : KoneContext {
    @KoneContextInclude
    val bar: Foo get() = Foo(179)
}

context(bar: Bar)
fun makeItBar(): Int = bar.bar.foo.work()

class Baz : Bar {
    @KoneContextInclude
    val baz = Foo(57)
    @KoneContextExclude
    override val bar: Foo get() = super.bar
}

context(baz: Baz)
fun makeItBaz(): Int = baz.baz.foo.work()

fun box(): String {
    KoneContext.localUnwrap(Baz())
    return when {
        work() != 57 -> "INCORRECT 1"
        makeItFoo() != 57 -> "INCORRECT 2"
        makeItBar() != 179 -> "INCORRECT 3"
        makeItBaz() != 57 -> "INCORRECT 4"
        else -> "OK"
    }
}