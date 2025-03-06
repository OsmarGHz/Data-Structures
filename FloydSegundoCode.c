#include <stdio.h>
#include <limits.h>

#define V 5 // Número de vértices
#define INF INT_MAX // Representación de infinito

void floydWarshall(int graph[V][V]) {
    int dist[V][V], i, j, k;

    // Inicializar la matriz de distancias
    for (i = 0; i < V; i++)
        for (j = 0; j < V; j++)
            dist[i][j] = graph[i][j];

    // Aplicar el algoritmo de Floyd-Warshall
    for (k = 0; k < V; k++) {
        for (i = 0; i < V; i++) {
            for (j = 0; j < V; j++) {
                if (dist[i][k] != INF && dist[k][j] != INF && dist[i][k] + dist[k][j] < dist[i][j])
                    dist[i][j] = dist[i][k] + dist[k][j];
            }
        }
    }

    // Imprimir la matriz de distancias más cortas
    printf("Matriz de distancias más cortas:\n");
    for (i = 0; i < V; i++) {
        for (j = 0; j < V; j++) {
            if (dist[i][j] == INF)
                printf("INF ");
            else
                printf("%d ", dist[i][j]);
        }
        printf("\n");
    }
}

int main() {
    int graph[V][V] = {
        {0, 3, 2, INF, INF},
        {INF, 0, INF, 1, INF},
        {INF, INF, 0, 4, 5},
        {INF, INF, INF, 0, 2},
        {INF, 6, INF, INF, 0}
    };

    floydWarshall(graph);

    return 0;
}
