public class GraphAlgorithmsDemo {
    public static void main(String[] args) {
        // --- Ejemplo para Dijkstra ---
        System.out.println("Ejemplo Dijkstra:");
        int V = 5;
        boolean directed = false; // Cambia a true para un grafo dirigido
        Graph graph = new Graph(V, directed);
        // Agregar aristas: addEdge(origen, destino, peso)
        graph.addEdge(0, 1, 10);
        graph.addEdge(0, 4, 5);
        graph.addEdge(1, 2, 1);
        graph.addEdge(1, 4, 2);
        graph.addEdge(2, 3, 4);
        graph.addEdge(3, 0, 7);
        graph.addEdge(3, 2, 6);
        graph.addEdge(4, 1, 3);
        graph.addEdge(4, 2, 9);
        graph.addEdge(4, 3, 2);
        
        int source = 0;
        int[] distances = Dijkstra.dijkstra(graph, source);
        Dijkstra.printDistances(distances);
        
        // --- Ejemplo para Floyd-Warshall ---
        System.out.println("\nEjemplo Floyd-Warshall:");
        // Se usa una matriz de adyacencia para 5 vértices
        int INF = FloydWarshall.INF;
        int[][] matrix = {
            {0, 10, INF, INF, 5},
            {INF, 0, 1, INF, 2},
            {INF, INF, 0, 4, INF},
            {7, INF, 6, 0, INF},
            {INF, 3, 9, 2, 0}
        };
        int[][] allPairDistances = FloydWarshall.floydWarshall(matrix);
        FloydWarshall.printMatrix(allPairDistances);
    }
}
