package itescia.raivk.gyrodraw

import android.app.Activity
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
import android.content.ContextWrapper
import android.graphics.*
import android.hardware.SensorManager
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.view.ScaleGestureDetector
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.sql.Time
import java.util.*


/**
 * Created by Raivk on 07/11/2017.
 */

class DrawingView(context : Context) : View(context), View.OnClickListener, View.OnTouchListener{


    companion object
    {
        var saved = false
    }

    private var mScaleFactor = 1f
    private lateinit var mScaleDetector : ScaleGestureDetector

    var cursor:Cursor
    var accelerometer:Sensor
    var magnetic:Sensor

    var sensorManager = context.getSystemService(SENSOR_SERVICE) as SensorManager

    var accelerometerSensorListener : SensorEventListener
    var magneticSensorListener : SensorEventListener

    var paths:ArrayList<CursorPath>

    var drawing = false

    init {
        mScaleDetector =  ScaleGestureDetector(context, ScaleListener())
        cursor = Cursor()
        cursor.position = Vector2(50f, 50f)
        cursor.speed = Vector2(5f, 7f)

        paths = ArrayList<CursorPath>()

        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        magnetic = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD)

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

        // Create a listener
        magneticSensorListener = object : SensorEventListener {
            override fun onSensorChanged(event: SensorEvent) {
                // Écouter le changement du gyroscope:
                if (event.sensor.type == Sensor.TYPE_MAGNETIC_FIELD) {
                    if(paths.size > 0 && drawing){
                        var color = event.values[1] + 30
                        if(color <= 15){
                            paths[paths.size - 1].paint.color = Color.BLACK
                            cursor.paint.color = Color.BLACK
                        } else if(color <= 30){
                            paths[paths.size - 1].paint.color = Color.RED
                            cursor.paint.color = Color.RED
                        } else if(color <= 45){
                            paths[paths.size - 1].paint.color = Color.BLUE
                            cursor.paint.color = Color.BLUE
                        } else {
                            paths[paths.size - 1].paint.color = Color.GREEN
                            cursor.paint.color = Color.GREEN
                        }
                    }
                }
            }

            override fun onAccuracyChanged(sensor: Sensor, i: Int) {}
        }

        sensorManager.registerListener(accelerometerSensorListener,
                accelerometer, SensorManager.SENSOR_DELAY_NORMAL)

        sensorManager.registerListener(magneticSensorListener,
                magnetic, SensorManager.SENSOR_DELAY_NORMAL)

        this.setOnTouchListener(this)
        this.setOnClickListener(this)
    }

    override fun onDraw(canvas: Canvas){
        cursor.Draw(canvas)
        move()
        for(cp:CursorPath in paths){
            cp.Draw(canvas)
        }
    }


    private fun getBitmap(): Bitmap
    {
        this.isDrawingCacheEnabled = true
        this.buildDrawingCache()
        var bitmap = Bitmap.createBitmap(this.drawingCache)
        this.isDrawingCacheEnabled = false

        return bitmap
    }

    private fun saveBitmap()
    {

        var random = Random().nextInt()
        var imageName = "GyroDraw" + random.toString() + "Image.jpg"
        var file = File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), imageName)


        var outPutStream : FileOutputStream? = null

        try
        {
            outPutStream = FileOutputStream(file)
            getBitmap().compress(Bitmap.CompressFormat.JPEG, 100, outPutStream)

        }
        catch (e :Exception)
        {
            Log.d("Error:", e.toString())
        }
        finally
        {
            try
            {
                outPutStream?.close()
            }
            catch (e: Exception)
            {
                Log.d("Error:", e.toString())
            }

        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT)
        {
            val scanIntent = Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE)
            val contentUri = Uri.fromFile(file)
            scanIntent.setData(contentUri)
            this.context.sendBroadcast(scanIntent)
        }
        else
        {
            var intent = Intent(Intent.ACTION_MEDIA_MOUNTED, Uri.parse("file://" + Environment.getExternalStorageDirectory()))
            this.context.sendBroadcast(intent)
        }

        var intent = Intent();
        intent.setAction(android.content.Intent.ACTION_VIEW);
        intent.setType("image/*");
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        this.context.startActivity(intent);
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


    override fun onTouchEvent(event: MotionEvent?): Boolean {
        if(event?.pointerCount == 3 && !saved)
        {
            saved = true
            Drawing.verifyStoragePermissions(this.context as Activity)
            saveBitmap()
        }
        return super.onTouchEvent(event)
    }

    override fun onClick(v: View?)
    {
            if (drawing) {
                drawing = false
            } else {
                drawing = true
                paths.add(CursorPath(mScaleFactor))
                paths[paths.size - 1].path.moveTo(cursor.position.x, cursor.position.y)
            }
    }



    override fun onTouch(p0: View?, p1: MotionEvent?): Boolean
    {

            mScaleDetector.onTouchEvent((p1))

        return false
    }


    private inner class ScaleListener : ScaleGestureDetector.SimpleOnScaleGestureListener()
    {
        override fun onScale(detector: ScaleGestureDetector): Boolean {
            mScaleFactor *= detector.scaleFactor

            // Don't let the object get too small or too large.
            mScaleFactor = Math.max(20f, Math.min(mScaleFactor, 100f))
            cursor.size = mScaleFactor
            //CursorPath.size = mScaleFactor
            invalidate()
            return true
        }
    }
}