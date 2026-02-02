// SUPPRESS_WARNINGS: PRE_RELEASE_CLASS

import dev.lounres.kone.suppliedTypes.*
import kotlin.reflect.KVariance


interface Foo<@Supply T>

interface Bar<@Supply U> : Foo<Map<out U, *>>