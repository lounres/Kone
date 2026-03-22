// SUPPRESS_WARNINGS: PRE_RELEASE_CLASS

import dev.lounres.kone.suppliedTypes.*
import kotlin.reflect.KVariance


@Suppliable
interface Foo<@Supply T>

@Suppliable
interface Bar<@Supply U> : Foo<Map<out U, String>>

@Suppliable
interface Baz<@Supply V> : Foo<Map<out Int, V>>

@Suppliable
interface Gee : Bar<Int>, Baz<String>