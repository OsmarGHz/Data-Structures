package modelo;

import java.util.*;

public class Dijkstra {
    public static int[] dijkstra(int[][] costMatrix, int source) {
        int n = costMatrix.length;
        int[] dist = new int[n];
        boolean[] sptSet = new boolean[n];
        final int INF = Integer.MAX_VALUE;
        
        Arrays.fill(dist, INF);
        dist[source] = 0;
        
        for (int count = 0; count < n - 1; count++){
            int u = minDistance(dist, sptSet);
            sptSet[u] = true;
            for (int v = 0; v < n; v++){
                if (!sptSet[v] && costMatrix[u][v] != 0 && dist[u] != INF &&
                    dist[u] + costMatrix[u][v] < dist[v]) {
                    dist[v] = dist[u] + costMatrix[u][v];
                }
            }
        }
        return dist;
    }
    
    private static int minDistance(int[] dist, boolean[] sptSet) {
        int min = Integer.MAX_VALUE, index = -1;
        for (int v = 0; v < dist.length; v++){
            if (!sptSet[v] && dist[v] <= min) {
                min = dist[v];
                index = v;
            }
        }
        return index;
    }
}