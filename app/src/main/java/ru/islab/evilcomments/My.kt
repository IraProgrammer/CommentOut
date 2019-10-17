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
import androidx.core.graphics.drawable.toBitmap


class CircleImageView : ImageView {

    private val borderColor = resources.getColor(R.color.transparent1)
    private val borderColor2 = resources.getColor(R.color.transparent2)
    private val borderWidth = 4.0f
    private val borderWidth2 = 6.0f

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
        paint.color = borderColor2
        paint.style = Paint.Style.STROKE
        paint.strokeWidth = borderWidth2
        canvas.drawCircle(width / 2f, width / 2f, radius - borderWidth * 3, paint)
        paint.color = borderColor
        paint.strokeWidth = borderWidth
        canvas.drawCircle(width / 2f, width / 2f, radius - borderWidth, paint)
    }

    private fun drawRoundImage(canvas: Canvas) {

        var b: Bitmap = drawable.toBitmap()
        val bitmap = b.copy(Bitmap.Config.ARGB_8888, true)

        val scaledBitmap: Bitmap
        val ratio: Float = bitmap.width.toFloat() / bitmap.height.toFloat()
        val height: Int = Math.round(width / ratio)

        scaledBitmap = Bitmap.createScaledBitmap(
            bitmap,
            (width).toInt(),
            (height).toInt(),
            true
        )

        /* Cutting the outer of the circle */
        val shader: Shader
        shader = BitmapShader(scaledBitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP)

        val imagePaint = Paint()
        imagePaint.isAntiAlias = true
        imagePaint.shader = shader
        canvas.drawCircle(width / 2f, width / 2f, width / 2f - borderWidth2 * 3, imagePaint)
    }

}