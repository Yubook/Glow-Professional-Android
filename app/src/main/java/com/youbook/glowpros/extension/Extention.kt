package com.youbook.glowpros.extension

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.renderscript.Allocation
import android.renderscript.Element
import android.renderscript.RenderScript
import android.renderscript.ScriptIntrinsicBlur
import android.text.Html
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat

import com.bumptech.glide.Glide
import com.youbook.glowpros.R
import com.google.android.material.snackbar.Snackbar
import kotlin.math.roundToInt

/**
 * Activity extensions
 */
fun <A : Activity> Activity.startNewActivity(activity: Class<A>) {
    Intent(this, activity).also {
        it.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(it)
    }
}

/*fun Uri?.getFilePath(context: Context): String {
    return this?.let { uri -> RealPathUtil.getRealPath(context, uri) ?: "" } ?: ""
}*/


fun ImageView.setImgResource(resource: Int) {
    this.setImageResource(resource)
}

fun ImageView.loadNetworkImage(url: String) {
    loadingImage(this.context, url, this, false)
}

fun ImageView.loadNetworkProfile(url: String) {
    loadingImage(this.context, url, this, true)
}

fun loadingImage(context: Context, url: String, imageView: ImageView, isProfile: Boolean) {
    Glide.with(context).load(url)
        .placeholder(if (isProfile) R.drawable.ic_avtar_placeholder else R.drawable.place_holder)
        .into(imageView)
}

fun ImageView.loadLocalImage(uri: Uri) {
    Glide.with(context).load(uri)
        .into(this)
}

fun ImageView.loadLocalImage(filePath: String) {
    Glide.with(context).load(filePath)
        .into(this)
}

fun View.visible(isVisible: Boolean) {
    visibility = if (isVisible) View.VISIBLE else View.GONE
}

fun TextView.setColor(color: Int) {
    this.setTextColor(ContextCompat.getColor(this.context, color))
}


fun TextView.html(content: String) {
    this.text = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
        Html.fromHtml(content, Html.FROM_HTML_MODE_COMPACT)
    } else {
        Html.fromHtml(content)
    }
}

fun View.snackBar(message: String, actionButton: String, action: (() -> Unit)? = null) {
    val snackBar = Snackbar.make(this, message, Snackbar.LENGTH_LONG)
    action?.let {
        snackBar.setAction(actionButton) {
            it()
        }
    }
    snackBar.show()
}

fun View.snackBar(message: String, action: (() -> Unit)? = null) {
    val snackbar = Snackbar.make(this, message, Snackbar.LENGTH_LONG)
    action?.let {
        snackbar.setAction("Retry") {
            it()
        }
    }
    snackbar.show()
}


fun View.showKeyboard() {
    val imm = this.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0)
}

fun View.hideKeyboard() {
    val imm = this.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    imm.hideSoftInputFromWindow(windowToken, 0)
}

fun ImageView.blur(drawable: Int) {
    val image = BitmapFactory.decodeResource(
        resources,
        drawable
    )

    val width = (image.width * 0.4f).roundToInt()
    val height = (image.height * 0.4f).roundToInt()
    var inputBitmap = Bitmap.createScaledBitmap(image, width, height, true)
    try {
        inputBitmap = rgb565toArgb888(inputBitmap)
    } catch (e: Exception) {
        e.printStackTrace()
    }
    val outputBitmap = Bitmap.createBitmap(inputBitmap!!)
    val rs = RenderScript.create(this.context)
    val theIntrinsic = ScriptIntrinsicBlur.create(rs, Element.U8_4(rs))
    val tmpIn: Allocation = Allocation.createFromBitmap(rs, inputBitmap)
    val tmpOut: Allocation = Allocation.createFromBitmap(rs, outputBitmap)
    theIntrinsic.setRadius(15f)
    theIntrinsic.setInput(tmpIn)
    theIntrinsic.forEach(tmpOut)
    tmpOut.copyTo(outputBitmap)
    this.setImageBitmap(outputBitmap)
}

private fun rgb565toArgb888(img: Bitmap): Bitmap? {
    val numPixels = img.width * img.height
    val pixels = IntArray(numPixels)

    //Get JPEG pixels.  Each int is the color values for one pixel.
    img.getPixels(pixels, 0, img.width, 0, 0, img.width, img.height)

    //Create a Bitmap of the appropriate format.
    val result = Bitmap.createBitmap(img.width, img.height, Bitmap.Config.ARGB_8888)

    //Set RGB pixels.
    result.setPixels(pixels, 0, result.width, 0, 0, result.width, result.height)
    return result
}








