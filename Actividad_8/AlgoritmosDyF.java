package algoritmosdyf;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.PriorityQueue;

// Clase que representa una arista con destino y cossto
class Arista {
    int destino;
    int peso;

    public Arista(int destino, int peso) {
        this.destino = destino;
        this.peso = peso;
    }
}

// Clase que representa el grafo utilizando listas de adyacencia con ponderación
class Grafo {
    int numeroVertices;
    java.util.ArrayList<java.util.ArrayList<Arista>> listaAdyacencia;  // Lista de adyacencia de aristas ponderadas
    java.util.ArrayList<Boolean> visitados;                              // Seguimiento de nodos visitados
    boolean dirigido;                                                   // true: grafo dirigido, false: no dirigido

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
    
    // Retorna una representación en cadena de la lista de adyacencia con ponderación
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
}

// Panel para dibujar el grafo (distribución circular) y marcar los nodos visitados
class PanelGrafo extends JPanel {
    private Grafo grafo;
    private java.util.ArrayList<Integer> nodosVisitados = new java.util.ArrayList<>();
    // Arreglo para indicar qué aristas están resaltadas: [origen][destino]
    private boolean[][] highlightedEdges;
    // Arreglo para indicar el color de cada vértice (null: sin resaltar)
    private Color[] highlightedVertices;
    
    public PanelGrafo(){
        // Opcional: setBackground(new Color(0, 128, 255));
    }

    public void setGrafo(Grafo g) {
        this.grafo = g;
        nodosVisitados.clear();
        if(g != null) {
            highlightedEdges = new boolean[g.numeroVertices][g.numeroVertices];
            highlightedVertices = new Color[g.numeroVertices];
        }
        repaint();
    }
    
    public void setNodosVisitados(java.util.ArrayList<Integer> visitados) {
        this.nodosVisitados = new java.util.ArrayList<>(visitados);
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
     */
    private void drawArrowLine(Graphics2D g2, int x1, int y1, int x2, int y2, int d, int h) {
        int nodeRadius = 15; // Para que la flecha no se dibuje dentro del nodo destino
        int dx = x2 - x1, dy = y2 - y1;
        double D = Math.sqrt(dx * dx + dy * dy);
        if (D == 0) return;  
        double cos = dx / D, sin = dy / D;
        double newD = D - nodeRadius;
        int newX2 = x1 + (int)(newD * cos);
        int newY2 = y1 + (int)(newD * sin);
        
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
        
        g2.drawLine(x1, y1, newX2, newY2);
        int[] xpoints = { newX2, x2a, x3 };
        int[] ypoints = { newY2, y2a, y3 };
        g2.fillPolygon(xpoints, ypoints, 3);
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (grafo == null) return;
        int n = grafo.numeroVertices;
        int ancho = getWidth(), alto = getHeight();
        int radio = Math.min(ancho, alto) / 2 - 50;
        int centroX = ancho / 2, centroY = alto / 2;
        Point[] posiciones = new Point[n];
        for (int i = 0; i < n; i++) {
            double angulo = 2 * Math.PI * i / n;
            int x = centroX + (int)(radio * Math.cos(angulo));
            int y = centroY + (int)(radio * Math.sin(angulo));
            posiciones[i] = new Point(x, y);
        }
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        // Dibuja aristas
        for (int i = 0; i < n; i++) {
            for (Arista arista : grafo.listaAdyacencia.get(i)) {
                Point p1 = posiciones[i];
                Point p2 = posiciones[arista.destino];
                if (grafo.dirigido) {
                    if (highlightedEdges != null && highlightedEdges[i][arista.destino]) {
                        g2.setColor(Color.BLUE);
                    } else {
                        g2.setColor(Color.BLACK);
                    }
                    drawArrowLine(g2, p1.x, p1.y, p2.x, p2.y, 10, 5);
                } else {
                    if (i < arista.destino) {
                        if (highlightedEdges != null && (highlightedEdges[i][arista.destino] || highlightedEdges[arista.destino][i])) {
                            g2.setColor(Color.BLUE);
                        } else {
                            g2.setColor(Color.BLACK);
                        }
                        g2.drawLine(p1.x, p1.y, p2.x, p2.y);
                    }
                }
                int midX = (p1.x + p2.x) / 2, midY = (p1.y + p2.y) / 2;
                g2.setColor(Color.BLUE);
                g2.drawString(String.valueOf(arista.peso), midX, midY);
            }
        }
        // Dibuja nodos: si hay color resaltado en el arreglo se usa; de lo contrario, si fue visitado se pinta en verde, sino en rojo.
        for (int i = 0; i < n; i++) {
            Point p = posiciones[i];
            Color nodoColor = (highlightedVertices != null && highlightedVertices[i] != null) ? highlightedVertices[i]
                              : (nodosVisitados.contains(i) ? Color.GREEN : Color.RED);
            g2.setColor(nodoColor);
            g2.fillOval(p.x - 15, p.y - 15, 30, 30);
            g2.setColor(Color.WHITE);
            g2.drawString(String.valueOf(i), p.x - 5, p.y + 5);
        }
    }
}

// Panel para mostrar la estructura de datos (pila o cola)
class PanelEstructuraDatos extends JPanel {
    private java.util.ArrayList<Integer> estructuraDatos = new java.util.ArrayList<>();
    private String tipo = ""; // "Pila" o "Cola"
    
    public PanelEstructuraDatos() {
        setPreferredSize(new Dimension(150, 600));
    }
    
    public void setEstructuraDatos(java.util.ArrayList<Integer> ds, String tipo) {
        this.estructuraDatos = new java.util.ArrayList<>(ds);
        this.tipo = tipo;
        repaint();
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.setColor(Color.BLACK);
        g.drawString(tipo, 10, 20);
        int anchoCaja = 40, altoCaja = 30, espacio = 10, inicioX = 10, inicioY = 40;
        for (int i = 0; i < estructuraDatos.size(); i++) {
            int x = inicioX, y = inicioY + i * (altoCaja + espacio);
            g.setColor(Color.LIGHT_GRAY);
            g.fillRect(x, y, anchoCaja, altoCaja);
            g.setColor(Color.BLACK);
            g.drawRect(x, y, anchoCaja, altoCaja);
            g.drawString(String.valueOf(estructuraDatos.get(i)), x + anchoCaja/2 - 5, y + altoCaja/2 + 5);
        }
    }
}

// Clase principal: ventana con la interfaz gráfica
public class AlgoritmosDyF extends JFrame {
    private static Grafo grafo;                      
    private static PanelGrafo panelGrafo;              
    private static PanelEstructuraDatos panelEstructura; 
    private javax.swing.Timer temporizadorAlgoritmo;   
    
    public AlgoritmosDyF() {
        super("Grafo en Java Swing");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(900, 600);
        setLocationRelativeTo(null);
        
        JToolBar barraHerramientas = new JToolBar();
        barraHerramientas.setFloatable(false);
        
        JButton btnCrearGrafo = new JButton("Crear Grafo (1)");
        btnCrearGrafo.addActionListener(e -> crearGrafo());
        barraHerramientas.add(btnCrearGrafo);
        
        JButton btnAgregarArista = new JButton("Agregar Arista (2)");
        btnAgregarArista.addActionListener(e -> agregarArista());
        barraHerramientas.add(btnAgregarArista);
        
        JButton btnMostrarLista = new JButton("Mostrar Lista (3)");
        btnMostrarLista.addActionListener(e -> mostrarListaAdyacencia());
        barraHerramientas.add(btnMostrarLista);
        
        JButton btnDFS = new JButton("DFS (4)");
        btnDFS.addActionListener(e -> ejecutarDFS());
        barraHerramientas.add(btnDFS);
        
        JButton btnBFS = new JButton("BFS (5)");
        btnBFS.addActionListener(e -> ejecutarBFS());
        barraHerramientas.add(btnBFS);
        
        JButton btnEliminarArista = new JButton("Eliminar Arista (6)");
        btnEliminarArista.addActionListener(e -> eliminarArista());
        barraHerramientas.add(btnEliminarArista);
        
        JButton btnEliminarGrafo = new JButton("Eliminar Grafo (7)");
        btnEliminarGrafo.addActionListener(e -> eliminarGrafo());
        barraHerramientas.add(btnEliminarGrafo);
        
        JButton btnDijkstra = new JButton("Dijkstra");
        btnDijkstra.addActionListener(e -> ejecutarDijkstra());
        barraHerramientas.add(btnDijkstra);
        
        JButton btnFloyd = new JButton("Floyd");
        btnFloyd.addActionListener(e -> ejecutarFloyd());
        barraHerramientas.add(btnFloyd);
        
        JButton btnSalir = new JButton("Salir (8)");
        btnSalir.addActionListener(e -> System.exit(0));
        barraHerramientas.add(btnSalir);
        
        panelGrafo = new PanelGrafo();
        panelEstructura = new PanelEstructuraDatos();
        
        JPanel panelPrincipal = new JPanel(new BorderLayout());
        panelPrincipal.add(panelGrafo, BorderLayout.CENTER);
        panelPrincipal.add(panelEstructura, BorderLayout.EAST);
        
        getContentPane().add(barraHerramientas, BorderLayout.NORTH);
        getContentPane().add(panelPrincipal, BorderLayout.CENTER);
        
        JMenuBar barraMenu = new JMenuBar();
        JMenu menu = new JMenu("Opciones");
        JMenuItem miCrearGrafo = new JMenuItem("Crear Grafo (1)");
        miCrearGrafo.addActionListener(e -> crearGrafo());
        menu.add(miCrearGrafo);
        JMenuItem miAgregarArista = new JMenuItem("Agregar Arista (2)");
        miAgregarArista.addActionListener(e -> agregarArista());
        menu.add(miAgregarArista);
        JMenuItem miMostrarLista = new JMenuItem("Mostrar Lista (3)");
        miMostrarLista.addActionListener(e -> mostrarListaAdyacencia());
        menu.add(miMostrarLista);
        JMenuItem miDFS = new JMenuItem("DFS (4)");
        miDFS.addActionListener(e -> ejecutarDFS());
        menu.add(miDFS);
        JMenuItem miBFS = new JMenuItem("BFS (5)");
        miBFS.addActionListener(e -> ejecutarBFS());
        menu.add(miBFS);
        JMenuItem miEliminarArista = new JMenuItem("Eliminar Arista (6)");
        miEliminarArista.addActionListener(e -> eliminarArista());
        menu.add(miEliminarArista);
        JMenuItem miEliminarGrafo = new JMenuItem("Eliminar Grafo (7)");
        miEliminarGrafo.addActionListener(e -> eliminarGrafo());
        menu.add(miEliminarGrafo);
        JMenuItem miDijkstra = new JMenuItem("Dijkstra");
        miDijkstra.addActionListener(e -> ejecutarDijkstra());
        menu.add(miDijkstra);
        JMenuItem miFloyd = new JMenuItem("Floyd");
        miFloyd.addActionListener(e -> ejecutarFloyd());
        menu.add(miFloyd);
        JMenuItem miSalir = new JMenuItem("Salir (8)");
        miSalir.addActionListener(e -> System.exit(0));
        menu.add(miSalir);
        barraMenu.add(menu);
        setJMenuBar(barraMenu);
        
        configurarAtajosTeclado();
    }
    
    private void configurarAtajosTeclado() {
        InputMap mapaEntrada = getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
        ActionMap mapaAccion = getRootPane().getActionMap();
        mapaEntrada.put(KeyStroke.getKeyStroke("1"), "crearGrafo");
        mapaAccion.put("crearGrafo", new AbstractAction() { public void actionPerformed(ActionEvent e) { crearGrafo(); }});
        mapaEntrada.put(KeyStroke.getKeyStroke("2"), "agregarArista");
        mapaAccion.put("agregarArista", new AbstractAction() { public void actionPerformed(ActionEvent e) { agregarArista(); }});
        mapaEntrada.put(KeyStroke.getKeyStroke("3"), "mostrarLista");
        mapaAccion.put("mostrarLista", new AbstractAction() { public void actionPerformed(ActionEvent e) { mostrarListaAdyacencia(); }});
        mapaEntrada.put(KeyStroke.getKeyStroke("4"), "ejecutarDFS");
        mapaAccion.put("ejecutarDFS", new AbstractAction() { public void actionPerformed(ActionEvent e) { ejecutarDFS(); }});
        mapaEntrada.put(KeyStroke.getKeyStroke("5"), "ejecutarBFS");
        mapaAccion.put("ejecutarBFS", new AbstractAction() { public void actionPerformed(ActionEvent e) { ejecutarBFS(); }});
        mapaEntrada.put(KeyStroke.getKeyStroke("6"), "eliminarArista");
        mapaAccion.put("eliminarArista", new AbstractAction() { public void actionPerformed(ActionEvent e) { eliminarArista(); }});
        mapaEntrada.put(KeyStroke.getKeyStroke("7"), "eliminarGrafo");
        mapaAccion.put("eliminarGrafo", new AbstractAction() { public void actionPerformed(ActionEvent e) { eliminarGrafo(); }});
        mapaEntrada.put(KeyStroke.getKeyStroke("8"), "salir");
        mapaAccion.put("salir", new AbstractAction() { public void actionPerformed(ActionEvent e) { System.exit(0); }});
    }
    
    // Opción 1: Crear Grafo
    private void crearGrafo() {
        if (grafo != null) {
            int confirm = JOptionPane.showConfirmDialog(null, "Ya existe un grafo. ¿Desea eliminarlo y crear uno nuevo?", "Confirmar", JOptionPane.YES_NO_OPTION);
            if (confirm != JOptionPane.YES_OPTION) return;
        }
        String input = JOptionPane.showInputDialog("Ingrese el número de vértices:");
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
            int directedOption = JOptionPane.showConfirmDialog(null,
                    "¿El grafo es dirigido?\n(Selecciona Sí para dirigido, No para no dirigido)",
                    "Tipo de grafo", JOptionPane.YES_NO_OPTION);
            boolean esDirigido = (directedOption == JOptionPane.YES_OPTION);
            grafo = new Grafo(numVertices, esDirigido);
            panelGrafo.setGrafo(grafo);
            panelEstructura.setEstructuraDatos(new java.util.ArrayList<>(), "");
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
        String input = JOptionPane.showInputDialog("Ingrese origen, destino y peso separados por coma (origen,destino,peso):");
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
    
    // Opción 4: DFS
    private void ejecutarDFS() {
        if (grafo == null) {
            JOptionPane.showMessageDialog(null, "No hay grafo creado.");
            return;
        }
        String input = JOptionPane.showInputDialog("Ingrese el vértice de inicio para DFS:");
        try {
            int inicio = Integer.parseInt(input.trim());
            if (inicio < 0 || inicio >= grafo.numeroVertices) {
                JOptionPane.showMessageDialog(null, "Vértice inválido.");
                return;
            }
            animarDFS(inicio);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(null, "Entrada inválida.");
        }
    }
    
    // Opción 5: BFS
    private void ejecutarBFS() {
        if (grafo == null) {
            JOptionPane.showMessageDialog(null, "No hay grafo creado.");
            return;
        }
        String input = JOptionPane.showInputDialog("Ingrese el vértice de inicio para BFS:");
        try {
            int inicio = Integer.parseInt(input.trim());
            if (inicio < 0 || inicio >= grafo.numeroVertices) {
                JOptionPane.showMessageDialog(null, "Vértice inválido.");
                return;
            }
            animarBFS(inicio);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(null, "Entrada inválida.");
        }
    }
    
    // Opción 6: Eliminar Arista
    private void eliminarArista() {
        if (grafo == null) {
            JOptionPane.showMessageDialog(null, "No hay grafo creado.");
            return;
        }
        String input = JOptionPane.showInputDialog("Ingrese dos vértices (origen,destino) de la arista a eliminar:");
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
    
    // Opción 7: Eliminar Grafo
    private void eliminarGrafo() {
        if (grafo == null) {
            JOptionPane.showMessageDialog(null, "No hay grafo creado.");
            return;
        }
        grafo = null;
        panelGrafo.setGrafo(null);
        panelEstructura.setEstructuraDatos(new java.util.ArrayList<>(), "");
    }
    
    // Método auxiliar para animar paso a paso un camino (lista de vértices) usando arreglos en lugar de hash
    // Se resalta: origen (verde), intermedios (amarillo) y destino (cian) y se van "encendiendo" las aristas.
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
        final int[] index = {0};
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
        int n = grafo.numeroVertices;
        int[] dist = new int[n];
        int[] prev = new int[n];
        Arrays.fill(dist, Integer.MAX_VALUE);
        Arrays.fill(prev, -1);
        dist[0] = 0;
        PriorityQueue<Integer> cola = new PriorityQueue<>(Comparator.comparingInt(v -> dist[v]));
        cola.add(0);
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
        // Se anima cada camino desde 0 hasta cada vértice (v = 1..n-1)
        StringBuilder resumen = new StringBuilder("Resumen Dijkstra:\n");
        animateNextPathDijkstra(1, n, prev, dist, resumen, () -> {
            JOptionPane.showMessageDialog(null, resumen.toString(), "Resultado Dijkstra", JOptionPane.INFORMATION_MESSAGE);
            //limpiar grafo
            panelGrafo.setHighlightedEdges(new boolean[grafo.numeroVertices][grafo.numeroVertices]);
                panelGrafo.setHighlightedVertices(new Color[grafo.numeroVertices]);
        });
    }
    
    // Método auxiliar para animar secuencialmente cada camino en Dijkstra
    private void animateNextPathDijkstra(int v, int n, int[] prev, int[] dist, StringBuilder resumen, Runnable finalCallback) {
        if (v >= n) {
            finalCallback.run();
            return;
        }
        if (dist[v] == Integer.MAX_VALUE) {
            resumen.append("0 -> ").append(v).append(": No alcanzable\n");
            animateNextPathDijkstra(v + 1, n, prev, dist, resumen, finalCallback);
        } else {
            java.util.ArrayList<Integer> camino = new java.util.ArrayList<>();
            int actual = v;
            while (actual != -1) {
                camino.add(actual);
                actual = prev[actual];
            }
            Collections.reverse(camino);
            animatePath(camino, () -> {
                resumen.append("0 -> ").append(v).append(": ").append(camino)
                       .append(" | Costo total: ").append(dist[v]).append("\n");
                new Timer(500, new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        ((Timer)e.getSource()).stop();
                        animateNextPathDijkstra(v + 1, n, prev, dist, resumen, finalCallback);
                    }
                }).start();
            });
        }
    }
    
    // Función para ejecutar Floyd–Warshall y animar cada camino desde 0 hasta cada vértice
    private void ejecutarFloyd() {
        if (grafo == null) {
            JOptionPane.showMessageDialog(null, "No hay grafo creado.");
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
        animateNextPathFloyd(1, n, next, dist, resumen, () -> {
            JOptionPane.showMessageDialog(null, resumen.toString(), "Resultado Floyd", JOptionPane.INFORMATION_MESSAGE);
            //limpiar grafo despues de
            panelGrafo.setHighlightedEdges(new boolean[grafo.numeroVertices][grafo.numeroVertices]);
                panelGrafo.setHighlightedVertices(new Color[grafo.numeroVertices]);
        });
    }
    
    // Método auxiliar para animar cada camino en Floyd desde 0 a cada vértice
    private void animateNextPathFloyd(int v, int n, int[][] next, int[][] dist, StringBuilder resumen, Runnable finalCallback) {
        if (v >= n) {
            finalCallback.run();
            return;
        }
        if (dist[0][v] >= Integer.MAX_VALUE / 2) {
            resumen.append("0 -> ").append(v).append(": No alcanzable\n");
            animateNextPathFloyd(v + 1, n, next, dist, resumen, finalCallback);
        } else {
            java.util.ArrayList<Integer> camino = new java.util.ArrayList<>();
            int u = 0;
            camino.add(u);
            while (u != v) {
                u = next[u][v];
                camino.add(u);
            }
            animatePath(camino, () -> {
                resumen.append("0 -> ").append(v).append(": ").append(camino)
                       .append(" | Costo total: ").append(dist[0][v]).append("\n");
                new Timer(500, new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        ((Timer)e.getSource()).stop();
                        animateNextPathFloyd(v + 1, n, next, dist, resumen, finalCallback);
                    }
                }).start();
            });
        }
    }
    
    
    private void animarDFS(int inicio) {
        grafo.resetearVisitados();
        java.util.ArrayList<Integer> ordenVisitados = new java.util.ArrayList<>();
        java.util.Stack<Integer> pila = new java.util.Stack<>();
        pila.push(inicio);
        panelEstructura.setEstructuraDatos(new java.util.ArrayList<>(pila), "Pila");
        if (temporizadorAlgoritmo != null && temporizadorAlgoritmo.isRunning()) {
            temporizadorAlgoritmo.stop();
        }
        temporizadorAlgoritmo = new javax.swing.Timer(3000, new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                panelEstructura.setEstructuraDatos(new java.util.ArrayList<>(pila), "Pila");
                if (pila.isEmpty()) {
                    ((javax.swing.Timer)e.getSource()).stop();
                    return;
                }
                int actual = pila.pop();
                if (!grafo.visitados.get(actual)) {
                    grafo.visitados.set(actual, true);
                    ordenVisitados.add(actual);
                    panelGrafo.setNodosVisitados(ordenVisitados);
                    for (int i = grafo.listaAdyacencia.get(actual).size() - 1; i >= 0; i--) {
                        Arista arista = grafo.listaAdyacencia.get(actual).get(i);
                        int vecino = arista.destino;
                        if (!grafo.visitados.get(vecino)) {
                            pila.push(vecino);
                        }
                    }
                }
                panelGrafo.repaint();
                panelEstructura.repaint();
            }
        });
        temporizadorAlgoritmo.start();
    }

    private void animarBFS(int inicio) {
        grafo.resetearVisitados();
        java.util.ArrayList<Integer> ordenVisitados = new java.util.ArrayList<>();
        java.util.Queue<Integer> cola = new java.util.LinkedList<>();
        cola.add(inicio);
        java.util.ArrayList<Boolean> visitadosBFS = new java.util.ArrayList<>(Collections.nCopies(grafo.numeroVertices, false));
        visitadosBFS.set(inicio, true);
        panelEstructura.setEstructuraDatos(new java.util.ArrayList<>(cola), "Cola");
        if (temporizadorAlgoritmo != null && temporizadorAlgoritmo.isRunning()) {
            temporizadorAlgoritmo.stop();
        }
        temporizadorAlgoritmo = new javax.swing.Timer(3000, new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                panelEstructura.setEstructuraDatos(new java.util.ArrayList<>(cola), "Cola");
                if (cola.isEmpty()) {
                    ((javax.swing.Timer)e.getSource()).stop();
                    return;
                }
                int actual = cola.poll();
                if (!grafo.visitados.get(actual)) {
                    grafo.visitados.set(actual, true);
                    ordenVisitados.add(actual);
                    panelGrafo.setNodosVisitados(ordenVisitados);
                    for (Arista arista : grafo.listaAdyacencia.get(actual)) {
                        if (!visitadosBFS.get(arista.destino)) {
                            visitadosBFS.set(arista.destino, true);
                            cola.add(arista.destino);
                        }
                    }
                }
                panelGrafo.repaint();
                panelEstructura.repaint();
            }
        });
        temporizadorAlgoritmo.start();
    }
        
    public static void main(String[] args) {
        UIManager.put("OptionPane.yesButtonText", "Sí");
         UIManager.put("OptionPane.noButtonText", "No");
        SwingUtilities.invokeLater(() -> {
            new AlgoritmosDyF().setVisible(true);
        });
    }
}
