package ru.islab.evilcomments

import android.content.Context
import android.util.AttributeSet
import android.widget.ImageView
import android.R
import android.graphics.*
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.graphics.RectF
import android.opengl.ETC1.getWidth
import androidx.core.graphics.drawable.RoundedBitmapDrawableFactory
import androidx.core.graphics.drawable.RoundedBitmapDrawable
import android.graphics.Bitmap


class RoundRectCornerImageView : ImageView {

    constructor(context: Context) : super(context) {
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
    }

    constructor(context: Context, attrs: AttributeSet, defStyle: Int) : super(
        context,
        attrs,
        defStyle
    ) {
    }

    override fun setImageDrawable(drawable: Drawable?) {
        val radius = 200f
        val bitmap = (drawable as BitmapDrawable).bitmap
        val rid = RoundedBitmapDrawableFactory.create(resources, bitmap)
        rid.cornerRadius = bitmap.width * radius
        super.setImageDrawable(rid)
    }
}