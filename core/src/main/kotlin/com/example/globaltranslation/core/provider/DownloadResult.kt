package com.example.globaltranslation.core.provider

/**
 * Represents the outcome of a model download request.
 */
sealed class DownloadResult {
    /** Successful download. */
    data object Success : DownloadResult()

    /** Failed download with the provided reason. */
    data class Failure(val reason: DownloadFailureReason) : DownloadResult() {
        companion object {
            /** Convenience factory to wrap a [Throwable] in a [DownloadFailureReason]. */
            operator fun invoke(throwable: Throwable): Failure = Failure(DownloadFailureReason.Exception(throwable))
        }
    }
}

/**
 * Describes why a download failed.
 */
sealed interface DownloadFailureReason {
    /** Failure caused by an underlying exception. */
    data class Exception(val throwable: Throwable) : DownloadFailureReason

    /** Failure because WiFi is required for the download. */
    data object WifiRequired : DownloadFailureReason
}
