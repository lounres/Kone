/*
 * Copyright © 2023 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.misc.lattices

import dev.lounres.kone.collections.*
import dev.lounres.kone.collections.implementations.KoneResizableHashSet
import dev.lounres.kone.collections.utils.*
import dev.lounres.kone.combinatorics.enumerative.combinations
import dev.lounres.kone.computations.*
import dev.lounres.kone.computations.CancellationException
import dev.lounres.kone.context.KoneContext
import kotlinx.atomicfu.AtomicRef
import kotlinx.atomicfu.atomic
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.channels.SendChannel
import kotlin.coroutines.CoroutineContext
import kotlin.jvm.JvmName


public data class Position<C, K>(val coordinates: C, val kind: K)
public data class Cell<C, K, A>(val position: Position<C, K>, val attributes: Set<A> = emptySet())
public fun <C, K, A> Cell(coordinates: C, kind: K, attributes: Set<A> = emptySet()): Cell<C, K, A> =
    Cell(Position(coordinates, kind), attributes)

@Suppress("INAPPLICABLE_JVM_NAME")
public interface Lattice<C, K, V>: KoneContext {
    @JvmName("plus-V-V")
    public operator fun V.plus(other: V): V
    @JvmName("minus-V-V")
    public operator fun V.minus(other: V): V

    @JvmName("plus-C-V")
    public operator fun C.plus(other: V): C
    @JvmName("minus-C-V")
    public operator fun C.minus(other: V): C
    @JvmName("minus-C-C")
    public operator fun C.minus(other: C): V

    public val rotations: List<(Position<C, K>) -> Position<C, K>>
}


context(Lattice<C, K, V>)
public operator fun <C, K, A, V> Cell<C, K, A>.plus(other: V): Cell<C, K, A> = Cell(Position(position.coordinates + other, position.kind), attributes)
context(Lattice<C, K, V>)
public operator fun <C, K, A, V> Cell<C, K, A>.minus(other: V): Cell<C, K, A> = Cell(Position(position.coordinates - other, position.kind), attributes)
context(Lattice<C, K, V>)
public operator fun <C, K, A, V> Cell<C, K, A>.minus(other: Cell<C, K, A>): V {
    require(this.position.kind == other.position.kind)
    return this.position.coordinates - other.position.coordinates
}

// FIXME: Uncomment when KT-5837 will be fixed.
//context(Lattice<C, K, *>)
//public inline operator fun <C, K, A> ((Position<C, K>) -> Position<C, K>).invoke(cell: Cell<C, K, A>): Cell<C, K, A> =
//    Cell(this(cell.position), cell.attributes)

// FIXME: Uncomment when KT-5837 will be fixed.
//context(Lattice<C, K, *>)
//public inline operator fun <C, K, A> ((Position<C, K>) -> Position<C, K>).invoke(cells: KoneIterableSet<Cell<C, K, A>>): KoneIterableSet<Cell<C, K, A>> =
//    cells.mapTo(KoneResizableHashSet(/* TODO: Replace with fixed capacity implementation with capacity `cells.size` */)) { this(it) }

context(CoroutineScope, Lattice<C, K, V>)
public fun <C, K, A, V> KoneIterableSet<Cell<C, K, A>>.divideInParts(numberOfParts: UInt, takeFormIf: (KoneIterableSet<Position<C, K>>) -> Boolean = { true }): Sequence<KoneIterableList<KoneIterableSet<Cell<C, K, A>>>> = sequence {
    // TODO: В идеале здесь нужна проверка на то, что никакие две клетки не равны одновременно в координатах и в типе
    if (this@divideInParts.groupingBy { it.attributes }.eachCount().valuesView.any { it % numberOfParts != 0u }) return@sequence
    if (this@divideInParts.isEmpty()) {
        yield(emptyKoneIterableList())
        return@sequence
    }
    val cellsPerPart = size / numberOfParts
    val allCells = this@divideInParts

    val firstCell = allCells.first()
    for (otherCellsOfFirstPart in buildKoneIterableList { addAllFrom(allCells); remove(firstCell) }.combinations(cellsPerPart - 1u)) {
        if(!isActive) return@sequence
        val firstPart = buildKoneIterableSet(initialCapacity = otherCellsOfFirstPart.size + 1u) {
            addAllFrom(otherCellsOfFirstPart)
            add(firstCell)
        }
        if (!takeFormIf(firstPart.mapTo(KoneResizableHashSet(/* TODO: Replace with fixed capacity implementation with capacity `firstPart.size` */)) { it.position })) continue
        val restCells = buildKoneIterableSet {
            addAllFrom(allCells)
            removeAllFrom(firstPart)
        }

        data class Form<C, K, A>(val startCell: Cell<C, K ,A>, val cells: KoneIterableSet<Cell<C, K, A>>)
        val forms = rotations.map {
            Form(
                Cell(it(firstCell.position), firstCell.attributes),
                firstPart.mapTo(KoneResizableHashSet(/* TODO: Replace with fixed capacity implementation with capacity `cells.size` */)) { cell ->
                    Cell(it(cell.position), cell.attributes)
                }
            )
        }

        val allPossibleParts = buildKoneIterableSet {
            for (form in forms) for (otherFirstCell in restCells) {
                if(!isActive) return@sequence
                if (otherFirstCell.position.kind != form.startCell.position.kind) continue
                val shift = otherFirstCell - form.startCell
                val part = form.cells.mapTo(KoneResizableHashSet(/* TODO: Replace with fixed capacity implementation with capacity `cellsPerPart` */)) { it + shift }
                if (part.all { it in allCells } && part.none { it in firstPart }) add(part)
            }
        }.toKoneIterableList()

        for (parts in allPossibleParts.combinations(numberOfParts - 1u)) {
            if(!isActive) return@sequence
            if (parts.combinations(2u).any { (part1, part2) -> part1.any { it in part2 } }) continue
            yield(buildKoneIterableList { addAllFrom(parts); add(firstPart) })
        }
    }
}

// TODO: Architect and implement sets with built-in partition. Maybe.

internal sealed interface State {
    data class Pausing(val jobUntilPaused: CompletableJob, val jobUntilActive: CompletableJob = Job()) : State
    data class Paused(val jobUntilActive: CompletableJob) : State
    data class Activating(val jobUntilActive: CompletableJob, val jobUntilPaused: CompletableJob = Job()) : State
    data class Active(val jobUntilPaused: CompletableJob, val effectiveJob: Job) : State
    
    data class PausingCompletion(val pausingJob: CompletableJob = Job()) : State
    data object PausedCompletion : State
    data class ActivatingCompletion(val activatingJob: CompletableJob = Job()) : State
    data object Completion: State
    
    data object Completed : State
    
    data object Cancelling : State
    data object Cancelled : State
}

context(CoroutineScope, Lattice<C, K, V>)
public fun <C, K, A, V> KoneIterableSet<Cell<C, K, A>>.divideInParts2(numberOfParts: UInt, takeFormIf: (KoneIterableSet<Position<C, K>>) -> Boolean = { true }): ChannelComputation<KoneIterableList<KoneIterableSet<Cell<C, K, A>>>> {
    // TODO: В идеале здесь нужна проверка на то, что никакие две клетки не равны одновременно в координатах и в типе
    if (this@divideInParts2.groupingBy { it.attributes }.eachCount().valuesView.any { it % numberOfParts != 0u })
        return object : ChannelComputation<KoneIterableList<KoneIterableSet<Cell<C, K, A>>>> {
            override val key: CoroutineContext.Key<*> get() = Computation.Key
            
            override val parent: Computation? get() = null // TODO: Implement parent-child relations
            override val children: Sequence<Computation> get() = emptySequence() // TODO: Implement parent-child relations
            
            private val _state: AtomicRef<State> = atomic(State.Completed)
            
            override val isActive: Boolean
                get() = _state.value.let { it is State.Pausing || it is State.Active || it === State.Completion }
            override val isPaused: Boolean
                get() = _state.value.let { it is State.Paused || it is State.Activating }
            override val isCompleted: Boolean
                get() = _state.value.let { it === State.Completed || it === State.Cancelled }
            override val isCancelled: Boolean
                get() = _state.value.let { it === State.Cancelling || it === State.Cancelled }
            
            override val resultsChannel: ReceiveChannel<KoneIterableList<KoneIterableSet<Cell<C, K, A>>>> =
                Channel<KoneIterableList<KoneIterableSet<Cell<C, K, A>>>>(1).apply { close() }
            
            override fun resume() {}
            override fun pause() {}
            
            override fun cancel(cause: CancellationException?) {}
            
            override suspend fun joinUntilResumed() {}
            override suspend fun joinUntilPaused() {}
            
            override suspend fun resumeJoining() {}
            override suspend fun pauseJoining() {}
            
            override suspend fun join() {}
        }
    if (this@divideInParts2.isEmpty())
        return object : ChannelComputation<KoneIterableList<KoneIterableSet<Cell<C, K, A>>>> {
            override val key: CoroutineContext.Key<*> get() = Computation.Key
            
            override val parent: Computation? get() = null // TODO: Implement parent-child relations
            override val children: Sequence<Computation> get() = emptySequence() // TODO: Implement parent-child relations
            
            private val _state: AtomicRef<State> = atomic(State.Completed)
            
            override val isActive: Boolean
                get() = _state.value.let { it is State.Pausing || it is State.Active || it === State.Completion }
            override val isPaused: Boolean
                get() = _state.value.let { it is State.Paused || it is State.Activating }
            override val isCompleted: Boolean
                get() = _state.value.let { it === State.Completed || it === State.Cancelled }
            override val isCancelled: Boolean
                get() = _state.value.let { it === State.Cancelling || it === State.Cancelled }
            
            override val resultsChannel: ReceiveChannel<KoneIterableList<KoneIterableSet<Cell<C, K, A>>>> =
                Channel<KoneIterableList<KoneIterableSet<Cell<C, K, A>>>>(1).apply { trySend(emptyKoneIterableList()); close() }
            
            override fun resume() {}
            override fun pause() {}
            
            override fun cancel(cause: CancellationException?) {}
            
            override suspend fun joinUntilResumed() {}
            override suspend fun joinUntilPaused() {}
            
            override suspend fun resumeJoining() {}
            override suspend fun pauseJoining() {}
            
            override suspend fun join() {}
        }
    
    val cellsPerPart = size / numberOfParts
    val allCells = this@divideInParts2
    
    val firstCell = allCells.first()
    
    val otherCellsOfFirstPartIterator = buildKoneIterableList { addAllFrom(allCells); remove(firstCell) }.combinations(cellsPerPart - 1u).iterator()
    
    val logic: suspend CoroutineScope.(SendChannel<KoneIterableList<KoneIterableSet<Cell<C, K, A>>>>) -> Unit = logic@{
        for (otherCellsOfFirstPart in otherCellsOfFirstPartIterator) {
            val firstPart = buildKoneIterableSet(initialCapacity = otherCellsOfFirstPart.size + 1u) {
                addAllFrom(otherCellsOfFirstPart)
                add(firstCell)
            }
            if (!takeFormIf(firstPart.mapTo(KoneResizableHashSet(/* TODO: Replace with fixed capacity implementation with capacity `firstPart.size` */)) { it.position })) continue
            val restCells = buildKoneIterableSet {
                addAllFrom(allCells)
                removeAllFrom(firstPart)
            }
            
            data class Form<C, K, A>(val startCell: Cell<C, K ,A>, val cells: KoneIterableSet<Cell<C, K, A>>)
            val forms = rotations.map {
                Form(
                    Cell(it(firstCell.position), firstCell.attributes),
                    firstPart.mapTo(KoneResizableHashSet(/* TODO: Replace with fixed capacity implementation with capacity `cells.size` */)) { cell ->
                        Cell(it(cell.position), cell.attributes)
                    }
                )
            }
            
            val allPossibleParts = buildKoneIterableSet {
                for (form in forms) for (otherFirstCell in restCells) {
                    if (otherFirstCell.position.kind != form.startCell.position.kind) continue
                    val shift = otherFirstCell - form.startCell
                    val part = form.cells.mapTo(KoneResizableHashSet(/* TODO: Replace with fixed capacity implementation with capacity `cellsPerPart` */)) { it + shift }
                    if (part.all { it in allCells } && part.none { it in firstPart }) add(part)
                }
            }.toKoneIterableList()
            
            for (parts in allPossibleParts.combinations(numberOfParts - 1u)) {
                if (parts.combinations(2u).any { (part1, part2) -> part1.any { it in part2 } }) continue
                it.send(buildKoneIterableList { addAllFrom(parts); add(firstPart) })
            }
            if(!isActive) return@logic
        }
    }
    
    return object : ChannelComputation<KoneIterableList<KoneIterableSet<Cell<C, K, A>>>> {
        override val key: CoroutineContext.Key<*> get() = Computation.Key
        
        override val parent: Computation? get() = null // TODO: Implement parent-child relations
        override val children: Sequence<Computation> get() = emptySequence() // TODO: Implement parent-child relations
        
        private val _state: AtomicRef<State> = atomic(State.Paused(Job()))
        
        override val isActive: Boolean
            get() = _state.value.let { it is State.Pausing || it is State.Active || it === State.Completion }
        override val isPaused: Boolean
            get() = _state.value.let { it is State.Paused || it is State.Activating }
        override val isCompleted: Boolean
            get() = _state.value.let { it === State.Completed || it === State.Cancelled }
        override val isCancelled: Boolean
            get() = _state.value.let { it === State.Cancelling || it === State.Cancelled }
        
        private val lifecycleJob = Job(this@CoroutineScope.coroutineContext[Job])
        private val _resultsChannel: Channel<KoneIterableList<KoneIterableSet<Cell<C, K, A>>>> =
            Channel(Channel.UNLIMITED)
        override val resultsChannel: ReceiveChannel<KoneIterableList<KoneIterableSet<Cell<C, K, A>>>> get() = _resultsChannel
        
        override fun resume() {
            while (true) { // TODO: Include possible children
                val currentState = _state.value
                val newState: State = when (currentState) {
                    is State.Pausing -> return
                    is State.Paused -> State.Activating(jobUntilActive = currentState.jobUntilActive, jobUntilPaused = Job())
                    is State.Activating -> currentState
                    is State.Active -> return
                    
                    is State.PausingCompletion -> TODO("Parent-child relation is not yet implemented")
                    State.PausedCompletion -> TODO("Parent-child relation is not yet implemented")
                    is State.ActivatingCompletion -> TODO("Parent-child relation is not yet implemented")
                    State.Completion -> TODO("Parent-child relation is not yet implemented")
                    
                    State.Completed -> return
                    
                    State.Cancelled -> return
                    State.Cancelling -> return
                }
                if (!_state.compareAndSet(currentState, newState)) continue
                
                when (currentState) {
                    is State.Pausing -> error("Reached unreachable code")
                    is State.Paused -> {
                        val effectiveJob = launch(context = lifecycleJob, start = CoroutineStart.LAZY) { logic(_resultsChannel) }
                        newState as State.Activating
                        _state.value = State.Active(jobUntilPaused = newState.jobUntilPaused, effectiveJob = effectiveJob)
                        newState.jobUntilActive.complete()
                    }
                    is State.Activating -> {}
                    is State.Active -> error("Reached unreachable code")
                    
                    is State.PausingCompletion -> TODO("Parent-child relation is not yet implemented")
                    State.PausedCompletion -> TODO("Parent-child relation is not yet implemented")
                    is State.ActivatingCompletion -> TODO("Parent-child relation is not yet implemented")
                    State.Completion -> TODO("Parent-child relation is not yet implemented")
                    
                    State.Completed -> error("Reached unreachable code")
                    
                    State.Cancelled -> error("Reached unreachable code")
                    State.Cancelling -> error("Reached unreachable code")
                }
                
                return
            }
        }
        
        override fun pause() {
            while (true) { // TODO: Include possible children
                val currentState = _state.value
                val newState: State = when (currentState) {
                    is State.Pausing -> currentState
                    is State.Paused -> return
                    is State.Activating -> return
                    is State.Active -> State.Pausing(jobUntilPaused = currentState.jobUntilPaused, jobUntilActive = Job())
                    
                    is State.PausingCompletion -> TODO("Parent-child relation is not yet implemented")
                    State.PausedCompletion -> TODO("Parent-child relation is not yet implemented")
                    is State.ActivatingCompletion -> TODO("Parent-child relation is not yet implemented")
                    State.Completion -> TODO("Parent-child relation is not yet implemented")
                    
                    State.Completed -> return
                    
                    State.Cancelled -> return
                    State.Cancelling -> return
                }
                if (!_state.compareAndSet(currentState, newState)) continue
                
                when (currentState) {
                    is State.Pausing -> {}
                    is State.Paused -> error("Reached unreachable code")
                    is State.Activating -> error("Reached unreachable code")
                    is State.Active -> {
                        newState as State.Pausing
                        launch(lifecycleJob) {
                            currentState.effectiveJob.cancelAndJoin()
                            _state.value = State.Paused(jobUntilActive = newState.jobUntilActive)
                            newState.jobUntilPaused.complete()
                        }
                    }
                    
                    is State.PausingCompletion -> TODO("Parent-child relation is not yet implemented")
                    State.PausedCompletion -> TODO("Parent-child relation is not yet implemented")
                    is State.ActivatingCompletion -> TODO("Parent-child relation is not yet implemented")
                    State.Completion -> TODO("Parent-child relation is not yet implemented")
                    
                    State.Completed -> error("Reached unreachable code")
                    
                    State.Cancelled -> error("Reached unreachable code")
                    State.Cancelling -> error("Reached unreachable code")
                }
                
                return
            }
        }
        
        override fun cancel(cause: CancellationException?) {
            while (true) {
                val currentState = _state.value
                val newState = when (currentState) {
                    is State.Pausing -> State.Cancelling
                    is State.Paused -> State.Cancelling
                    is State.Activating -> State.Cancelling
                    is State.Active -> State.Cancelling
                    
                    is State.PausingCompletion -> TODO("Parent-child relation is not yet implemented")
                    State.PausedCompletion -> TODO("Parent-child relation is not yet implemented")
                    is State.ActivatingCompletion -> TODO("Parent-child relation is not yet implemented")
                    State.Completion -> TODO("Parent-child relation is not yet implemented")
                    
                    State.Completed -> return
                    
                    State.Cancelled -> return
                    State.Cancelling -> return
                }
                if (!_state.compareAndSet(currentState, newState)) continue
                
                launch {
                    val cancellationException = if (cause != null) kotlinx.coroutines.CancellationException(
                        cause.message,
                        cause.cause
                    ) else null
                    
                    lifecycleJob.cancel(cancellationException)
                    lifecycleJob.join()
                    
                    _state.value = State.Cancelled
                }
                
                return
            }
        }
        
        override suspend fun joinUntilResumed() {
            when (val currentState = _state.value) {
                is State.Pausing -> currentState.jobUntilActive.join()
                is State.Paused -> currentState.jobUntilActive.join()
                is State.Activating -> currentState.jobUntilActive.join()
                is State.Active -> {}
                
                is State.PausingCompletion -> TODO("Parent-child relation is not yet implemented")
                State.PausedCompletion -> TODO("Parent-child relation is not yet implemented")
                is State.ActivatingCompletion -> TODO("Parent-child relation is not yet implemented")
                State.Completion -> TODO("Parent-child relation is not yet implemented")
                
                State.Completed -> return
                
                State.Cancelled -> return
                State.Cancelling -> return
            }
        }
        
        override suspend fun joinUntilPaused() {
            when (val currentState = _state.value) {
                is State.Pausing -> currentState.jobUntilPaused.join()
                is State.Paused -> {}
                is State.Activating -> currentState.jobUntilPaused.join()
                is State.Active -> currentState.jobUntilPaused.join()
                
                is State.PausingCompletion -> TODO("Parent-child relation is not yet implemented")
                State.PausedCompletion -> TODO("Parent-child relation is not yet implemented")
                is State.ActivatingCompletion -> TODO("Parent-child relation is not yet implemented")
                State.Completion -> TODO("Parent-child relation is not yet implemented")
                
                State.Completed -> return
                
                State.Cancelled -> return
                State.Cancelling -> return
            }
        }
        
        override suspend fun resumeJoining() {
            while (true) { // TODO: Include possible children
                val currentState = _state.value
                val newState: State = when (currentState) {
                    is State.Pausing -> { currentState.jobUntilActive.join(); return }
                    is State.Paused -> State.Activating(jobUntilActive = currentState.jobUntilActive, jobUntilPaused = Job())
                    is State.Activating -> currentState
                    is State.Active -> return
                    
                    is State.PausingCompletion -> TODO("Parent-child relation is not yet implemented")
                    State.PausedCompletion -> TODO("Parent-child relation is not yet implemented")
                    is State.ActivatingCompletion -> TODO("Parent-child relation is not yet implemented")
                    State.Completion -> TODO("Parent-child relation is not yet implemented")
                    
                    State.Completed -> return
                    
                    State.Cancelled -> return
                    State.Cancelling -> return
                }
                if (!_state.compareAndSet(currentState, newState)) continue
                
                when (currentState) {
                    is State.Pausing -> error("Reached unreachable code")
                    is State.Paused -> {
                        val effectiveJob = launch(context = lifecycleJob, start = CoroutineStart.LAZY) { logic(_resultsChannel) }
                        newState as State.Activating
                        _state.value = State.Active(jobUntilPaused = newState.jobUntilPaused, effectiveJob = effectiveJob)
                        newState.jobUntilActive.complete()
                        newState.jobUntilActive.join()
                    }
                    is State.Activating -> {}
                    is State.Active -> error("Reached unreachable code")
                    
                    is State.PausingCompletion -> TODO("Parent-child relation is not yet implemented")
                    State.PausedCompletion -> TODO("Parent-child relation is not yet implemented")
                    is State.ActivatingCompletion -> TODO("Parent-child relation is not yet implemented")
                    State.Completion -> TODO("Parent-child relation is not yet implemented")
                    
                    State.Completed -> error("Reached unreachable code")
                    
                    State.Cancelled -> error("Reached unreachable code")
                    State.Cancelling -> error("Reached unreachable code")
                }
                
                return
            }
        }
        
        override suspend fun pauseJoining() {
            while (true) { // TODO: Include possible children
                val currentState = _state.value
                val newState: State = when (currentState) {
                    is State.Pausing -> currentState
                    is State.Paused -> return
                    is State.Activating -> return
                    is State.Active -> State.Pausing(jobUntilPaused = currentState.jobUntilPaused, jobUntilActive = Job())
                    
                    is State.PausingCompletion -> TODO("Parent-child relation is not yet implemented")
                    State.PausedCompletion -> TODO("Parent-child relation is not yet implemented")
                    is State.ActivatingCompletion -> TODO("Parent-child relation is not yet implemented")
                    State.Completion -> TODO("Parent-child relation is not yet implemented")
                    
                    State.Completed -> return
                    
                    State.Cancelled -> return
                    State.Cancelling -> return
                }
                if (!_state.compareAndSet(currentState, newState)) continue
                
                when (currentState) {
                    is State.Pausing -> {}
                    is State.Paused -> error("Reached unreachable code")
                    is State.Activating -> error("Reached unreachable code")
                    is State.Active -> {
                        newState as State.Pausing
                        launch(lifecycleJob) {
                            currentState.effectiveJob.cancelAndJoin()
                            _state.value = State.Paused(jobUntilActive = newState.jobUntilActive)
                            newState.jobUntilPaused.complete()
                        }
                        newState.jobUntilPaused.join()
                    }
                    
                    is State.PausingCompletion -> TODO("Parent-child relation is not yet implemented")
                    State.PausedCompletion -> TODO("Parent-child relation is not yet implemented")
                    is State.ActivatingCompletion -> TODO("Parent-child relation is not yet implemented")
                    State.Completion -> TODO("Parent-child relation is not yet implemented")
                    
                    State.Completed -> error("Reached unreachable code")
                    
                    State.Cancelled -> error("Reached unreachable code")
                    State.Cancelling -> error("Reached unreachable code")
                }
                
                return
            }
        }
        
        override suspend fun join() {
            lifecycleJob.join()
        }
    }
}

internal
fun foo() {}