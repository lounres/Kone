package dev.lounres.kone.hub

import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.RememberObserver
import androidx.compose.runtime.SnapshotMutationPolicy
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.structuralEqualityPolicy


//@Composable
//public fun <Value> KoneState<Value>.subscribeAsState(policy: SnapshotMutationPolicy<Value> = structuralEqualityPolicy()): State<Value> {
//    val state = remember(this, policy) { mutableStateOf(value, policy) }
//
//    DisposableEffect(this) {
//        val disposable = subscribe { state.value = it }
//        onDispose { disposable.cancel() }
//    }
//
//    return state
//}

private class KoneAsynchronousHubSubscriptionComposeState<Value>(
    private val subscription: KoneAsynchronousHub.Subscription,
    private val actualState: MutableState<Value>,
) : RememberObserver, MutableState<Value> by actualState {
    override fun onRemembered() {}
    override fun onForgotten() {
        subscription.cancel()
    }
    override fun onAbandoned() {
        subscription.cancel()
    }
}

@Composable
public fun <Value> KoneAsynchronousHub<Value>.subscribeAsState(policy: SnapshotMutationPolicy<Value> = structuralEqualityPolicy()): State<Value> =
    remember(this, policy) {
        buildSubscription { initialValue ->
            val state = mutableStateOf(initialValue)
            val subscription = subscribe { state.value = it }
            KoneAsynchronousHubSubscriptionComposeState(subscription, state)
        }
    }