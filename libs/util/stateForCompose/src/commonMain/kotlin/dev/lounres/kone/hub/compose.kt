/*
 * Copyright © 2025 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.hub

import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.RememberObserver
import androidx.compose.runtime.SnapshotMutationPolicy
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.structuralEqualityPolicy


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


private class KoneBlockingHubSubscriptionComposeState<Value>(
    private val subscription: KoneBlockingHub.Subscription,
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
public fun <Value> KoneBlockingHub<Value>.subscribeAsState(policy: SnapshotMutationPolicy<Value> = structuralEqualityPolicy()): State<Value> =
    remember(this, policy) {
        buildSubscription { initialValue ->
            val state = mutableStateOf(initialValue)
            val subscription = subscribe { state.value = it }
            KoneBlockingHubSubscriptionComposeState(subscription, state)
        }
    }