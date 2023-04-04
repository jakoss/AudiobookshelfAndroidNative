package pl.jsyty.audiobookshelfnative.ui.components

import androidx.compose.animation.*
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import pl.jsyty.audiobookshelfnative.core.*

/**
 * Adds a fullscreen handler for all [Async] states when loading resource that's needed to display success state.
 * This handler is specialized for components that should take whole available space.
 * Fullscreen components are basically "fillMaxSize"
 *
 * @param state State to be handled
 * @param onRetryAction Handler for "try again" click
 * @param error Component for error state. Defaults to [FullscreenError]
 * @param loading Component for loading state. Defaults to [FullscreenLoader]
 * @param uninitialized Component for uninitialized state. Defaults to [FullscreenLoader]
 * @param success Component for success state
 */
@Composable
fun <T> FullscreenAsyncHandler(
    state: Async<T>,
    onRetryAction: () -> Unit,
    error: @Composable (Throwable) -> Unit = { FullscreenError(retryAction = onRetryAction) },
    loading: @Composable () -> Unit = { FullscreenLoader() },
    uninitialized: @Composable () -> Unit = { FullscreenLoader() },
    success: @Composable (T) -> Unit,
) {
    Crossfade(targetState = state, label = "Async state crossfade") {
        when {
            it is Uninitialized -> uninitialized()
            it is Success || it() != null -> success(requireNotNull(it()))
            it is Loading -> loading()
            it is Fail -> error(it.error)
        }
    }
}

/**
 * Adds a fullscreen handler for [Async] loading state.
 * This handler is specialized for components that should take whole available space.
 * Fullscreen components are basically "fillMaxSize"
 *
 * @param state State to be handled
 * @param loading Component for loading state. Defaults to [FullscreenLoader]
 * @param content Content displayed in uninitialized and success modes
 */
@Composable
fun <T> FullscreenLoadingAsyncHandler(
    state: Async<T>,
    modifier: Modifier = Modifier,
    loading: @Composable () -> Unit = { FullscreenLoader() },
    content: @Composable () -> Unit,
) {
    Box(modifier) {
        content()
        AnimatedVisibility(visible = state is Loading, enter = fadeIn(), exit = fadeOut()) {
            loading()
        }
    }
}
