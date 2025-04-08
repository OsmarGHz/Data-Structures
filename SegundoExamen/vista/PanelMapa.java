
import javax.swing.*;
import javax.swing.Timer;

import modelo.grafo.Arista;
//import modelo.grafo.Aro;
import modelo.grafo.GeneradorGrafo;
//import modelo.grafo.posVertice;
//import modelo.grafo.posVertice;

import java.awt.event.*;
import java.util.*;
import java.util.List;
import java.util.Queue;
import java.util.stream.Collectors;
//import java.util.List;
import java.awt.*;
import java.awt.geom.Line2D;
//import java.awt.geom.Ellipse2D;
import java.awt.image.BufferedImage;

public class PanelMapa extends JPanel {
    private GeneradorGrafo grafo;
    //private int T_MATRIZ = grafo.TAM_MATRIZ + 1;
    private int T_MATRIZ = 13;
    private Image mapa;
    private final int RADIO_VERTICE = 16;
    // Variables de clase
    private Font fontPesos;
    private Font fontVertices;

    //metodos nuevos
    // private java.util.List<Integer> nodosVisitados = new ArrayList<>();
    private java.util.ArrayList<Integer> nodosVisitados = new java.util.ArrayList<>();
    // Arreglo para indicar qué aristas están resaltadas: [origen][destino]
    private boolean[][] highlightedEdges;
    // Arreglo para indicar el color de cada vértice (null: sin resaltar)
    private Color[] highlightedVertices;
    private Point[] posiciones; // Arreglo para almacenar las posiciones de los vértices
    private int verticeSeleccionado = -1; // Índice del vértice seleccionado para mover
    private BufferedImage buffer;

    //para el toggleButon
    private boolean mostrarCentros = false;
    private java.util.List<Integer> centrosRecolectores = new ArrayList<>();
    private static final double PORCENTAJE_CENTROS = 0.4;

    private JToggleButton boton;

    public boolean esBFSODFS;

    public PanelMapa() {

        mapa = new ImageIcon(getClass().getResource("/recursos/mapa.png")).getImage();
        //grafo = new GeneradorGrafo();
        //inicializarGrafo()
        fontPesos = new Font("SansSerif", Font.PLAIN, (int)(RADIO_VERTICE * 0.8));
        fontVertices = new Font("SansSerif", Font.BOLD, (int)(RADIO_VERTICE * 1.2));

        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                if (grafo != null) {  // Actualiza posiciones si el grafo ya existe
                    actualizarPosicionesVertices();
                } else {
                    iniciarGrafo();
                }
            }
        });

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

    public void iniciarGrafo() {
        grafo = new GeneradorGrafo();
        setGrafo(grafo);
        revalidate();
        repaint();
    }

    public void setGrafo(GeneradorGrafo g) {
        this.grafo = g;
        nodosVisitados.clear();
        if (g != null) {
            highlightedEdges = new boolean[g.numeroVertices][g.numeroVertices];
            highlightedVertices = new Color[g.numeroVertices];
            posiciones = new Point[g.numeroVertices];
            actualizarPosicionesVertices();
        }
        repaint();
    }
    
    private void actualizarPosicionesVertices() {
        if (grafo == null || getWidth() <= 0 || getHeight() <= 0) return;
        
        double xlineas = (double) getWidth() / T_MATRIZ;
        double ylineas = (double) getHeight() / T_MATRIZ;
        
        // Si posiciones es null (primera vez), inicialízalo
        if (posiciones == null || posiciones.length != grafo.numeroVertices) {
            posiciones = new Point[grafo.numeroVertices];
        }
        
        // Conversión de coordenadas matriz a coordenadas de pantalla
        for (int i = 0; i < grafo.numeroVertices; i++) {
            int x = (int) ((grafo.posicionVertices[i].y + 1) * xlineas);
            int y = (int) ((grafo.posicionVertices[i].x + 1) * ylineas);
            
            // Mantener la posición actual si el vértice fue movido manualmente
            if (posiciones[i] == null || !verticeFueMovido(i)) {
                posiciones[i] = new Point(x, y);
            }
        }
        repaint();
    }
    
    // Método auxiliar para verificar si un vértice fue movido manualmente
    private boolean verticeFueMovido(int index) {
        if (posiciones[index] == null) return false;
        
        double xlineas = (double) getWidth() / T_MATRIZ;
        double ylineas = (double) getHeight() / T_MATRIZ;
        
        int expectedX = (int) ((grafo.posicionVertices[index].y + 1) * xlineas);
        int expectedY = (int) ((grafo.posicionVertices[index].x + 1) * ylineas);
        
        return posiciones[index].x != expectedX || posiciones[index].y != expectedY;
    }

    // Verificar si el vértice en la nueva posición colisiona con otro
    private boolean verificarColision(int newX, int newY) {
        // Pre-calcular el cuadrado de la distancia mínima para evitar sqrt
        int minDistSq = 4 * RADIO_VERTICE * RADIO_VERTICE;
        
        for (int i = 0; i < posiciones.length; i++) {
            if (posiciones[i] != null && i != verticeSeleccionado) {
                int dx = newX - posiciones[i].x;
                int dy = newY - posiciones[i].y;
                if (dx*dx + dy*dy < minDistSq) {
                    return true;
                }
            }
        }
        return false;
    }
    /*
    private boolean verificarColision(int newX, int newY) {
    // Pre-calcular el cuadrado de la distancia mínima para evitar sqrt
    int minDistSq = 4 * RADIO_VERTICE * RADIO_VERTICE;
    
    for (int i = 0; i < posiciones.length; i++) {
        if (posiciones[i] != null && i != verticeSeleccionado) {
            int dx = newX - posiciones[i].x;
            int dy = newY - posiciones[i].y;
            if (dx*dx + dy*dy < minDistSq) {
                return true;
            }
        }
    }
    return false;
    }
    */
    private void dibujarFondo(Graphics2D g2) {
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.drawImage(mapa, 0, 0, getWidth(), getHeight(), this);
        
        // Dibujar cuadrícula
        g2.setColor(Color.BLACK);
        g2.setStroke(new BasicStroke(1));
        
        double xlineas = (double) getWidth() / T_MATRIZ;
        double ylineas = (double) getHeight() / T_MATRIZ;
        for (int i = 0; i < T_MATRIZ; i++) {
            g2.draw(new Line2D.Double((i+1)*xlineas, 0, (i+1)*xlineas, getHeight()));
            g2.draw(new Line2D.Double(0, (i+1)*ylineas, getWidth(), (i+1)*ylineas));
        }
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

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (grafo == null || posiciones == null)
            return;
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        //setGrafo(grafo);
        // Dibujar el buffer si existe
        if (buffer == null || buffer.getWidth() != getWidth() || buffer.getHeight() != getHeight()) {
            buffer = new BufferedImage(getWidth(), getHeight(), BufferedImage.TYPE_INT_ARGB);
            Graphics2D bg = buffer.createGraphics();
            dibujarFondo(bg); // Método que dibuja fondo y cuadrícula una sola vez
            bg.dispose();
        }
        g2.drawImage(buffer, 0, 0, null);

     // Dibuja aristas
        for (int i = 0; i < grafo.numeroVertices; i++) {
            for (Arista arista : grafo.listaAdyacencia.get(i)) {
                if (i < arista.destino) {
                    Point p1 = posiciones[i];
                    Point p2 = posiciones[arista.destino];

                    g2.setStroke(new BasicStroke(RADIO_VERTICE / 5.0f));
                    g2.setColor(highlightedEdges != null && highlightedEdges[i][arista.destino] || 
                            highlightedEdges[arista.destino][i] ? Color.ORANGE : Color.BLACK);
                    g2.drawLine(p1.x, p1.y, p2.x, p2.y);

                    // Solo mostrar pesos si no estamos en BFS o DFS
                    esBFSODFS = false; // Cambiar esto según el algoritmo actual
                    if (!esBFSODFS) {
                        int midX = (p1.x + p2.x) / 2, midY = (p1.y + p2.y) / 2;
                        g2.setFont(fontPesos);
                        FontMetrics fm = g2.getFontMetrics();
                        String pesoTexto = String.valueOf(arista.peso);
                        int textWidth = fm.stringWidth(pesoTexto);
                        int textHeight = fm.getAscent();
                        int width = textWidth + 10;
                        int height = textHeight + 5;
                        int textX = midX - textWidth / 2;
                        int textY = midY + textHeight / 3;
                        g2.setColor(Color.WHITE);
                        g2.fillRect(midX - width / 2, midY - height / 2, width, height);
                        g2.setColor(Color.BLACK);
                        g2.drawString(pesoTexto, textX, textY);
                    }
                }
            }
        }

        // Dibuja nodos
        for (int i = 0; i < grafo.numeroVertices; i++) {
            if (posiciones[i] == null) continue;
        
            // Color del vértice
            Color colorVertice;
        
            if (highlightedVertices != null && highlightedVertices[i] != null) {
                colorVertice = highlightedVertices[i];
            } else if (mostrarCentros && centrosRecolectores.contains(i)) {
                colorVertice = new Color(189, 183, 107); // Custom brown color for the centers
            } else if (nodosVisitados.contains(i)) {
                colorVertice = Color.CYAN;
            } else {
                // Color base según el tipo de zona
                int tipoZona = grafo.posicionVertices[i].tipoZona;
                colorVertice = switch (tipoZona) {
                    case 1 -> Color.WHITE; // Zona no contaminada
                    case 2 -> Color.RED;        // Zona contaminada
                    default -> Color.WHITE;
                };
            }
        
            g2.setColor(colorVertice);
            g2.fillOval(posiciones[i].x - RADIO_VERTICE, posiciones[i].y - RADIO_VERTICE,
                        RADIO_VERTICE * 2, RADIO_VERTICE * 2);
            
            g2.setColor(Color.BLACK);
            g2.drawOval(posiciones[i].x - RADIO_VERTICE, posiciones[i].y - RADIO_VERTICE,
                        RADIO_VERTICE * 2, RADIO_VERTICE * 2);
        
            // Dibujar el número del vértice
            g2.setFont(fontVertices);
            String texto = String.valueOf(i);
            FontMetrics fm = g2.getFontMetrics();
            int textWidth = fm.stringWidth(texto);
            g2.drawString(texto, posiciones[i].x - textWidth / 2, posiciones[i].y + fm.getAscent() / 2);
        }
    
    }
////////////////////////////////////////////////////////////////////////////////////
/// 
/// 
/// 
/// 
/// 
/// 
/// 
    // Métodos agregados para la lógica de botones y algoritmos


    // Método para verificar si el grafo es conexo usando DFS
    public boolean esConexo() {
        if (grafo.numeroVertices == 0) {
            return false; // Grafo vacío no es conexo
        }
    
        // Arreglo para marcar nodos visitados
        boolean[] visitados = new boolean[grafo.numeroVertices];
        java.util.Stack<Integer> pila = new java.util.Stack<>();
        pila.push(0); // Empezar desde el nodo 0
        visitados[0] = true;
    
        // Recorrido DFS
        while (!pila.isEmpty()) {
            int u = pila.pop();
            for (Arista arista : grafo.listaAdyacencia.get(u)) {
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


    public void ejecutarNuevoGrafo() {
        // Crea un nuevo grafo y reinicia las posiciones
        grafo = new GeneradorGrafo();
        iniciarGrafo();
        //repaint();
        JOptionPane.showMessageDialog(this, "Se generó un nuevo Bosque.");
    }

    public void ejecutarBFS() {
        JOptionPane.showMessageDialog(this, "Ejecutando BFS (simulación)...\n" +
            " - Se extenderá por vértices de tipoZona 1 y se detendrá al encontrar uno de tipoZona 2.");
    
        if (grafo == null) {
            JOptionPane.showMessageDialog(null, "No hay grafo creado.");
            return;
        }

        esBFSODFS = true; // Cambiar a true para indicar que se está ejecutando BFS
        
        String inputInicio = JOptionPane.showInputDialog("Ingrese el vértice de inicio para BFS:");
        try {
            int inicio = Integer.parseInt(inputInicio.trim());
            if (inicio < 0 || inicio >= grafo.numeroVertices) {
                JOptionPane.showMessageDialog(null, "Vértice inválido.");
                return;
            }
    
            boolean[] visitado = new boolean[grafo.numeroVertices];
            int[] predecesor = new int[grafo.numeroVertices];
            Arrays.fill(predecesor, -1);
    
            Queue<Integer> cola = new LinkedList<>();
            cola.add(inicio);
            visitado[inicio] = true;
    
            // Cambiamos a arrays de un elemento
            final boolean[] zonaContaminadaEncontrada = {false};
            final int[] encontrado = {-1};
            
            while (!cola.isEmpty() && !zonaContaminadaEncontrada[0]) {
                int u = cola.poll();
                
                for (Arista arista : grafo.listaAdyacencia.get(u)) {
                    int v = arista.destino;
                    if (!visitado[v]) {
                        visitado[v] = true;
                        predecesor[v] = u;
                        
                        if (grafo.posicionVertices[v].tipoZona == 2) {
                            zonaContaminadaEncontrada[0] = true;
                            encontrado[0] = v;
                            break;
                        }
                        
                        if (grafo.posicionVertices[v].tipoZona == 1) {
                            cola.add(v);
                        }
                    }
                }
            }
    
            ArrayList<Integer> camino = new ArrayList<>();
            if (zonaContaminadaEncontrada[0]) {
                int actual = encontrado[0];
                while (actual != -1) {
                    camino.add(actual);
                    actual = predecesor[actual];
                }
                Collections.reverse(camino);
            } else {
                // Solo mostrar el inicio si no se encontró nada
                camino.add(inicio);
            }
    
            animatePath(camino, () -> {
                StringBuilder resumen = new StringBuilder("Resumen BFS:\n");
                resumen.append("Camino recorrido: ").append(camino).append("\n");
                if (zonaContaminadaEncontrada[0]) {
                    resumen.append("Zona contaminada encontrada en vértice: ").append(encontrado[0]).append("\n");
                } else {
                    resumen.append("No se encontró ninguna zona contaminada accesible.\n");
                }
                JOptionPane.showMessageDialog(null, resumen.toString(), "Resultado BFS",
                        JOptionPane.INFORMATION_MESSAGE);
                setHighlightedEdges(new boolean[grafo.numeroVertices][grafo.numeroVertices]);
                setHighlightedVertices(new Color[grafo.numeroVertices]);
            });
    
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(null, "Entrada inválida.");
        }
    }

    public void ejecutarDFS() {
        JOptionPane.showMessageDialog(this, "Ejecutando DFS (simulación)...\n" +
            " - Se recorrerán solo zonas con el mismo tipo que el vértice seleccionado.");
    
        if (grafo == null) {
            JOptionPane.showMessageDialog(null, "No hay grafo creado.");
            return;
        }

        esBFSODFS = true; // Cambiar a true para indicar que se está ejecutando DFS
        
        String inputInicio = JOptionPane.showInputDialog("Ingrese el vértice de inicio para DFS:");
        try {
            int inicio = Integer.parseInt(inputInicio.trim());
            if (inicio < 0 || inicio >= grafo.numeroVertices) {
                JOptionPane.showMessageDialog(null, "Vértice inválido.");
                return;
            }
    
            int tipoZonaObjetivo = grafo.posicionVertices[inicio].tipoZona;
            boolean[] visitado = new boolean[grafo.numeroVertices];
            int[] predecesor = new int[grafo.numeroVertices];
            Arrays.fill(predecesor, -1);
    
            Stack<Integer> pila = new Stack<>();
            pila.push(inicio);
    
            while (!pila.isEmpty()) {
                int u = pila.pop();
                
                if (!visitado[u] && grafo.posicionVertices[u].tipoZona == tipoZonaObjetivo) {
                    visitado[u] = true;
                    
                    for (Arista arista : grafo.listaAdyacencia.get(u)) {
                        int v = arista.destino;
                        if (!visitado[v]) {
                            pila.push(v);
                            predecesor[v] = u;
                        }
                    }
                }
            }
    
            // Construir camino desde el inicio hasta el nodo más lejano encontrado
            ArrayList<Integer> camino = new ArrayList<>();
            
            // Encontrar el nodo más lejano del inicio
            final int nodoMasLejano = encontrarNodoMasLejano(inicio, visitado, predecesor);
            
            // Construir camino hasta el nodo más lejano
            if (nodoMasLejano != inicio) {
                ArrayList<Integer> caminoInverso = new ArrayList<>();
                int actual = nodoMasLejano;
                while (actual != -1) {
                    caminoInverso.add(actual);
                    actual = predecesor[actual];
                }
                Collections.reverse(caminoInverso);
                camino.addAll(caminoInverso); // No se duplica el inicio
            } else {
                camino.add(inicio); // Solo agregar inicio si no hay otro nodo
            }
    
            // Crear una copia final de tipoZonaObjetivo para usar en el lambda
            final int tipoZonaFinal = tipoZonaObjetivo;
            
            animatePath(camino, () -> {
                StringBuilder resumen = new StringBuilder("Resumen DFS:\n");
                resumen.append("Zonas del tipo ").append(tipoZonaFinal).append(" alcanzadas:\n");
                
                // Contar vértices visitados
                int contador = 0;
                for (boolean v : visitado) {
                    if (v) contador++;
                }
                
                resumen.append("Total vértices alcanzados: ").append(contador).append("\n");
                resumen.append("Camino desde inicio (").append(inicio).append(") hasta el nodo más lejano (")
                      .append(nodoMasLejano).append("): ").append(camino).append("\n");
                
                JOptionPane.showMessageDialog(null, resumen.toString(), "Resultado DFS",
                        JOptionPane.INFORMATION_MESSAGE);
                setHighlightedEdges(new boolean[grafo.numeroVertices][grafo.numeroVertices]);
                setHighlightedVertices(new Color[grafo.numeroVertices]);
            });
    
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(null, "Entrada inválida.");
        }
    }
    
    // Método auxiliar para encontrar el nodo más lejano
    private int encontrarNodoMasLejano(int inicio, boolean[] visitado, int[] predecesor) {
        int nodoMasLejano = inicio;
        int maxDistancia = 0;
        
        for (int i = 0; i < visitado.length; i++) {
            if (i != inicio && visitado[i]) {
                int distancia = 0;
                int actual = i;
                while (actual != inicio && actual != -1) {
                    distancia++;
                    actual = predecesor[actual];
                }
                
                if (distancia > maxDistancia) {
                    maxDistancia = distancia;
                    nodoMasLejano = i;
                }
            }
        }
        
        return nodoMasLejano;
    }

    public void ejecutarDijkstra() {
        JOptionPane.showMessageDialog(this, "Ejecutando Dijkstra (simulación)...\n" +
                " - Se pedirá seleccionar vértices de inicio y destino.");
        // ...implementación de animación y resaltado de la ruta...
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
                    setHighlightedEdges(new boolean[grafo.numeroVertices][grafo.numeroVertices]);
                    setHighlightedVertices(new Color[grafo.numeroVertices]);
                });
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(null, "Entrada inválida.");
        }
    }

    public void ejecutarFloyd() {
        JOptionPane.showMessageDialog(this, "Ejecutando Floyd (simulación)...\n" +
                " - Se pedirá seleccionar un vértice y calculará caminos a vértices del otro tipo.");
        // ...implementación de animación e informe de costos...
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

            //mostrar el resumen con scroll
            // Crear un área de texto con fuente más grande
            JTextArea textArea = new JTextArea(resumen.toString());
            textArea.setEditable(false);
            textArea.setLineWrap(true);
            textArea.setWrapStyleWord(true);
            textArea.setFont(new Font("Monospaced", Font.PLAIN, 16)); // fuente más grande

            // Scroll con tamaño inicial
            JScrollPane scrollPane = new JScrollPane(textArea);
            scrollPane.setPreferredSize(new Dimension(600, 400));

            // Crear el panel principal
            JPanel panel = new JPanel(new BorderLayout());
            panel.add(scrollPane, BorderLayout.CENTER);

            // Crear el botón "Aceptar"
            JButton btnAceptar = new JButton("Aceptar");
            btnAceptar.addActionListener(e -> {
                Window window = SwingUtilities.getWindowAncestor(btnAceptar);
                if (window != null) {
                    window.dispose();
                }
            });

            // Panel para el botón
            JPanel btnPanel = new JPanel();
            btnPanel.add(btnAceptar);
            panel.add(btnPanel, BorderLayout.SOUTH);

            // Crear el diálogo
            JDialog dialog = new JDialog((Frame) null, "Resultado Floyd", true);
            dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
            dialog.getContentPane().add(panel);
            dialog.pack();
            dialog.setResizable(true); // permite redimensionar
            dialog.setLocationRelativeTo(null); // centrado
            dialog.setVisible(true);
            // Limpiar grafo
            setHighlightedEdges(new boolean[grafo.numeroVertices][grafo.numeroVertices]);
            setHighlightedVertices(new Color[grafo.numeroVertices]);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(null, "Entrada inválida.");
        }
        //repaint();
    }

    public void ejecutarPrim() {
        JOptionPane.showMessageDialog(this, "Ejecutando Prim (simulación)...\n" +
                " - Se utilizarán los centros de recolección para formar el MST.");
        
        if (grafo == null) {
            JOptionPane.showMessageDialog(null, "No hay grafo creado.");
            return;
        }
    
        if (!mostrarCentros || centrosRecolectores.isEmpty()) {
            JOptionPane.showMessageDialog(null, 
                "No hay centros de recolección visibles.\n" +
                "Active la visualización de centros primero.");
            return;
        }
    
        // Crear un mapa de índices para los centros
        Map<Integer, Integer> centroToIndex = new HashMap<>();
        for (int i = 0; i < centrosRecolectores.size(); i++) {
            centroToIndex.put(centrosRecolectores.get(i), i);
        }
    
        // Algoritmo de Prim
        int n = centrosRecolectores.size();
        boolean[] enMST = new boolean[n];
        int[] key = new int[n];
        int[] parent = new int[n];
        Arrays.fill(key, Integer.MAX_VALUE);
        Arrays.fill(parent, -1);
        key[0] = 0;
    
        ArrayList<Arista> mst = new ArrayList<>();
    
        for (int i = 0; i < n - 1; i++) {
            int u = -1;
            for (int v = 0; v < n; v++) {
                if (!enMST[v] && (u == -1 || key[v] < key[u])) {
                    u = v;
                }
            }
    
            enMST[u] = true;
    
            int centroU = centrosRecolectores.get(u);
            for (Arista arista : grafo.listaAdyacencia.get(centroU)) {
                if (centroToIndex.containsKey(arista.destino)) {
                    int v = centroToIndex.get(arista.destino);
                    int peso = arista.peso;
                    if (!enMST[v] && peso < key[v]) {
                        parent[v] = u;
                        key[v] = peso;
                    }
                }
            }
        }
    
        // Construir MST con los índices originales
        for (int i = 1; i < n; i++) {
            int origen = centrosRecolectores.get(parent[i]);
            int destino = centrosRecolectores.get(i);
            mst.add(new Arista(origen, destino, key[i]));
        }
    
        animarMST(mst, "Prim");
        // Limpiar grafo
        setHighlightedEdges(new boolean[grafo.numeroVertices][grafo.numeroVertices]);
        setHighlightedVertices(new Color[grafo.numeroVertices]);
    }

    public void ejecutarKruskal() {
        JOptionPane.showMessageDialog(this, "Ejecutando Kruskal (simulación)...\n" +
                " - Se utilizarán los centros de recolección para formar el MST.");
        
        if (grafo == null) {
            JOptionPane.showMessageDialog(null, "No hay grafo creado.");
            return;
        }
    
        if (!mostrarCentros || centrosRecolectores.isEmpty()) {
            JOptionPane.showMessageDialog(null, 
                "No hay centros de recolección visibles.\n" +
                "Active la visualización de centros primero.");
            return;
        }
    
        // Verificar conexión entre centros de recolección
        if (!sonCentrosConectados()) {
            JOptionPane.showMessageDialog(null, 
                "Los centros de recolección no están todos conectados entre sí.");
            return;
        }
    
        // Crear lista de aristas entre centros
        ArrayList<Edge> edges = new ArrayList<>();
        for (int i = 0; i < centrosRecolectores.size(); i++) {
            int u = centrosRecolectores.get(i);
            for (Arista arista : grafo.listaAdyacencia.get(u)) {
                if (centrosRecolectores.contains(arista.destino) && u < arista.destino) {
                    edges.add(new Edge(u, arista.destino, arista.peso));
                }
            }
        }
    
        // Ordenar aristas por peso
        edges.sort(Comparator.comparingInt(e -> e.peso));
    
        // Kruskal
        int[] parent = new int[grafo.numeroVertices];
        for (int i = 0; i < parent.length; i++) {
            parent[i] = i;
        }
    
        ArrayList<Edge> mst = new ArrayList<>();
        int totalCost = 0;
    
        for (Edge edge : edges) {
            int rootU = find(parent, edge.origen);
            int rootV = find(parent, edge.destino);
            if (rootU != rootV) {
                mst.add(edge);
                totalCost += edge.peso;
                parent[rootV] = rootU;
            }
        }
    
        if (mst.size() != centrosRecolectores.size() - 1) {
            JOptionPane.showMessageDialog(null, 
                "No se pudo conectar todos los centros de recolección.");
            return;
        }
    
        animarKruskal(mst, totalCost);
    }

    private boolean sonCentrosConectados() {
        if (centrosRecolectores.isEmpty()) {
            System.out.println("No hay centros seleccionados");
            return false;
        }

        Set<Integer> visitados = new HashSet<>();
        Queue<Integer> cola = new LinkedList<>();
        
        int primerCentro = centrosRecolectores.get(0);
        cola.add(primerCentro);
        visitados.add(primerCentro);
        
        while (!cola.isEmpty()) {
            int u = cola.poll();
            
            for (Arista arista : grafo.listaAdyacencia.get(u)) {
                int v = arista.destino;
                if (centrosRecolectores.contains(v) && !visitados.contains(v)) {
                    System.out.println("Conexión encontrada: " + u + " -> " + v);
                    visitados.add(v);
                    cola.add(v);
                }
            }
        }
        
        boolean conectados = visitados.size() == centrosRecolectores.size();
        if (!conectados) {
            System.out.println("Centros no conectados. Faltan: " + 
                centrosRecolectores.stream()
                    .filter(c -> !visitados.contains(c))
                    .collect(Collectors.toList()));
        }
        return conectados;
    }
    /*
    @SuppressWarnings("unchecked")
    private List<Arista>[] crearSubgrafoCentros() {
        // Crear mapa de índices
        Map<Integer, Integer> centroToIndex = new HashMap<>();
        for (int i = 0; i < centrosRecolectores.size(); i++) {
            centroToIndex.put(centrosRecolectores.get(i), i);
        }
    
        List<Arista>[] subgrafo = new List[centrosRecolectores.size()];
        for (int i = 0; i < subgrafo.length; i++) {
            subgrafo[i] = new ArrayList<>();
        }
    
        // Llenar el subgrafo
        for (int i = 0; i < centrosRecolectores.size(); i++) {
            int u = centrosRecolectores.get(i);
            for (Arista arista : grafo.listaAdyacencia.get(u)) {
                if (centroToIndex.containsKey(arista.destino)) {
                    int v = centroToIndex.get(arista.destino);
                    subgrafo[i].add(new Arista(i, v, arista.peso));
                }
            }
        }
    
        return subgrafo;
    }
    */
    private int find(int[] parent, int i) {
        if (parent[i] != i) {
            parent[i] = find(parent, parent[i]);
        }
        return parent[i];
    }

    private void animarMST(ArrayList<Arista> mst, String algoritmo) {
        final int[] index = {0};
        final boolean[][] animEdges = new boolean[grafo.numeroVertices][grafo.numeroVertices];
        final Color[] animVertices = new Color[grafo.numeroVertices];
        
        // Colorear todos los centros de recolección
        for (int centro : centrosRecolectores) {
            animVertices[centro] = new Color(189, 183, 107); // Marrón
        }
    
        Timer timer = new Timer(1000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (index[0] < mst.size()) {
                    Arista arista = mst.get(index[0]);
                    animEdges[arista.origen][arista.destino] = true;
                    animEdges[arista.destino][arista.origen] = true;
                    
                    setHighlightedEdges(animEdges);
                    setHighlightedVertices(animVertices);
                    repaint();
                    
                    index[0]++;
                } else {
                    ((Timer) e.getSource()).stop();
                    
                    // Mostrar resumen
                    StringBuilder resumen = new StringBuilder("MST (" + algoritmo + ") para centros de recolección:\n");
                    int pesoTotal = 0;
                    for (Arista arista : mst) {
                        resumen.append(arista.origen).append(" - ").append(arista.destino)
                               .append(" (Peso: ").append(arista.peso).append(")\n");
                        pesoTotal += arista.peso;
                    }
                    resumen.append("Peso total: ").append(pesoTotal);
                    JOptionPane.showMessageDialog(null, resumen.toString(), "Resultado " + algoritmo, 
                                                JOptionPane.INFORMATION_MESSAGE);
                    // Limpiar grafo
                    setHighlightedEdges(new boolean[grafo.numeroVertices][grafo.numeroVertices]);
                    setHighlightedVertices(new Color[grafo.numeroVertices]);
                }
            }
        });
        timer.start();
    }

    public void setBoton(JToggleButton boton) {
        this.boton = boton;
    }

    public void toggleCentros() {
        if (grafo == null) {
            JOptionPane.showMessageDialog(null, "No hay grafo cargado.");
            mostrarCentros = false;
            if (boton != null) boton.setSelected(false);
            return;
        }
    
        mostrarCentros = !mostrarCentros;
        if (boton != null) {
            boton.setSelected(mostrarCentros);
        }
    
        if (mostrarCentros) {
            // Nueva versión que garantiza conectividad
            seleccionarCentrosGarantizandoConexion();
        }
        repaint();
    }

    private void seleccionarCentrosGarantizandoConexion() {
        centrosRecolectores.clear();
        List<Integer> noContaminados = new ArrayList<>();
        
        for (int i = 0; i < grafo.numeroVertices; i++) {
            if (grafo.posicionVertices[i].tipoZona == 1) {
                noContaminados.add(i);
            }
        }
    
        if (noContaminados.isEmpty()) {
            JOptionPane.showMessageDialog(null, "No hay vértices no contaminados.");
            return;
        }
    
        int cantidadDeseada = Math.max(1, (int)(noContaminados.size() * PORCENTAJE_CENTROS));
    
        // Usar BFS desde un vértice aleatorio para garantizar conexión
        Random rand = new Random();
        int inicio = noContaminados.get(rand.nextInt(noContaminados.size()));
        
        Queue<Integer> cola = new LinkedList<>();
        Set<Integer> visitados = new HashSet<>();
        cola.add(inicio);
        visitados.add(inicio);
        centrosRecolectores.add(inicio);
    
        while (!cola.isEmpty() && centrosRecolectores.size() < cantidadDeseada) {
            int u = cola.poll();
            
            for (Arista arista : grafo.listaAdyacencia.get(u)) {
                int v = arista.destino;
                if (!visitados.contains(v) && noContaminados.contains(v)) {
                    visitados.add(v);
                    cola.add(v);
                    centrosRecolectores.add(v);
                    if (centrosRecolectores.size() >= cantidadDeseada) break;
                }
            }
        }
    }
/*
    private void actualizarColoresVertices() {
        // Inicializar el arreglo de colores, si es necesario
        if (highlightedVertices == null || highlightedVertices.length != grafo.numeroVertices) {
            highlightedVertices = new Color[grafo.numeroVertices];
        }
    
        // Inicializamos todos los vértices con un color por defecto (por ejemplo, gris)
        for (int i = 0; i < grafo.numeroVertices; i++) {
            highlightedVertices[i] = Color.WHITE;
        }
    
        // Ahora marcamos los centros de recolección con un color específico (verde)
        for (Integer centro : centrosRecolectores) {
            highlightedVertices[centro] = new Color(165, 42, 42); // Custom brown color
        }
    
        // Si el grafo tiene vértices contaminados, marcamos estos con otro color (por ejemplo, rojo)
        for (int i = 0; i < grafo.numeroVertices; i++) {
            if (grafo.posicionVertices[i].tipoZona == 2) {  // Zonas contaminadas
                highlightedVertices[i] = Color.RED;
            }
        }
    
        repaint();  // Redibujar el panel para reflejar los cambios
    }
*/
    public int obtenerDistanciaMinima(int inicio, int destino) {
        // Usamos Dijkstra para encontrar el camino más corto desde 'inicio' hasta 'destino'
        
        // Si no hay grafo cargado, retornamos -1
        if (grafo == null || grafo.listaAdyacencia == null) {
            return -1;
        }
    
        int n = grafo.numeroVertices;
        int[] dist = new int[n];
        boolean[] visitado = new boolean[n];
        
        // Inicializamos las distancias como infinito (o un valor muy alto)
        Arrays.fill(dist, Integer.MAX_VALUE);
        dist[inicio] = 0;
        
        // Cola de prioridad para Dijkstra (usamos una mínima)
        PriorityQueue<Integer> pq = new PriorityQueue<>(Comparator.comparingInt(v -> dist[v]));
        pq.add(inicio);
        
        while (!pq.isEmpty()) {
            int u = pq.poll();
            
            // Si el vértice ya está visitado, lo ignoramos
            if (visitado[u]) continue;
            visitado[u] = true;
    
            // Iteramos sobre las aristas del vértice u
            for (Arista arista : grafo.listaAdyacencia.get(u)) {
                int v = arista.destino;
                int peso = arista.peso;
                
                // Si encontramos una distancia más corta a v, la actualizamos
                if (dist[u] + peso < dist[v]) {
                    dist[v] = dist[u] + peso;
                    pq.add(v);
                }
            }
        }
        
        // Si la distancia al destino sigue siendo infinito, significa que no hay camino
        return dist[destino] == Integer.MAX_VALUE ? -1 : dist[destino];
    }

    public void terminarEjercicio() {
        JOptionPane.showMessageDialog(this, "Ejercicio terminado.\nRegresando al menú principal...");
        // Aquí se llamaría a la función que regresa al menú

    }

        // Método auxiliar para animar paso a paso un camino (lista de vértices) usando
    // arreglos en lugar de hash
    // Se resalta: origen (verde), intermedios (amarillo) y destino (cian) y se van
    // "encendiendo" las aristas.
    private void animatePath(java.util.ArrayList<Integer> camino, Runnable callback) {
        final int delay = 1000; // 1 segundo entre pasos
        int n = grafo.numeroVertices;
        // Arreglo para aristas resaltadas (inicialmente todas falsas)
        boolean[][] animEdges = new boolean[n][n];
        // Arreglo para colores de vértices (inicialmente todos null)
        Color[] animVertices = new Color[n];
        
        // Resalta el origen según su tipoZona
        int origen = camino.get(0);
        int tipoZonaOrigen = grafo.posicionVertices[origen].tipoZona;
        animVertices[origen] = getColorPorZona(tipoZonaOrigen, true); // esInicio = true

        setHighlightedEdges(animEdges);
        setHighlightedVertices(animVertices);
        
        final int[] index = {0};
        Timer timer = new Timer(delay, null);
        timer.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (index[0] < camino.size() - 1) {
                    int origen = camino.get(index[0]);
                    int destino = camino.get(index[0] + 1);
                    
                    // Marcar arista en ambas direcciones (para grafos no dirigidos)
                    animEdges[origen][destino] = true;
                    animEdges[destino][origen] = true;
                    
                    // Colorear vértices
                    if (index[0] + 1 < camino.size() - 1) {
                        animVertices[destino] = Color.YELLOW; // Intermedios amarillos
                    } else {
                        animVertices[destino] = Color.CYAN;   // Destino cian
                    }
                    
                    // Mantener el origen en verde
                    animVertices[camino.get(0)] = Color.GREEN;
                    
                    // Actualizar visualización
                    setHighlightedEdges(animEdges);
                    setHighlightedVertices(animVertices);
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

        // Devuelve el color según tipo de zona y posición (inicio/intermedio/final)
    private Color getColorPorZona(int tipoZona, boolean esInicio) {
        return getColorPorZona(tipoZona, esInicio, false);
    }

    private Color getColorPorZona(int tipoZona, boolean esInicio, boolean esFinal) {
        switch (tipoZona) {
            case 0: return Color.LIGHT_GRAY; // zona sin vértice (no debería animarse)
            case 1: // zona no contaminada
                if (esInicio) return Color.GREEN;
                if (esFinal) return Color.CYAN;
                return Color.YELLOW;
            case 2: return Color.RED; // zona contaminada, siempre en rojo
            default: return Color.DARK_GRAY;
        }
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
                    //Darkkhaki rgb(189, 183, 107)

                    setHighlightedEdges(animEdges);
                    setHighlightedVertices(animVertices);
                    repaint();

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
                    // Limpiar grafo
                    setHighlightedEdges(new boolean[grafo.numeroVertices][grafo.numeroVertices]);
                    setHighlightedVertices(new Color[grafo.numeroVertices]);
                }
            }
        });

        timer.start();
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
