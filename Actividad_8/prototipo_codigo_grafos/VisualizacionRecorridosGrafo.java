import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;

// Clase que representa el grafo utilizando listas de adyacencia
class Grafo {
    int numeroVertices;
    ArrayList<ArrayList<Integer>> listaAdyacencia;  // Lista de adyacencia
    ArrayList<Boolean> visitados;                    // Seguimiento de nodos visitados
    boolean dirigido;                              // true: grafo dirigido, false: no dirigido

    // Constructor que recibe el número de vértices y el tipo de grafo (dirigido o no)
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
    
    // Agrega una arista entre dos vértices.
    // Si el grafo es no dirigido, se agrega la arista en ambos sentidos.
    public void agregarArista(int origen, int destino) {
        if (listaAdyacencia.get(origen).contains(destino)) {
            return; // Evita duplicados
        }
        listaAdyacencia.get(origen).add(destino);
        if (!dirigido) {
            listaAdyacencia.get(destino).add(origen);
        }
    }
    
    // Elimina una arista entre dos vértices.
    // En un grafo no dirigido, elimina la conexión de ambos lados.
    public void eliminarArista(int origen, int destino) {
        listaAdyacencia.get(origen).remove((Integer) destino);
        if (!dirigido) {
            listaAdyacencia.get(destino).remove((Integer) origen);
        }
    }
    
    // Retorna una representación en cadena de la lista de adyacencia.
    public String obtenerListaAdyacencia() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < numeroVertices; i++) {
            sb.append(i).append(": ");
            for (int v : listaAdyacencia.get(i)) {
                sb.append(v).append(" -> ");
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
            for (int vecino : grafo.listaAdyacencia.get(i)) {
                if (grafo.dirigido) {
                    // Dibuja flecha para cada arista dirigida
                    drawArrowLine(g2, posiciones[i].x, posiciones[i].y, posiciones[vecino].x, posiciones[vecino].y, 10, 5);
                } else {
                    // En grafo no dirigido, evita duplicados
                    if (i < vecino) {
                        g2.drawLine(posiciones[i].x, posiciones[i].y, posiciones[vecino].x, posiciones[vecino].y);
                    }
                }
            }
        }
        // Dibuja los nodos: los visitados se pintan en verde y los no visitados en rojo
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
        setSize(900, 600); // Se amplía el ancho para incluir el panel lateral
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
        
        JButton btnSalir = new JButton("Salir (8)");
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
        
        JMenuItem miSalir = new JMenuItem("Salir (8)");
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
        
        mapaEntrada.put(KeyStroke.getKeyStroke("8"), "salir");
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
        String input = JOptionPane.showInputDialog("Ingrese dos vértices (origen,destino) separados por coma:");
        try {
            String[] partes = input.split(",");
            int origen = Integer.parseInt(partes[0].trim());
            int destino = Integer.parseInt(partes[1].trim());
            if (origen < 0 || origen >= grafo.numeroVertices || destino < 0 || destino >= grafo.numeroVertices) {
                JOptionPane.showMessageDialog(null, "Vértice inválido.");
                return;
            }
            if (grafo.listaAdyacencia.get(origen).contains(destino)) {
                JOptionPane.showMessageDialog(null, "La arista ya existe.");
                return;
            }
            grafo.agregarArista(origen, destino);
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
            if (!grafo.listaAdyacencia.get(origen).contains(destino)) {
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
                    java.util.List<Integer> vecinos = grafo.listaAdyacencia.get(actual);
                    for (int i = vecinos.size() - 1; i >= 0; i--) {
                        int vecino = vecinos.get(i);
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
                    for (int vecino : grafo.listaAdyacencia.get(actual)) {
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
            new VisualizacionRecorridosGrafo().setVisible(true);
        });
    }
}
