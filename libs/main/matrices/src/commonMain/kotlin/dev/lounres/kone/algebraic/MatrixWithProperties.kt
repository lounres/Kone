package dev.lounres.kone.algebraic

import dev.lounres.kone.collections.map.KoneMap
import dev.lounres.kone.contexts.KoneContextRegistry
import dev.lounres.kone.multidimensionalCollections.MDIndex
import dev.lounres.kone.multidimensionalCollections.MDList2
import dev.lounres.kone.registry.MutableOwnedRegistry
import dev.lounres.kone.registry.OwnedRegistry
import dev.lounres.kone.registry.RegisteredValueProvider
import dev.lounres.kone.registry.build
import dev.lounres.kone.registry.cached
import dev.lounres.kone.registry.correspondsTo
import dev.lounres.kone.registry.get
import dev.lounres.kone.suppliedTypes.DelicateSuppliedTypeConstructor
import dev.lounres.kone.suppliedTypes.SuppliedProjection
import dev.lounres.kone.suppliedTypes.SuppliedType


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
    propertiesBuilder: MutableOwnedRegistry<MatrixWithProperties<Number, Matrix>>.(matrix: MatrixWithProperties.Provider<Number, Matrix>) -> Unit,
): MatrixWithProperties<Number, Matrix> {
    val provider = object : MatrixWithProperties.Provider<Number, Matrix> {
        var result: MatrixWithProperties<Number, Matrix>? = null
        override fun get(): MatrixWithProperties<Number, Matrix> =
            result ?: error("MatrixWithProperties is not yet initialized but was requested by its properties.")
    }
    val result = MatrixWithProperties(
        matrix = matrix,
        properties = OwnedRegistry.build { propertiesBuilder(provider) }
    )
    provider.result = result
    return result
}

private class MatrixWithPropertiesFactory<Number, Matrix : MDList2<Number>>(
    private val matrixFactory: MatrixFactory<Number, Matrix>,
    private val propertiesBuilder: context(MatrixWithProperties.Provider<Number, Matrix>) MutableOwnedRegistry<MatrixWithProperties<Number, Matrix>>.() -> Unit,
) : MatrixFactory<Number, MatrixWithProperties<Number, Matrix>> {
    override fun generateMatrix(
        rowNumber: UInt,
        columnNumber: UInt,
        generator: (row: UInt, column: UInt) -> Number,
    ): MatrixWithProperties<Number, Matrix> =
        MatrixWithProperties(
            matrix = matrixFactory.generateMatrix(rowNumber, columnNumber, generator),
            propertiesBuilder = { propertiesBuilder(it, this) },
        )
    
    override fun fillMatrix(
        rowNumber: UInt,
        columnNumber: UInt,
        number: Number,
    ): MatrixWithProperties<Number, Matrix> =
        MatrixWithProperties(
            matrix = matrixFactory.fillMatrix(rowNumber, columnNumber, number),
            propertiesBuilder = { propertiesBuilder(it, this) },
        )
    
    override fun mapMatrix(
        rowNumber: UInt,
        columnNumber: UInt,
        numbers: KoneMap<MDIndex, Number>,
    ): MatrixWithProperties<Number, Matrix> =
        MatrixWithProperties(
            matrix = matrixFactory.mapMatrix(rowNumber, columnNumber, numbers),
            propertiesBuilder = { propertiesBuilder(it, this) },
        )
}

public fun <Number, Matrix : MDList2<Number>> MatrixWithProperties.Companion.factory(
    matrixFactory: MatrixFactory<Number, Matrix>,
    propertiesBuilder: context(MatrixWithProperties.Provider<Number, Matrix>) MutableOwnedRegistry<MatrixWithProperties<Number, Matrix>>.() -> Unit,
): MatrixFactory<Number, MatrixWithProperties<Number, Matrix>> =
    MatrixWithPropertiesFactory(
        matrixFactory = matrixFactory,
        propertiesBuilder = propertiesBuilder,
    )

context(_: MutableOwnedRegistry<KoneContextRegistry>, koneContextRegistry: KoneContextRegistry.Provider)
public fun <Number, Matrix : MDList2<Number>> MatrixWithProperties.Companion.setFactory(
    numberType: SuppliedType,
    matrixType: SuppliedType,
    propertiesBuilder: context(MatrixWithProperties.Provider<Number, Matrix>) MutableOwnedRegistry<MatrixWithProperties<Number, Matrix>>.() -> Unit,
) {
    @OptIn(DelicateSuppliedTypeConstructor::class)
    val matrixWithPropertiesType = SuppliedType.Regular(
        fullyQualifiedName = "dev.lounres.kone.algebraic.MatrixWithProperties",
        typeArguments = listOf(
            SuppliedProjection.Regular(
                variance = INVARIANT,
                type = numberType,
            ),
            SuppliedProjection.Regular(
                variance = INVARIANT,
                type = matrixType,
            ),
        ),
        isNullable = false,
    )
    MatrixFactory.Key<Number, MatrixWithProperties<Number, Matrix>>(matrixWithPropertiesType) correspondsTo RegisteredValueProvider.cached {
        val koneContextRegistry = koneContextRegistry.get()
        factory(
            matrixFactory = koneContextRegistry[MatrixFactory.Key<Number, Matrix>(matrixType = matrixType)],
            propertiesBuilder = propertiesBuilder,
        )
    }
}