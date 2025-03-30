/*
package vista;

import javax.swing.*;
import java.awt.event.*;
import java.util.*;
import java.util.List;
import java.awt.*;
import java.awt.geom.Line2D;
import java.awt.geom.Ellipse2D;
import modelo.GeneradorGrafo;
import modelo.posVertice;

public class PanelMapa extends JPanel {
    private GeneradorGrafo grafo = new GeneradorGrafo();
    private int T_MATRIZ = grafo.TAM_MATRIZ + 1;
    //private int T_MATRIZ = 13;
    private Image mapa;
    private final int RADIO_VERTICE = 15;
    private posVertice verticeSeleccionado = null;
    private boolean arrastrando = false;
    private Point puntoArrastre = null;
    private List<Ellipse2D> areasVertices = new ArrayList<>();
    private Map<posVertice, Point> posicionesPixeles = new HashMap<>();

    public PanelMapa() {
        mapa = new ImageIcon(getClass().getResource("/recursos/mapa.png")).getImage();
        
        inicializarPosicionesPixeles();

        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if (SwingUtilities.isLeftMouseButton(e)) {
                    verificarSeleccionVertice(e.getPoint());
                    if (arrastrando) {
                        puntoArrastre = e.getPoint();
                    }
                }
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                if (SwingUtilities.isLeftMouseButton(e)) {
                    verticeSeleccionado = null;
                    arrastrando = false;
                    puntoArrastre = null;
                }
            }
        });

        addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                if (arrastrando && verticeSeleccionado != null) {
                    if (isMouseInPanel(e.getPoint())) {
                        moverVertice(e.getPoint());
                    }
                }
            }
        });
    }

    private void inicializarPosicionesPixeles() {
        double xlineas = (double) getWidth() / T_MATRIZ;
        double ylineas = (double) getHeight() / T_MATRIZ;
        
        for (posVertice vertice : grafo.posicionVertices) {
            int x = (int) ((vertice.y + 1) * xlineas);
            int y = (int) ((vertice.x + 1) * ylineas);
            posicionesPixeles.put(vertice, new Point(x, y));
        }
    }

    private void verificarSeleccionVertice(Point punto) {
        areasVertices.clear();
        for (int i = 0; i < grafo.posicionVertices.length; i++) {
            Point posicion = posicionesPixeles.get(grafo.posicionVertices[i]);
            int x = posicion.x - RADIO_VERTICE;
            int y = posicion.y - RADIO_VERTICE;
            
            Ellipse2D area = new Ellipse2D.Double(x, y, RADIO_VERTICE * 2, RADIO_VERTICE * 2);
            areasVertices.add(area);
            
            if (area.contains(punto)) {
                verticeSeleccionado = grafo.posicionVertices[i];
                arrastrando = true;
                break;
            }
        }
    }

    private boolean isMouseInPanel(Point p) {
        return p.x >= 0 && p.x < getWidth() && p.y >= 0 && p.y < getHeight();
    }

    private void moverVertice(Point nuevoPunto) {
        Point posActual = posicionesPixeles.get(verticeSeleccionado);
        int deltaX = nuevoPunto.x - puntoArrastre.x;
        int deltaY = nuevoPunto.y - puntoArrastre.y;

        int nuevaX = Math.max(RADIO_VERTICE, 
            Math.min(getWidth() - RADIO_VERTICE, posActual.x + deltaX));
        int nuevaY = Math.max(RADIO_VERTICE, 
            Math.min(getHeight() - RADIO_VERTICE, posActual.y + deltaY));

        if ((nuevaX != posActual.x || nuevaY != posActual.y) && !hayColision(nuevaX, nuevaY)) {
            posicionesPixeles.put(verticeSeleccionado, new Point(nuevaX, nuevaY));
            actualizarPosicionMatriz(verticeSeleccionado, nuevaX, nuevaY);
            puntoArrastre = nuevoPunto;
            repaint();
        }
    }

    private boolean hayColision(int x, int y) {
        for (posVertice v : grafo.posicionVertices) {
            if (v != verticeSeleccionado) {
                Point p = posicionesPixeles.get(v);
                if (Math.hypot(x - p.x, y - p.y) < RADIO_VERTICE * 2) {
                    return true;
                }
            }
        }
        return false;
    }

    private void actualizarPosicionMatriz(posVertice vertice, int xPixel, int yPixel) {
        double xlineas = (double) getWidth() / T_MATRIZ;
        double ylineas = (double) getHeight() / T_MATRIZ;
        
        vertice.y = Math.max(0, Math.min(grafo.TAM_MATRIZ - 1, 
            (int) (xPixel / xlineas) - 1));
        vertice.x = Math.max(0, Math.min(grafo.TAM_MATRIZ - 1, 
            (int) (yPixel / ylineas) - 1));
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.drawImage(mapa, 0, 0, getWidth(), getHeight(), this);

        // Dibujar cuadrícula
        float opacidad = 1.0f;
        g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, opacidad));
        g2.setColor(Color.BLACK);
        g2.setStroke(new BasicStroke(1));
        
        double xlineas = (double) getWidth() / T_MATRIZ;
        double ylineas = (double) getHeight() / T_MATRIZ;
        for (int i = 0; i < T_MATRIZ; i++) {
            g2.draw(new Line2D.Double((i+1)*xlineas, 0, (i+1)*xlineas, getHeight()));
            g2.draw(new Line2D.Double(0, (i+1)*ylineas, getWidth(), (i+1)*ylineas));
        }

        // Dibujar aristas
        g2.setStroke(new BasicStroke(2));
        for (int i = 0; i < grafo.numeroAristas; i++) {
            Point origen = posicionesPixeles.get(grafo.posicionAristas[i].origen);
            Point destino = posicionesPixeles.get(grafo.posicionAristas[i].destino);
            
            g2.setColor(Color.ORANGE);
            g2.drawLine(origen.x, origen.y, destino.x, destino.y);
            
            g2.setColor(Color.WHITE);
            g2.drawString(String.valueOf(grafo.posicionAristas[i].peso), 
                (origen.x + destino.x)/2, (origen.y + destino.y)/2);
        }

        // Dibujar vértices
        areasVertices.clear();
        for (int i = 0; i < grafo.posicionVertices.length; i++) {
            Point posicion = posicionesPixeles.get(grafo.posicionVertices[i]);
            int x = posicion.x - RADIO_VERTICE;
            int y = posicion.y - RADIO_VERTICE;
            
            g2.setColor(grafo.posicionVertices[i].tipoZona == 1 ? Color.BLUE : Color.YELLOW);
            g2.fillOval(x, y, RADIO_VERTICE * 2, RADIO_VERTICE * 2);
            
            g2.setColor(Color.BLACK);
            g2.drawString(String.valueOf(i), x + RADIO_VERTICE - 3, y + RADIO_VERTICE + 5);
            areasVertices.add(new Ellipse2D.Double(x, y, RADIO_VERTICE * 2, RADIO_VERTICE * 2));
        }
    }

    @Override
    public void setBounds(int x, int y, int width, int height) {
        super.setBounds(x, y, width, height);
        if (width > 0 && height > 0) {
            inicializarPosicionesPixeles();
        }
    }

    public static void main(String[] args) {
        JFrame ventana = new JFrame("Grafo Interactivo");
        ventana.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        ventana.setSize(800, 600);
        ventana.setLocationRelativeTo(null);
        ventana.add(new PanelMapa());
        ventana.setVisible(true);
    }
}
*/