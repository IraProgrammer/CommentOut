package ru.islab.evilcomments

import android.content.Context
import android.graphics.*
import android.graphics.drawable.BitmapDrawable
import android.util.AttributeSet
import android.widget.ImageView
import androidx.core.graphics.get
import android.opengl.ETC1.getHeight
import android.opengl.ETC1.getWidth
import android.graphics.Bitmap


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
//        var b: Bitmap = (drawable as BitmapDrawable).bitmap
//        val bitmap = b.copy(Bitmap.Config.ARGB_8888, true)
//
//        val dstBitmap = Bitmap.createBitmap(
//            (bitmap.getWidth() + borderWidth * 2).toInt(), // Width
//            (bitmap.getHeight() + borderWidth * 2).toInt(), // Height
//            Bitmap.Config.ARGB_8888 // Config
//        )
//
//        val scaledBitmap = Bitmap.createScaledBitmap(
//            bitmap,
//            (bitmap.getWidth() + borderWidth * 2).toInt(), // Width
//            (bitmap.getHeight() + borderWidth * 2).toInt(), true
//        )
//
//        val canvas = Canvas()
//
//        // Initialize a new Paint instance to draw border
//        val paint = Paint()
//        paint.color = borderColor
//        paint.style = Paint.Style.STROKE
//        paint.strokeWidth = borderWidth
//        paint.isAntiAlias = true
//
//        canvas.drawBitmap(bitmap, borderWidth, borderWidth, null)
//        canvas.drawBitmap(bitmap, 50f, 50f, null)
//        canvas.drawBitmap(dstBitmap, 50f, 50f, null)
//        canvas.drawBitmap(scaledBitmap, 50f, 50f, null)

        var b: Bitmap = (drawable as BitmapDrawable).bitmap
        val bitmap = b.copy(Bitmap.Config.ARGB_8888, true)

        /* Scale the bitmap */
        val scaledBitmap: Bitmap
        val ratio: Float = bitmap.width.toFloat() / bitmap.height.toFloat()
        val height: Int = Math.round(width / ratio)
        scaledBitmap = Bitmap.createScaledBitmap(bitmap, width, height, false)

        /* Cutting the outer of the circle */
        val shader: Shader
        shader = BitmapShader(scaledBitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP)

        val rect = RectF()
        rect.set(0f, 0f, width.toFloat(), height.toFloat())

        val imagePaint = Paint()
        imagePaint.isAntiAlias = true
        imagePaint.shader = shader
        canvas.drawRoundRect(rect, width.toFloat(), height.toFloat(), imagePaint)
    }

}