package ru.islab.evilcomments.presentation.custom

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.widget.ImageView
import android.graphics.Bitmap
import androidx.core.graphics.drawable.toBitmap
import ru.islab.evilcomments.R
import kotlin.math.roundToInt


class CircleImageView : ImageView {

    private var borderColor = resources.getColor(R.color.transparent1)
    private var borderColor2 = resources.getColor(R.color.transparent2)
    private var borderWidth = 4.0f
    private var borderWidth2 = 6.0f

    var p1 = Paint(Paint.ANTI_ALIAS_FLAG)
    var p2 = Paint(Paint.ANTI_ALIAS_FLAG)

    constructor(context: Context) : super(context) {
        init(null)
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init(attrs)
    }

    constructor(context: Context, attrs: AttributeSet, defStyle: Int) : super(
        context,
        attrs,
        defStyle
    ) {
        init(attrs)
    }

    private fun init(set: AttributeSet?) {

        if (set == null) return

        val ta = context.obtainStyledAttributes(set, R.styleable.MyCustomView)
        borderColor = ta.getColor(
            R.styleable.MyCustomView_border_color1, resources.getColor(
                R.color.transparent1
            )
        )
        borderColor2 = ta.getColor(
            R.styleable.MyCustomView_border_color2, resources.getColor(
                R.color.transparent2
            )
        )

        borderWidth = ta.getDimension(R.styleable.MyCustomView_border_width1, 4f)
        borderWidth2 = ta.getDimension(R.styleable.MyCustomView_border_width2, 6f)

        p1.color = borderColor2
        p2.color = borderColor

        p1.strokeWidth = borderWidth
        p2.strokeWidth = borderWidth2

    }

    override fun onDraw(canvas: Canvas) {
        drawRoundImage(canvas)
        drawStroke(canvas)
    }

    private fun drawStroke(canvas: Canvas) {
        val radius = width / 2f

        p1.style = Paint.Style.STROKE
        p2.style = Paint.Style.STROKE

        canvas.drawCircle(width / 2f, width / 2f, radius - borderWidth * 3, p1)

        canvas.drawCircle(width / 2f, width / 2f, radius - borderWidth, p2)
    }

    private fun drawRoundImage(canvas: Canvas) {

        val b: Bitmap = drawable.toBitmap()
        val bitmap = b.copy(Bitmap.Config.ARGB_8888, true)

        val scaledBitmap: Bitmap
        val ratio: Float = bitmap.width.toFloat() / bitmap.height.toFloat()
        val height: Int = (width / ratio).roundToInt()

        scaledBitmap = Bitmap.createScaledBitmap(
            bitmap,
            width,
            height,
            true
        )

        val shader: Shader
        shader = BitmapShader(scaledBitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP)

        val imagePaint = Paint()
        imagePaint.isAntiAlias = true
        imagePaint.shader = shader
        canvas.drawCircle(
            width / 2f,
            width / 2f,
            width / 2f - borderWidth * 4 - borderWidth2 / 2,
            imagePaint
        )
    }

}