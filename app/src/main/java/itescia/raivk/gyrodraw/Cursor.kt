package itescia.raivk.gyrodraw

import android.app.Activity
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.view.View
import android.widget.Toast
import java.text.FieldPosition
import java.util.*

/**
 * Created by Raivk on 07/11/2017.
 */

class Cursor{

    var position: Vector2
    var speed: Vector2
    var size: Float

    var paint: Paint

    init {
        position = Vector2()
        speed = Vector2()
        paint = Paint()
        size = 20f
    }

    fun CollisionHandling(view : DrawingView){
        if(position.x - size < 0 || position.x + size > view.width) {
            if(position.x - size < 0){
                position.x = size
            } else {
                position.x = view.width - size
            }

//            speed.x *= -1
        }

        if(position.y - size < 0 || position.y + size > view.height){
            if(position.y - size < 0){
                position.y = size
            } else {
                position.y = view.height - size
            }

//            speed.y *= -1
        }

    }

    fun Draw(canvas: Canvas){
        canvas.drawCircle(position.x, position.y, size, paint)
    }

}