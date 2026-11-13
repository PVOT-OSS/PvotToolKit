// SPDX-FileCopyrightText: 2026 Saalim Quadri <danascape@gmail.com>
// SPDX-License-Identifier: Apache-2.0

package org.prauga.toolkit.feature.news.ui

internal sealed interface NewsEffect {
    data class OpenArticle(val url: String) : NewsEffect
}
