import java.util.*;

class Graph {
    private int V;
    private List<List<Edge>> adj;
    private boolean directed;
    
    public Graph(int V, boolean directed) {
        this.V = V;
        this.directed = directed;
        adj = new ArrayList<>(V);
        for (int i = 0; i < V; i++) {
            adj.add(new ArrayList<>());
        }
    }
    
    // Agrega una arista entre u y v con cierto peso.
    // Si el grafo es no dirigido, se agrega la arista en ambas direcciones.
    public void addEdge(int u, int v, int weight) {
        adj.get(u).add(new Edge(u, v, weight));
        if (!directed) {
            adj.get(v).add(new Edge(v, u, weight));
        }
    }
    
    public int getV() {
        return V;
    }
    
    public List<List<Edge>> getAdj() {
        return adj;
    }
    
    public static class Edge {
        public int src;
        public int dest;
        public int weight;
        
        public Edge(int src, int dest, int weight) {
            this.src = src;
            this.dest = dest;
            this.weight = weight;
        }
    }
}

class Dijkstra {
    
    // Clase auxiliar para la prioridad
    private static class Node {
        int vertex;
        int distance;
        public Node(int vertex, int distance) {
            this.vertex = vertex;
            this.distance = distance;
        }
    }
    
    // Retorna un arreglo de distancias mínimas desde 'source' a cada vértice.
    public static int[] dijkstra(Graph graph, int source) {
        int V = graph.getV();
        int[] dist = new int[V];
        Arrays.fill(dist, Integer.MAX_VALUE);
        dist[source] = 0;
        
        PriorityQueue<Node> pq = new PriorityQueue<>(Comparator.comparingInt(n -> n.distance));
        pq.offer(new Node(source, 0));
        
        while (!pq.isEmpty()) {
            Node current = pq.poll();
            int u = current.vertex;
            // Si ya encontramos un camino más corto, saltamos
            if (current.distance > dist[u])
                continue;
            for (Graph.Edge edge : graph.getAdj().get(u)) {
                int v = edge.dest;
                int weight = edge.weight;
                if (dist[u] != Integer.MAX_VALUE && dist[u] + weight < dist[v]) {
                    dist[v] = dist[u] + weight;
                    pq.offer(new Node(v, dist[v]));
                }
            }
        }
        return dist;
    }
    
    public static void printDistances(int[] dist) {
        System.out.println("Distancias desde la fuente:");
        for (int i = 0; i < dist.length; i++) {
            System.out.println("Nodo " + i + " : " + dist[i]);
        }
    }
}
