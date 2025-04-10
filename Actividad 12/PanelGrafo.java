/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package visualizacionrecorridosgrafo;

/**
 *
 * @author vanes
 */
import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class PanelGrafo extends JPanel {

    private VerticeBinario raiz;                // Raíz del árbol
    private List<VerticeBinario> recorrido;     // Lista de vertices a resaltar

    public PanelGrafo() {
        this.raiz = null;
        this.recorrido = new ArrayList<>();
        setBackground(Color.WHITE);
    }

    public void setRaiz(VerticeBinario raiz) {
        this.raiz = raiz;
        repaint();
    }

    public void setRecorrido(List<VerticeBinario> recorrido) {
        this.recorrido = recorrido;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (raiz == null) {
            return;
        }
        int profundidad = calcularProfundidad(raiz);
        dibujarArbol(g, raiz, 0, getWidth(), 0, getHeight() / (profundidad + 1));
    }

    private int calcularProfundidad(VerticeBinario vertice) {
        if (vertice == null) return 0;
        return 1 + Math.max(calcularProfundidad(vertice.izq), calcularProfundidad(vertice.der));
    }

    private void dibujarArbol(Graphics g, VerticeBinario vertice,
                              int minX, int maxX,
                              int nivel, int nivelHeight) {
        if (vertice == null) return;
        int x = (minX + maxX) / 2;
        int y = nivel * nivelHeight + 50;

        if (vertice.izq != null) {
            int xHijoIzq = (minX + x) / 2;
            int yHijo = (nivel + 1) * nivelHeight + 50;
            g.setColor(Color.BLACK);
            g.drawLine(x, y, xHijoIzq, yHijo);
            dibujarArbol(g, vertice.izq, minX, x, nivel + 1, nivelHeight);
        }

        if (vertice.der != null) {
            int xHijoDer = (x + maxX) / 2;
            int yHijo = (nivel + 1) * nivelHeight + 50;
            g.setColor(Color.BLACK);
            g.drawLine(x, y, xHijoDer, yHijo);
            dibujarArbol(g, vertice.der, x, maxX, nivel + 1, nivelHeight);
        }

        if (recorrido.contains(vertice)) {
            g.setColor(Color.ORANGE);
        } else {
            g.setColor(Color.PINK);
        }
        int radio = 20;
        g.fillOval(x - radio, y - radio, radio * 2, radio * 2);
        g.setColor(Color.BLACK);
        g.drawOval(x - radio, y - radio, radio * 2, radio * 2);
        String valor = String.valueOf(vertice.dato);
        FontMetrics fm = g.getFontMetrics();
        int anchoTexto = fm.stringWidth(valor);
        int altoTexto = fm.getAscent();
        g.drawString(valor, x - anchoTexto / 2, y + altoTexto / 4);
    }
}
