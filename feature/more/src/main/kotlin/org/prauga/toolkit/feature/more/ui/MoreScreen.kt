// SPDX-FileCopyrightText: 2026 Saalim Quadri <danascape@gmail.com>
// SPDX-License-Identifier: Apache-2.0

package org.prauga.toolkit.feature.more.ui

import android.Manifest
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.toggleableState
import androidx.compose.ui.state.ToggleableState
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.prauga.pvot.designsystem.components.PvotBottomSheet
import com.prauga.pvot.designsystem.components.PvotScreen
import com.prauga.pvot.designsystem.components.PvotScreenDefaults
import com.prauga.pvot.designsystem.components.PvotSheetItem
import com.prauga.pvot.designsystem.motion.pvotReveal
import com.prauga.pvot.designsystem.theme.PvotAppTheme
import com.prauga.pvot.designsystem.theme.PvotSpacing
import org.prauga.toolkit.core.designsystem.component.ActionRow
import org.prauga.toolkit.core.designsystem.component.SettingsGroup
import org.prauga.toolkit.core.designsystem.component.ValueRow
import org.prauga.toolkit.core.designsystem.link.openArticle
import org.prauga.toolkit.core.model.AppVersion
import org.prauga.toolkit.core.model.LinkItem
import org.prauga.toolkit.core.model.LinkSection
import org.prauga.toolkit.core.model.ThemePreference
import org.prauga.toolkit.feature.more.R

@Composable
internal fun MoreScreen(
    onOpenDevices: () -> Unit,
    onOpenNews: () -> Unit,
    onOpenLicenses: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: MoreViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val toolbarColor = MaterialTheme.colorScheme.surface.toArgb()
    val openDevices by rememberUpdatedState(onOpenDevices)
    val openNews by rememberUpdatedState(onOpenNews)
    val openLicenses by rememberUpdatedState(onOpenLicenses)
    val currentToolbarColor by rememberUpdatedState(toolbarColor)

    val notificationPermission =
        rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
            viewModel.onEvent(MoreEvent.NotificationPermissionResult(granted))
        }

    LaunchedEffect(viewModel) {
        viewModel.effects.collect { effect ->
            when (effect) {
                MoreEffect.RequestNotificationPermission ->
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        notificationPermission.launch(Manifest.permission.POST_NOTIFICATIONS)
                    }
                MoreEffect.OpenDevices -> openDevices()
                MoreEffect.OpenNews -> openNews()
                MoreEffect.OpenLicenses -> openLicenses()
                is MoreEffect.OpenLink -> context.openArticle(effect.url, currentToolbarColor)
            }
        }
    }

    MoreContent(state = state, onEvent = viewModel::onEvent, modifier = modifier)
}

@Composable
private fun MoreContent(
    state: MoreUiState,
    onEvent: (MoreEvent) -> Unit,
    modifier: Modifier = Modifier,
) {
    PvotScreen(
        modifier = modifier.fillMaxSize(),
        contentPadding = PvotScreenDefaults.contentPadding(),
    ) {
        item(key = "header") { Header() }

        item(key = "browse") {
            BrowseGroup(
                onOpenDevices = { onEvent(MoreEvent.DevicesClicked) },
                onOpenNews = { onEvent(MoreEvent.NewsClicked) },
            )
        }

        item(key = "battery") {
            BatteryMonitorGroup(
                enabled = state.batteryMonitorEnabled,
                onToggle = { onEvent(MoreEvent.BatteryMonitorToggled(it)) },
            )
        }

        item(key = "appearance") {
            SettingsGroup(title = stringResource(R.string.more_appearance)) {
                ThemeRow(
                    selected = state.theme,
                    onSelect = { onEvent(MoreEvent.ThemeSelected(it)) },
                )
            }
        }

        items(state.sections.size, key = { state.sections[it].id }) { index ->
            LinkGroup(
                section = state.sections[index],
                onLinkClick = { onEvent(MoreEvent.LinkClicked(it)) },
            )
        }

        item(key = "about") {
            AboutGroup(
                appVersion = state.appVersion,
                onOpenLicenses = { onEvent(MoreEvent.LicensesClicked) },
            )
        }
    }
}

/** Off by default: no discharge history exists before the user opts in. */
@Composable
private fun BatteryMonitorGroup(enabled: Boolean, onToggle: (Boolean) -> Unit) {
    SettingsGroup(title = stringResource(R.string.more_battery)) {
        ActionRow(
            label = stringResource(R.string.more_battery_toggle),
            index = 0,
            count = 1,
            description = stringResource(R.string.more_battery_description),
            trailing =
                stringResource(
                    if (enabled) R.string.more_battery_on else R.string.more_battery_off
                ),
            onClick = { onToggle(!enabled) },
            modifier =
                Modifier.semantics {
                    role = Role.Switch
                    toggleableState = ToggleableState(enabled)
                },
        )
    }
}

@Composable
private fun BrowseGroup(onOpenDevices: () -> Unit, onOpenNews: () -> Unit) {
    SettingsGroup(title = stringResource(R.string.more_browse)) {
        ActionRow(
            label = stringResource(R.string.more_browse_devices),
            index = 0,
            count = 2,
            description = stringResource(R.string.more_browse_devices_description),
            onClick = onOpenDevices,
        )
        ActionRow(
            label = stringResource(R.string.more_browse_news),
            index = 1,
            count = 2,
            description = stringResource(R.string.more_browse_news_description),
            onClick = onOpenNews,
        )
    }
}

@Composable
private fun Header() {
    Column(
        modifier = Modifier.fillMaxWidth().pvotReveal(0),
        verticalArrangement = Arrangement.spacedBy(PvotSpacing.S),
    ) {
        Text(
            text = stringResource(R.string.more_kicker),
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.tertiary,
        )
        Text(
            text = stringResource(R.string.more_title),
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.onBackground,
        )
    }
}

@Composable
private fun LinkGroup(section: LinkSection, onLinkClick: (LinkItem) -> Unit) {
    SettingsGroup(title = section.title) {
        section.items.forEachIndexed { index, item ->
            ActionRow(
                label = item.label,
                index = index,
                count = section.items.size,
                description = item.description,
                trailing = stringResource(R.string.more_soon).takeIf { item.soon },
                enabled = item.openable,
                onClick = { onLinkClick(item) },
            )
        }
    }
}

@Composable
private fun ThemeRow(selected: ThemePreference, onSelect: (ThemePreference) -> Unit) {
    var open by rememberSaveable { mutableStateOf(false) }

    ActionRow(
        label = stringResource(R.string.more_theme),
        index = 0,
        count = 1,
        trailing = stringResource(selected.labelRes()),
        onClick = { open = true },
    )
    PvotBottomSheet(
        visible = open,
        onDismissRequest = { open = false },
        title = stringResource(R.string.more_theme),
    ) {
        ThemePreference.entries.forEach { option ->
            PvotSheetItem(
                title = stringResource(option.labelRes()),
                onClick = {
                    onSelect(option)
                    open = false
                },
                subtitle =
                    stringResource(R.string.more_theme_selected).takeIf { option == selected },
            )
        }
    }
}

@Composable
private fun AboutGroup(appVersion: AppVersion, onOpenLicenses: () -> Unit) {
    SettingsGroup(title = stringResource(R.string.more_about)) {
        ValueRow(
            label = stringResource(R.string.more_about_version),
            value =
                stringResource(
                    R.string.more_about_version_format,
                    appVersion.name,
                    appVersion.code,
                ),
            index = 0,
            count = 2,
        )
        ActionRow(
            label = stringResource(R.string.more_about_licenses),
            index = 1,
            count = 2,
            description = stringResource(R.string.more_about_licenses_description),
            onClick = onOpenLicenses,
        )
    }
}

private fun ThemePreference.labelRes(): Int =
    when (this) {
        ThemePreference.SYSTEM -> R.string.more_theme_system
        ThemePreference.LIGHT -> R.string.more_theme_light
        ThemePreference.DARK -> R.string.more_theme_dark
    }

@Preview
@Composable
private fun MorePreview() {
    PvotAppTheme {
        MoreContent(
            state = MoreUiState(appVersion = AppVersion(name = "0.1.0", code = 100)),
            onEvent = {},
        )
    }
}
