// SPDX-FileCopyrightText: 2026 Saalim Quadri <danascape@gmail.com>
// SPDX-License-Identifier: Apache-2.0

package org.prauga.toolkit.core.data.vitals

import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.runningFold
import kotlinx.coroutines.flow.shareIn
import kotlinx.coroutines.isActive
import org.prauga.toolkit.core.common.coroutines.ApplicationScope
import org.prauga.toolkit.core.data.device.DeviceRepository
import org.prauga.toolkit.core.model.Vitals

// The replay is dropped when collectors leave, so each session starts a fresh history.
@Singleton
class VitalsSampler
@Inject
constructor(deviceRepository: DeviceRepository, @ApplicationScope scope: CoroutineScope) {
    val samples: SharedFlow<VitalsSample> =
        vitalsSamples(deviceRepository::vitals)
            .shareIn(
                scope = scope,
                started = SharingStarted.WhileSubscribed(replayExpirationMillis = 0),
                replay = 1,
            )
}

// Reads back to back: each read spans the load window, which is the sampling interval.
internal fun vitalsSamples(read: suspend () -> Vitals): Flow<VitalsSample> =
    flow { while (currentCoroutineContext().isActive) emit(read()) }
        .runningFold<Vitals, VitalsSample?>(null) { last, vitals ->
            VitalsSample(vitals, (last?.history ?: MetricHistory()).append(vitals, last?.vitals))
        }
        .filterNotNull()
