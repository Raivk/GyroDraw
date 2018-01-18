package itescia.raivk.gyrodraw

import android.app.Activity
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.view.View
import android.widget.Toast
import java.text.FieldPosition
import java.util.*

/**
 * Created by Raivk on 07/11/2017.
 */

class CursorPath{

    //PATH
    var paint:Paint
    var path: Path
    var size: Float = 10f

    constructor(size: Float)
    {
        this.size = size
    }


    init {
        paint = Paint()
        paint.style = Paint.Style.STROKE
        paint.color = Color.BLACK
        paint.strokeWidth = size
        path = Path()
    }

    fun Move(position: Vector2){
        path.lineTo(position.x, position.y)
    }

    fun Draw(canvas: Canvas){
        paint.strokeWidth = size
        canvas.drawPath(path, paint)
    }

}