// SUPPRESS_WARNINGS: PRE_RELEASE_CLASS

import dev.lounres.kone.suppliedTypes.*


@Suppliable
interface A<@Supply T>

typealias A2<P, Q> = A<Map<Q, P>>
typealias A3<P, Q> = A2<List<P>, Q>

@Suppliable
interface B<<!SUPPLIANCE_IS_NEEDED!>X<!>, Y, <!SUPPLIANCE_IS_NEEDED!>Z<!>> : A3<X, Z>
