// SPDX-FileCopyrightText: 2026 Saalim Quadri <danascape@gmail.com>
// SPDX-License-Identifier: Apache-2.0

package org.prauga.toolkit.core.data.device

import java.io.IOException
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.async
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertSame
import org.junit.Assert.fail
import org.junit.Test

class SuspendLazyTest {
    @Test
    fun `concurrent callers share one load`() = runBlocking {
        val gate = CompletableDeferred<Unit>()
        var loads = 0
        val lazy = SuspendLazy {
            loads++
            gate.await()
            Any()
        }

        val first = async(start = CoroutineStart.UNDISPATCHED) { lazy.get() }
        val second = async(start = CoroutineStart.UNDISPATCHED) { lazy.get() }
        gate.complete(Unit)

        assertSame(first.await(), second.await())
        assertEquals(1, loads)
    }

    @Test
    fun `a loaded value is reused`() = runBlocking {
        var loads = 0
        val lazy = SuspendLazy { ++loads }

        lazy.get()
        lazy.get()

        assertEquals(1, lazy.get())
    }

    @Test
    fun `a failed load is retried by the next caller`() = runBlocking {
        var loads = 0
        val lazy = SuspendLazy {
            loads++
            if (loads == 1) throw IOException("first read fails")
            loads
        }

        try {
            lazy.get()
            fail("the first load should have thrown")
        } catch (_: IOException) {
            assertEquals(1, loads)
        }

        assertEquals(2, lazy.get())
    }
}
