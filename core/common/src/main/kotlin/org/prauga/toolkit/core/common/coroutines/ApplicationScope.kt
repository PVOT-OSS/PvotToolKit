// SPDX-FileCopyrightText: 2026 Saalim Quadri <danascape@gmail.com>
// SPDX-License-Identifier: Apache-2.0

package org.prauga.toolkit.core.common.coroutines

import javax.inject.Qualifier

/** Outlives every screen; for work that must finish even when the UI goes away. */
@Qualifier @Retention(AnnotationRetention.RUNTIME) annotation class ApplicationScope
