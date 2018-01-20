package itescia.raivk.gyrodraw

import android.Manifest
import android.app.Activity
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.Window
import android.view.WindowManager
import android.hardware.Sensor.TYPE_GYROSCOPE
import android.hardware.SensorEvent
import android.support.v4.app.ActivityCompat
import android.content.pm.PackageManager
import android.Manifest.permission
import android.Manifest.permission.WRITE_EXTERNAL_STORAGE





class Drawing : Activity() {

    lateinit var dv:DrawingView



    override fun onCreate(savedInstanceState: Bundle?) {
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        super.onCreate(savedInstanceState)
        //setContentView(R.layout.activity_drawing)

        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN)

        dv = DrawingView(this)

        setContentView(dv)
    }

    override fun onResume() {
        DrawingView.saved = false
        super.onResume()
    }

    override fun onRestart() {
        DrawingView.saved = false
        super.onRestart()
    }

    companion object
    {
        private val REQUEST_EXTERNAL_STORAGE = 1
        private val PERMISSIONS_STORAGE = arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE)

        fun verifyStoragePermissions(activity: Activity)
        {
            // Check if we have write permission
            val permission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE)

            if (permission != PackageManager.PERMISSION_GRANTED) {
                // We don't have permission so prompt the user
                ActivityCompat.requestPermissions(
                        activity,
                        PERMISSIONS_STORAGE,
                        REQUEST_EXTERNAL_STORAGE
                )
            }
        }
    }


}
