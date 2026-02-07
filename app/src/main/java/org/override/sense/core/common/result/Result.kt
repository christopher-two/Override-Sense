package org.override.sense.core.common.result

/**
 * Represents the result of an operation that can either succeed or fail.
 *
 * This sealed interface provides a type-safe way to handle operation results
 * throughout the application.
 *
 * @param T The type of the success data
 */
sealed interface Result<out T> {
    /**
     * Represents a successful operation with data.
     */
    data class Success<T>(val data: T) : Result<T>

    /**
     * Represents a failed operation with an error.
     */
    data class Error(val error: AppError) : Result<Nothing>
}

/**
 * Returns the success data if this is a Success result, or null otherwise.
 */
fun <T> Result<T>.getOrNull(): T? = when (this) {
    is Result.Success -> data
    is Result.Error -> null
}

/**
 * Returns the success data if this is a Success result, or throws the error.
 */
fun <T> Result<T>.getOrThrow(): T = when (this) {
    is Result.Success -> data
    is Result.Error -> throw error.cause ?: IllegalStateException(error.toString())
}

/**
 * Executes the given action if this is a Success result.
 * Returns this Result to allow chaining.
 */
inline fun <T> Result<T>.onSuccess(action: (T) -> Unit): Result<T> {
    if (this is Result.Success) {
        action(data)
    }
    return this
}

/**
 * Executes the given action if this is an Error result.
 * Returns this Result to allow chaining.
 */
inline fun <T> Result<T>.onError(action: (AppError) -> Unit): Result<T> {
    if (this is Result.Error) {
        action(error)
    }
    return this
}

/**
 * Transforms the success data using the given transform function.
 * Errors pass through unchanged.
 */
inline fun <T, R> Result<T>.map(transform: (T) -> R): Result<R> = when (this) {
    is Result.Success -> Result.Success(transform(data))
    is Result.Error -> this
}

/**
 * Transforms the success data using the given transform function that returns a Result.
 * Errors pass through unchanged.
 */
inline fun <T, R> Result<T>.flatMap(transform: (T) -> Result<R>): Result<R> = when (this) {
    is Result.Success -> transform(data)
    is Result.Error -> this
}

/**
 * Returns the success data if this is a Success result, or the default value otherwise.
 */
fun <T> Result<T>.getOrDefault(default: T): T = when (this) {
    is Result.Success -> data
    is Result.Error -> default
}

/**
 * Returns the success data if this is a Success result,
 * or the result of calling the defaultValue function with the error.
 */
inline fun <T> Result<T>.getOrElse(defaultValue: (AppError) -> T): T = when (this) {
    is Result.Success -> data
    is Result.Error -> defaultValue(error)
}
