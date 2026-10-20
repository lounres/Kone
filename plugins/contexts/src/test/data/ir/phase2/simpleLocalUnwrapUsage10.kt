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

interface Bar : KoneContext {
    @KoneContextInclude
    val baz: Foo get() = Foo(179)
}

class Baz : Bar {
    @KoneContextInclude
    val bar = Foo(57)
    @KoneContextExclude
    override val baz: Foo get() = super.baz
}

fun box(): String {
    KoneContext.localUnwrap(Baz())
    return if (work() == 57) "OK" else "INCORRECT"
}