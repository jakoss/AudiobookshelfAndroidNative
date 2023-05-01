package pl.jsyty.audiobookshelfnative.player

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.core.graphics.drawable.toBitmap
import androidx.media3.common.util.Assertions
import androidx.media3.common.util.UnstableApi
import androidx.media3.session.BitmapLoader
import coil.imageLoader
import coil.request.ImageRequest
import com.google.common.util.concurrent.ListenableFuture
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.guava.future
import org.koin.core.annotation.Single

@UnstableApi
@Single
internal class PlayerBitmapLoader(
    private val context: Context,
) : BitmapLoader {
    private val scope = CoroutineScope(Dispatchers.IO)
    override fun decodeBitmap(data: ByteArray): ListenableFuture<Bitmap> = scope.future {
        val bitmap = BitmapFactory.decodeByteArray(data, 0, data.size)
        requireNotNull(bitmap) {
            "Could not decode image data"
        }
    }

    override fun loadBitmap(uri: Uri): ListenableFuture<Bitmap> = scope.future {
        val imageRequest = ImageRequest.Builder(context)
            .data(uri)
            .build()
        val result = context.imageLoader.execute(imageRequest)
        requireNotNull(result.drawable?.toBitmap()) {
            "Cannot docode bitmap from drawable"
        }
    }
}
