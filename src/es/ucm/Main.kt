package es.ucm

import es.ucm.presentacion.Controller
import es.ucm.presentacion.MainWindow
import java.awt.EventQueue

    private fun createAndShowGUI() {
        val controller = Controller()
        val mainWindow = MainWindow(5, 5, controller)
        mainWindow.isVisible = true

    }

    fun main(args: Array<String>) {
        EventQueue.invokeLater(::createAndShowGUI)
    }

