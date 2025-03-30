package modelo;

import java.util.*;

public class BFS {
    public static List<Integer> bfs(int[][] adjMatrix, int start) {
        int n = adjMatrix.length;
        boolean[] visited = new boolean[n];
        List<Integer> order = new ArrayList<>();
        Queue<Integer> queue = new LinkedList<>();
        
        visited[start] = true;
        queue.offer(start);
        
        while (!queue.isEmpty()){
            int u = queue.poll();
            order.add(u);
            for (int v = 0; v < n; v++){
                if (adjMatrix[u][v] == 1 && !visited[v]){
                    visited[v] = true;
                    queue.offer(v);
                }
            }
        }
        return order;
    }
}