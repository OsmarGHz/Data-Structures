// vista/animaciones/AnimacionBFS.java
package vista.animaciones;

import modelo.grafo.posVertice;
import java.awt.*;
import java.util.List;
import java.util.Map;

public class AnimarBFS {
    private final List<posVertice> pasos;
    private int pasoActual;
    private boolean encontradoContaminado;

    public AnimarBFS(List<posVertice> pasos) {
        this.pasos = pasos;
        this.pasoActual = 0;
        this.encontradoContaminado = pasos.get(pasos.size()-1).tipoZona == 2;
    }

    public void render(Graphics2D g2, Map<posVertice, Point> posiciones) {
        // Dibujar todos los pasos hasta el actual
        for (int i = 0; i <= pasoActual; i++) {
            posVertice vertice = pasos.get(i);
            Point punto = posiciones.get(pasos.get(i));

            // Validación crítica
            if (punto == null) {
                System.err.println("¡Vértice sin posición!: " + vertice+ " " + pasos.get(i) + " " + i);
                continue; // Saltar este vértice
            }

            Color color = (i == 0) ? Color.GREEN : // Inicio
                         (pasos.get(i).tipoZona == 2) ? Color.RED : // Contaminado
                         Color.BLACK; // Exploración
            
            g2.setColor(color);
            g2.fillOval(punto.x - 15, punto.y - 15, 30, 30);
            
            // Resaltar encontrado
            if (encontradoContaminado && i == pasos.size()-1) {
                g2.setColor(Color.RED);
                g2.setStroke(new BasicStroke(3));
                g2.drawOval(punto.x - 20, punto.y - 20, 40, 40);
            }
        }
    }

    public boolean siguientePaso() {
        if (pasoActual < pasos.size() - 1) {
            pasoActual++;
            return !(encontradoContaminado && pasoActual == pasos.size() - 1);
        }
        return false;
    }
}