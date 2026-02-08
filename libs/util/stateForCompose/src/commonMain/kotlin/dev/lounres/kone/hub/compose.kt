/*
 * Copyright © 2026 Gleb Minaev
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


private class KoneAsynchronousHubViewSubscriptionComposeState<Value>(
    private val subscription: KoneAsynchronousHubView.Subscription,
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
public fun <Value> KoneAsynchronousHubView<Value, *>.subscribeAsState(policy: SnapshotMutationPolicy<Value> = structuralEqualityPolicy()): State<Value> =
    remember(this, policy) {
        /*buildSubscriptionAtomic*/ /* FIXME: Replace when `buildSubscriptionAtomic` will be available*/
        buildSubscriptionLocking { initialValue ->
            val state = mutableStateOf(initialValue)
            val subscription = subscribe { state.value = it }
            KoneAsynchronousHubViewSubscriptionComposeState(subscription, state)
        }
    }


private class KoneBlockingHubViewSubscriptionComposeState<Value>(
    private val subscription: KoneBlockingHubView.Subscription,
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
public fun <Value> KoneBlockingHubView<Value, *>.subscribeAsState(policy: SnapshotMutationPolicy<Value> = structuralEqualityPolicy()): State<Value> =
    remember(this, policy) {
        /*buildSubscriptionAtomic*/ /* FIXME: Replace when `buildSubscriptionAtomic` will be available*/
        buildSubscriptionLocking { initialValue ->
            val state = mutableStateOf(initialValue)
            val subscription = subscribe { state.value = it }
            KoneBlockingHubViewSubscriptionComposeState(subscription, state)
        }
    }