import dev.lounres.kone.suppliedTypes.*


@Suppliable
abstract class Foo<@Supply T> {
    fun foo() = suppliedTypeOf<T & Any>().toString()
}

@Suppliable
object Bar : Foo<String?>()

fun box(): String = if (Bar.foo() == "kotlin.String") "OK" else "INCORRECT"