package modelo;

public class Floyd {
    public static int[][] floydWarshall(int[][] costMatrix) {
        int n = costMatrix.length;
        final int INF = 1000000000; // valor grande para representar infinito
        int[][] dist = new int[n][n];
        
        // Inicialización
        for (int i = 0; i < n; i++){
            for (int j = 0; j < n; j++){
                if (i == j)
                    dist[i][j] = 0;
                else if(costMatrix[i][j] == 0)
                    dist[i][j] = INF;
                else
                    dist[i][j] = costMatrix[i][j];
            }
        }
        
        // Algoritmo de Floyd-Warshall
        for (int k = 0; k < n; k++){
            for (int i = 0; i < n; i++){
                for (int j = 0; j < n; j++){
                    if (dist[i][k] + dist[k][j] < dist[i][j])
                        dist[i][j] = dist[i][k] + dist[k][j];
                }
            }
        }
        return dist;
    }
}
