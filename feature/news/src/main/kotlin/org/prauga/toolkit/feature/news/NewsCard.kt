// SPDX-FileCopyrightText: 2026 Saalim Quadri <danascape@gmail.com>
// SPDX-License-Identifier: Apache-2.0

package org.prauga.toolkit.feature.news

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.prauga.pvot.designsystem.components.PvotCard
import com.prauga.pvot.designsystem.motion.pvotReveal
import com.prauga.pvot.designsystem.theme.PvotAppTheme
import com.prauga.pvot.designsystem.theme.PvotSpacing
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import org.prauga.toolkit.core.model.NewsPost

private val DateFormat: DateTimeFormatter = DateTimeFormatter.ofPattern("dd MMM yyyy")

/** One post, laid out like the website's news card. */
@Composable
fun NewsCard(
    post: NewsPost,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    showCover: Boolean = false,
    revealIndex: Int = 0,
) {
    val openLabel = stringResource(R.string.news_open_article, post.title)
    PvotCard(
        modifier =
            modifier.fillMaxWidth().pvotReveal(revealIndex).semantics {
                contentDescription = openLabel
            },
        onClick = onClick,
    ) {
        if (showCover && post.coverUrl != null) {
            AsyncImage(
                model = post.coverUrl,
                contentDescription = null,
                modifier =
                    Modifier.fillMaxWidth()
                        .aspectRatio(16f / 9f)
                        .background(MaterialTheme.colorScheme.surfaceContainerLowest),
                contentScale = ContentScale.Crop,
            )
        }

        Column(
            modifier = Modifier.padding(PvotSpacing.L),
            verticalArrangement = Arrangement.spacedBy(PvotSpacing.S),
        ) {
            MetaLine(post)

            Text(
                text = post.title,
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onSurface,
            )

            post.summary?.let {
                Text(
                    text = it,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }

            Text(
                text = "${stringResource(R.string.news_read)} →",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.primary,
            )
        }
    }
}

@Composable
private fun MetaLine(post: NewsPost) {
    val separator = stringResource(R.string.news_meta_separator)

    Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        MetaText(post.date.format(DateFormat))
        MetaText(separator)
        MetaText(
            pluralStringResource(
                R.plurals.news_reading_time,
                post.readingMinutes,
                post.readingMinutes,
            )
        )
        post.tag?.let {
            MetaText(separator)
            Text(
                text = it,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.tertiary,
            )
        }
    }
}

@Composable
private fun MetaText(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.labelSmall,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
    )
}

@Preview
@Composable
private fun NewsCardPreview() {
    PvotAppTheme {
        NewsCard(
            post =
                NewsPost(
                    id = "2026-08-11-we-are-alive",
                    title = "We're alive, and we're building again",
                    date = LocalDate.of(2026, 8, 11),
                    tag = "Announcement",
                    author = "Saalim Quadri",
                    summary =
                        "It went quiet for a while. It never went away. " +
                            "New home, revived charter, and the work is moving again.",
                    coverUrl = null,
                    url = "https://stormbreaker.squadri.me/news/2026-08-11-we-are-alive/",
                    readingMinutes = 3,
                ),
            onClick = {},
            modifier = Modifier.padding(16.dp),
        )
    }
}
