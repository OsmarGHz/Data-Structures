package modelo.algoritmos;

import java.util.*;

public class Prim {
    public static List<int[]> primMST(int[][] costMatrix) {
        int n = costMatrix.length;
        int[] key = new int[n]; 
        int[] parent = new int[n];
        boolean[] mstSet = new boolean[n];
        final int INF = Integer.MAX_VALUE;
        
        Arrays.fill(key, INF);
        key[0] = 0; 
        parent[0] = -1; // raíz del MST
        
        for (int count = 0; count < n - 1; count++){
            int u = minKey(key, mstSet);
            mstSet[u] = true;
            
            for (int v = 0; v < n; v++){
                if (costMatrix[u][v] != 0 && !mstSet[v] && costMatrix[u][v] < key[v]){
                    parent[v] = u;
                    key[v] = costMatrix[u][v];
                }
            }
        }
        
        List<int[]> mstEdges = new ArrayList<>();
        for (int i = 1; i < n; i++){
            mstEdges.add(new int[]{ parent[i], i, costMatrix[parent[i]][i] });
        }
        return mstEdges;
    }
    
    private static int minKey(int[] key, boolean[] mstSet) {
        int min = Integer.MAX_VALUE, minIndex = -1;
        for (int v = 0; v < key.length; v++){
            if (!mstSet[v] && key[v] < min){
                min = key[v];
                minIndex = v;
            }
        }
        return minIndex;
    }
}