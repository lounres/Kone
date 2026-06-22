// SUPPRESS_WARNINGS: PRE_RELEASE_CLASS

package foo.bar

import dev.lounres.kone.suppliedTypes.*


@Suppliable
fun <@Supply Gee> baz(): Gee? = null

@Suppliable
fun <Gee> baz(suppliedTypeParameterForGee: SuppliedType): Gee? = null
