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
    private final int RADIO_VERTICE = 20;
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

    private void drawArrowLine(Graphics2D g2, int x1, int y1, int x2, int y2) {
        // Radio del nodo (para no dibujar la flecha dentro del nodo destino)
        int nodeRadius = RADIO_VERTICE;
        // Tamaño de la flecha proporcional al radio del nodo
        double arrowSize = nodeRadius * 0.5; // Puedes ajustar este factor (0.5) según lo grande que quieras la flecha
        int d = (int) (arrowSize * 1.5); // Longitud de la flecha
        int h = (int) arrowSize;         // Ancho de la base de la flecha
        // Se calcula la diferencia en X e Y entre el punto de inicio (x1, y1) y el
        // destino (x2, y2)
        int dx = x2 - x1, dy = y2 - y1;
        // Se obtiene D, la distancia total entre ambos puntos.
        double D = Math.sqrt(dx * dx + dy * dy);
        // Si D es 0 (los puntos son iguales), se sale de la función para evitar dividir
        // por cero.
        if (D == 0)
            return; // Evita división por cero

        // Normalizamos la dirección: Se obtiene el vector unitario que apunta desde el
        // origen al destino.
        double cos = dx / D, sin = dy / D;
        // Acortar la línea para que termine en el borde del nodo de destino
        double newD = D - nodeRadius;
        double distanciaRecortada = D - nodeRadius - d;
        // Se calcula el nuevo punto final (newX2, newY2) multiplicando
        // el vector unitario por newD y sumándolo al origen.
        int newX2 = x1 + (int) (newD * cos);
        int newY2 = y1 + (int) (newD * sin);
        //calcular puntos finales de la distanciaRecortada
        int drX2 = x1 + (int) (distanciaRecortada * cos);
        int drY2 = y1 + (int) (distanciaRecortada * sin);

        // Calcula las coordenadas para la flecha
        double xm = newD - d;
        double xn = xm;
        double ym = h, yn = -h;
        double xA = xm * cos - ym * sin + x1;
        double yA = xm * sin + ym * cos + y1;
        int x2a = (int) xA;
        int y2a = (int) yA;
        double xB = xn * cos - yn * sin + x1;
        double yB = xn * sin + yn * cos + y1;
        int x3 = (int) xB;
        int y3 = (int) yB;

        // Dibuja la línea y la flecha
        g2.drawLine(x1, y1, drX2, drY2);
        int[] xpoints = { newX2, x2a, x3 };
        int[] ypoints = { newY2, y2a, y3 };
        g2.fillPolygon(xpoints, ypoints, 3);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
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
        for (int i = 0; i < grafo.numeroAristas; i++) {
            Point origen = posicionesPixeles.get(grafo.posicionAristas[i].origen);
            Point destino = posicionesPixeles.get(grafo.posicionAristas[i].destino);
            //grosor de linea
            // Configurar el grosor de la línea en función del tamaño del vértice
            float grosorLinea = RADIO_VERTICE / 5.0f; // Grosor proporcional al radio del vértice
            Stroke strokeAnterior = g2.getStroke(); // Guardar el stroke actual
            g2.setStroke(new BasicStroke(grosorLinea)); // Establecer el nuevo grosor
            //g2.setStroke(new BasicStroke(2));
            if (grafo.esDirigido) {
                g2.setColor(Color.ORANGE);
                drawArrowLine(g2, origen.x, origen.y, destino.x, destino.y);
            } else {
                g2.setColor(Color.ORANGE);
                g2.drawLine(origen.x, origen.y, destino.x, destino.y);
            }

            //g2.drawString(String.valueOf(grafo.posicionAristas[i].peso), 
            //(origen.x + destino.x)/2, (origen.y + destino.y)/2);
            // Restaurar el grosor de la línea
            g2.setStroke(strokeAnterior);

            // Calcular el punto medio de la arista
            int midX = (origen.x + destino.x)/2, midY = (origen.y + destino.y)/2;

            // Configurar el tamaño de la fuente del peso en función del tamaño del vértice
            int fontSize = (int) (RADIO_VERTICE * 0.8); // Tamaño proporcional al radio del vértice
            //Font fuenteActual = g2.getFont(); // Obtener la fuente actual
            //Font nuevaFuente = fuenteActual.deriveFont((float) fontSize); // Derivar la fuente con el nuevo tamaño
            Font font = new Font("SansSerif", Font.BOLD, fontSize); // Crear la fuente
            g2.setFont(font); // Establecer la nueva fuente

            // Calcular dimensiones del texto
            FontMetrics fm = g2.getFontMetrics();
            String pesoTexto = String.valueOf(grafo.posicionAristas[i].peso);
            int textWidth = fm.stringWidth(pesoTexto);
            int textHeight = fm.getAscent(); // Altura desde la línea base hasta la parte superior del texto

            // Ajustar el tamaño del fondo en función del tamaño del texto
            int width = textWidth + 10; // Ancho del fondo (texto + margen)
            int height = textHeight + 5; // Alto del fondo (texto + margen)

            // Ajustar posición del texto para que quede centrado en el rectángulo
            int textX = midX - textWidth / 2;
            int textY = midY + textHeight / 3; // Ajuste para centrar verticalmente

            // Dibujar fondo blanco
            g2.setColor(Color.WHITE);
            g2.fillRect(midX - width / 2, midY - height / 2, width, height);

            // Dibujar el peso centrado
            g2.setColor(Color.BLACK);
            g2.drawString(pesoTexto, textX, textY);
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

            // Calcular el tamaño de la fuente en función del radio del vértice
            int fontSize = (int) (RADIO_VERTICE * 1.0); // Tamaño proporcional al radio
            Font font = new Font("SansSerif", Font.BOLD, fontSize); // Crear la fuente
            g2.setFont(font); // Establecer la fuente

            // Calcular la posición del texto para centrarlo en el vértice
            FontMetrics metrics = g2.getFontMetrics();
            String texto = String.valueOf(i);
            int textoWidth = metrics.stringWidth(texto); // Ancho del texto
            int textoHeight = metrics.getHeight(); // Altura del texto

            // Dibujar el texto centrado en el vértice
            int textoX = (x + RADIO_VERTICE) - textoWidth / 2; // Centrar horizontalmente
            int textoY = (y + RADIO_VERTICE) + textoHeight / 4; // Centrar verticalmente (ajustar según sea necesario)
            g2.drawString(texto, textoX, textoY);

            //g2.drawString(String.valueOf(i), x + RADIO_VERTICE - 10, y + RADIO_VERTICE + 5);
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
