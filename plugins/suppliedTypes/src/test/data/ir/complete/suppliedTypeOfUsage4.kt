import dev.lounres.kone.suppliedTypes.*


@Suppliable
interface Foo<@Supply T> {
    fun foo() = suppliedTypeOf<List<T>>().toString()
}

@Suppliable
class Bar : Foo<String>

fun box(): String = if (Bar().foo() == "kotlin.collections.List<out kotlin.String>") "OK" else "INCORRECT"