// modelo/algoritmos/BFS.java
package modelo.algoritmos;

import modelo.grafo.GeneradorGrafo;
import modelo.grafo.posVertice;
import java.util.*;

public class BFS {
    public List<posVertice> encontrarZonaContaminada(GeneradorGrafo grafo, int indiceInicio) {
        List<posVertice> pasos = new ArrayList<>();
        Queue<posVertice> cola = new LinkedList<>();
        boolean[] visitados = new boolean[grafo.numeroVertices];

        //modificar matriz de costos
        grafo.matrizCostos = grafo.grafoNoPonderado(grafo.matrizCostos);
        
        posVertice inicio = grafo.posicionVertices[indiceInicio];
        cola.add(inicio);
        visitados[indiceInicio] = true;
        pasos.add(inicio); // Paso 1: Iluminar inicio

        while (!cola.isEmpty()) {
            posVertice actual = cola.poll();
            
            // Paso 4: Detectar zona contaminada
            if (actual.tipoZona == 2) {
                pasos.add(actual); // Añade el contaminado como último paso
                return pasos;
            }
            
            // Explorar vecinos
            for (int i = 0; i < grafo.numeroVertices; i++) {
                if (grafo.matrizCostos[Arrays.asList(grafo.posicionVertices).indexOf(actual)][i] != 0 && !visitados[i]) {
                    visitados[i] = true;
                    cola.add(grafo.posicionVertices[i]);
                    pasos.add(grafo.posicionVertices[i]); // Paso 3: Iluminar progresivo
                }
            }
        }
        return pasos; // Si no encuentra zonas contaminadas
    }
}