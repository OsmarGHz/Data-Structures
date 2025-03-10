class FloydWarshall {
    public static final int INF = Integer.MAX_VALUE;
    
    // Recibe una matriz de adyacencia 'graph' y retorna una matriz de distancias mínimas.
    public static int[][] floydWarshall(int[][] graph) {
        int V = graph.length;
        int[][] dist = new int[V][V];
        
        // Inicializa la matriz de distancias.
        for (int i = 0; i < V; i++) {
            for (int j = 0; j < V; j++) {
                dist[i][j] = graph[i][j];
            }
        }
        
        // Algoritmo de Floyd-Warshall: para cada vértice intermedio k, actualiza los caminos.
        for (int k = 0; k < V; k++) {
            for (int i = 0; i < V; i++) {
                for (int j = 0; j < V; j++) {
                    if (dist[i][k] != INF && dist[k][j] != INF &&
                        dist[i][k] + dist[k][j] < dist[i][j]) {
                        dist[i][j] = dist[i][k] + dist[k][j];
                    }
                }
            }
        }
        
        return dist;
    }
    
    public static void printMatrix(int[][] matrix) {
        int V = matrix.length;
        System.out.println("Matriz de distancias:");
        for (int i = 0; i < V; i++) {
            for (int j = 0; j < V; j++) {
                if (matrix[i][j] == INF)
                    System.out.print("INF ");
                else
                    System.out.print(matrix[i][j] + " ");
            }
            System.out.println();
        }
    }
}
