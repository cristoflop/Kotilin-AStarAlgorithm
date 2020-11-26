package es.ucm.presentacion

import es.ucm.modelo.Casilla
import es.ucm.modelo.TipoCasilla
import java.awt.*
import java.awt.Color
import java.awt.event.ActionListener
import java.awt.event.MouseAdapter
import java.awt.event.MouseEvent
import javax.swing.*
import javax.swing.border.Border


class MainWindow(private var N: Int,
                 private var M: Int,
                 private val controller: Controller) : JFrame() {

    private val filasTf = JTextField()
    private val columnasTf = JTextField()

    private val dimensionBt = JButton("Set")

    private val classLoader = Thread.currentThread().contextClassLoader

    private val playBt = JButton(ImageIcon(classLoader.getResource("resources/jugar.png"))) // la ruta base es 'src/(.......)'
    private val resetBt = JButton(ImageIcon(classLoader.getResource("resources/reajustar.png")))
    private val softResetBt = JButton(ImageIcon(classLoader.getResource("resources/reset.png")))
    private val gameButtons = listOf(playBt, resetBt, softResetBt, dimensionBt)

    private val blockBt = JButton(ImageIcon(classLoader.getResource("resources/planta-carnivora_white.png")))
    private val startBt = JButton(ImageIcon(classLoader.getResource("resources/super-mario_white.png")))
    private val endBt = JButton(ImageIcon(classLoader.getResource("resources/seta_white.png")))
    private val wayPointBt = JButton(ImageIcon(classLoader.getResource("resources/monedas.png")))
    private val warningBt = JButton(ImageIcon(classLoader.getResource("resources/warning.png")))
    private val buttons = listOf(blockBt, startBt, endBt, wayPointBt, warningBt)

    private val wayPoints = mutableListOf<Casilla>()

    private val violin = Color(153, 0, 77)
    private val warning = Color(255, 153, 51)
    private val warningThrough = Color(255, 95, 0)
    private val caminoA = Color(0, 191, 255)
    private val caminoB = Color(51, 102, 255)

    private var selectedBt: String = "start" // por defecto marcamos la salida
    private var selectedBtLb = JLabel("INICIO")

    private val tablero: MutableList<List<JButton>> = mutableListOf()
    private val iniCell: Casilla = Casilla(-1, -1)
    private val endCell: Casilla = Casilla(-1, -1)

    private var panelCentral = panelCentral(N, M)

    private var t: Thread? = null

    init {
        this.title = "Algoritmo A*"

        playBt.toolTipText = "play"
        resetBt.toolTipText = "reset"
        softResetBt.toolTipText = "soft reset"
        endBt.toolTipText = "end"
        startBt.toolTipText = "start"
        blockBt.toolTipText = "block"
        wayPointBt.toolTipText = "wp"
        warningBt.toolTipText = "warning"
        dimensionBt.toolTipText = "cambia el tamaño"

        this.layout = BorderLayout()
        this.add(panelCentral, BorderLayout.CENTER)

        this.addActionListenerToButtons()

        this.softResetBt.addActionListener {
            softReset()
        }

        this.resetBt.addActionListener {
            reset()
        }

        this.playBt.addActionListener {
            play(this)
        }

        // formato a los botones
        this.daTamALosBotones()

        val separador1 = JSeparator(JSeparator.VERTICAL)
        separador1.preferredSize = Dimension(50, 0)
        separador1.maximumSize = Dimension(50, 0)

        val toolbar = JPanel()
        toolbar.layout = BoxLayout(toolbar, BoxLayout.X_AXIS)
        toolbar.border = BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.black), "Casillas disponibles")
        toolbar.add(JLabel(" Inicio: "))
        toolbar.add(startBt)
        toolbar.add(JLabel(" WayPoint: "))
        toolbar.add(wayPointBt)
        toolbar.add(JLabel(" Final: "))
        toolbar.add(endBt)
        toolbar.add(JLabel(" Block: "))
        toolbar.add(blockBt)
        toolbar.add(JLabel(" Peligro: "))
        toolbar.add(warningBt)
        toolbar.add(separador1)
        toolbar.add(JLabel("Play: "))
        toolbar.add(playBt)
        toolbar.add(JLabel(" Reset: "))
        toolbar.add(resetBt)
        toolbar.add(JLabel(" Soft Reset: "))
        toolbar.add(softResetBt)
        this.add(toolbar, BorderLayout.NORTH)

        dimensionBt.addActionListener(ActionListener {
            redimensiona()
        })

        columnasTf.maximumSize = Dimension(40, 30)
        filasTf.maximumSize = Dimension(40, 30)

        val separador2 = JSeparator(JSeparator.VERTICAL)
        separador2.preferredSize = Dimension(20, 0)
        separador2.maximumSize = Dimension(20, 0)

        val navbar = JPanel()
        navbar.background = Color.white
        navbar.layout = BoxLayout(navbar, BoxLayout.X_AXIS)
        navbar.border = BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.black), "Selecciona el tamaño")
        navbar.add(JLabel("  Filas "))
        navbar.add(filasTf)
        navbar.add(JLabel("  Columnas "))
        navbar.add(columnasTf)
        navbar.add(dimensionBt)
        navbar.add(separador2)
        navbar.add(JLabel("Casilla seleccionada actualmente: "))
        navbar.add(selectedBtLb)
        this.add(navbar, BorderLayout.SOUTH)

        this.defaultCloseOperation = EXIT_ON_CLOSE
        this.isResizable = false
        this.pack()
        setLocationRelativeTo(null)
    }

    private fun panelCentral(N: Int, M: Int): JPanel {

        tablero.clear()

        val layout = GridLayout(N, M)
        layout.hgap = 3
        layout.vgap = 3

        val panelCentral = JPanel(layout)
        panelCentral.maximumSize = Dimension(500, 600)
        panelCentral.preferredSize = Dimension(500, 600)
        panelCentral.border = BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.black), "Marca casillas")

        // de 0 a N-1
        for (i in 0 until N) {
            val auxList = mutableListOf<JButton>()
            for (j in 0 until M) {
                val auxLabel = JButton()



                auxLabel.toolTipText = "vacio"
                auxLabel.background = Color.white
                auxLabel.border = RoundedBorder(8)

                auxLabel.addMouseListener(object : MouseAdapter() {
                    override fun mouseClicked(e: MouseEvent) {
                        when (selectedBt) {
                            "start" -> {
                                if (!esIni(i, j) && !esFin(i, j) && auxLabel.toolTipText == "vacio") { // click en celda distinta de inicio o final y vacia
                                    if (iniCell.x != -1 && iniCell.y != -1) {
                                        tablero[iniCell.x][iniCell.y].toolTipText = "vacio"
                                        tablero[iniCell.x][iniCell.y].background = Color.white
                                    }
                                    iniCell.x = i
                                    iniCell.y = j
                                    auxLabel.toolTipText = "mario"
                                    auxLabel.background = Color.GREEN
                                }
                            }
                            "end" -> {
                                if (!esIni(i, j) && !esFin(i, j) && auxLabel.toolTipText == "vacio") { // click en celda distinta de inicio o final y vacia
                                    if (endCell.x != -1 && endCell.y != -1) {
                                        tablero[endCell.x][endCell.y].toolTipText = "vacio"
                                        tablero[endCell.x][endCell.y].background = Color.white
                                    }
                                    endCell.x = i
                                    endCell.y = j
                                    auxLabel.toolTipText = "seta"
                                    auxLabel.background = Color.RED
                                }
                            }
                            "block" -> {
                                if (!esIni(i, j) && !esFin(i, j)) { // click en celda distinta de inicio o final
                                    if (auxLabel.background == Color.black) {
                                        auxLabel.toolTipText = "vacio"
                                        auxLabel.background = Color.white
                                    } else if (auxLabel.toolTipText == "vacio") {
                                        auxLabel.toolTipText = "planta"
                                        auxLabel.background = Color.black
                                    }
                                }
                            }
                            "wp" -> {
                                if (!esIni(i, j) && !esFin(i, j)) { // click en celda distinta de inicio o final
                                    if (auxLabel.background == violin) {
                                        auxLabel.toolTipText = "vacio"
                                        auxLabel.background = Color.white
                                        wayPoints.remove(Casilla(i, j))
                                    } else if (auxLabel.toolTipText == "vacio") {
                                        auxLabel.toolTipText = "wp"
                                        auxLabel.background = violin
                                        wayPoints.add(Casilla(i, j, TipoCasilla.Waypoint))
                                    }
                                }
                            }
                            "warning" -> {
                                if (!esIni(i, j) && !esFin(i, j)) { // click en celda distinta de inicio o final
                                    if (auxLabel.background == warning || auxLabel.background == warningThrough) {
                                        auxLabel.toolTipText = "vacio"
                                        auxLabel.background = Color.white
                                    } else if (auxLabel.toolTipText == "vacio") {
                                        auxLabel.toolTipText = "warning"
                                        auxLabel.background = warning
                                    }
                                }
                            }
                        }
                    }
                })
                auxList.add(auxLabel)
                panelCentral.add(auxLabel)
            }
            tablero.add(auxList)
        }
        return panelCentral
    }

    fun pintaCamino(camino: List<Casilla>, noPintar: List<Casilla>, color: Int) {
        // JOptionPane.showMessageDialog(null, "Hemos jugado")
        if (camino.isEmpty()) {
            muestraMensajeError("Meta inalcanzable")
        } else {
            val colorCamino = if (color == 1) {
                caminoA
            } else {
                caminoB
            }
            camino.forEach() { casilla ->
                val auxCasilla = noPintar.indexOf(casilla)
                if (auxCasilla == -1) {
                    tablero[casilla.x][casilla.y].background = colorCamino
                    Thread.sleep(30)
                }
                else{
                    if(noPintar[auxCasilla].tipo == TipoCasilla.Warning){
                        tablero[casilla.x][casilla.y].background = warningThrough
                        Thread.sleep(30)
                    }
                }
            }
        }
    }

    // ponemos el action listener de los botones que marcan las casillas
    private fun addActionListenerToButtons() {
        for (button in this.buttons) {
            button.addActionListener {
                this.selectedBt = button.toolTipText
                this.selectedBtLb.text = when(selectedBt) {
                    "start" -> "INICIO"
                    "end" -> "FINAL"
                    "wp" -> "WAYPOINT"
                    "block" -> "PROHIBIDO"
                    "warning" -> "PELIGRO"
                    else -> selectedBt
                }
            }
        }
    }

    private fun daTamALosBotones() {
        gameButtons.forEach {
            if (it.toolTipText != "cambia el tamaño") {
                it.maximumSize = Dimension(60, 65)
                it.preferredSize = Dimension(60, 65)
                it.background = Color.white
            }
            it.isFocusPainted = false
        }

        for (button in this.buttons) {
            button.maximumSize = Dimension(60, 65)
            button.preferredSize = Dimension(60, 65)
            button.background = Color.white
            button.isFocusPainted = false
        }
    }

    private fun softReset() {
        tablero.forEach() {
            it.forEach() { it2 ->
                if (it2.background == caminoA || it2.background == caminoB) {
                    it2.background = Color.white
                }
                else if(it2.background == warningThrough){
                    it2.background = warning
                }
            }
        }
    }

    private fun reset() {
        tablero.forEach() {
            it.forEach() { it2 ->
                it2.background = Color.white
                it2.toolTipText = "vacio"
            }
        }
        iniCell.x = -1
        iniCell.y = -1

        endCell.x = -1
        endCell.y = -1

        wayPoints.clear()
    }

    private fun esIni(x: Int, y: Int): Boolean {
        return x == iniCell.x && y == iniCell.y
    }

    private fun esFin(x: Int, y: Int): Boolean {
        return x == endCell.x && y == endCell.y
    }

    private fun play(mw: MainWindow) {
        if (t == null) { /// MUY IMPORTANTE PARA EVITAR LA CREACION DE MULTI-HEBRAS
            t = object : Thread() {
                override fun run() {

                    if (iniCell.x == -1 && iniCell.y == -1) {
                        muestraMensajeError("Selecciona la casilla de salida")
                    } else if (endCell.x == -1 && endCell.y == -1) {
                        muestraMensajeError("Selecciona la meta")
                    } else {
                        buttons.forEach { it.isEnabled = false }
                        gameButtons.forEach { it.isEnabled = false }

                        wayPoints.add(endCell)
                        controller.resuelve(N, M, iniCell, endCell, tablero, wayPoints, mw)
                        wayPoints.remove(endCell)

                        buttons.forEach { it.isEnabled = true }
                        gameButtons.forEach { it.isEnabled = true }
                    }

                    t = null
                }
            }
            (t as Thread).start()
        }
    }

    private fun redimensiona() {
        val filas = filasTf.text
        val columnas = columnasTf.text
        try {
            // aqui se lanzan las posibles excepciones que se contemplan
            N = Integer.parseInt(filas)
            M = Integer.parseInt(columnas)
            if (!esTamValido(N, M))
                throw TamInvalidoException("El tablero debe de ser de al menos 2 casillas hasta un maximo de 40x40")

            // una vez sabemos que el nuevo tablero es posible
            reset()
            this.remove(panelCentral)
            panelCentral = panelCentral(N, M)
            this.add(panelCentral, BorderLayout.CENTER)

        } catch (n: NumberFormatException) {
            muestraMensajeError("Tamaño no valido")
        } catch (t: TamInvalidoException) {
            muestraMensajeError(t.message!!)
        } finally {
            SwingUtilities.updateComponentTreeUI(this)
        }
    }


    fun muestraMensajeError(msg: String) {
        JOptionPane.showMessageDialog(
                null,
                msg,
                "Error",
                JOptionPane.ERROR_MESSAGE
        )
    }

    private fun esTamValido(N: Int, M: Int): Boolean {
        if (N == M && N == 1) return false
        return N > 0 && M > 0 && N <= 40 && M <= 40
    }

    /*************************************************************************************************************************************/

    private class RoundedBorder(val radius: Int) : Border {
        override fun getBorderInsets(c: Component?): Insets {
            return Insets(radius + 1, radius + 1, radius + 2, radius)
        }

        override fun isBorderOpaque(): Boolean {
            return true
        }

        override fun paintBorder(c: Component?, g: Graphics, x: Int, y: Int, width: Int, height: Int) {
            g.drawRoundRect(x, y, width - 1, height - 1, radius, radius)
        }
    }

    /*************************************************************************************************************************************/



}