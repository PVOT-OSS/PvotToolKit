// SPDX-FileCopyrightText: 2026 Saalim Quadri <danascape@gmail.com>
// SPDX-License-Identifier: Apache-2.0

package org.prauga.toolkit.feature.more.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import org.prauga.toolkit.core.batterymonitor.BatteryMonitorController
import org.prauga.toolkit.core.data.links.LinksRepository
import org.prauga.toolkit.core.data.settings.ThemeRepository
import org.prauga.toolkit.core.model.AppVersion
import org.prauga.toolkit.core.model.LinkSection
import timber.log.Timber

private const val STOP_TIMEOUT_MILLIS = 5_000L

@HiltViewModel
class MoreViewModel
@Inject
constructor(
    linksRepository: LinksRepository,
    appVersion: AppVersion,
    private val themeRepository: ThemeRepository,
    private val batteryMonitor: BatteryMonitorController,
) : ViewModel() {
    internal val state: StateFlow<MoreUiState> =
        combine(
                flow { emit(linksRepository.links().data) }
                    .onStart { emit(emptyList<LinkSection>()) },
                themeRepository.theme,
                batteryMonitor.enabled,
            ) { sections, theme, batteryMonitorEnabled ->
                MoreUiState(
                    appVersion = appVersion,
                    sections = sections,
                    theme = theme,
                    batteryMonitorEnabled = batteryMonitorEnabled,
                )
            }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(STOP_TIMEOUT_MILLIS),
                initialValue = MoreUiState(appVersion = appVersion),
            )

    private val _effects = Channel<MoreEffect>(Channel.BUFFERED)
    internal val effects: Flow<MoreEffect> = _effects.receiveAsFlow()

    internal fun onEvent(event: MoreEvent) {
        when (event) {
            MoreEvent.DevicesClicked -> send(MoreEffect.OpenDevices)
            MoreEvent.NewsClicked -> send(MoreEffect.OpenNews)
            MoreEvent.LicensesClicked -> send(MoreEffect.OpenLicenses)
            is MoreEvent.LinkClicked -> event.item.url?.let { send(MoreEffect.OpenLink(it)) }
            is MoreEvent.ThemeSelected ->
                viewModelScope.launch { themeRepository.setTheme(event.theme) }
            is MoreEvent.BatteryMonitorToggled -> setBatteryMonitorEnabled(event.enabled)
            is MoreEvent.NotificationPermissionResult ->
                if (!event.granted) {
                    Timber.i(
                        "Notification permission denied; the battery monitor runs without its readout"
                    )
                }
        }
    }

    private fun setBatteryMonitorEnabled(enabled: Boolean) {
        viewModelScope.launch {
            if (enabled) _effects.send(MoreEffect.RequestNotificationPermission)
            batteryMonitor.setEnabled(enabled)
        }
    }

    private fun send(effect: MoreEffect) {
        viewModelScope.launch { _effects.send(effect) }
    }
}
