package modelo;

import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.PriorityQueue;

import javax.swing.JOptionPane;
import javax.swing.Timer;

import modelo.grafo.Arista;

public class pintarMapa {
    




    //metodos para pintar


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

    //para los algoritmos

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

    //metodos que ocupa prim

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
