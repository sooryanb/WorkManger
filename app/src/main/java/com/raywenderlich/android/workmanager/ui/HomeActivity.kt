/*
 * Copyright (c) 2020 Razeware LLC
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * Notwithstanding the foregoing, you may not use, copy, modify, merge, publish,
 * distribute, sublicense, create a derivative work, and/or sell copies of the
 * Software in any work that is designed, intended, or marketed for pedagogical or
 * instructional purposes related to programming, coding, application development,
 * or information technology.  Permission for such use, copying, modification,
 * merger, publication, distribution, sublicensing, creation of derivative works,
 * or sale is expressly withheld.
 *
 * This project and source code may use libraries or frameworks that are
 * released under various Open-Source licenses. Use of those libraries and
 * frameworks are governed by their own individual licenses.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package com.raywenderlich.android.workmanager.ui

import android.Manifest
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.net.toUri
import androidx.work.*
import com.raywenderlich.android.workmanager.R
import com.raywenderlich.android.workmanager.databinding.ActivityHomeBinding
import com.raywenderlich.android.workmanager.workers.ImageDownloadWorker
import java.util.*

class HomeActivity : AppCompatActivity() {

  private val workManager by lazy {
    WorkManager.getInstance(applicationContext)
  }

  private val constraints = Constraints.Builder()
    .setRequiredNetworkType(NetworkType.CONNECTED)
    .setRequiresStorageNotLow(true)
    .setRequiresBatteryNotLow(true)
    .build()


  private lateinit var activityHomeBinding: ActivityHomeBinding


  override fun onCreate(savedInstanceState: Bundle?) {
    setTheme(R.style.AppTheme)
    super.onCreate(savedInstanceState)
    activityHomeBinding = ActivityHomeBinding.inflate(layoutInflater)
    setContentView(activityHomeBinding.root)
    activityHomeBinding.tvWorkInfo.visibility = View.GONE

    requestStoragePermissions()

    activityHomeBinding.btnImageDownload.setOnClickListener {
      showLottieAnimation()
      activityHomeBinding.downloadLayout.visibility = View.GONE
      createOneTimeWorkRequest()
    }


  }

  private fun createOneTimeWorkRequest() {
    // 1
    val imageWorker = OneTimeWorkRequestBuilder<ImageDownloadWorker>()
      .setConstraints(constraints)
      .addTag("imageWork")
      .build()
    // 2
    workManager.enqueueUniqueWork(
      "oneTimeImageDownload",
      ExistingWorkPolicy.KEEP,
      imageWorker
    )
    observeWork(imageWorker.id)
  }

  private fun observeWork(id: UUID) {
    // 1
    workManager.getWorkInfoByIdLiveData(id)
      .observe(this, { info ->
        // 2
        if (info != null && info.state.isFinished) {
          hideLottieAnimation()
          activityHomeBinding.downloadLayout.visibility = View.VISIBLE
          // 3
          val uriResult = info.outputData.getString("IMAGE_URI")
          if (uriResult != null) {
            showDownloadedImage(uriResult.toUri())
          }
        }
      })
  }



  private fun requestStoragePermissions() {
    requestMultiplePermissions.launch(
      arrayOf(
        Manifest.permission.READ_EXTERNAL_STORAGE,
        Manifest.permission.WRITE_EXTERNAL_STORAGE
      )
    )
  }

  private val requestMultiplePermissions =
    registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) {
    }

  private fun showLottieAnimation() {
    activityHomeBinding.animationView.visibility = View.VISIBLE
    activityHomeBinding.animationView.playAnimation()

  }

  private fun hideLottieAnimation() {
    activityHomeBinding.animationView.visibility = View.GONE
    activityHomeBinding.animationView.cancelAnimation()

  }

  private fun showDownloadedImage(resultUri: Uri?) {
    activityHomeBinding.completeLayout.visibility = View.VISIBLE
    activityHomeBinding.downloadLayout.visibility = View.GONE
    hideLottieAnimation()
    activityHomeBinding.imgDownloaded.setImageURI(resultUri)
  }
}
