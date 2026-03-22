// SUPPRESS_WARNINGS: PRE_RELEASE_CLASS

import dev.lounres.kone.suppliedTypes.*
import kotlin.reflect.KVariance


@Suppliable
interface Foo<@Supply T>

@Suppliable
interface Bar<@Supply U> : Foo<Map<out U, String>>

@Suppliable
open class Baz<@Supply V> : Foo<Map<out Int, V>>

@Suppliable
class Gee : Bar<Int>, Baz<String>()