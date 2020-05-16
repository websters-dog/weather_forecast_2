package io.github.websters_dog.weather_forecast_2.location

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.util.Log
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.*
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.core.SingleEmitter


private fun runWithLocationPermissionCheck(
    emitter: SingleEmitter<Coords>,
    context: Context,
    action: (SingleEmitter<Coords>) -> Unit
) {
    val permission = ActivityCompat
        .checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION)
    if (permission == PackageManager.PERMISSION_GRANTED) {
        Log.d("[location]", ".runWithLocationPermissionCheck : have permission")
        action(emitter)
    } else {
        Log.d("[location]", ".runWithLocationPermissionCheck : no permission")
        emitter.onError(LocationPermissionException())
    }
}

private fun runWithLocationAvailabilityCheck(
    fusedLocationClient: FusedLocationProviderClient,
    emitter: SingleEmitter<Coords>,
    action: (SingleEmitter<Coords>) -> Unit
) {
    fusedLocationClient.locationAvailability.addOnCompleteListener {
        val result = it.result
        if (it.isSuccessful && result != null && result.isLocationAvailable) {
            Log.d("[location]", ".runWithLocationAvailabilityCheck : OnCompleteListener : available")
            action(emitter)
        } else {
            Log.d("[location]", ".runWithLocationAvailabilityCheck : OnCompleteListener : not available")
            emitter.onError(LocationIsNotAvailableException(it.exception))
        }
    }
}


class LastKnownLocationSource (
    private val context: Context
) : LocationSource {

    private val fusedLocationClient: FusedLocationProviderClient =
        LocationServices.getFusedLocationProviderClient(context)

    override fun getLocation(): Single<Coords> {
        return Single.create {
            runWithLocationPermissionCheck(it, context) {
                runWithLocationAvailabilityCheck(this.fusedLocationClient, it, ::emitLastKnownLocation)
            }
        }
    }

    private fun emitLastKnownLocation(emitter: SingleEmitter<Coords>) {
        fusedLocationClient.lastLocation.addOnCompleteListener {
            val result = it.result
            if (it.isSuccessful && result != null) {
                Log.d("[location]", "${javaClass.simpleName}.emitLastKnownLocation : OnCompleteListener : succces : $result")
                emitter.onSuccess(Coords(result.latitude.toFloat(), result.longitude.toFloat()))
            } else {
                Log.d("[location]", "${javaClass.simpleName}.emitLastKnownLocation : OnCompleteListener : failure")
                emitter.onError(LocationNotFoundException(it.exception))
            }
        }
    }
}


class GPSLocationSource (
    private val context: Context
) : LocationSource {

    private val fusedLocationClient: FusedLocationProviderClient =
        LocationServices.getFusedLocationProviderClient(context)

    private val locationCallbacks = mutableListOf<LocationCallback>()

    override fun getLocation(): Single<Coords> {
        return Single
            .create<Coords> {
                runWithLocationPermissionCheck(it, context, ::emitLocation)
            }.doOnDispose {
                Log.d("[location]", "${javaClass.simpleName}.getLocation : doOnDispose")
                locationCallbacks.forEach { fusedLocationClient.removeLocationUpdates(it)}
            }
    }

    private fun emitLocation(emitter: SingleEmitter<Coords>) {
        val locationCallback = object : LocationCallback() {
            override fun onLocationResult(r: LocationResult?) {
                if (r != null && r.lastLocation != null) {
                    Log.d("[location]", "${javaClass.simpleName}.emitLocation : onLocationResult : success : ${r.lastLocation}")
                    fusedLocationClient.removeLocationUpdates(this)
                    emitter.onSuccess(
                        Coords(
                            r.lastLocation.latitude.toFloat(),
                            r.lastLocation.longitude.toFloat())
                    )
                } else {
                    Log.d("[location]", "${javaClass.name}.emitLocation : onLocationResult : failure")
                }
            }

            override fun onLocationAvailability(a: LocationAvailability?) {
                if (a != null && !a.isLocationAvailable) {
                    Log.d("[location]", "${javaClass.simpleName}.emitLocation : onLocationAvailability : disabled")
                    fusedLocationClient.removeLocationUpdates(this)
                    emitter.onError(LocationIsNotAvailableException())
                } else {
                    Log.d("[location]", "${javaClass.simpleName}.emitLocation : onLocationAvailability : enabled")
                }
            }

        }

        val locationRequest = LocationRequest.create()?.apply {
            interval = 1000
            fastestInterval = 500
            priority = LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY
        }
        fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, null)
        locationCallbacks.add(locationCallback)
    }

}


class LocationNotFoundException(t: Throwable? = null) : Exception("Location not found", t)
class LocationIsNotAvailableException(t: Throwable? = null) : Exception("Location is not available", t)
class LocationPermissionException(t: Throwable? = null) : Exception("Permission required", t)