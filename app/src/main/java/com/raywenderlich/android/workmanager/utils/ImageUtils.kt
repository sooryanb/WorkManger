/*
 *
 *  * Copyright (c) 2020 Razeware LLC
 *  *
 *  * Permission is hereby granted, free of charge, to any person obtaining a copy
 *  * of this software and associated documentation files (the "Software"), to deal
 *  * in the Software without restriction, including without limitation the rights
 *  * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 *  * copies of the Software, and to permit persons to whom the Software is
 *  * furnished to do so, subject to the following conditions:
 *  *
 *  * The above copyright notice and this permission notice shall be included in
 *  * all copies or substantial portions of the Software.
 *  *
 *  * Notwithstanding the foregoing, you may not use, copy, modify, merge, publish,
 *  * distribute, sublicense, create a derivative work, and/or sell copies of the
 *  * Software in any work that is designed, intended, or marketed for pedagogical or
 *  * instructional purposes related to programming, coding, application development,
 *  * or information technology.  Permission for such use, copying, modification,
 *  * merger, publication, distribution, sublicensing, creation of derivative works,
 *  * or sale is expressly withheld.
 *  *
 *  * This project and source code may use libraries or frameworks that are
 *  * released under various Open-Source licenses. Use of those libraries and
 *  * frameworks are governed by their own individual licenses.
 *  *
 *  * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 *  * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 *  * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 *  * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 *  * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 *  * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 *  * THE SOFTWARE.
 *
 */

package com.raywenderlich.android.workmanager.utils

import android.content.Context
import android.content.ContextWrapper
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.OutputStream
import java.net.URL
import java.util.*

fun URL.toBitmap(): Bitmap? {
  return try {
    BitmapFactory.decodeStream(openStream())
  } catch (e: IOException) {
    null
  }
}

fun Bitmap.saveToInternalStorage(context: Context): Uri? {
  val wrapper = ContextWrapper(context)
  var file = wrapper.getDir("images", Context.MODE_PRIVATE)
  file = File(file, "${UUID.randomUUID()}.jpg")
  return try {
    val stream: OutputStream = FileOutputStream(file)
    compress(Bitmap.CompressFormat.JPEG, 100, stream)
    stream.flush()
    stream.close()
    Uri.parse(file.absolutePath)
  } catch (e: IOException) {
    e.printStackTrace()
    null
  }
}


fun Context.getUriFromUrl(): Uri? {
  // 1
  val imageUrl =
    URL(
      "https://images.pexels.com/photos/169573/pexels-photo-169573.jpeg" +
              "?auto=compress&cs=tinysrgb&dpr=2&h=650&w=940"
    )
  // 2
  val bitmap = imageUrl.toBitmap()

  // 3
  var savedUri: Uri? = null
  bitmap?.apply {
    savedUri = saveToInternalStorage(this@getUriFromUrl)
  }
  return savedUri
}
