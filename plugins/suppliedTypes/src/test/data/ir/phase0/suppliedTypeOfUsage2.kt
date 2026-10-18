import dev.lounres.kone.suppliedTypes.*


fun box(): String = if (SuppliedType.of<List<Map<out String, String?>>>().toString() == "kotlin.collections.List<out kotlin.collections.Map<out kotlin.String, out kotlin.String?>>") "OK" else "INCORRECT"