// SPDX-FileCopyrightText: 2026 Saalim Quadri <danascape@gmail.com>
// SPDX-License-Identifier: Apache-2.0

package org.prauga.toolkit.core.data.device

import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

// A failed or cancelled load is not cached, so the next caller retries it.
internal class SuspendLazy<T : Any>(private val load: suspend () -> T) {
    private val mutex = Mutex()

    @Volatile private var value: T? = null

    suspend fun get(): T = value ?: mutex.withLock { value ?: load().also { value = it } }
}
