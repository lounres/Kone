import dev.lounres.kone.suppliedTypes.*


@Suppliable
fun <@Supply T> foo() = SuppliedType.of<List<T>>().toString()

fun <T> bar(arg: SuppliedType) = withSupplied<T, _>(arg) { foo<Map<out T, T?>>() }

fun box(): String = if (bar<String>(SuppliedType.of<String>()) == "kotlin.collections.List<out kotlin.collections.Map<out kotlin.String, out kotlin.String?>>") "OK" else "INCORRECT"