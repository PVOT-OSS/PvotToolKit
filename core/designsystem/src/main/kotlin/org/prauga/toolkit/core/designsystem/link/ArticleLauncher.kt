// SPDX-FileCopyrightText: 2026 Saalim Quadri <danascape@gmail.com>
// SPDX-License-Identifier: Apache-2.0

package org.prauga.toolkit.core.designsystem.link

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.browser.customtabs.CustomTabColorSchemeParams
import androidx.browser.customtabs.CustomTabsIntent
import androidx.core.net.toUri
import timber.log.Timber

private val WebSchemes = setOf("https", "http")

/** URLs come from the site's published JSON, so anything but a web link is refused. */
fun Context.openArticle(url: String, toolbarColor: Int) {
    val uri = url.toUri()
    if (uri.scheme?.lowercase() !in WebSchemes) {
        Timber.w("Refused to open a link with scheme %s", uri.scheme)
        return
    }
    val colors = CustomTabColorSchemeParams.Builder().setToolbarColor(toolbarColor).build()

    val customTab =
        CustomTabsIntent.Builder()
            .setDefaultColorSchemeParams(colors)
            .setShowTitle(true)
            .setUrlBarHidingEnabled(true)
            .build()

    try {
        customTab.launchUrl(this, uri)
    } catch (noCustomTab: ActivityNotFoundException) {
        Timber.i(noCustomTab, "Custom Tab unavailable, opening the link directly")
        openInBrowser(uri)
    }
}

private fun Context.openInBrowser(uri: Uri) {
    try {
        startActivity(Intent(Intent.ACTION_VIEW, uri))
    } catch (noBrowser: ActivityNotFoundException) {
        Timber.w(noBrowser, "No app can open web links")
    }
}
