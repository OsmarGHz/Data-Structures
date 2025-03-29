package vista;

import javax.swing.*;
import java.awt.event.*;
import java.util.*;
import java.util.List;

import modelo.GeneradorGrafo;
import modelo.Vertice;
import modelo.posVertice;

import java.awt.*;
import java.awt.geom.Line2D;

public class PanelMapa extends JPanel {
    GeneradorGrafo grafo = new GeneradorGrafo();
    int T_MATRIZ = grafo.TAM_MATRIZ + 1;
    private Image mapa;
    private final int RADIO_VERTICE = 15;
    private posVertice verticeCandidato = null; // Vértice donde se hizo mousePressed
    private boolean esArrastre = false;
    private final int UMBRAL_ARRASTRE = 5;    // Umbral para distinguir arrastre de click
    private int inicioX, inicioY; // Coordenadas del mouse al presionar
    //int[] intersecciones = new int[];



    public PanelMapa() {
        // Cargar la imagen
        mapa = new ImageIcon(getClass().getResource("/recursos/mapa.png")).getImage();
        //inicializar matriz de costos
        grafo.llenarMatriz();
        grafo.contarVertices();
        grafo.generarMatrizCostos();
        grafo.mostrarMatriz();
        /*
        //funcionalidades para mover los vertices
                // Agregar listeners de mouse para mover los vértices
        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if (SwingUtilities.isLeftMouseButton(e)) {
                    // Si se presiona sobre un vértice, lo marca como candidato
                    posVertice vEncontrado = buscarVertice(e.getX(), e.getY());
                    System.out.println(vEncontrado);
                    if (vEncontrado != null) {
                        System.out.println("se encontro un vertice");
                        System.out.println(vEncontrado);
                        verticeCandidato = vEncontrado;
                        inicioX = e.getX();
                        inicioY = e.getY();
                        System.out.println("inicioX: "+inicioX+" inicioY: "+inicioY);
                        esArrastre = false;
                    }
                }
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                verticeCandidato = null; // Liberar el vértice seleccionado
            }
        });

        addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                if (verticeCandidato != null) {
                    // Limitar los movimientos dentro de los límites del panel
                    int newX = Math.max(RADIO_VERTICE, Math.min(getWidth() - RADIO_VERTICE, e.getPoint().x));
                    int newY = Math.max(RADIO_VERTICE, Math.min(getHeight() - RADIO_VERTICE, e.getPoint().y));
                    posVertice verticeSuplente = new posVertice(newX, newY, verticeCandidato.tipoZona);
                    // Verificar si la nueva posición colisiona con otro vértice
                    if (!verificarColision(verticeSuplente)) {
                        // Si no hay colisión, mover el vértice
                        verticeCandidato.x = verticeSuplente.x;
                        verticeCandidato.y = verticeSuplente.y;
                        repaint();
                    }
                }
            }
        });
        */
    }
    /* 
    // Retorna el vértice cuyo centro está cerca de (x, y)
    private posVertice buscarVertice(int x, int y) {
        double xlineas = (double) getWidth() / T_MATRIZ;
        double ylineas = (double) getHeight() / T_MATRIZ;
        for (int i = 0; i < grafo.posicionVertices.length; i++) {
            if (Math.hypot(((int) ((grafo.posicionVertices[i].y+1) * xlineas)) - x, 
                            ((int) ((grafo.posicionVertices[i].x+1) * ylineas)) - y) <= RADIO_VERTICE) {
                return grafo.posicionVertices[i];
            }
        }
        return null;
    }

    // Verifica si el vértice 'v' está colisionando con otro y, de ser así, ajusta su posición
    private boolean verificarColision(posVertice v) {
        boolean colision = true;
        while (colision) {
            colision = false;
            for (posVertice otro : grafo.posicionVertices) {
                if (otro != v) {
                    if (Math.hypot(v.x - otro.x, v.y - otro.y) < 2 * RADIO_VERTICE) {
                        colision = true;
                        return colision;
                    }
                }
            }
        }
        return false;
        
    }
    */

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g); // Limpia el panel antes de dibujar
        Graphics2D g2 = (Graphics2D) g;
        g2.drawImage(mapa, 0, 0, getWidth(), getHeight(), this); // Dibuja la imagen
        System.out.println("Ancho: "+getWidth());
        System.out.println("Alto: "+getHeight());

        //dibujar filas y columnas segun el valor TAM_MATRIZ
        double xlineas = (double) getWidth() / T_MATRIZ;
        System.out.println("Ancho: "+getWidth()+" Valor TAM_MATRIZ: "+T_MATRIZ+" Valor x: "+xlineas);
        double ylineas = (double) getHeight() / T_MATRIZ;
        System.out.println("Alto: "+getHeight()+" Valor TAM_MATRIZ: "+T_MATRIZ+" Valor y: "+ylineas);

        // Aplicar opacidad (0.0 = totalmente transparente, 1.0 = totalmente opaco)
        float opacidad = 0.8f; // 50% de opacidad
        g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, opacidad));
        //establecer color de las lineas
        g2.setColor(Color.BLACK);
        //establecer grosor de linea
        g2.setStroke(new BasicStroke(3));
        //dibujar las lineas verticales y horizontales
        for (int i = 0; i < T_MATRIZ; i++) {
            g2.draw(new Line2D.Double((i+1)*xlineas, 0, (i+1)*xlineas, getHeight()));
            g2.draw(new Line2D.Double(0, (i+1)*ylineas, getWidth(), (i+1)*ylineas));
            //g2.drawLine((i+1)*x, 0,(i+1)*x , getHeight());
            //g2.drawLine( 0, (i+1)*y, getWidth(), (i+1)*y);
        }

        //dibujar vertices
        for (int i = 0; i < grafo.posicionVertices.length; i++) {

            if (grafo.posicionVertices[i].tipoZona == 1) {
                g2.setColor(Color.BLUE);
                /*
                 * aqui x y y estan invertidos debido a
                 * hay que revisar porque no entiendo
                 * "x" deberia multiplicarse con "x" y "y" con "y"
                 * pero se invierten y funciona jajaj
                 */
                g2.fillOval((int) ((grafo.posicionVertices[i].y+1) * xlineas) - RADIO_VERTICE, 
                            (int) ((grafo.posicionVertices[i].x+1) * ylineas) - RADIO_VERTICE, 
                            RADIO_VERTICE * 2, RADIO_VERTICE * 2);
            } else if (grafo.posicionVertices[i].tipoZona == 2) {
                g2.setColor(Color.YELLOW);
                g2.fillOval((int) ((grafo.posicionVertices[i].y+1) * xlineas) - RADIO_VERTICE, 
                            (int) ((grafo.posicionVertices[i].x+1) * ylineas) - RADIO_VERTICE, 
                            RADIO_VERTICE * 2, RADIO_VERTICE * 2);
            }
            //System.out.println("X: "+grafo.posicionVertices[i].x+" Y: "+ grafo.posicionVertices[i].y);    
        }

        //dibujar aristas
        for (int i = 0; i < grafo.numeroAristas; i++) {
            g2.setColor(Color.ORANGE);
            g2.drawLine((int) ((grafo.posicionAristas[i].origen.y+1) * xlineas),
                        (int) ((grafo.posicionAristas[i].origen.x+1) * ylineas), 
                        (int) ((grafo.posicionAristas[i].destino.y+1) * xlineas), 
                        (int) ((grafo.posicionAristas[i].destino.x+1) * ylineas));
                        
            //dibujar el peso de la arista
            //calcular punto medio
            int midX = ((int) ((grafo.posicionAristas[i].origen.y+1) * xlineas) + 
                        (int) ((grafo.posicionAristas[i].destino.y+1) * xlineas)) / 2, 
                midY = ((int) ((grafo.posicionAristas[i].origen.x+1) * ylineas) + 
                        (int) ((grafo.posicionAristas[i].destino.x+1) * ylineas)) / 2;

            g2.setColor(Color.WHITE);
            g2.drawString(""+grafo.posicionAristas[i].peso, midX, midY);
        }
    }

    public static void main(String[] args) {
        JFrame ventana = new JFrame();
        ventana.setTitle("Ventana pruebas");
        ventana.setSize(500, 500);
        ventana.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        ventana.setLocationRelativeTo(null);

        PanelMapa panelMapa = new PanelMapa();
        ventana.add(panelMapa);
        ventana.setVisible(true);
    }
}
