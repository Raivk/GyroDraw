package itescia.raivk.gyrodraw

/**
 * Created by Raivk on 07/11/2017.
 */

class Vector2{

    var x:Float
    var y:Float

    constructor() {
        x = 0f
        y = 0f
    }

    constructor(x: Float, y:Float){
        this.x = x
        this.y = y
    }

    override fun toString(): String {
        return "x : " + x + " | y : " + y
    }
}