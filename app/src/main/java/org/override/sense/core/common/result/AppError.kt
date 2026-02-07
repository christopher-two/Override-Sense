package org.override.sense.core.common.result

import android.content.Context
import androidx.annotation.StringRes
import org.override.sense.R
import java.io.FileNotFoundException
import java.io.IOException
import java.net.SocketTimeoutException
import java.net.UnknownHostException

/**
 * Represents different types of errors that can occur in the application.
 *
 * Each error type provides a user-friendly message through [toUserMessage].
 */
sealed interface AppError {
    /**
     * The underlying cause of the error, if available.
     */
    val cause: Throwable?

    /**
     * Converts this error to a user-friendly message.
     *
     * @param context Android context for accessing string resources
     * @return A localized error message suitable for displaying to users
     */
    fun toUserMessage(context: Context): String

    /**
     * Network-related errors (connectivity, timeouts, etc.).
     */
    data class Network(override val cause: Throwable? = null) : AppError {
        override fun toUserMessage(context: Context): String =
            context.getString(R.string.error_network)
    }

    /**
     * Database-related errors (query failures, corruption, etc.).
     */
    data class Database(override val cause: Throwable? = null) : AppError {
        override fun toUserMessage(context: Context): String =
            context.getString(R.string.error_database)
    }

    /**
     * File system errors (I/O failures, access denied, etc.).
     */
    data class FileSystem(override val cause: Throwable? = null) : AppError {
        override fun toUserMessage(context: Context): String =
            context.getString(R.string.error_file_system)
    }

    /**
     * Permission-related errors (missing permissions, denied access, etc.).
     */
    data class PermissionDenied(override val cause: Throwable? = null) : AppError {
        override fun toUserMessage(context: Context): String =
            context.getString(R.string.error_permission_denied)
    }

    /**
     * File not found errors.
     */
    data class FileNotFound(override val cause: Throwable? = null) : AppError {
        override fun toUserMessage(context: Context): String =
            context.getString(R.string.error_file_not_found)
    }

    /**
     * Invalid file format errors.
     */
    data class InvalidFile(override val cause: Throwable? = null) : AppError {
        override fun toUserMessage(context: Context): String =
            context.getString(R.string.error_invalid_file)
    }

    /**
     * Unknown or unexpected errors.
     */
    data class Unknown(override val cause: Throwable? = null) : AppError {
        override fun toUserMessage(context: Context): String =
            context.getString(R.string.error_unknown)
    }

    /**
     * Custom error with a specific string resource.
     *
     * @param messageRes String resource ID for the error message
     * @param cause The underlying cause of the error
     */
    data class Custom(
        val messageRes: Int,
        override val cause: Throwable? = null
    ) : AppError {
        override fun toUserMessage(context: Context): String =
            context.getString(messageRes)
    }
}

/**
 * Maps common exceptions to appropriate AppError types.
 */
fun Throwable.toAppError(): AppError = when (this) {
    is FileNotFoundException -> AppError.FileNotFound(this)
    is IOException -> AppError.FileSystem(this)
    is SecurityException -> AppError.PermissionDenied(this)
    is UnknownHostException,
    is SocketTimeoutException -> AppError.Network(this)
    else -> AppError.Unknown(this)
}
