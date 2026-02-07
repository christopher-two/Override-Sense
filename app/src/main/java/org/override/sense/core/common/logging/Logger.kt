package org.override.sense.core.common.logging

/**
 * Logger interface for abstracting logging implementation.
 * 
 * This interface allows for:
 * - Testability (easy to mock in unit tests)
 * - Flexibility (can swap implementations without changing client code)
 * - Consistency (unified logging API across the app)
 * 
 * Usage:
 * ```kotlin
 * class MyViewModel(private val logger: Logger) : ViewModel() {
 *     init {
 *         logger.d("MyViewModel", "ViewModel initialized")
 *     }
 *     
 *     fun onError(error: Exception) {
 *         logger.e("MyViewModel", "An error occurred", error)
 *     }
 * }
 * ```
 * 
 * Implementation note: By default, this is implemented by [TimberLogger]
 * which wraps Timber. In tests, you can provide a mock or no-op implementation.
 */
interface Logger {
    /**
     * Log a debug message. These logs are only visible in DEBUG builds.
     * Use for development and debugging purposes.
     * 
     * @param tag Used to identify the source of a log message
     * @param message The message to log
     */
    fun d(tag: String, message: String)
    
    /**
     * Log an info message. These logs are only visible in DEBUG builds.
     * Use for informational messages about app state.
     * 
     * @param tag Used to identify the source of a log message
     * @param message The message to log
     */
    fun i(tag: String, message: String)
    
    /**
     * Log a warning message. These logs are only visible in DEBUG builds.
     * Use for potentially harmful situations that don't prevent app execution.
     * 
     * @param tag Used to identify the source of a log message
     * @param message The message to log
     */
    fun w(tag: String, message: String)
    
    /**
     * Log an error message. These logs are visible in both DEBUG and RELEASE builds.
     * Use for error conditions that should be monitored in production.
     * 
     * @param tag Used to identify the source of a log message
     * @param message The message to log
     * @param throwable Optional throwable/exception to log
     */
    fun e(tag: String, message: String, throwable: Throwable? = null)
    
    /**
     * Log a "What a Terrible Failure" message. These logs are visible in both DEBUG and RELEASE builds.
     * Use for conditions that should never happen.
     * 
     * @param tag Used to identify the source of a log message
     * @param message The message to log
     * @param throwable Optional throwable/exception to log
     */
    fun wtf(tag: String, message: String, throwable: Throwable? = null)
}
