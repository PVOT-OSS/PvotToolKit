// SPDX-FileCopyrightText: 2026 Saalim Quadri <danascape@gmail.com>
// SPDX-License-Identifier: Apache-2.0

package org.prauga.toolkit.core.network

class ApiStatusException(val status: Int, cause: Throwable) : Exception("HTTP $status", cause)
