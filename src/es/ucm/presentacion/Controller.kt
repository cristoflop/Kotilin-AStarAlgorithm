package es.ucm.presentacion

import es.ucm.modelo.Casilla
import es.ucm.modelo.TipoCasilla
import es.ucm.servicio.ServicioAStar
import javax.swing.JButton
import kotlin.math.pow
import kotlin.math.sqrt

class Controller {

    private val servicioAStar = ServicioAStar()

    fun resuelve(N: Int, M: Int, iniCell: Casilla, endCell: Casilla, tablero: List<List<JButton>>,
                 wayPoints: List<Casilla>, mw: MainWindow) {

        val noPintar = mutableListOf<Casilla>()
        noPintar.add(iniCell)
        noPintar.addAll(wayPoints)

        val factorCorreccion = sqrt(N.toDouble().pow(2) + M.toDouble().pow(2)) * 0.1 // 10% de la diagonal del tablero
        val matriz = mutableListOf<List<Casilla>>()
        for(i in tablero.indices){
            val fila = mutableListOf<Casilla>()
            for(j in tablero[i].indices){
                val auxCasilla = Casilla(i, j)
                if(tablero[i][j].toolTipText == "planta"){ // las plantas son casillas inalcanzables
                    auxCasilla.tipo = TipoCasilla.Block
                }
                else if(tablero[i][j].toolTipText == "warning"){
                    auxCasilla.tipo = TipoCasilla.Warning

                    auxCasilla.peso = factorCorreccion
                    noPintar.add(auxCasilla)
                }
                fila.add(auxCasilla)
            }
            matriz.add(fila)
        }

        var casillaActual = iniCell
        var colorCamino = 1
        wayPoints.forEach(){esoNoGilipollas ->
            if(casillaActual != endCell){
                val camino : List<Casilla> = servicioAStar.resuelve(N, M, casillaActual, esoNoGilipollas, matriz)
                mw.pintaCamino(camino, noPintar, colorCamino)
                if(camino.isEmpty()) return

                colorCamino = if(colorCamino == 1) 0
                                else 1

                casillaActual = Casilla(esoNoGilipollas.x, esoNoGilipollas.y, iniCell.tipo)
            }
        }
    }

}