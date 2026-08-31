package org.fossify.keyboard.extensions

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.graphics.drawable.LayerDrawable
import android.view.Gravity
import org.fossify.commons.extensions.darkenColor
import org.fossify.commons.extensions.lightenColor

/**
 * Builds a glossy gel-style rounded-rect background for the two UI
 * elements that use minikeyboard_background.xml - the key-press preview
 * bubble (MyKeyboardView.showKey()) and the popup/mini keyboard's own
 * frame (MyKeyboardView.setupKeyboard(), the changedView.id ==
 * R.id.mini_keyboard_view branch). Both previously used the same pattern:
 * find the fill/stroke layers by id in the static XML LayerDrawable and
 * apply a flat PorterDuff tint to each - a flat tint can't produce a
 * gradient, so this builds a fresh gradient + highlight drawable from the
 * same two colors instead, same technique as the Messages/Gallery gel
 * elements elsewhere in this session's work.
 *
 * The key-press bubble specifically is arguably the single most-seen UI
 * element in the whole app, short of the keys themselves - it shows on
 * every keypress.
 *
 * strokeColor is kept as a genuinely separate, caller-provided value
 * (not derived from baseColor the way rim colors are in the other gel
 * helpers) - that matches how this element already worked: background
 * and stroke are two independently configurable colors here, not one
 * base color with a derived rim.
 */
fun Context.createGelMinikeyboardBackground(baseColor: Int, strokeColor: Int): LayerDrawable {
    val lightColor = baseColor.lightenColor(35)
    val darkColor = baseColor.darkenColor(20)
    val density = resources.displayMetrics.density
    val cornerRadius = resources.getDimension(org.fossify.commons.R.dimen.medium_margin)
    val strokeWidthPx = (1 * density).toInt()

    val body = GradientDrawable(
        GradientDrawable.Orientation.TOP_BOTTOM,
        intArrayOf(lightColor, baseColor, darkColor)
    ).apply {
        this.cornerRadius = cornerRadius
        setStroke(strokeWidthPx, strokeColor)
    }

    val highlightColor = Color.argb(90, 255, 255, 255)
    val highlight = GradientDrawable(
        GradientDrawable.Orientation.TOP_BOTTOM,
        intArrayOf(highlightColor, Color.TRANSPARENT)
    ).apply {
        shape = GradientDrawable.OVAL
    }

    return LayerDrawable(arrayOf(body, highlight)).apply {
        val highlightWidth = (28 * density).toInt()
        val highlightHeight = (14 * density).toInt()
        setLayerSize(1, highlightWidth, highlightHeight)
        setLayerGravity(1, Gravity.TOP or Gravity.CENTER_HORIZONTAL)
        setLayerInsetTop(1, (6 * density).toInt())
    }
}
