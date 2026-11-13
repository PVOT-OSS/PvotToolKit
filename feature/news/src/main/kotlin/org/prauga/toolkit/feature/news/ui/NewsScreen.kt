// SPDX-FileCopyrightText: 2026 Saalim Quadri <danascape@gmail.com>
// SPDX-License-Identifier: Apache-2.0

package org.prauga.toolkit.feature.news.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.prauga.pvot.coreui.PvotEmptyContent
import com.prauga.pvot.coreui.PvotErrorContent
import com.prauga.pvot.designsystem.components.PvotCard
import com.prauga.pvot.designsystem.components.PvotScreen
import com.prauga.pvot.designsystem.components.PvotScreenDefaults
import com.prauga.pvot.designsystem.components.PvotSkeleton
import com.prauga.pvot.designsystem.motion.pvotReveal
import com.prauga.pvot.designsystem.theme.PvotAppTheme
import com.prauga.pvot.designsystem.theme.PvotShapes
import com.prauga.pvot.designsystem.theme.PvotSpacing
import java.time.LocalDate
import org.prauga.toolkit.core.common.LoadError
import org.prauga.toolkit.core.designsystem.link.openArticle
import org.prauga.toolkit.core.model.NewsPost
import org.prauga.toolkit.feature.news.R

private const val SKELETON_CARDS = 4
private val SkeletonCardHeight = 132.dp

@Composable
internal fun NewsScreen(modifier: Modifier = Modifier, viewModel: NewsViewModel = hiltViewModel()) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val toolbarColor by rememberUpdatedState(MaterialTheme.colorScheme.surface.toArgb())

    LaunchedEffect(viewModel) {
        viewModel.effects.collect { effect ->
            when (effect) {
                is NewsEffect.OpenArticle -> context.openArticle(effect.url, toolbarColor)
            }
        }
    }

    NewsContent(
        state = state,
        onPostClick = { post -> viewModel.onEvent(NewsEvent.ArticleClicked(post.url)) },
        onRetry = { viewModel.onEvent(NewsEvent.Retry) },
        modifier = modifier,
    )
}

@Composable
private fun NewsContent(
    state: NewsUiState,
    onPostClick: (NewsPost) -> Unit,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier,
) {
    when (state) {
        NewsUiState.Loading -> {
            NewsSkeleton(modifier)
        }

        NewsUiState.Empty -> {
            CenteredContent(modifier) {
                PvotEmptyContent(message = stringResource(R.string.news_empty))
            }
        }

        is NewsUiState.Failed -> {
            CenteredContent(modifier) { LoadFailure(error = state.error, onRetry = onRetry) }
        }

        is NewsUiState.Ready -> {
            NewsList(
                posts = state.posts,
                stale = state.stale,
                onPostClick = onPostClick,
                modifier = modifier,
            )
        }
    }
}

@Composable
private fun NewsList(
    posts: List<NewsPost>,
    stale: Boolean,
    onPostClick: (NewsPost) -> Unit,
    modifier: Modifier = Modifier,
) {
    PvotScreen(
        modifier = modifier.fillMaxSize(),
        contentPadding = PvotScreenDefaults.contentPadding(),
    ) {
        item(key = "header") { Header() }

        if (stale) {
            item(key = "stale") { OfflineNotice() }
        }

        newsItems(posts, onPostClick)
    }
}

/** Only the newest post carries its cover, matching the website. */
private fun LazyListScope.newsItems(posts: List<NewsPost>, onPostClick: (NewsPost) -> Unit) {
    val featuredId = posts.firstOrNull()?.id
    itemsIndexed(items = posts, key = { _, post -> post.id }) { index, post ->
        NewsCard(
            post = post,
            onClick = { onPostClick(post) },
            showCover = post.id == featuredId,
            revealIndex = index,
        )
    }
}

@Composable
private fun NewsSkeleton(modifier: Modifier = Modifier) {
    PvotScreen(
        modifier = modifier.fillMaxSize(),
        contentPadding = PvotScreenDefaults.contentPadding(),
        verticalArrangement = Arrangement.spacedBy(PvotSpacing.M),
    ) {
        item(key = "header") { Header() }
        items(SKELETON_CARDS) {
            PvotSkeleton(
                modifier = Modifier.fillMaxWidth().height(SkeletonCardHeight),
                shape = PvotShapes.Card,
            )
        }
    }
}

@Composable
private fun Header() {
    Column(
        modifier = Modifier.fillMaxWidth().pvotReveal(0).padding(bottom = PvotSpacing.XS),
        verticalArrangement = Arrangement.spacedBy(PvotSpacing.S),
    ) {
        Text(
            text = stringResource(R.string.news_kicker),
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.tertiary,
        )
        Text(
            text = stringResource(R.string.news_title),
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.onBackground,
        )
        Text(
            text = stringResource(R.string.news_subtitle),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

@Composable
private fun OfflineNotice() {
    PvotCard(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = stringResource(R.string.news_offline_copy),
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(horizontal = PvotSpacing.M, vertical = PvotSpacing.S),
        )
    }
}

@Composable
private fun LoadFailure(error: LoadError, onRetry: () -> Unit) {
    val (title, message) =
        when (error) {
            LoadError.OFFLINE -> {
                stringResource(R.string.news_error_offline_title) to
                    stringResource(R.string.news_error_offline_message)
            }

            LoadError.SERVER -> {
                stringResource(R.string.news_error_server_title) to
                    stringResource(R.string.news_error_server_message)
            }

            LoadError.MALFORMED -> {
                stringResource(R.string.news_error_malformed_title) to
                    stringResource(R.string.news_error_malformed_message)
            }
        }

    PvotErrorContent(title = title, message = message, onRetry = onRetry)
}

@Composable
private fun CenteredContent(modifier: Modifier = Modifier, content: @Composable () -> Unit) {
    Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) { content() }
}

private val PreviewPosts =
    listOf(
        NewsPost(
            id = "2026-08-12-stormbreaker-clang-default",
            title = "StormBreaker Clang is now our default toolchain",
            date = LocalDate.of(2026, 8, 12),
            tag = "Toolchain",
            author = "Saalim Quadri",
            summary =
                "Our in-house LLVM/Clang build is back, and from here on it " +
                    "compiles every StormBreaker kernel by default.",
            coverUrl = null,
            url = "https://stormbreaker.squadri.me/news/2026-08-12-stormbreaker-clang-default/",
            readingMinutes = 2,
        ),
        NewsPost(
            id = "2026-08-11-we-are-alive",
            title = "We're alive, and we're building again",
            date = LocalDate.of(2026, 8, 11),
            tag = "Announcement",
            author = "Saalim Quadri",
            summary = "It went quiet for a while. It never went away.",
            coverUrl = null,
            url = "https://stormbreaker.squadri.me/news/2026-08-11-we-are-alive/",
            readingMinutes = 3,
        ),
    )

@Preview
@Composable
private fun NewsReadyPreview() {
    PvotAppTheme {
        NewsContent(
            state = NewsUiState.Ready(PreviewPosts, stale = false),
            onPostClick = {},
            onRetry = {},
        )
    }
}

@Preview
@Composable
private fun NewsOfflinePreview() {
    PvotAppTheme {
        NewsContent(
            state = NewsUiState.Ready(PreviewPosts, stale = true),
            onPostClick = {},
            onRetry = {},
        )
    }
}

@Preview
@Composable
private fun NewsFailedPreview() {
    PvotAppTheme {
        NewsContent(state = NewsUiState.Failed(LoadError.OFFLINE), onPostClick = {}, onRetry = {})
    }
}
