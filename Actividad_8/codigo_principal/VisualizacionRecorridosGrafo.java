import javax.swing.*;
import javax.swing.Timer;
import java.awt.*;
import java.awt.event.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;
import java.util.Queue;

//Clase que representa una arista con destino y costo
class Arista {
    int origen;   // Nuevo campo para el origen (opcional)
    int destino;
    int peso;

    // Constructor original (usado para representar aristas del grafo)
    public Arista(int destino, int peso) {
        this.destino = destino;
        this.peso = peso;
    }

    // Nuevo constructor para el MST que almacena origen, destino y peso
    public Arista(int origen, int destino, int peso) {
        this.origen = origen;
        this.destino = destino;
        this.peso = peso;
    }
}

// Clase que representa el grafo utilizando listas de adyacencia
class Grafo {
    int numeroVertices;
    java.util.ArrayList<java.util.ArrayList<Arista>> listaAdyacencia; // Lista de adyacencia de aristas ponderadas
    java.util.ArrayList<Boolean> visitados; // Seguimiento de nodos visitados
    boolean dirigido; // true: grafo dirigido, false: no dirigido

    // Constructor que recibe el número de vértices y el tipo de grafo
    public Grafo(int vertices, boolean dirigido) {
        this.numeroVertices = vertices;
        this.dirigido = dirigido;
        listaAdyacencia = new java.util.ArrayList<>(vertices);
        visitados = new java.util.ArrayList<>(vertices);
        for (int i = 0; i < vertices; i++) {
            listaAdyacencia.add(new java.util.ArrayList<>());
            visitados.add(false);
        }
    }

    // En la clase Grafo agrega:
    public void agregarVertice() {
        numeroVertices++;
        listaAdyacencia.add(new ArrayList<>());
        visitados.add(false);
    }

    public void eliminarVertice(int vertice) {
        if (vertice < 0 || vertice >= numeroVertices) {
            throw new IllegalArgumentException("Vértice inválido");
        }
        // Se elimina la lista de adyacencia del vértice y se quita de los visitados.
        listaAdyacencia.remove(vertice);
        visitados.remove(vertice);
        numeroVertices--;

        // Se recorren todas las listas para quitar aristas que apunten al vértice
        // eliminado,
        // y actualizar los índices de las aristas con destino mayor al vértice
        // eliminado.
        for (java.util.List<Arista> lista : listaAdyacencia) {
            lista.removeIf(arista -> arista.destino == vertice);
            for (Arista arista : lista) {
                if (arista.destino > vertice) {
                    arista.destino--;
                }
            }
        }
    }

    // Agrega una arista con peso entre dos vértices.
    // Si el grafo es no dirigido, se agrega la arista en ambos sentidos.
    public void agregarArista(int origen, int destino, int peso) {
        // Evitar duplicados en origen
        for (Arista arista : listaAdyacencia.get(origen)) {
            if (arista.destino == destino) {
                return;
            }
        }
        listaAdyacencia.get(origen).add(new Arista(destino, peso));
        if (!dirigido) {
            // Evitar duplicados en destino
            for (Arista arista : listaAdyacencia.get(destino)) {
                if (arista.destino == origen) {
                    return;
                }
            }
            listaAdyacencia.get(destino).add(new Arista(origen, peso));
        }
    }

    // Elimina una arista entre dos vértices.
    // En grafo no dirigido, elimina la conexión en ambos sentidos.
    public void eliminarArista(int origen, int destino) {
        listaAdyacencia.get(origen).removeIf(arista -> arista.destino == destino);
        if (!dirigido) {
            listaAdyacencia.get(destino).removeIf(arista -> arista.destino == origen);
        }
    }

    // Retorna una representación en cadena de la lista de adyacencia con
    // ponderación
    public String obtenerListaAdyacencia() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < numeroVertices; i++) {
            sb.append(i).append(": ");
            for (Arista arista : listaAdyacencia.get(i)) {
                sb.append("(").append(arista.destino).append(", ").append(arista.peso).append(") -> ");
            }
            sb.append("NULL\n");
        }
        return sb.toString();
    }

    // Resetea la bandera de visitados.
    public void resetearVisitados() {
        for (int i = 0; i < visitados.size(); i++) {
            visitados.set(i, false);
        }
    }

    // Calcular la matriz de distancias usando Floyd-Warshall con pesos
    public int[][] calcularMatrizDistancias() {
        int[][] distancias = new int[numeroVertices][numeroVertices];

        // Inicializar la matriz de distancias
        for (int i = 0; i < numeroVertices; i++) {
            for (int j = 0; j < numeroVertices; j++) {
                if (i == j) {
                    distancias[i][j] = 0; // Distancia de un nodo a sí mismo es 0
                } else {
                    distancias[i][j] = Integer.MAX_VALUE; // Infinito para nodos no conectados
                }
            }
        }

        // Llenar la matriz con los pesos de las aristas usando un for mejorado con
        // Arista
        for (int i = 0; i < numeroVertices; i++) {
            for (Arista arista : listaAdyacencia.get(i)) {
                distancias[i][arista.destino] = arista.peso;
            }
        }

        // Aplicar el algoritmo de Floyd-Warshall
        for (int k = 0; k < numeroVertices; k++) {
            for (int i = 0; i < numeroVertices; i++) {
                for (int j = 0; j < numeroVertices; j++) {
                    if (distancias[i][k] != Integer.MAX_VALUE && distancias[k][j] != Integer.MAX_VALUE) {
                        distancias[i][j] = Math.min(distancias[i][j], distancias[i][k] + distancias[k][j]);
                    }
                }
            }
        }

        return distancias;
    }

    // Obtener la matriz de distancias como una cadena formateada
    public String obtenerMatrizDistancias() {
        int[][] distancias = calcularMatrizDistancias();
        StringBuilder sb = new StringBuilder();
        sb.append("Matriz de Distancias:\n");

        // Encabezado de la matriz
        sb.append("    ");
        for (int i = 0; i < numeroVertices; i++) {
            sb.append(String.format("%-6d", i));
        }
        sb.append("\n");

        // Filas de la matriz
        for (int i = 0; i < numeroVertices; i++) {
            sb.append(i).append(": ");
            for (int j = 0; j < numeroVertices; j++) {
                if (distancias[i][j] == Integer.MAX_VALUE) {
                    sb.append("INF   "); // Representar infinito como "INF"
                } else {
                    sb.append(String.format("%-6d", distancias[i][j]));
                }
            }
            sb.append("\n");
        }

        return sb.toString();
    }

    // Dentro de la clase Grafo
    public java.util.ArrayList<Arista> primMST() {
        java.util.ArrayList<Arista> mst = new java.util.ArrayList<>(); // Almacena las aristas del MST
        boolean[] enMST = new boolean[numeroVertices]; // Indica si un vértice está en el MST
        int[] key = new int[numeroVertices]; // Almacena el costo mínimo para conectar cada vértice al MST
        int[] parent = new int[numeroVertices]; // Almacena el padre de cada vértice en el MST

        // Inicializar arreglos
        Arrays.fill(key, Integer.MAX_VALUE);
        Arrays.fill(parent, -1);

        // Empezar con el primer vértice
        key[0] = 0; // El costo para el primer vértice es 0

        // Construir el MST
        for (int i = 0; i < numeroVertices - 1; i++) {
            // Seleccionar el vértice con el valor mínimo de key que no esté en el MST
            int u = -1;
            for (int v = 0; v < numeroVertices; v++) {
                if (!enMST[v] && (u == -1 || key[v] < key[u])) {
                    u = v;
                }
            }

            // Añadir el vértice seleccionado al MST
            enMST[u] = true;

            // Actualizar los valores de key y parent para los vértices adyacentes
            for (Arista arista : listaAdyacencia.get(u)) {
                int v = arista.destino;
                int peso = arista.peso;
                if (!enMST[v] && peso < key[v]) {
                    parent[v] = u;
                    key[v] = peso;
                }
            }
        }

        // Construir la lista de aristas del MST: usar el nuevo constructor para almacenar el peso
        for (int i = 1; i < numeroVertices; i++) {
            mst.add(new Arista(parent[i], i, key[i]));
        }

        return mst;
    }

    // Método para verificar si el grafo es conexo usando DFS
    public boolean esConexo() {
        if (numeroVertices == 0) {
            return false; // Grafo vacío no es conexo
        }
    
        // Arreglo para marcar nodos visitados
        boolean[] visitados = new boolean[numeroVertices];
        java.util.Stack<Integer> pila = new java.util.Stack<>();
        pila.push(0); // Empezar desde el nodo 0
        visitados[0] = true;
    
        // Recorrido DFS
        while (!pila.isEmpty()) {
            int u = pila.pop();
            for (Arista arista : listaAdyacencia.get(u)) {
                int v = arista.destino;
                if (!visitados[v]) {
                    visitados[v] = true;
                    pila.push(v);
                }
            }
        }
    
        // Verificar si todos los nodos fueron visitados
        for (boolean visitado : visitados) {
            if (!visitado) {
                return false; // Si algún nodo no fue visitado, el grafo no es conexo
            }
        }
        return true;
    }
}

// Panel para dibujar el grafo (distribución circular) y marcar los nodos
// visitados
class PanelGrafo extends JPanel {
    private final int RADIO_VERTICE = 25;
    private Grafo grafo;
    // private java.util.List<Integer> nodosVisitados = new ArrayList<>();
    private java.util.ArrayList<Integer> nodosVisitados = new java.util.ArrayList<>();
    // Arreglo para indicar qué aristas están resaltadas: [origen][destino]
    private boolean[][] highlightedEdges;
    // Arreglo para indicar el color de cada vértice (null: sin resaltar)
    private Color[] highlightedVertices;
    private Point[] posiciones; // Arreglo para almacenar las posiciones de los vértices
    private int verticeSeleccionado = -1; // Índice del vértice seleccionado para mover

    public PanelGrafo() {
        // Agregar listeners de mouse para mover los vértices
        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if (posiciones == null)
                    return; // Add this null check
                // Verificar si se hizo clic sobre un vértice
                for (int i = 0; i < posiciones.length; i++) {
                    if (posiciones[i] != null && e.getPoint().distance(posiciones[i]) <= RADIO_VERTICE) {
                        verticeSeleccionado = i;
                        break;
                    }
                }
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                verticeSeleccionado = -1; // Liberar el vértice seleccionado
            }
        });

        addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                if (verticeSeleccionado != -1) {
                    // Limitar los movimientos dentro de los límites del panel
                    int newX = Math.max(RADIO_VERTICE, Math.min(getWidth() - RADIO_VERTICE, e.getPoint().x));
                    int newY = Math.max(RADIO_VERTICE, Math.min(getHeight() - RADIO_VERTICE, e.getPoint().y));

                    // Verificar si la nueva posición colisiona con otro vértice
                    if (!verificarColision(newX, newY)) {
                        // Si no hay colisión, mover el vértice
                        posiciones[verticeSeleccionado] = new Point(newX, newY);
                        repaint();
                    }
                }
            }
        });
    }

    // Verificar si el vértice en la nueva posición colisiona con otro
    private boolean verificarColision(int newX, int newY) {
        int radio = RADIO_VERTICE; 
        for (int i = 0; i < posiciones.length; i++) {
            if (posiciones[i] != null && i != verticeSeleccionado) {
                // Verificar si la distancia entre el vértice y el nuevo punto es menor que el
                // doble del radio (colisión)
                if (new Point(newX, newY).distance(posiciones[i]) < 2 * RADIO_VERTICE) {
                    return true; // Hay colisión
                }
            }
        }
        return false; // No hay colisión
    }

    public void setGrafo(Grafo g) {
        this.grafo = g;
        nodosVisitados.clear();
        if (g != null) {
            highlightedEdges = new boolean[g.numeroVertices][g.numeroVertices];
            highlightedVertices = new Color[g.numeroVertices];
            posiciones = new Point[g.numeroVertices];
            // Inicializar las posiciones de los vértices en una distribución circular
            int ancho = getWidth(), alto = getHeight();
            System.out.println("Medidas: " + ancho + ", " + alto);
            int radio = Math.min(ancho, alto) / 2 - 50;
            int centroX = ancho / 2, centroY = alto / 2;
            for (int i = 0; i < g.numeroVertices; i++) {
                double angulo = 2 * Math.PI * i / g.numeroVertices;
                int x = centroX + (int) (radio * Math.cos(angulo));
                int y = centroY + (int) (radio * Math.sin(angulo));
                posiciones[i] = new Point(x, y);
            }
        }
        repaint();
    }

    public void setNodosVisitados(java.util.ArrayList<Integer> visitados) {
        this.nodosVisitados = new java.util.ArrayList<>(visitados);
        repaint();
    }

    // Actualiza la lista de nodos visitados para pintar en verde
    public void setNodosVisitados(java.util.List<Integer> visitados) {
        this.nodosVisitados = new ArrayList<>(visitados);
        repaint();
    }

    // Actualiza el arreglo de aristas resaltadas
    public void setHighlightedEdges(boolean[][] edges) {
        highlightedEdges = edges;
        repaint();
    }

    // Actualiza el arreglo de vértices resaltados
    public void setHighlightedVertices(Color[] vertices) {
        highlightedVertices = vertices;
        repaint();
    }

    /**
     * Dibuja una línea con una flecha al final.
     * 
     * @param g2 El Graphics2D sobre el que dibujar.
     * @param x1 Coordenada X de inicio.
     * @param y1 Coordenada Y de inicio.
     * @param x2 Coordenada X de destino.
     * @param y2 Coordenada Y de destino.
     * @param d  Longitud de la base de la flecha.
     * @param h  Altura de la flecha.
     */
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
        if (grafo == null || posiciones == null)
            return;
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

     // Dibuja aristas
        for (int i = 0; i < grafo.numeroVertices; i++) {
            for (Arista arista : grafo.listaAdyacencia.get(i)) {
                Point p1 = posiciones[i];
                Point p2 = posiciones[arista.destino];

                // Configurar el grosor de la línea en función del tamaño del vértice
                float grosorLinea = RADIO_VERTICE / 5.0f; // Grosor proporcional al radio del vértice
                Stroke strokeAnterior = g2.getStroke(); // Guardar el stroke actual
                g2.setStroke(new BasicStroke(grosorLinea)); // Establecer el nuevo grosor

                if (grafo.dirigido) {
                    if (highlightedEdges != null && highlightedEdges[i][arista.destino]) {
                        g2.setColor(Color.ORANGE);
                    } else {
                        g2.setColor(Color.BLACK);
                    }
                    drawArrowLine(g2, p1.x, p1.y, p2.x, p2.y);
                } else {
                    if (i < arista.destino) {
                        if (highlightedEdges != null
                                && (highlightedEdges[i][arista.destino] || highlightedEdges[arista.destino][i])) {
                            g2.setColor(Color.ORANGE);
                        } else {
                            g2.setColor(Color.BLACK);
                        }
                        g2.drawLine(p1.x, p1.y, p2.x, p2.y);
                    }
                }

                // Restaurar el grosor de la línea
                g2.setStroke(strokeAnterior);

                // Calcular el punto medio de la arista
                int midX = (p1.x + p2.x) / 2, midY = (p1.y + p2.y) / 2;

                // Configurar el tamaño de la fuente del peso en función del tamaño del vértice
                int fontSize = (int) (RADIO_VERTICE * 0.8); // Tamaño proporcional al radio del vértice
                //Font fuenteActual = g2.getFont(); // Obtener la fuente actual
                //Font nuevaFuente = fuenteActual.deriveFont((float) fontSize); // Derivar la fuente con el nuevo tamaño
                Font font = new Font("SansSerif", Font.PLAIN, fontSize); // Crear la fuente
                g2.setFont(font); // Establecer la nueva fuente

                // Calcular dimensiones del texto
                FontMetrics fm = g2.getFontMetrics();
                String pesoTexto = String.valueOf(arista.peso);
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
        }

     // Dibuja nodos
        for (int i = 0; i < grafo.numeroVertices; i++) {
            Point p = posiciones[i];
            Color nodoColor = (highlightedVertices != null && highlightedVertices[i] != null) ? highlightedVertices[i]
                    : (nodosVisitados.contains(i) ? Color.GREEN : Color.RED);
            /*
            // Dibujar el vértice
            if (i == verticeSeleccionadoParaArista) {
                // Vértice resaltado: relleno amarillo y aro gris
                g.setColor(Color.YELLOW);
                g.fillOval(p.x - RADIO_VERTICE, p.y - RADIO_VERTICE, RADIO_VERTICE * 2, RADIO_VERTICE * 2);
                g.setColor(Color.GRAY);
                g.drawOval(p.x - RADIO_VERTICE - 3, p.y - RADIO_VERTICE - 3, RADIO_VERTICE * 2 + 6, RADIO_VERTICE * 2 + 6);
            }*/
            
            g2.setColor(nodoColor);
            g2.fillOval(p.x - RADIO_VERTICE, p.y - RADIO_VERTICE, RADIO_VERTICE * 2, RADIO_VERTICE * 2);

            // Configurar el color del texto
            if (nodoColor == Color.RED) {
                g2.setColor(Color.WHITE);
            } else {
                g2.setColor(Color.BLACK);
            }

            // Calcular el tamaño de la fuente en función del radio del vértice
            int fontSize = (int) (RADIO_VERTICE * 1.2); // Tamaño proporcional al radio
            Font font = new Font("SansSerif", Font.BOLD, fontSize); // Crear la fuente
            g2.setFont(font); // Establecer la fuente

            // Calcular la posición del texto para centrarlo en el vértice
            FontMetrics metrics = g2.getFontMetrics();
            String texto = String.valueOf(i);
            int textoWidth = metrics.stringWidth(texto); // Ancho del texto
            int textoHeight = metrics.getHeight(); // Altura del texto

            // Dibujar el texto centrado en el vértice
            int textoX = p.x - textoWidth / 2; // Centrar horizontalmente
            int textoY = p.y + textoHeight / 4; // Centrar verticalmente (ajustar según sea necesario)
            g2.drawString(texto, textoX, textoY);
        }
    }
}

// Clase principal: ventana que contiene el grafo, el panel de estructura de
// datos,
// un toolbar con las opciones en la parte superior, atajos de teclado y un
// menú.
public class VisualizacionRecorridosGrafo extends JFrame {
    private static Grafo grafo; // Instancia del grafo
    private static PanelGrafo panelGrafo; // Panel para dibujar el grafo
    private javax.swing.Timer temporizadorAlgoritmo; // Temporizador para la animación

    public VisualizacionRecorridosGrafo() {
        super("Grafo en Java Swing");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(900, 600);
        setLocationRelativeTo(null);
        setResizable(true);
        // Toolbar en la parte superior
        JToolBar barraHerramientas = new JToolBar();
        barraHerramientas.setFloatable(false);
        // barraHerramientas.setLayout(new FlowLayout(FlowLayout.LEFT));

        JButton btnCrearGrafo = new JButton("Crear Grafo (1)");
        btnCrearGrafo.addActionListener(e -> crearGrafo());
        barraHerramientas.add(btnCrearGrafo);

        JButton btnAgregarArista = new JButton("Agregar Arista (2)");
        btnAgregarArista.addActionListener(e -> agregarArista());
        barraHerramientas.add(btnAgregarArista);

        JButton btnMostrarLista = new JButton("Mostrar Lista (3)");
        btnMostrarLista.addActionListener(e -> mostrarListaAdyacencia());
        barraHerramientas.add(btnMostrarLista);

        JButton btnEliminarArista = new JButton("Eliminar Arista (4)");
        btnEliminarArista.addActionListener(e -> eliminarArista());
        barraHerramientas.add(btnEliminarArista);

        JButton btnEliminarGrafo = new JButton("Eliminar Grafo (5)");
        btnEliminarGrafo.addActionListener(e -> eliminarGrafo());
        barraHerramientas.add(btnEliminarGrafo);

        JButton btnMatrizDistancias = new JButton("Mostrar Matriz de Distancias (6)");
        btnMatrizDistancias.addActionListener(e -> mostrarMatrizDistancias());
        barraHerramientas.add(btnMatrizDistancias);

        JButton btnDijkstra = new JButton("Dijkstra (7)");
        btnDijkstra.addActionListener(e -> ejecutarDijkstra());
        barraHerramientas.add(btnDijkstra);

        JButton btnFloyd = new JButton("Floyd (8)");
        btnFloyd.addActionListener(e -> ejecutarFloyd());
        barraHerramientas.add(btnFloyd);

        JButton btnGuardarGrafo = new JButton("Guardar Grafo (9)");
        btnGuardarGrafo.addActionListener(e -> guardarGrafo());
        barraHerramientas.add(btnGuardarGrafo);

        JButton btnCargarGrafo = new JButton("Cargar Grafo (0)");
        btnCargarGrafo.addActionListener(e -> cargarGrafo());
        barraHerramientas.add(btnCargarGrafo);

        JButton btnPrim = new JButton("Prim (P)");
        btnPrim.addActionListener(e -> ejecutarPrim());
        barraHerramientas.add(btnPrim);

        JButton btnKruskal = new JButton("Kruskal (K)");
        btnKruskal.addActionListener(e -> ejecutarKruskal());
        barraHerramientas.add(btnKruskal);

        JButton btnAgregarVertice = new JButton("Agregar Vértice (A)");
        btnAgregarVertice.addActionListener(e -> agregarVerticeUI());
        barraHerramientas.add(btnAgregarVertice);
        
        JButton btnEliminarVertice = new JButton("Eliminar Vértice (E)");
        btnEliminarVertice.addActionListener(e -> eliminarVerticeUI());
        barraHerramientas.add(btnEliminarVertice);

        JButton btnSalir = new JButton("Salir (ESC)");
        btnSalir.addActionListener(e -> System.exit(0));
        barraHerramientas.add(btnSalir);

        // crear ScrollBar
        // Agregar la barra de herramientas a un JScrollPane para habilitar
        // desplazamiento
        JScrollPane scrollBarraHerramientas = new JScrollPane(barraHerramientas,
                JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);

        // Instanciar los paneles
        panelGrafo = new PanelGrafo();
        System.out.println("Medidas panelGrafo: " + panelGrafo.getWidth() + ", " + panelGrafo.getHeight());

        // Panel central que contiene el panel del grafo
        JPanel panelPrincipal = new JPanel(new BorderLayout());
        panelPrincipal.add(scrollBarraHerramientas, BorderLayout.NORTH);
        panelPrincipal.add(panelGrafo, BorderLayout.CENTER);

        // Agregar toolbar y panel principal al content pane
        // getContentPane().add(scrollBarraHerramientas, BorderLayout.NORTH);
        // getContentPane().add(barraHerramientas, BorderLayout.NORTH);
        getContentPane().add(panelPrincipal, BorderLayout.CENTER);

        configurarAtajosTeclado();
    }

    private void configurarAtajosTeclado() {
        InputMap mapaEntrada = getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
        ActionMap mapaAccion = getRootPane().getActionMap();

        mapaEntrada.put(KeyStroke.getKeyStroke("1"), "crearGrafo");
        mapaAccion.put("crearGrafo", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                crearGrafo();
            }
        });

        mapaEntrada.put(KeyStroke.getKeyStroke("2"), "agregarArista");
        mapaAccion.put("agregarArista", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                agregarArista();
            }
        });

        mapaEntrada.put(KeyStroke.getKeyStroke("3"), "mostrarLista");
        mapaAccion.put("mostrarLista", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                mostrarListaAdyacencia();
            }
        });

        mapaEntrada.put(KeyStroke.getKeyStroke("4"), "eliminarArista");
        mapaAccion.put("eliminarArista", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                eliminarArista();
            }
        });

        mapaEntrada.put(KeyStroke.getKeyStroke("5"), "eliminarGrafo");
        mapaAccion.put("eliminarGrafo", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                eliminarGrafo();
            }
        });

        mapaEntrada.put(KeyStroke.getKeyStroke("6"), "MatrizDistancias");
        mapaAccion.put("MatrizDistancias", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                mostrarMatrizDistancias();
            }
        });

        mapaEntrada.put(KeyStroke.getKeyStroke("7"), "ejecutarDijkstra");
        mapaAccion.put("ejecutarDijkstra", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                ejecutarDijkstra();
            }
        });

        mapaEntrada.put(KeyStroke.getKeyStroke("8"), "ejecutarFloyd");
        mapaAccion.put("ejecutarFloyd", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                ejecutarFloyd();
            }
        });

        mapaEntrada.put(KeyStroke.getKeyStroke("9"), "Guardar");
        mapaAccion.put("Guardar", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                guardarGrafo();
            }
        });

        mapaEntrada.put(KeyStroke.getKeyStroke("0"), "Cargar");
        mapaAccion.put("Cargar", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                cargarGrafo();
            }
        });

        // Configurar atajo de teclado
        mapaEntrada.put(KeyStroke.getKeyStroke("P"), "ejecutarPrim");
        mapaAccion.put("ejecutarPrim", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                ejecutarPrim();
            }
        });
        // Configurar atajo de teclado
        mapaEntrada.put(KeyStroke.getKeyStroke("K"), "ejecutarKruskal");
        mapaAccion.put("ejecutarKruskal", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
            	ejecutarKruskal();
            }
        });
        // Configurar atajo de teclado
        mapaEntrada.put(KeyStroke.getKeyStroke("A"), "agregarVerticeUI");
        mapaAccion.put("agregarVerticeUI", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
            	agregarVerticeUI();
            }
        });
        // Configurar atajo de teclado
        mapaEntrada.put(KeyStroke.getKeyStroke("E"), "eliminarVerticeUI");
        mapaAccion.put("eliminarVerticeUI", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
            	eliminarVerticeUI();
            }
        });

        mapaEntrada.put(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), "salir");
        mapaAccion.put("salir", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });
    }

    // Opción 1: Crear Grafo
    private void crearGrafo() {
        if (grafo != null) {
            int confirm = JOptionPane.showConfirmDialog(null,
                    "Ya existe un grafo. ¿Desea eliminarlo y crear uno nuevo?",
                    "Confirmar", JOptionPane.YES_NO_OPTION);
            if (confirm != JOptionPane.YES_OPTION) {
                return;
            }
        }
        String input = JOptionPane.showInputDialog("Ingrese el número de vértices:");
        if (input == null) {
            // Si el usuario cancela, puedes salir o hacer otra acción
            JOptionPane.showMessageDialog(null, "Operación cancelada.");
            return;
        }
        try {
            int numVertices = Integer.parseInt(input.trim());
            if (numVertices <= 0) {
                JOptionPane.showMessageDialog(null, "El número de vértices debe ser mayor que 0.");
                return;
            }
            if (numVertices > 20) {
                JOptionPane.showMessageDialog(null, "El número de vértices no puede ser mayor a 20.");
                return;
            }
            // Preguntar al usuario si el grafo es dirigido
            int directedOption = JOptionPane.showConfirmDialog(null,
                    "¿El grafo es dirigido?\n(Selecciona Sí para dirigido, No para no dirigido)",
                    "Tipo de grafo", JOptionPane.YES_NO_OPTION);
            boolean esDirigido = (directedOption == JOptionPane.YES_OPTION);

            // Crear el grafo con el número de vértices y el tipo seleccionado
            grafo = new Grafo(numVertices, esDirigido);
            panelGrafo.setGrafo(grafo);

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(null, "Entrada inválida.");
        }
    }

    // Opción 2: Agregar Arista
    private void agregarArista() {
        if (grafo == null) {
            JOptionPane.showMessageDialog(null, "Primero debe crear un grafo.");
            return;
        }
        String input = JOptionPane
                .showInputDialog("Ingrese origen, destino y peso separados por coma (origen,destino,peso):");
        if (input == null) {
            // Si el usuario cancela, puedes salir o hacer otra acción
            // JOptionPane.showMessageDialog(null, "Operación cancelada.");
            return;
        }
        try {
            String[] partes = input.split(",");
            int origen = Integer.parseInt(partes[0].trim());
            int destino = Integer.parseInt(partes[1].trim());
            int peso = Integer.parseInt(partes[2].trim());
            if (origen < 0 || origen >= grafo.numeroVertices || destino < 0 || destino >= grafo.numeroVertices) {
                JOptionPane.showMessageDialog(null, "Vértice inválido.");
                return;
            }
            for (Arista arista : grafo.listaAdyacencia.get(origen)) {
                if (arista.destino == destino) {
                    JOptionPane.showMessageDialog(null, "La arista ya existe.");
                    return;
                }
            }
            grafo.agregarArista(origen, destino, peso);
            panelGrafo.repaint();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(null, "Entrada inválida.");
        }
    }

    // Opción 3: Mostrar Lista de Adyacencia
    private void mostrarListaAdyacencia() {
        if (grafo == null) {
            JOptionPane.showMessageDialog(null, "No hay grafo creado.");
            return;
        }
        String lista = grafo.obtenerListaAdyacencia();
        JOptionPane.showMessageDialog(null, lista, "Lista de Adyacencia", JOptionPane.INFORMATION_MESSAGE);
    }

    // Opción 4: Eliminar Arista
    private void eliminarArista() {
        if (grafo == null) {
            JOptionPane.showMessageDialog(null, "Primero debe crear un grafo.");
            return;
        }
        // Verificar si el grafo tiene aristas
        boolean tieneAristas = false;
        for (int i = 0; i < grafo.numeroVertices; i++) {
            if (!grafo.listaAdyacencia.get(i).isEmpty()) {
                tieneAristas = true;
                break;
            }
        }

        if (!tieneAristas) {
            JOptionPane.showMessageDialog(null, "El grafo no tiene aristas.");
            return;
        }

        String input = JOptionPane.showInputDialog("Ingrese dos vértices (origen,destino) de la arista a eliminar:");
        if (input == null) {
            // Si el usuario cancela, puedes salir o hacer otra acción
            // JOptionPane.showMessageDialog(null, "Operación cancelada.");
            return;
        }
        try {
            String[] partes = input.split(",");
            int origen = Integer.parseInt(partes[0].trim());
            int destino = Integer.parseInt(partes[1].trim());
            if (origen < 0 || origen >= grafo.numeroVertices || destino < 0 || destino >= grafo.numeroVertices) {
                JOptionPane.showMessageDialog(null, "Vértice inválido.");
                return;
            }
            boolean existe = false;
            for (Arista arista : grafo.listaAdyacencia.get(origen)) {
                if (arista.destino == destino) {
                    existe = true;
                    break;
                }
            }
            if (!existe) {
                JOptionPane.showMessageDialog(null, "La arista no existe.");
                return;
            }
            grafo.eliminarArista(origen, destino);
            panelGrafo.repaint();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(null, "Entrada inválida.");
        }
    }

    // Opción 5: Eliminar Grafo
    private void eliminarGrafo() {
        if (grafo == null) {
            JOptionPane.showMessageDialog(null, "No hay grafo creado.");
            return;
        }
        grafo = null;
        panelGrafo.setGrafo(null);
    }

    // opcion 6: matriz
    private void mostrarMatrizDistancias() {
        if (grafo == null) {
            JOptionPane.showMessageDialog(null, "No hay grafo creado.");
            return;
        }
        String matriz = grafo.obtenerMatrizDistancias();
        JOptionPane.showMessageDialog(null, matriz, "Matriz de Distancias", JOptionPane.INFORMATION_MESSAGE);
    }

    // Opcion 7: Guardar
    private void guardarGrafo() {
        if (grafo == null) {
            JOptionPane.showMessageDialog(null, "No hay grafo creado.");
            return;
        }
        String nombreArchivo = JOptionPane.showInputDialog("Ingrese el nombre del archivo para guardar el grafo:");
        if (nombreArchivo == null || nombreArchivo.trim().isEmpty()) {
            JOptionPane.showMessageDialog(null, "Nombre de archivo inválido.");
            return;
        }
        try (FileWriter writer = new FileWriter(nombreArchivo + ".txt")) {
            // Guardar número de vértices y tipo de grafo
            writer.write(grafo.numeroVertices + "\n");
            writer.write(grafo.dirigido + "\n");

            // Guardar aristas con pesos
            for (int i = 0; i < grafo.numeroVertices; i++) {
                for (Arista arista : grafo.listaAdyacencia.get(i)) {
                    // Si el grafo es dirigido, escribimos todas las aristas
                    // Si el grafo es no dirigido, solo escribimos cuando i < destino para evitar
                    // duplicados
                    if (grafo.dirigido || i < arista.destino) {
                        writer.write(i + "," + arista.destino + "," + arista.peso + "\n");
                    }
                }
            }

            JOptionPane.showMessageDialog(null, "Grafo guardado correctamente.");
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(null, "Error al guardar el grafo.");
        }
    }

    // extra cargar grafo
    // Extra cargar grafo
    private void cargarGrafo() {
        // Verificar si ya hay un grafo cargado
        if (grafo != null) {
            int opcion = JOptionPane.showConfirmDialog(this, "Ya hay un grafo cargado. ¿Deseas cargar otro?",
                    "Cargar otro grafo", JOptionPane.YES_NO_OPTION);
            if (opcion == JOptionPane.YES_OPTION) {
                // Llamar a eliminarGrafo() para borrar el grafo actual
                eliminarGrafo();
            } else {
                return; // Si el usuario no quiere cargar otro, se sale del método
            }
        }

        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Seleccione un archivo de grafo");

        while (true) { // Bucle para permitir seleccionar otro archivo en caso de error
            int resultado = fileChooser.showOpenDialog(this);
            if (resultado == JFileChooser.APPROVE_OPTION) {
                File archivo = fileChooser.getSelectedFile();
                String nombreArchivo = archivo.getName();

                // Verificar si el archivo tiene la extensión .txt
                if (!nombreArchivo.toLowerCase().endsWith(".txt")) {
                    JOptionPane.showMessageDialog(this, "Error: Solo archivos .txt", "Error",
                            JOptionPane.ERROR_MESSAGE);
                    continue; // Volver a mostrar el diálogo para seleccionar otro archivo
                }

                try (BufferedReader reader = new BufferedReader(new FileReader(archivo))) {
                    // Leer número de vértices y tipo de grafo
                    int vertices = Integer.parseInt(reader.readLine());
                    boolean dirigido = Boolean.parseBoolean(reader.readLine());
                    grafo = new Grafo(vertices, dirigido);

                    // Leer aristas con pesos
                    String linea;
                    while ((linea = reader.readLine()) != null) {
                        String[] partes = linea.split(",");
                        int origen = Integer.parseInt(partes[0]);
                        int destino = Integer.parseInt(partes[1]);
                        int peso = Integer.parseInt(partes[2]);
                        grafo.agregarArista(origen, destino, peso);
                    }

                    panelGrafo.setGrafo(grafo);
                    System.out.println("Grafo cargado correctamente.");
                    JOptionPane.showMessageDialog(this, "Grafo cargado correctamente.", "Éxito",
                            JOptionPane.INFORMATION_MESSAGE);
                    break; // Salir del bucle si el archivo se cargó correctamente
                } catch (IOException ex) {
                    JOptionPane.showMessageDialog(this, "Error al cargar el grafo.", "Error",
                            JOptionPane.ERROR_MESSAGE);
                    break; // Salir del bucle si hay un error al leer el archivo
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(this, "Error: El archivo no tiene el formato correcto.", "Error",
                            JOptionPane.ERROR_MESSAGE);
                    break; // Salir del bucle si el archivo no tiene el formato correcto
                }
            } else {
                break; // Salir del bucle si el usuario cancela la selección
            }
        }
    }

    // Método auxiliar para animar paso a paso un camino (lista de vértices) usando
    // arreglos en lugar de hash
    // Se resalta: origen (verde), intermedios (amarillo) y destino (cian) y se van
    // "encendiendo" las aristas.
    private void animatePath(java.util.ArrayList<Integer> camino, Runnable callback) {
        final int delay = 1000; // 1 segundo entre pasos
        int n = grafo.numeroVertices;
        // Arreglo para aristas resaltadas
        boolean[][] animEdges = new boolean[n][n];
        // Arreglo para colores de vértices (inicialmente todos null)
        Color[] animVertices = new Color[n];
        // Resalta el origen en verde
        animVertices[camino.get(0)] = Color.GREEN;
        panelGrafo.setHighlightedEdges(animEdges);
        panelGrafo.setHighlightedVertices(animVertices);
        final int[] index = { 0 };
        Timer timer = new Timer(delay, null);
        timer.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (index[0] < camino.size() - 1) {
                    int origen = camino.get(index[0]);
                    int destino = camino.get(index[0] + 1);
                    animEdges[origen][destino] = true;
                    // Final verde, else amarillo.
                    if (index[0] + 1 < camino.size() - 1) {
                        animVertices[destino] = Color.YELLOW;
                    } else {
                        animVertices[destino] = Color.GREEN;
                    }
                    panelGrafo.setHighlightedEdges(animEdges);
                    panelGrafo.setHighlightedVertices(animVertices);
                    index[0]++;
                } else {
                    timer.stop();
                    if (callback != null) {
                        callback.run();
                    }
                }
            }
        });
        timer.start();
    }

    // Función para ejecutar Dijkstra y animar cada camino encontrado
    private void ejecutarDijkstra() {
        if (grafo == null) {
            JOptionPane.showMessageDialog(null, "No hay grafo creado.");
            return;
        }
        String inputInicio = JOptionPane.showInputDialog("Ingrese el vértice de inicio para Dijkstra:");
        String inputDestino = JOptionPane.showInputDialog("Ingrese el vértice de destino para Dijkstra:");
        try {
            int inicio = Integer.parseInt(inputInicio.trim());
            int destino = Integer.parseInt(inputDestino.trim());
            if (inicio < 0 || inicio >= grafo.numeroVertices || destino < 0 || destino >= grafo.numeroVertices) {
                JOptionPane.showMessageDialog(null, "Vértice inválido.");
                return;
            }
            int n = grafo.numeroVertices;
            int[] dist = new int[n];
            int[] prev = new int[n];
            Arrays.fill(dist, Integer.MAX_VALUE);
            Arrays.fill(prev, -1);
            dist[inicio] = 0;
            PriorityQueue<Integer> cola = new PriorityQueue<>(Comparator.comparingInt(v -> dist[v]));
            cola.add(inicio);
            while (!cola.isEmpty()) {
                int u = cola.poll();
                for (Arista arista : grafo.listaAdyacencia.get(u)) {
                    int v = arista.destino;
                    int peso = arista.peso;
                    if (dist[u] != Integer.MAX_VALUE && dist[u] + peso < dist[v]) {
                        dist[v] = dist[u] + peso;
                        prev[v] = u;
                        cola.remove(v);
                        cola.add(v);
                    }
                }
            }
            // Se anima el camino desde el vértice de inicio hasta el vértice de destino
            StringBuilder resumen = new StringBuilder("Resumen Dijkstra:\n");
            if (dist[destino] == Integer.MAX_VALUE) {
                resumen.append(inicio).append(" -> ").append(destino).append(": No alcanzable\n");
                JOptionPane.showMessageDialog(null, resumen.toString(), "Resultado Dijkstra",
                        JOptionPane.INFORMATION_MESSAGE);
            } else {
                java.util.ArrayList<Integer> camino = new java.util.ArrayList<>();
                int actual = destino;
                while (actual != -1) {
                    camino.add(actual);
                    actual = prev[actual];
                }
                Collections.reverse(camino);
                animatePath(camino, () -> {
                    resumen.append(inicio).append(" -> ").append(destino).append(": ").append(camino)
                            .append(" | Costo total: ").append(dist[destino]).append("\n");
                    JOptionPane.showMessageDialog(null, resumen.toString(), "Resultado Dijkstra",
                            JOptionPane.INFORMATION_MESSAGE);
                    // Limpiar grafo
                    panelGrafo.setHighlightedEdges(new boolean[grafo.numeroVertices][grafo.numeroVertices]);
                    panelGrafo.setHighlightedVertices(new Color[grafo.numeroVertices]);
                });
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(null, "Entrada inválida.");
        }
    }

    // Función para ejecutar Floyd–Warshall y animar los caminos desde un vértice a
    // todos los demás
    private void ejecutarFloyd() {
        if (grafo == null) {
            JOptionPane.showMessageDialog(null, "No hay grafo creado.");
            return;
        }
        String inputInicio = JOptionPane.showInputDialog("Ingrese el vértice de inicio para Floyd:");
        try {
            int inicio = Integer.parseInt(inputInicio.trim());
            if (inicio < 0 || inicio >= grafo.numeroVertices) {
                JOptionPane.showMessageDialog(null, "Vértice inválido.");
                return;
            }
            int n = grafo.numeroVertices;
            int[][] dist = new int[n][n];
            int[][] next = new int[n][n];
            for (int i = 0; i < n; i++) {
                Arrays.fill(dist[i], Integer.MAX_VALUE / 2);
                for (int j = 0; j < n; j++) {
                    next[i][j] = -1;
                }
                dist[i][i] = 0;
                next[i][i] = i;
            }
            for (int u = 0; u < n; u++) {
                for (Arista arista : grafo.listaAdyacencia.get(u)) {
                    int v = arista.destino;
                    dist[u][v] = arista.peso;
                    next[u][v] = v;
                }
            }
            for (int k = 0; k < n; k++) {
                for (int i = 0; i < n; i++) {
                    for (int j = 0; j < n; j++) {
                        if (dist[i][k] + dist[k][j] < dist[i][j]) {
                            dist[i][j] = dist[i][k] + dist[k][j];
                            next[i][j] = next[i][k];
                        }
                    }
                }
            }
            StringBuilder resumen = new StringBuilder("Resumen Floyd:\n");
            for (int destino = 0; destino < n; destino++) {
                if (inicio == destino)
                    continue;
                if (dist[inicio][destino] >= Integer.MAX_VALUE / 2) {
                    resumen.append(inicio).append(" -> ").append(destino).append(": No alcanzable\n");
                } else {
                    java.util.ArrayList<Integer> camino = new java.util.ArrayList<>();
                    int u = inicio;
                    camino.add(u);
                    while (u != destino) {
                        u = next[u][destino];
                        camino.add(u);
                    }
                    animatePath(camino, null);
                    resumen.append(inicio).append(" -> ").append(destino).append(": ").append(camino)
                            .append(" | Costo total: ").append(dist[inicio][destino]).append("\n");
                }
            }
            JOptionPane.showMessageDialog(null, resumen.toString(), "Resultado Floyd", JOptionPane.INFORMATION_MESSAGE);
            // Limpiar grafo
            panelGrafo.setHighlightedEdges(new boolean[grafo.numeroVertices][grafo.numeroVertices]);
            panelGrafo.setHighlightedVertices(new Color[grafo.numeroVertices]);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(null, "Entrada inválida.");
        }
    }

    private void ejecutarPrim() {
        if (grafo == null) {
            JOptionPane.showMessageDialog(null, "No hay grafo creado.");
            return;
        }
    
        // Verificar si el grafo es conexo
        if (!grafo.esConexo()) {
            JOptionPane.showMessageDialog(null, "El grafo no es conexo. El algoritmo de Prim no puede ejecutarse.");
            return;
        }
    
        // Obtener el MST usando el algoritmo de Prim
        java.util.ArrayList<Arista> mst = grafo.primMST();
    
        // Animación paso a paso del MST
        final int[] index = { 0 };
        final boolean[][] animEdges = new boolean[grafo.numeroVertices][grafo.numeroVertices];
        final Color[] animVertices = new Color[grafo.numeroVertices];
        final Color[] colores = { Color.RED, Color.BLUE, Color.GREEN, Color.ORANGE, Color.MAGENTA }; // Colores para las aristas
    
        Timer timer = new Timer(1000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (index[0] < mst.size()) {
                    Arista arista = mst.get(index[0]);
                    animEdges[arista.origen][arista.destino] = true; // Resaltar la arista
                    animVertices[arista.origen] = Color.GREEN; // Resaltar el vértice
                    animVertices[arista.destino] = Color.GREEN; // Resaltar el vértice
    
                    // Dibujar la arista con un color específico
                    panelGrafo.setHighlightedEdges(animEdges);
                    panelGrafo.setHighlightedVertices(animVertices);
                    panelGrafo.repaint(); // Forzar la actualización del panel
    
                    index[0]++;
                } else {
                    ((Timer) e.getSource()).stop();
    
                    // Mostrar el resumen del MST
                    StringBuilder resumen = new StringBuilder("Recorrido del Árbol Abarcador de Costo Mínimo (MST):\n");
                    int pesoTotal = 0;
                    for (Arista arista : mst) {
                        resumen.append(arista.origen).append(" - ").append(arista.destino).append(" (Peso: ")
                                .append(arista.peso).append(")\n");
                        pesoTotal += arista.peso;
                    }
                    resumen.append("Peso total del MST: ").append(pesoTotal);
                    JOptionPane.showMessageDialog(null, resumen.toString(), "Resultado de Prim",
                            JOptionPane.INFORMATION_MESSAGE);
    
                    // Limpiar resaltados después de mostrar el mensaje
                    panelGrafo.setHighlightedEdges(new boolean[grafo.numeroVertices][grafo.numeroVertices]);
                    panelGrafo.setHighlightedVertices(new Color[grafo.numeroVertices]);
                    panelGrafo.repaint();
                }
            }
        });
        timer.start();
    }

    
    // Nueva Opción: Ejecutar Kruskal para Árbol Abarcador de Costo Mínimo
    private void ejecutarKruskal() {
        if (grafo == null) {
            JOptionPane.showMessageDialog(null, "No hay grafo creado.");
            return;
        }
        if (grafo.dirigido) {
            JOptionPane.showMessageDialog(null, "El algoritmo de Kruskal solo aplica a grafos no dirigidos.");
            return;
        }
        int numVertices = grafo.numeroVertices;
        // Recopilar todas las aristas sin duplicados (solo si i < destino)
        java.util.ArrayList<Edge> edges = new java.util.ArrayList<>();
        for (int i = 0; i < numVertices; i++) {
            for (Arista arista : grafo.listaAdyacencia.get(i)) {
                if (i < arista.destino) {
                    edges.add(new Edge(i, arista.destino, arista.peso));
                }
            }
        }
        // Ordenar las aristas por peso
        edges.sort((e1, e2) -> Integer.compare(e1.peso, e2.peso));
        // Disjoint set: inicializar padre
        int[] parent = new int[numVertices];
        for (int i = 0; i < numVertices; i++) {
            parent[i] = i;
        }
        // Función find con compresión de ruta
        java.util.function.IntUnaryOperator find = new java.util.function.IntUnaryOperator() {
            @Override
            public int applyAsInt(int i) {
                if (parent[i] != i)
                    parent[i] = this.applyAsInt(parent[i]);
                return parent[i];
            }
        };
        // Procesar aristas del menor al mayor
        java.util.ArrayList<Edge> mst = new java.util.ArrayList<>();
        int totalCost = 0;
        for (Edge edge : edges) {
            int rootU = find.applyAsInt(edge.origen);
            int rootV = find.applyAsInt(edge.destino);
            if (rootU != rootV) {
                mst.add(edge);
                totalCost += edge.peso;
                parent[rootV] = rootU;
            }
        }
        // Si el grafo no es conexo, no se forma árbol abarcador
        /* Un grafo no dirigido es conexo si, para cualquier par de vértices u y v, 
         * existe una secuencia de aristas que conecta u con v.*/
        if (mst.size() != numVertices - 1) {
            JOptionPane.showMessageDialog(null, "El grafo no es conexo, no se puede formar un Árbol Abarcador.");
            return;
        }

        // Construir mensaje de salida
        /*
        StringBuilder resultado = new StringBuilder();
        resultado.append("Árbol Abarcador de costo mínimo (Kruskal):\n");
        for (Edge edge : mst) {
            resultado.append(edge.origen).append(" - ").append(edge.destino)
                    .append(" (Peso: ").append(edge.peso).append(")\n");
        }
        resultado.append("Costo Total: ").append(totalCost);
        JOptionPane.showMessageDialog(null, resultado.toString(), "Kruskal", JOptionPane.INFORMATION_MESSAGE);
        */
        // Llamar al método de animación
        animarKruskal(mst, totalCost);
    }

    // Clase auxiliar para representar aristas en Kruskal
    private static class Edge {
        int origen;
        int destino;
        int peso;

        Edge(int origen, int destino, int peso) {
            this.origen = origen;
            this.destino = destino;
            this.peso = peso;
        }
    }
    
    /**
     * Método que maneja la animación del algoritmo de Kruskal
     */
    private void animarKruskal(java.util.ArrayList<Edge> mst, int totalCost) {
        final int[] index = { 0 };
        final boolean[][] animEdges = new boolean[grafo.numeroVertices][grafo.numeroVertices];
        final Color[] animVertices = new Color[grafo.numeroVertices];

        Timer timer = new Timer(1000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (index[0] < mst.size()) {
                    Edge edge = mst.get(index[0]);
                    animEdges[edge.origen][edge.destino] = true;
                    animEdges[edge.destino][edge.origen] = true;
                    animVertices[edge.origen] = Color.GREEN;
                    animVertices[edge.destino] = Color.GREEN;

                    panelGrafo.setHighlightedEdges(animEdges);
                    panelGrafo.setHighlightedVertices(animVertices);
                    panelGrafo.repaint();

                    index[0]++;
                } else {
                    ((Timer) e.getSource()).stop();

                    // Mostrar el resumen del MST
                    StringBuilder resultado = new StringBuilder("Árbol Abarcador de Costo Mínimo (Kruskal):\n");
                    for (Edge edge : mst) {
                        resultado.append(edge.origen).append(" - ").append(edge.destino)
                                .append(" (Peso: ").append(edge.peso).append(")\n");
                    }
                    resultado.append("Costo Total: ").append(totalCost);
                    JOptionPane.showMessageDialog(null, resultado.toString(), "Kruskal", JOptionPane.INFORMATION_MESSAGE);
                }
            }
        });

        timer.start();
    }

    // Método para agregar un vértice usando la interfaz
    private void agregarVerticeUI() {
        if (grafo == null) {
            JOptionPane.showMessageDialog(null, "Primero debe crear un grafo.");
            return;
        }
        int confirm = JOptionPane.showConfirmDialog(null, "¿Desea agregar un vértice?");
        if (confirm != JOptionPane.YES_OPTION)
            return;

        // Se llama al método ya implementado en Grafo.
        grafo.agregarVertice();
        // Actualiza el panel para recalcular posiciones y refrescar la visualización
        panelGrafo.setGrafo(grafo);
    }

    // Método para eliminar un vértice usando la interfaz
    private void eliminarVerticeUI() {
        if (grafo == null) {
            JOptionPane.showMessageDialog(null, "Primero debe crear un grafo.");
            return;
        }
        String input = JOptionPane.showInputDialog("Ingrese el índice del vértice a eliminar:");
        if (input == null)
            return;
        try {
            int v = Integer.parseInt(input.trim());
            if (v < 0 || v >= grafo.numeroVertices) {
                JOptionPane.showMessageDialog(null, "Vértice inválido.");
                return;
            }
            int confirm = JOptionPane.showConfirmDialog(null, "¿Está seguro de eliminar el vértice " + v + "?");
            if (confirm != JOptionPane.YES_OPTION)
                return;

            // Se utiliza el método de la clase Grafo.
            grafo.eliminarVertice(v);
            panelGrafo.setGrafo(grafo);
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(null, "Entrada inválida.");
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            VisualizacionRecorridosGrafo ventana = new VisualizacionRecorridosGrafo();
            ventana.setVisible(true);
        });
    }
}
