package ru.islab.evilcomments

import android.content.Context
import android.graphics.*
import android.graphics.drawable.BitmapDrawable
import android.util.AttributeSet
import android.widget.ImageView
import androidx.core.graphics.get

class CircleImageView : ImageView {

    private val borderColor = Color.BLACK
    private val borderWidth = 8.0f

    constructor(context: Context) : super(context) {}

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {}

    constructor(context: Context, attrs: AttributeSet, defStyle: Int) : super(
        context,
        attrs,
        defStyle
    ) {
    }

    override fun onDraw(canvas: Canvas) {
        drawRoundImage(canvas)
        drawStroke(canvas)
    }

    private fun drawStroke(canvas: Canvas) {
        val paint = Paint(Paint.ANTI_ALIAS_FLAG)
        val radius = width / 2f

        /* Border paint */
        paint.color = borderColor
        paint.style = Paint.Style.STROKE
        paint.strokeWidth = borderWidth
        canvas.drawCircle(width / 2f, width / 2f, radius - borderWidth / 2 - 20, paint)

        canvas.drawCircle(width / 2f, width / 2f, radius - borderWidth, paint)
    }

    private fun drawRoundImage(canvas: Canvas) {
        var b: Bitmap = (drawable as BitmapDrawable).bitmap
        val bitmap = b.copy(Bitmap.Config.ARGB_8888, true)

        /* Scale the bitmap */
        val scaledBitmap: Bitmap
        val ratio: Float = bitmap.width.toFloat() / bitmap.height.toFloat()
        val height: Int = Math.round(width / ratio)

        val matrix = Matrix()
        matrix.postScale(0.4f, 0.4f)

        scaledBitmap =
                //Bitmap.createScaledBitmap(bitmap, (width * 0.7).toInt(), (height * 0.7).toInt(), true)

            Bitmap.createBitmap(
                bitmap,
                2,
                2,
                (width * 1.8).toInt(),
                (height * 1.8).toInt(),
                matrix,
                true
            )

        /* Cutting the outer of the circle */
        val shader: Shader
        shader = BitmapShader(scaledBitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP)

        val rect = RectF(20f, 20f, width.toFloat() * 0.7f + 20f, height.toFloat() * 0.7f + 20f)

        val imagePaint = Paint()
        imagePaint.isAntiAlias = true
        imagePaint.shader = shader
        canvas.drawRoundRect(rect, width.toFloat(), height.toFloat(), imagePaint)
        //canvas.drawBitmap(scaledBitmap, 50f, 50f, null);
    }

}