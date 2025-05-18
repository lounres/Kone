// SUPPRESS_WARNINGS: PRE_RELEASE_CLASS

import dev.lounres.kone.suppliedTypes.*
import kotlin.reflect.KVariance


fun box() {
    val st1 = suppliedTypeOf<List<Map<out Int, String>>>()
    
    @OptIn(DelicateSuppliedTypeConstructor::class)
    val st2 = SuppliedType.Regular(
        fullyQualifiedName = List::class.qualifiedName!!,
        typeArguments = listOf(
            SuppliedProjection.Regular(
                KVariance.INVARIANT,
                SuppliedType.Regular(
                    fullyQualifiedName = Map::class.qualifiedName!!,
                    typeArguments = listOf(
                        SuppliedProjection.Regular(
                            KVariance.OUT,
                            SuppliedType.Regular(
                                fullyQualifiedName = Int::class.qualifiedName!!,
                                typeArguments = listOf(),
                                isNullable = false,
                            )
                        ),
                        SuppliedProjection.Regular(
                            KVariance.INVARIANT,
                            SuppliedType.Regular(
                                fullyQualifiedName = String::class.qualifiedName!!,
                                typeArguments = listOf(),
                                isNullable = false,
                            )
                        )
                    ),
                    isNullable = false,
                )
            )
        ),
        isNullable = false,
    )
    println(st1)
    println(st2)
    println(st1 == st2)
}
