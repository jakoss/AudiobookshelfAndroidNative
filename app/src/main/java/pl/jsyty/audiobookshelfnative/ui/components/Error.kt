package pl.jsyty.audiobookshelfnative.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import pl.jsyty.audiobookshelfnative.R
import pl.jsyty.audiobookshelfnative.ui.theme.AudiobookshelfNativeTheme

/**
 * Error screen that will fill all available space
 *
 *
 * @param modifier Modifier for the parent view
 * @param retryAction Will be called when used tries to retry action that failed
 */
@Composable
fun FullscreenError(
    modifier: Modifier = Modifier,
    retryAction: (() -> Unit)? = null,
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .then(modifier), contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 60.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = painterResource(id = R.drawable.img_error),
                contentDescription = "Error",
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1f)
            )
            Spacer(modifier = Modifier.height(32.dp))
            Text(
                text = "Ops! Something Went Wrong",
                style = MaterialTheme.typography.titleMedium,
                textAlign = TextAlign.Center,
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = "Please check your internet connection and try again",
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center,
            )
            if (retryAction != null) {
                Spacer(modifier = Modifier.height(24.dp))
                OutlinedButton(
                    onClick = retryAction, modifier = Modifier
                        .fillMaxWidth()
                ) {
                    Text(text = "Retry")
                }
            }
        }
    }
}

@Preview
@Composable
private fun FullscreenErrorPreview() {
    AudiobookshelfNativeTheme {
        FullscreenError(retryAction = {})
    }
}
