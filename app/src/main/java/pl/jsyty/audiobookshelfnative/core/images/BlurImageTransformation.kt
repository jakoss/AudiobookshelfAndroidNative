package pl.jsyty.audiobookshelfnative.core.images

import android.graphics.*
import androidx.core.graphics.applyCanvas
import androidx.core.graphics.createBitmap
import coil3.decode.DecodeUtils
import coil3.size.*
import coil3.transform.Transformation

import kotlin.math.roundToInt

class BlurImageTransformation : Transformation() {

    override suspend fun transform(input: Bitmap, size: Size): Bitmap {
        // we will do the transformation only for non-square images
        if (input.width == input.height) return input

        val paint = Paint(Paint.ANTI_ALIAS_FLAG or Paint.FILTER_BITMAP_FLAG)
        val (outputWidth, outputHeight) = calculateOutputSize(input, size)

        val output = createBitmap(outputWidth, outputHeight, input.config ?: Bitmap.Config.ARGB_8888)
        output.applyCanvas {
            // draw blurred background
            drawBlurredBackground(input, outputWidth, outputHeight, paint)
            // draw original image on top of that
            drawOriginalImage(input, outputWidth, outputHeight, paint)
        }
        return output
    }

    private fun Canvas.drawBlurredBackground(
        input: Bitmap,
        outputWidth: Int,
        outputHeight: Int,
        paint: Paint
    ) {
        val blurredBitmap = fastBlur(input, 10)
        val matrix = Matrix()
        val multiplier = DecodeUtils.computeSizeMultiplier(
            srcWidth = blurredBitmap.width,
            srcHeight = blurredBitmap.height,
            dstWidth = outputWidth,
            dstHeight = outputHeight,
            scale = Scale.FILL
        ).toFloat()
        val dx = (outputWidth - multiplier * blurredBitmap.width) / 2
        val dy = (outputHeight - multiplier * blurredBitmap.height) / 2
        matrix.setTranslate(dx, dy)
        matrix.preScale(multiplier, multiplier)
        drawBitmap(blurredBitmap, matrix, paint)
    }

    private fun Canvas.drawOriginalImage(
        input: Bitmap,
        outputWidth: Int,
        outputHeight: Int,
        paint: Paint
    ) {
        val matrix = Matrix()
        val multiplier = DecodeUtils.computeSizeMultiplier(
            srcWidth = input.width,
            srcHeight = input.height,
            dstWidth = outputWidth,
            dstHeight = outputHeight,
            scale = Scale.FIT
        ).toFloat()
        val dx = (outputWidth - multiplier * input.width) / 2
        val dy = (outputHeight - multiplier * input.height) / 2
        matrix.setTranslate(dx, dy)
        matrix.preScale(multiplier, multiplier)
        drawBitmap(input, matrix, paint)
    }

    private fun calculateOutputSize(input: Bitmap, size: Size): Pair<Int, Int> {
        if (size.isOriginal) {
            return input.width to input.height
        }

        val (dstWidth, dstHeight) = size
        if (dstWidth is Dimension.Pixels && dstHeight is Dimension.Pixels) {
            return dstWidth.px to dstHeight.px
        }

        val multiplier = DecodeUtils.computeSizeMultiplier(
            srcWidth = input.width,
            srcHeight = input.height,
            dstWidth = size.width.pxOrElse { Int.MIN_VALUE },
            dstHeight = size.height.pxOrElse { Int.MIN_VALUE },
            scale = Scale.FILL
        )
        val outputWidth = (multiplier * input.width).roundToInt()
        val outputHeight = (multiplier * input.height).roundToInt()
        return outputWidth to outputHeight
    }

    override val cacheKey: String
        get() = javaClass.name

    override fun equals(other: Any?) = other is BlurImageTransformation

    override fun hashCode() = javaClass.hashCode()
}
