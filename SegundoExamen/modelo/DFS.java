package modelo;

import java.util.*;

public class DFS {
    public static List<Integer> dfs(int[][] adjMatrix, int start) {
        int n = adjMatrix.length;
        boolean[] visited = new boolean[n];
        List<Integer> order = new ArrayList<>();
        dfsHelper(adjMatrix, start, visited, order);
        return order;
    }
    
    private static void dfsHelper(int[][] adjMatrix, int u, boolean[] visited, List<Integer> order) {
        visited[u] = true;
        order.add(u);
        for (int v = 0; v < adjMatrix.length; v++) {
            if (adjMatrix[u][v] == 1 && !visited[v]) {
                dfsHelper(adjMatrix, v, visited, order);
            }
        }
    }
}