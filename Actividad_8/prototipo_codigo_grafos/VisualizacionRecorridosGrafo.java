import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;
import java.util.Queue;

// Clase que representa el grafo utilizando listas de adyacencia
class Grafo {
    int numeroVertices;
    ArrayList<ArrayList<Pair<Integer, Integer>>> listaAdyacencia; // Lista de adyacencia con pesos
    ArrayList<Boolean> visitados;
    boolean dirigido;

    // Constructor
    public Grafo(int vertices, boolean dirigido) {
        this.numeroVertices = vertices;
        this.dirigido = dirigido;
        listaAdyacencia = new ArrayList<>(vertices);
        visitados = new ArrayList<>(vertices);
        for (int i = 0; i < vertices; i++) {
            listaAdyacencia.add(new ArrayList<>());
            visitados.add(false);
        }
    }

    // Clase auxiliar para representar pares (destino, peso)
    class Pair<A, B> {
        A first;
        B second;
        Pair(A first, B second) {
            this.first = first;
            this.second = second;
        }
    }

    // Agregar una arista con peso
    public void agregarArista(int origen, int destino, int peso) {
        if (listaAdyacencia.get(origen).stream().anyMatch(p -> p.first == destino)) {
            return; // Evita duplicados
        }
        listaAdyacencia.get(origen).add(new Pair<>(destino, peso));
        if (!dirigido) {
            listaAdyacencia.get(destino).add(new Pair<>(origen, peso));
        }
    }

    // Eliminar una arista
    public void eliminarArista(int origen, int destino) {
        listaAdyacencia.get(origen).removeIf(p -> p.first == destino);
        if (!dirigido) {
            listaAdyacencia.get(destino).removeIf(p -> p.first == origen);
        }
    }

    // Obtener la lista de adyacencia con pesos
    public String obtenerListaAdyacencia() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < numeroVertices; i++) {
            sb.append(i).append(": ");
            for (Pair<Integer, Integer> p : listaAdyacencia.get(i)) {
                sb.append(p.first).append("(").append(p.second).append(") -> ");
            }
            sb.append("NULL\n");
        }
        return sb.toString();
    }

    public void resetearVisitados() {
        if (visitados == null || visitados.size() != numeroVertices) {
            // Si la lista no está inicializada o no tiene el tamaño correcto, la reiniciamos
            visitados = new ArrayList<>(Collections.nCopies(numeroVertices, false));
        } else {
            // Si la lista está correctamente inicializada, simplemente la reiniciamos
            for (int i = 0; i < visitados.size(); i++) {
                visitados.set(i, false);
            }
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

        // Llenar la matriz con los pesos de las aristas
        for (int i = 0; i < numeroVertices; i++) {
            for (Pair<Integer, Integer> p : listaAdyacencia.get(i)) {
                distancias[i][p.first] = p.second;
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
}

// Panel para dibujar el grafo (distribución circular) y marcar los nodos visitados
class PanelGrafo extends JPanel {
    private Grafo grafo;
    private java.util.List<Integer> nodosVisitados = new ArrayList<>();
    
    public PanelGrafo(){
        //setBackground(new Color(0, 128, 255));
    }

    public void setGrafo(Grafo g) {
        this.grafo = g;
        nodosVisitados.clear();
        repaint();
    }
    
    // Actualiza la lista de nodos visitados para pintar en verde
    public void setNodosVisitados(java.util.List<Integer> visitados) {
        this.nodosVisitados = new ArrayList<>(visitados);
        repaint();
    }
    
    /**
     * Dibuja una línea con una flecha al final.
     * @param g2 El Graphics2D sobre el que dibujar.
     * @param x1 Coordenada X de inicio.
     * @param y1 Coordenada Y de inicio.
     * @param x2 Coordenada X de destino.
     * @param y2 Coordenada Y de destino.
     * @param d Longitud de la base de la flecha.
     * @param h Altura de la flecha.
     */
    private void drawArrowLine(Graphics2D g2, int x1, int y1, int x2, int y2, int d, int h) {
        // Radio del nodo (para no dibujar la flecha dentro del nodo destino)
        int nodeRadius = 15;
        //Se calcula la diferencia en X e Y entre el punto de inicio (x1, y1) y el destino (x2, y2)
        int dx = x2 - x1, dy = y2 - y1;
        //Se obtiene D, la distancia total entre ambos puntos.
        double D = Math.sqrt(dx * dx + dy * dy);
        //Si D es 0 (los puntos son iguales), se sale de la función para evitar dividir por cero.
        if (D == 0) return;  // Evita división por cero

        // Normalizamos la dirección: Se obtiene el vector unitario que apunta desde el origen al destino.
        double cos = dx / D, sin = dy / D;
        // Acortar la línea para que termine en el borde del nodo de destino
        double newD = D - nodeRadius;
        //Se calcula el nuevo punto final (newX2, newY2) multiplicando 
        //el vector unitario por newD y sumándolo al origen.
        int newX2 = x1 + (int)(newD * cos);
        int newY2 = y1 + (int)(newD * sin);

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
        int ancho = getWidth();
        int alto = getHeight();
        int radio = Math.min(ancho, alto) / 2 - 50;
        int centroX = ancho / 2;
        int centroY = alto / 2;
        Point[] posiciones = new Point[n];
        for (int i = 0; i < n; i++) {
            double angulo = 2 * Math.PI * i / n;
            int x = centroX + (int)(radio * Math.cos(angulo));
            int y = centroY + (int)(radio * Math.sin(angulo));
            posiciones[i] = new Point(x, y);
        }
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        // Dibuja las aristas
        g2.setColor(Color.BLACK);
        for (int i = 0; i < n; i++) {
            for (Grafo.Pair<Integer, Integer> p : grafo.listaAdyacencia.get(i)) {
                int vecino = p.first;
                int peso = p.second;
                Point p1 = posiciones[i];
                Point p2 = posiciones[vecino];
                if (grafo.dirigido) {
                    drawArrowLine(g2, p1.x, p1.y, p2.x, p2.y, 10, 5);
                } else {
                    if (i < vecino) {
                        g2.drawLine(p1.x, p1.y, p2.x, p2.y);
                    }
                }
                // Dibujar el peso de la arista
                int midX = (p1.x + p2.x) / 2;
                int midY = (p1.y + p2.y) / 2;
                g2.setColor(Color.BLUE);
                g2.drawString(String.valueOf(peso), midX, midY);
                g2.setColor(Color.BLACK);
            }
        }
        // Dibuja los nodos
        for (int i = 0; i < n; i++) {
            Point p = posiciones[i];
            if (nodosVisitados.contains(i)) {
                g2.setColor(Color.GREEN);
            } else {
                g2.setColor(Color.RED);
            }
            g2.fillOval(p.x - 15, p.y - 15, 30, 30);
            g2.setColor(Color.WHITE);
            g2.drawString(String.valueOf(i), p.x - 5, p.y + 5);
        }
    }
}

// Panel para mostrar visualmente el contenido de la estructura de datos (pila o cola)
class PanelEstructuraDatos extends JPanel {
    private java.util.List<Integer> estructuraDatos = new ArrayList<>();
    private String tipo = ""; // "Pila" o "Cola"
    
    // Constructor: define un tamaño preferido para el panel
    public PanelEstructuraDatos() {
        setPreferredSize(new Dimension(150, 600));
    }
    
    public void setEstructuraDatos(java.util.List<Integer> ds, String tipo) {
        this.estructuraDatos = new ArrayList<>(ds);
        this.tipo = tipo;
        repaint();
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        // Muestra el tipo (Pila o Cola)
        g.setColor(Color.BLACK);
        g.drawString(tipo, 10, 20);
        // Dibuja cada elemento como una caja
        int anchoCaja = 40;
        int altoCaja = 30;
        int espacio = 10;
        int inicioX = 10;
        int inicioY = 40;
        for (int i = 0; i < estructuraDatos.size(); i++) {
            int x = inicioX;
            int y = inicioY + i * (altoCaja + espacio);
            g.setColor(Color.LIGHT_GRAY);
            g.fillRect(x, y, anchoCaja, altoCaja);
            g.setColor(Color.BLACK);
            g.drawRect(x, y, anchoCaja, altoCaja);
            g.drawString(String.valueOf(estructuraDatos.get(i)), x + anchoCaja/2 - 5, y + altoCaja/2 + 5);
        }
    }
}

class MenuInicial {
    public static int mostrar() {
        String[] opciones = {"Nuevo Grafo", "Cargar Grafo Guardado"};
        return JOptionPane.showOptionDialog(null, "¿Qué desea hacer?", "Menú Inicial",
                JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE, null, opciones, opciones[0]);
    }
}


// Clase principal: ventana que contiene el grafo, el panel de estructura de datos,
// un toolbar con las opciones en la parte superior, atajos de teclado y un menú.
public class VisualizacionRecorridosGrafo extends JFrame {
    private static Grafo grafo; // Instancia del grafo
    private static PanelGrafo panelGrafo; // Panel para dibujar el grafo
    private static PanelEstructuraDatos panelEstructura; // Panel para mostrar la estructura de datos
    private javax.swing.Timer temporizadorAlgoritmo;  // Temporizador para la animación
    
    public VisualizacionRecorridosGrafo() {
        super("Grafo en Java Swing");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1300, 900); // Se amplía el ancho para incluir el panel lateral
        setLocationRelativeTo(null);
        
        // Toolbar en la parte superior
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

        JButton btnMatrizDistancias = new JButton("Mostrar Matriz de Distancias (8)");
        btnMatrizDistancias.addActionListener(e -> mostrarMatrizDistancias());
        barraHerramientas.add(btnMatrizDistancias);

        JButton btnGuardarGrafo = new JButton("Guardar Grafo (9)");
        btnGuardarGrafo.addActionListener(e -> guardarGrafo());
        barraHerramientas.add(btnGuardarGrafo);

        JButton btnSalir = new JButton("Salir (10)");
        btnSalir.addActionListener(e -> System.exit(0));
        barraHerramientas.add(btnSalir);
        
        // Instanciar los paneles
        panelGrafo = new PanelGrafo();
        panelEstructura = new PanelEstructuraDatos();
        
        // Panel central que contiene el panel del grafo y, a la derecha, el panel de la estructura de datos
        JPanel panelPrincipal = new JPanel(new BorderLayout());
        panelPrincipal.add(panelGrafo, BorderLayout.CENTER);
        panelPrincipal.add(panelEstructura, BorderLayout.EAST);
        
        // Agregar toolbar y panel principal al content pane
        getContentPane().add(barraHerramientas, BorderLayout.NORTH);
        getContentPane().add(panelPrincipal, BorderLayout.CENTER);
        
        // Menú de opciones en la barra de menús
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
        
        JMenuItem miMatrizDistancias = new JMenuItem("Mostrar Matriz de Distancias (8)");
        miMatrizDistancias.addActionListener(e -> System.exit(0));
        menu.add(miMatrizDistancias);

        JMenuItem miGuardarGrafo = new JMenuItem("Guardar Grafo (9)");
        miGuardarGrafo.addActionListener(e -> guardarGrafo());
        menu.add(miGuardarGrafo);
        
        JMenuItem miSalir = new JMenuItem("Salir (10)");
        miSalir.addActionListener(e -> System.exit(0));
        menu.add(miSalir);

        barraMenu.add(menu);
        setJMenuBar(barraMenu);
        
        // Configurar atajos de teclado para las opciones (teclas 1 a 8)
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
        
        mapaEntrada.put(KeyStroke.getKeyStroke("4"), "ejecutarDFS");
        mapaAccion.put("ejecutarDFS", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                ejecutarDFS();
            }
        });
        
        mapaEntrada.put(KeyStroke.getKeyStroke("5"), "ejecutarBFS");
        mapaAccion.put("ejecutarBFS", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                ejecutarBFS();
            }
        });
        
        mapaEntrada.put(KeyStroke.getKeyStroke("6"), "eliminarArista");
        mapaAccion.put("eliminarArista", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                eliminarArista();
            }
        });
        
        mapaEntrada.put(KeyStroke.getKeyStroke("7"), "eliminarGrafo");
        mapaAccion.put("eliminarGrafo", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                eliminarGrafo();
            }
        });
         
        mapaEntrada.put(KeyStroke.getKeyStroke("8"), "MatrizDistancias");
        mapaAccion.put("MatrizDistancias", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                mostrarMatrizDistancias();
            }
        });

        mapaEntrada.put(KeyStroke.getKeyStroke("9"),"Guardar");
        mapaAccion.put("Guardar", new AbstractAction(){
            public void actionPerformed(ActionEvent e){
                guardarGrafo();
            }
        });

        mapaEntrada.put(KeyStroke.getKeyStroke("control 0"), "salir");
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
            panelEstructura.setEstructuraDatos(new ArrayList<>(), "");
            
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
        String input = JOptionPane.showInputDialog("Ingrese dos vértices y el peso (origen,destino,peso) separados por coma:");
        try {
            String[] partes = input.split(",");
            int origen = Integer.parseInt(partes[0].trim());
            int destino = Integer.parseInt(partes[1].trim());
            int peso = Integer.parseInt(partes[2].trim());
            if (origen < 0 || origen >= grafo.numeroVertices || destino < 0 || destino >= grafo.numeroVertices) {
                JOptionPane.showMessageDialog(null, "Vértice inválido.");
                return;
            }
            if (grafo.listaAdyacencia.get(origen).stream().anyMatch(p -> p.first == destino)) {
                JOptionPane.showMessageDialog(null, "La arista ya existe.");
                return;
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
    
    // Opción 4: Recorrido DFS con visualización animada de la pila
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
    
    // Opción 5: Recorrido BFS con visualización animada de la cola
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
            if (!grafo.listaAdyacencia.get(origen).stream().anyMatch(p -> p.first == destino)) {
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
        panelEstructura.setEstructuraDatos(new ArrayList<>(), "");
    }
    //opcion 8: matriz
    private void mostrarMatrizDistancias() {
        if (grafo == null) {
            JOptionPane.showMessageDialog(null, "No hay grafo creado.");
            return;
        }
        String matriz = grafo.obtenerMatrizDistancias();
        JOptionPane.showMessageDialog(null, matriz, "Matriz de Distancias", JOptionPane.INFORMATION_MESSAGE);
    }

    // Opcion 9: Guardar
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
                for (Grafo.Pair<Integer, Integer> p : grafo.listaAdyacencia.get(i)) {
                    writer.write(i + "," + p.first + "," + p.second + "\n");
                }
            }
            JOptionPane.showMessageDialog(null, "Grafo guardado correctamente.");
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(null, "Error al guardar el grafo.");
        }
    }

    //extra cargar grafo
    private void cargarGrafo() {
    JFileChooser fileChooser = new JFileChooser();
    fileChooser.setDialogTitle("Seleccione un archivo de grafo");
    
    while (true) { // Bucle para permitir seleccionar otro archivo en caso de error
        int resultado = fileChooser.showOpenDialog(this);
        if (resultado == JFileChooser.APPROVE_OPTION) {
            File archivo = fileChooser.getSelectedFile();
            String nombreArchivo = archivo.getName();

            // Verificar si el archivo tiene la extensión .txt
            if (!nombreArchivo.toLowerCase().endsWith(".txt")) {
                JOptionPane.showMessageDialog(this, "Error: Solo archivos .txt", "Error", JOptionPane.ERROR_MESSAGE);
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
                panelEstructura.setEstructuraDatos(new ArrayList<>(), "");
                JOptionPane.showMessageDialog(this, "Grafo cargado correctamente.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
                break; // Salir del bucle si el archivo se cargó correctamente
                } catch (IOException ex) {
                    JOptionPane.showMessageDialog(this, "Error al cargar el grafo.", "Error", JOptionPane.ERROR_MESSAGE);
                    break; // Salir del bucle si hay un error al leer el archivo
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(this, "Error: El archivo no tiene el formato correcto.", "Error", JOptionPane.ERROR_MESSAGE);
                    break; // Salir del bucle si el archivo no tiene el formato correcto
                }
            } else {
                break; // Salir del bucle si el usuario cancela la selección
            }
        }
    }
    
    // Animación paso a paso para DFS (utilizando una pila)
    private void animarDFS(int inicio) {
        grafo.resetearVisitados();
        java.util.List<Integer> ordenVisitados = new ArrayList<>();
        Stack<Integer> pila = new Stack<>();
        pila.push(inicio);
        panelEstructura.setEstructuraDatos(new ArrayList<>(pila), "Pila");
        if (temporizadorAlgoritmo != null && temporizadorAlgoritmo.isRunning()) {
            temporizadorAlgoritmo.stop();
        }
        temporizadorAlgoritmo = new javax.swing.Timer(3000, new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                panelEstructura.setEstructuraDatos(new ArrayList<>(pila), "Pila");
                if (pila.isEmpty()) {
                    ((javax.swing.Timer) e.getSource()).stop();
                    return;
                }
                int actual = pila.pop();
                if (!grafo.visitados.get(actual)) {
                    grafo.visitados.set(actual, true);
                    ordenVisitados.add(actual);
                    panelGrafo.setNodosVisitados(ordenVisitados);
                    for (Grafo.Pair<Integer, Integer> p : grafo.listaAdyacencia.get(actual)) {
                        int vecino = p.first;
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
    
    // Animación paso a paso para BFS (utilizando una cola)
    private void animarBFS(int inicio) {
        grafo.resetearVisitados();
        java.util.List<Integer> ordenVisitados = new ArrayList<>();
        Queue<Integer> cola = new LinkedList<>();
        cola.add(inicio);
        ArrayList<Boolean> visitadosBFS = new ArrayList<>(Collections.nCopies(grafo.numeroVertices, false));
        visitadosBFS.set(inicio, true);
        panelEstructura.setEstructuraDatos(new ArrayList<>(cola), "Cola");
        if (temporizadorAlgoritmo != null && temporizadorAlgoritmo.isRunning()) {
            temporizadorAlgoritmo.stop();
        }
        temporizadorAlgoritmo = new javax.swing.Timer(3000, new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                panelEstructura.setEstructuraDatos(new ArrayList<>(cola), "Cola");
                if (cola.isEmpty()) {
                    ((javax.swing.Timer) e.getSource()).stop();
                    return;
                }
                int actual = cola.poll();
                if (!grafo.visitados.get(actual)) {
                    grafo.visitados.set(actual, true);
                    ordenVisitados.add(actual);
                    panelGrafo.setNodosVisitados(ordenVisitados);
                    for (Grafo.Pair<Integer, Integer> p : grafo.listaAdyacencia.get(actual)) {
                        int vecino = p.first;
                        if (!visitadosBFS.get(vecino)) {
                            visitadosBFS.set(vecino, true);
                            cola.add(vecino);
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
    SwingUtilities.invokeLater(() -> {
        int opcion = MenuInicial.mostrar();
        VisualizacionRecorridosGrafo ventana = new VisualizacionRecorridosGrafo();
        if (opcion == 1) { // Cargar grafo guardado
            ventana.cargarGrafo();
        }
        ventana.setVisible(true);
    });
}
}
