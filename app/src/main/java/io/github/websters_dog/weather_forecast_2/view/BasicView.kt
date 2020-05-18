package io.github.websters_dog.weather_forecast_2.view

import android.Manifest
import android.content.Intent
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import io.github.websters_dog.weather_forecast_2.R


private const val LOCATION_PERMISSION_REQUEST = 9001


interface BasicView<T : BasicView.Presenter> {
    var presenter: T
    fun setInProgress(inProgress: Boolean)
    fun requestLocationPermission()
    fun showShareActivity(textRes: Int, vararg formatArgs: Any)
    fun showMessage(textRes: Int)

    interface Presenter {
        fun stopWork()
        fun starWork()
        fun onPermissionRequestResult(grantResults: IntArray)
    }
}


abstract class AbstractBasicViewFragment<T : BasicView.Presenter>(layoutId: Int) : Fragment(layoutId), BasicView<T> {

    override lateinit var presenter: T

    override fun onResume() {
        super.onResume()
        presenter.starWork()
    }

    override fun onPause() {
        super.onPause()
        presenter.stopWork()
    }

    override fun requestLocationPermission() {
        activity?.let {
            val needExplanation = ActivityCompat
                .shouldShowRequestPermissionRationale(it, Manifest.permission.ACCESS_COARSE_LOCATION)

            if (needExplanation) {
                AlertDialog.Builder(it)
                    .setTitle(R.string.location_permission_explanation_title)
                    .setMessage(R.string.location_permission_explanation_message)
                    .setNeutralButton(android.R.string.ok) { _, _ -> /* do nothing */ }
                    .setOnDismissListener() { _ -> makeLocationPermissionRequest() }
                    .create()
                    .show()
            } else {
                makeLocationPermissionRequest()
            }
        }
    }

    private fun makeLocationPermissionRequest() {
        activity?.let {
            ActivityCompat.requestPermissions(
                it,
                arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION),
                LOCATION_PERMISSION_REQUEST
            )
        }
    }

    override fun showShareActivity(textRes: Int, vararg formatArgs: Any) {
        val intent = Intent().apply {
            action = Intent.ACTION_SEND
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, getString(textRes, *formatArgs))
        }
        startActivity(intent)
    }

    override fun showMessage(textRes: Int) {
        //todo replace with shackbar
        Toast.makeText(context, textRes, Toast.LENGTH_LONG).show()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            LOCATION_PERMISSION_REQUEST -> {
                presenter.onPermissionRequestResult(grantResults)
            }
        }
    }
}