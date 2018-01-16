package itescia.raivk.gyrodraw

import android.app.Activity
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.Window
import android.view.WindowManager
import android.hardware.Sensor.TYPE_GYROSCOPE
import android.hardware.SensorEvent



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
}
