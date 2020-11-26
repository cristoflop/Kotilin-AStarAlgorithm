package es.ucm.modelo

import java.util.*
import kotlin.math.sqrt


class AStar(N: Int, M: Int, var G: List<List<Casilla>>, private val s: Casilla, private val d: Casilla) {

    private val direcciones: Array<Direccion> = arrayOf(
            Direccion(-1, 0), // up
            Direccion(1, 0), // down
            Direccion(0, -1), // left
            Direccion(0, 1), // right
            Direccion(-1, -1), // upleft
            Direccion(-1, 1), // upright
            Direccion(1, -1), // downleft
            Direccion(1, 1) // downright
    )

    private val recto = 1.0
    private val diagonal = sqrt(2.0)

    private var distTo = Array(N) { DoubleArray(M) }

    private var pq: PriorityQueue<Casilla> = PriorityQueue()

    init{
        for(i in G.indices){ // inicializar disTo a infinito
            for(j in G[i].indices){
                distTo[i][j] = Double.MAX_VALUE
            }
        }
        distTo[s.x][s.y] = 0.0
        pq.add(s)

        while(!pq.isEmpty()){
            val origen = pq.poll()
            for(d in direcciones){
                val destino = Casilla(origen.x + d.x, origen.y + d.y)
                if(esCasillaValida(destino) && G[destino.x][destino.y].tipo != TipoCasilla.Block){
                    relax(origen, destino, distEnDir(d))
                }
            }
        }
    }

    private fun relax(from: Casilla, to: Casilla, weight: Double) {
        if (distTo[to.x][to.y] > distTo[from.x][from.y] + G[to.x][to.y].peso + weight) {
            distTo[to.x][to.y] = distTo[from.x][from.y] + G[to.x][to.y].peso + weight
            G[to.x][to.y].from = from
            if(!pq.contains(to)){
                pq.add(to)
            }
        }
    }

    private fun distEnDir(d: Direccion): Double {
        if(d.x != 0 && d.y != 0) {
            return diagonal
        }
        return recto
    }

    private fun esCasillaValida(casilla: Casilla): Boolean {
        if(casilla.x >= G.size || casilla.x < 0 || casilla.y >= G[0].size || casilla.y < 0){
            return false
        }
        return true
    }


    fun getSolution(d: Casilla): MutableList<Casilla>{
        var result = mutableListOf<Casilla>()
        if(G[d.x][d.y].from != null && distTo[d.x][d.y] != Double.MAX_VALUE){
            if(d != s) {
                result = getSolution(G[d.x][d.y].from!!)
                result.add(G[d.x][d.y].from!!)
            }
        }
        return result
    }

}