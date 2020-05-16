package io.github.websters_dog.weather_forecast_2

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import io.github.websters_dog.weather_forecast_2.dummy.DummyContent
import io.github.websters_dog.weather_forecast_2.location.LocationIsNotAvailableException
import io.github.websters_dog.weather_forecast_2.location.LocationPermissionException
import io.github.websters_dog.weather_forecast_2.location.LocationRepository
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.kotlin.addTo
import kotlinx.android.synthetic.main.activity_item_list.*
import kotlinx.android.synthetic.main.item_list.*
import kotlinx.android.synthetic.main.item_list_content.view.*

/**
 * An activity representing a list of Pings. This activity
 * has different presentations for handset and tablet-size devices. On
 * handsets, the activity presents a list of items, which when touched,
 * lead to a [ItemDetailActivity] representing
 * item details. On tablets, the activity presents the list of items and
 * item details side-by-side using two vertical panes.
 */

private const val LOCATION_PERMISSION_REQUEST = 9001

class ItemListActivity : AppCompatActivity() {

    /**
     * Whether or not the activity is in two-pane mode, i.e. running on a tablet
     * device.
     */
    private var twoPane: Boolean = false

    private val compositeDisposable = CompositeDisposable()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_item_list)

        setSupportActionBar(toolbar)
        toolbar.title = title

        fab.setOnClickListener { view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show()
        }

        if (item_detail_container != null) {
            // The detail container view will be present only in the
            // large-screen layouts (res/values-w900dp).
            // If this view is present, then the
            // activity should be in two-pane mode.
            twoPane = true
        }

        setupRecyclerView(item_list)

        retrieveLocation()
    }

    override fun onDestroy() {
        compositeDisposable.clear()
        super.onDestroy()
    }

    //todo move
    private fun retrieveLocation() {
        val lr = LocationRepository.getInstance(this)
        lr.invalidateCache()
        lr.getLocation()
            .subscribe { coords, throwable ->
                when (throwable) {
                    null -> {
                        Log.d("[location]", "${javaClass.simpleName}.retrieveLocation : subscribe : $coords")
                        Toast.makeText(this, coords.toString(), Toast.LENGTH_LONG).show()
                    }
                    is LocationPermissionException -> {
                        Log.e("[location]", throwable.toString())
                        Toast.makeText(this, throwable.toString(), Toast.LENGTH_LONG).show()

                        checkLocationPermission()
                    }
                    is LocationIsNotAvailableException -> {
                        Log.e("[location]", throwable.toString())
                        Toast.makeText(this, throwable.toString(), Toast.LENGTH_LONG).show()
                    }
                }
            }
            .addTo(compositeDisposable)
    }

    private fun checkLocationPermission(): Boolean {
        // Should we show an explanation?
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_COARSE_LOCATION)) {
            AlertDialog.Builder(this)
                .setTitle(R.string.location_permission_explanation_title)
                .setMessage(R.string.location_permission_explanation_message)
                .setPositiveButton(android.R.string.ok) { _, _ ->
                    makeLocationPermissionRequest()
                }
                .create()
                .show()
        } else {
            makeLocationPermissionRequest()
        }
        return false
    }

    private fun makeLocationPermissionRequest() {
        ActivityCompat.requestPermissions(
            this, arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION),
            LOCATION_PERMISSION_REQUEST)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            LOCATION_PERMISSION_REQUEST -> {
                if (grantResults.isNotEmpty()
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED
                ) {
                    retrieveLocation()
                } else {
                    //todo show warning or something else
                }
                return
            }
        }
    }
    private fun setupRecyclerView(recyclerView: RecyclerView) {
        recyclerView.adapter = SimpleItemRecyclerViewAdapter(this, DummyContent.ITEMS, twoPane)
    }

    class SimpleItemRecyclerViewAdapter(private val parentActivity: ItemListActivity,
                                        private val values: List<DummyContent.DummyItem>,
                                        private val twoPane: Boolean) :
            RecyclerView.Adapter<SimpleItemRecyclerViewAdapter.ViewHolder>() {

        private val onClickListener: View.OnClickListener

        init {
            onClickListener = View.OnClickListener { v ->
                val item = v.tag as DummyContent.DummyItem
                if (twoPane) {
                    val fragment = ItemDetailFragment().apply {
                        arguments = Bundle().apply {
                            putString(ItemDetailFragment.ARG_ITEM_ID, item.id)
                        }
                    }
                    parentActivity.supportFragmentManager
                            .beginTransaction()
                            .replace(R.id.item_detail_container, fragment)
                            .commit()
                } else {
                    val intent = Intent(v.context, ItemDetailActivity::class.java).apply {
                        putExtra(ItemDetailFragment.ARG_ITEM_ID, item.id)
                    }
                    v.context.startActivity(intent)
                }
            }
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_list_content, parent, false)
            return ViewHolder(view)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val item = values[position]
            holder.idView.text = item.id
            holder.contentView.text = item.content

            with(holder.itemView) {
                tag = item
                setOnClickListener(onClickListener)
            }
        }

        override fun getItemCount() = values.size

        inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
            val idView: TextView = view.id_text
            val contentView: TextView = view.content
        }
    }
}
