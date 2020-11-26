## Kotilin-AStarAlgorithm - Autor: Cristofer López Cabañas

### Manual de usuario (Descargar archivo jar para probar)
    * Celda Verde: Salida
    * Celda Roja: Meta
    * Celda Fucsia: WayPoint
    * Celda Negra: Celda inalcanzable
    * Celda Marrón: Celda Peligrosa
    * Celdas azules: Camino que tiene que recorrer Mario

El objetivo del juego es sencillo, hay que ayudar a Mario.

Nuestro personaje a llegar a conseguir una vida extra para que tenga más opciones de rescatar a la princesa Peach.


Para ello deberá trazar el mejor camino desde la salida hasta la meta sin poder atravesar los muros que haya en el camino, recorriendo todos los waypoints e intentar evitar pasar por zonas peligrosas.

En la interfaz se dispone de los botones para marcar los diferentes tipos de casillas, estos botones irán cambiando el tipo de casilla seleccionada para marcar el tablero, también existe la posibilidad de borrar el resultado (solo el camino optimo) una vez se haya realizado la simulación para poder repetirla o se puede directamente desmarcar todo el tablero para volver a crear otra simulación, una vez marcadas las casillas deseadas por el usuario la simulación comenzará al presionar el botón de play. Además se dispone de una sección donde se pueden cambiar las dimensiones del tablero para realizar más simulaciones.

Solo puede seleccionarse una casilla de inicio y otra de fin, los waypoints se tomaran en el orden en el que el usuario los haya ido seleccionando para ser recorridos, las celdas prohibidas marcaran las paredes del mapa y las casillas peligrosas aumentarán el coste de pasar por ellas para que el algoritmo busque si hay un camino mejor.

Una vez calculada la mejor ruta, se colorearán las celdas seleccionadas para realizar la simulación de color azul y azul oscuro para distinguir entre los distintos segmentos entre waypoints que se deben recorrer, las casillas peligrosas que se hayan debido de atravesar se marcarán en un marrón un poco más oscuro.
