package es.ucm.servicio

import es.ucm.modelo.AStar
import es.ucm.modelo.Casilla

class ServicioAStar {

    /*
    * ini: casilla inicial
    * end: casilla destion
    * listaAbierta: lista de casillas que no hemos tratado aun
    * listaCerrada: lista de casillas que ya hemos tratado
    *
    * */
    fun resuelve(N: Int, M: Int, ini: Casilla, end: Casilla, matriz : List<List<Casilla>>): List<Casilla> {

        val x = AStar(N, M, matriz, ini, end)

        return x.getSolution(end)
    }
}