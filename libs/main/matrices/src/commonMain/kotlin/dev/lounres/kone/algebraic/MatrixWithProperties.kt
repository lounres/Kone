package dev.lounres.kone.algebraic

import dev.lounres.kone.multidimensionalCollections.MDList2
import dev.lounres.kone.registry.MutableOwnedRegistry
import dev.lounres.kone.registry.OwnedRegistry
import dev.lounres.kone.registry.build


public data class MatrixWithProperties<Number, Matrix : MDList2<Number>>(
    val matrix: Matrix,
    val properties: OwnedRegistry<MatrixWithProperties<Number, Matrix>>,
) : MDList2<Number> by matrix {
    public companion object;
    
    public fun interface Provider<Number, Matrix : MDList2<Number>> {
        public fun get(): MatrixWithProperties<Number, Matrix>
    }
}

public inline fun <Number, Matrix : MDList2<Number>> MatrixWithProperties(
    matrix: Matrix,
    propertiesBuilder: context(MatrixWithProperties.Provider<Number, Matrix>) MutableOwnedRegistry<MatrixWithProperties<Number, Matrix>>.() -> Unit,
): MatrixWithProperties<Number, Matrix> {
    val provider = object : MatrixWithProperties.Provider<Number, Matrix> {
        var result: MatrixWithProperties<Number, Matrix>? = null
        override fun get(): MatrixWithProperties<Number, Matrix> =
            result ?: error("MatrixWithProperties is not yet initialized but was requested by its properties.")
    }
    val result = MatrixWithProperties(
        matrix = matrix,
        properties = OwnedRegistry.build { propertiesBuilder(provider, this) }
    )
    provider.result = result
    return result
}