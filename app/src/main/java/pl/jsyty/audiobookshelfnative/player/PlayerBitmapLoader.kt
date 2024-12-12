package pl.jsyty.audiobookshelfnative.player

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.core.graphics.drawable.toBitmap
import androidx.media3.common.util.*
import coil3.asDrawable
import coil3.imageLoader
import coil3.request.ImageRequest
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
    override fun supportsMimeType(mimeType: String): Boolean = true

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
        requireNotNull(result.image?.asDrawable(context.resources)?.toBitmap()) {
            "Cannot docode bitmap from drawable"
        }
    }
}
