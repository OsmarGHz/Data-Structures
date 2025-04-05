// controlador/BotonesController.java
package controlador;

import modelo.grafo.GeneradorGrafo;
import modelo.grafo.posVertice;
import modelo.algoritmos.BFS;
import vista.PanelManejadorMapa;
import vista.animaciones.AnimarBFS;
import javax.swing.*;
import java.awt.event.ActionEvent;
import java.util.List;

public class BotonesControlador {
    private final PanelManejadorMapa vista;
    private final GeneradorGrafo modelo;
    private Timer timerAnimacion;

    public BotonesControlador(PanelManejadorMapa vista, GeneradorGrafo modelo) {
        this.vista = vista;
        this.modelo = modelo;
        configurarBotones();
    }

    private void configurarBotones() {
        JButton btnBFS = vista.getBoton("Ejecutar BFS");
        btnBFS.addActionListener(this::ejecutarBFS);
    }

    private void ejecutarBFS(ActionEvent e) {

        if (timerAnimacion != null && timerAnimacion.isRunning()) {
            timerAnimacion.stop();
        }

        BFS bfs = new BFS();
        List<posVertice> pasos = bfs.encontrarZonaContaminada(modelo, 0); // Empieza en vértice 0

        AnimarBFS animacion = new AnimarBFS(pasos);
        vista.getPanelMapa().setAnimacionActual(animacion);

        timerAnimacion = new Timer(1000, evt -> { // 1 segundo entre pasos
            if (!animacion.siguientePaso()) {
                timerAnimacion.stop();
                JOptionPane.showMessageDialog(vista, 
                    "¡Zona contaminada encontrada!", 
                    "Resultado BFS", 
                    JOptionPane.INFORMATION_MESSAGE);
            }
            vista.getPanelMapa().repaint();
        });
        timerAnimacion.start();
    }
}