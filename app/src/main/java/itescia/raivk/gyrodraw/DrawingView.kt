package itescia.raivk.gyrodraw

import android.content.Context
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.media.AudioManager
import android.media.MediaPlayer
import android.media.SoundPool
import android.os.Vibrator
import android.util.Log
import android.view.DragEvent
import android.view.MotionEvent
import android.view.View
import android.widget.TextView
import org.w3c.dom.Text
import android.content.ContentValues.TAG
import android.content.Intent
import android.content.BroadcastReceiver
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.content.Context.SENSOR_SERVICE
import android.graphics.*
import android.hardware.SensorManager




/**
 * Created by Raivk on 07/11/2017.
 */

class DrawingView(context : Context) : View(context), View.OnClickListener{

    var cursor:Cursor

    var accelerometer:Sensor

    var sensorManager = context.getSystemService(SENSOR_SERVICE) as SensorManager

    var accelerometerSensorListener : SensorEventListener

    var paths:ArrayList<CursorPath>

    var drawing = false

    init {
        cursor = Cursor()
        cursor.position = Vector2(50f, 50f)
        cursor.speed = Vector2(5f, 7f)

        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)

        // Create a listener
        accelerometerSensorListener = object : SensorEventListener {
            override fun onSensorChanged(event: SensorEvent) {
                // Écouter le changement du gyroscope:
                if (event.sensor.type == Sensor.TYPE_ACCELEROMETER) {
                    // La vitesse angulaire autour de chaque axe
                    cursor.speed.x = -event.values[0] * 2 //X
                    cursor.speed.y = event.values[1] * 2 //Y
                }
            }

            override fun onAccuracyChanged(sensor: Sensor, i: Int) {}
        }

        sensorManager.registerListener(accelerometerSensorListener,
                accelerometer, SensorManager.SENSOR_DELAY_NORMAL)

        paths = ArrayList<CursorPath>()

        this.setOnClickListener(this)
    }

    override fun onDraw(canvas: Canvas){

        cursor.Draw(canvas)
        move()
        for(cp:CursorPath in paths){
            cp.Draw(canvas)
        }
    }

    private fun move(){
        cursor.CollisionHandling(this)
        cursor.position.x += cursor.speed.x
        cursor.position.y += cursor.speed.y
        invalidate()

        if(paths.size > 0 && drawing){
            paths[paths.size - 1].Move(cursor.position)
        }
    }

    override fun onClick(v: View?) {
        if(drawing){
            drawing = false
        } else {
            drawing = true
            paths.add(CursorPath())
            paths[paths.size - 1].path.moveTo(cursor.position.x, cursor.position.y)
        }
    }
}