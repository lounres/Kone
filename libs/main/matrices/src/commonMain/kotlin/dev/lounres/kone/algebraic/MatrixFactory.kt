package dev.lounres.kone.algebraic

import dev.lounres.kone.collections.map.KoneMap
import dev.lounres.kone.collections.map.getOrElse
import dev.lounres.kone.collections.utils.all
import dev.lounres.kone.contexts.KoneContext
import dev.lounres.kone.contexts.KoneContextRegistry
import dev.lounres.kone.multidimensionalCollections.MDIndex
import dev.lounres.kone.multidimensionalCollections.MDList2
import dev.lounres.kone.multidimensionalCollections.of
import dev.lounres.kone.registry.MutableOwnedRegistry
import dev.lounres.kone.registry.RegisteredValueProvider
import dev.lounres.kone.registry.RegistryKey
import dev.lounres.kone.registry.cached
import dev.lounres.kone.registry.correspondsTo
import dev.lounres.kone.registry.get
import dev.lounres.kone.suppliedTypes.DelicateSuppliedTypeConstructor
import dev.lounres.kone.suppliedTypes.SuppliedProjection
import dev.lounres.kone.suppliedTypes.SuppliedType
import kotlin.reflect.KVariance.OUT


public interface MatrixFactory<Number, Matrix: MDList2<Number>> : KoneContext {
    // TODO: Think about adding:
//    public fun convertMatrix(matrix: MDList2<Number>): Matrix
    public fun generateMatrix(rowNumber: UInt, columnNumber: UInt, generator: (row: UInt, column: UInt) -> Number): Matrix
    public fun fillMatrix(rowNumber: UInt, columnNumber: UInt, number: Number): Matrix = generateMatrix(rowNumber, columnNumber) { _, _ -> number }
    public fun mapMatrix(rowNumber: UInt, columnNumber: UInt, numbers: KoneMap<MDIndex, Number>): Matrix
    
    public companion object;
    
    public class Key<Number, Matrix : MDList2<Number>>(
        public val matrixType: SuppliedType,
    ) : RegistryKey<MatrixFactory<Number, Matrix>> {
        override fun equals(other: Any?): Boolean = other is Key<*, *> && matrixType == other.matrixType
        override fun hashCode(): Int = matrixType.hashCode()
        override fun toString(): String = "dev.lounres.kone.algebraic.MatrixFactory.Key<?, $matrixType>"
    }
}

private class MDList2MatrixFactory<Number>(
    private val ring: CommutativeRing<Number>
) : MatrixFactory<Number, MDList2<Number>> {
    override fun generateMatrix(
        rowNumber: UInt,
        columnNumber: UInt,
        generator: (row: UInt, column: UInt) -> Number
    ): MDList2<Number> = MDList2(rowNumber = rowNumber, columnNumber = columnNumber, initializer = generator)
    
    override fun fillMatrix(rowNumber: UInt, columnNumber: UInt, number: Number): MDList2<Number> =
        MDList2(rowNumber = rowNumber, columnNumber = columnNumber) { _, _ ->  number }
    
    override fun mapMatrix(rowNumber: UInt, columnNumber: UInt, numbers: KoneMap<MDIndex, Number>): MDList2<Number> {
        require(numbers.keysView.all { it.size == 2u && it[0u] < rowNumber && it[1u] < columnNumber }) { TODO() }
        return MDList2(rowNumber, columnNumber) { row, column -> numbers.getOrElse(MDIndex.of(row, column)) { ring.zero } }
    }
}

public fun <Number> MatrixFactory.Companion.mdList2(ring: CommutativeRing<Number>): MatrixFactory<Number, MDList2<Number>> =
    MDList2MatrixFactory(ring = ring)

context(koneContextRegistry: KoneContextRegistry.Provider)
public fun <Number> MatrixFactory.Companion.mdList2(numberType: SuppliedType): MatrixFactory<Number, MDList2<Number>> =
    mdList2(ring = koneContextRegistry.get()[CommutativeRing.Key<Number>(numberType = numberType)])

context(_: MutableOwnedRegistry<KoneContextRegistry>, _: KoneContextRegistry.Provider)
public fun <Number> MatrixFactory.Companion.setMDList2(numberType: SuppliedType) {
    @OptIn(DelicateSuppliedTypeConstructor::class)
    val matrixType = SuppliedType.Regular(
        fullyQualifiedName = "dev.lounres.kone.multidimensionalCollections.MDList2",
        typeArguments = listOf(
            SuppliedProjection.Regular(
                variance = OUT,
                type = numberType
            )
        ),
        isNullable = false,
    )
    MatrixFactory.Key<Number, MDList2<Number>>(matrixType = matrixType) correspondsTo RegisteredValueProvider.cached {
        mdList2(numberType = numberType)
    }
}