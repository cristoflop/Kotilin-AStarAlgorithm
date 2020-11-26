package es.ucm.modelo

class Casilla(var x : Int,
              var y : Int,
              var tipo : TipoCasilla = TipoCasilla.Free,
              var peso: Double = 0.0,
              var from: Casilla? = null) : Comparable<Casilla> {

    override fun equals(other: Any?): Boolean {
        if(this === other) return true
        if(javaClass != other?.javaClass) return false

        other as Casilla

        if(x == other.x && y == other.y) return true
        return false
    }

    override fun toString(): String {
        return "{$x, $y}"
    }

    override fun hashCode(): Int {
        var result = x
        result = 31 * result + y
        return result
    }

    override fun compareTo(other: Casilla): Int {
        return 0
    }

}