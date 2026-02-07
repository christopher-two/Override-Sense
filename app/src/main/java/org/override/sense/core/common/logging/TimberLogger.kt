package org.override.sense.core.common.logging

import timber.log.Timber

/**
 * Timber-based implementation of [Logger].
 * 
 * This implementation wraps Timber for all logging operations.
 * Timber is configured in to:
 * - Show all logs in DEBUG builds
 * - Show only ERROR and WTF logs in RELEASE builds
 * 
 * Benefits of using this wrapper:
 * - Easy to test (can be mocked in unit tests)
 * - Can be replaced with a different logging library without changing client code
 * - Provides a consistent logging interface across the app
 */
class TimberLogger : Logger {
    
    override fun d(tag: String, message: String) {
        Timber.tag(tag).d(message)
    }
    
    override fun i(tag: String, message: String) {
        Timber.tag(tag).i(message)
    }
    
    override fun w(tag: String, message: String) {
        Timber.tag(tag).w(message)
    }
    
    override fun e(tag: String, message: String, throwable: Throwable?) {
        if (throwable != null) {
            Timber.tag(tag).e(throwable, message)
        } else {
            Timber.tag(tag).e(message)
        }
    }
    
    override fun wtf(tag: String, message: String, throwable: Throwable?) {
        if (throwable != null) {
            Timber.tag(tag).wtf(throwable, message)
        } else {
            Timber.tag(tag).wtf(message)
        }
    }
}

/**
 * No-op implementation of [Logger] for testing purposes.
 * 
 * Use this in unit tests where logging is not needed or desired.
 * 
 * Example:
 * ```kotlin
 * @Test
 * fun myTest() {
 *     val viewModel = MyViewModel(logger = NoOpLogger())
 *     // test code
 * }
 * ```
 */
class NoOpLogger : Logger {
    override fun d(tag: String, message: String) {}
    override fun i(tag: String, message: String) {}
    override fun w(tag: String, message: String) {}
    override fun e(tag: String, message: String, throwable: Throwable?) {}
    override fun wtf(tag: String, message: String, throwable: Throwable?) {}
}
