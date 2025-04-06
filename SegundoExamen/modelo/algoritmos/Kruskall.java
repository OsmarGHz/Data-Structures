package modelo.algoritmos;

import java.util.*;

public class Kruskall {
    public static List<int[]> kruskallMST(int[][] costMatrix) {
        int n = costMatrix.length;
        List<Edge> edges = new ArrayList<>();
        
        // Recopilar todas las aristas (solo una vez por par [u,v])
        for (int i = 0; i < n; i++){
            for (int j = i+1; j < n; j++){
                if (costMatrix[i][j] != 0){
                    edges.add(new Edge(i, j, costMatrix[i][j]));
                }
            }
        }
        
        // Ordenar aristas por peso
        edges.sort(Comparator.comparingInt(e -> e.weight));
        
        UnionFind uf = new UnionFind(n);
        List<int[]> mstEdges = new ArrayList<>();
        
        for (Edge e : edges){
            if (uf.find(e.u) != uf.find(e.v)){
                uf.union(e.u, e.v);
                mstEdges.add(new int[]{ e.u, e.v, e.weight });
            }
        }
        return mstEdges;
    }
    
    private static class Edge {
        int u, v, weight;
        Edge(int u, int v, int weight){
            this.u = u;
            this.v = v;
            this.weight = weight;
        }
    }
    
    private static class UnionFind {
        int[] parent;
        public UnionFind(int n){
            parent = new int[n];
            for (int i = 0; i < n; i++){
                parent[i] = i;
            }
        }
        int find(int i){
            if (parent[i] != i)
                parent[i] = find(parent[i]);
            return parent[i];
        }
        void union(int i, int j){
            parent[find(i)] = find(j);
        }
    }
}
