// SPDX-FileCopyrightText: 2026 Saalim Quadri <danascape@gmail.com>
// SPDX-License-Identifier: Apache-2.0

package org.prauga.toolkit.feature.news.ui

import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.MockEngineConfig
import io.ktor.client.engine.mock.MockRequestHandler
import io.ktor.client.engine.mock.respond
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.headersOf
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.prauga.toolkit.core.common.LoadError
import org.prauga.toolkit.core.data.news.NewsRepository
import org.prauga.toolkit.core.network.ToolkitApi

private const val POST_URL = "https://example.invalid/a/"
private const val FEED =
    """{"schema":1,"posts":[{"id":"a","title":"A","date":"2026-08-11","url":"$POST_URL"}]}"""
private const val EMPTY_FEED = """{"schema":1,"posts":[]}"""

private val JsonHeaders =
    headersOf(HttpHeaders.ContentType, ContentType.Application.Json.toString())

@OptIn(ExperimentalCoroutinesApi::class)
class NewsViewModelTest {
    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `starts loading and shows the feed once it arrives`() =
        runTest(testDispatcher) {
            val viewModel = viewModel { respond(FEED, HttpStatusCode.OK, JsonHeaders) }
            assertEquals(NewsUiState.Loading, viewModel.state.value)

            advanceUntilIdle()

            val state = viewModel.state.value
            assertTrue(state is NewsUiState.Ready)
            assertEquals(listOf("a"), (state as NewsUiState.Ready).posts.map { it.id })
        }

    @Test
    fun `an empty feed is empty rather than ready`() =
        runTest(testDispatcher) {
            val viewModel = viewModel { respond(EMPTY_FEED, HttpStatusCode.OK, JsonHeaders) }

            advanceUntilIdle()

            assertEquals(NewsUiState.Empty, viewModel.state.value)
        }

    @Test
    fun `retry after a failure loads again`() =
        runTest(testDispatcher) {
            var requests = 0
            val viewModel = viewModel {
                requests++
                if (requests == 1) {
                    respond("", HttpStatusCode.InternalServerError)
                } else {
                    respond(FEED, HttpStatusCode.OK, JsonHeaders)
                }
            }
            advanceUntilIdle()
            assertEquals(NewsUiState.Failed(LoadError.SERVER), viewModel.state.value)

            viewModel.onEvent(NewsEvent.Retry)
            advanceUntilIdle()

            assertTrue(viewModel.state.value is NewsUiState.Ready)
        }

    @Test
    fun `retry while a load is running does not start another`() =
        runTest(testDispatcher) {
            val release = CompletableDeferred<Unit>()
            var requests = 0
            val viewModel = viewModel {
                requests++
                release.await()
                respond(FEED, HttpStatusCode.OK, JsonHeaders)
            }
            runCurrent()

            viewModel.onEvent(NewsEvent.Retry)
            viewModel.onEvent(NewsEvent.Retry)
            runCurrent()
            release.complete(Unit)
            advanceUntilIdle()

            assertEquals(1, requests)
            assertTrue(viewModel.state.value is NewsUiState.Ready)
        }

    @Test
    fun `clicking an article asks the screen to open it`() =
        runTest(testDispatcher) {
            val viewModel = viewModel { respond(FEED, HttpStatusCode.OK, JsonHeaders) }
            advanceUntilIdle()

            viewModel.onEvent(NewsEvent.ArticleClicked(POST_URL))

            assertEquals(NewsEffect.OpenArticle(POST_URL), viewModel.effects.first())
        }

    private fun viewModel(handler: MockRequestHandler): NewsViewModel {
        val engine =
            MockEngine(
                MockEngineConfig().apply {
                    dispatcher = testDispatcher
                    addHandler(handler)
                    reuseHandlers = true
                }
            )
        return NewsViewModel(NewsRepository(ToolkitApi(engine)))
    }
}
