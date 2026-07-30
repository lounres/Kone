import dev.lounres.kone.suppliedTypes.*


@Suppliable
fun <@Supply T> foo(arg: T?) = suppliedTypeOf<List<T>>().toString()

fun box(): String = if (foo<String>(null) == "kotlin.collections.List<out kotlin.String>") "OK" else "INCORRECT"