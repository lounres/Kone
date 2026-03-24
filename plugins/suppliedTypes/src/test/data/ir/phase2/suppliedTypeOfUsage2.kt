// SUPPRESS_WARNINGS: PRE_RELEASE_CLASS

import dev.lounres.kone.suppliedTypes.*


fun box(): String {
    println(suppliedTypeOf<List<Map<out String, String?>>>())
    return "OK"
}