import dev.lounres.kone.suppliedTypes.*


@Suppliable
class Foo<@Supply T> {
    val foo by lazy { suppliedTypeOf<List<T>>().toString() }
}

fun box(): String = if (Foo<String>().foo == "kotlin.collections.List<out kotlin.String>") "OK" else "INCORRECT"