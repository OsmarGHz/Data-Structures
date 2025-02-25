#include <stdio.h>
#include <stdlib.h>

typedef struct {
    int * datos;      // Array dinámico para almacenar los elementos
    int capacidad;   // Capacidad actual del array
    int tamano;      // Número actual de elementos en la cola
} Cola;

// Función para crear una cola con capacidad inicial
Cola * crearCola(int capacidadInicial) {
    Cola * cola = (Cola *)malloc(sizeof(Cola));
    if (!cola) {
        fprintf(stderr, "Error al asignar memoria para la cola.\n");
        exit(EXIT_FAILURE);
    }
    cola -> datos = (int *)malloc(capacidadInicial * sizeof(int));
    if (!cola -> datos) {
        free(cola);
        fprintf(stderr, "Error al asignar memoria para los elementos.\n");
        exit(EXIT_FAILURE);
    }
    cola -> capacidad = capacidadInicial;
    cola -> tamano = 0;
    return cola;
}

// Función para redimensionar la cola (duplicar la capacidad)
void redimensionarCola(Cola * cola) {
    int nuevaCapacidad = cola -> capacidad * 2;
    int * nuevosDatos = (int *)realloc(cola->datos, nuevaCapacidad * sizeof(int));
    if (!nuevosDatos) {
        fprintf(stderr, "Error al redimensionar la cola.\n");
        exit(EXIT_FAILURE);
    }
    cola -> datos = nuevosDatos;
    cola -> capacidad = nuevaCapacidad;
    printf("Cola redimensionada a capacidad %d.\n", nuevaCapacidad);
}

// Función para encolar (agregar) un elemento a la cola
void encolar(Cola * cola, int elemento) {
    if (cola -> tamano == cola -> capacidad) {
        redimensionarCola(cola);
    }
    cola -> datos[cola -> tamano] = elemento;
    cola -> tamano++;
    printf("Elemento %d encolado.\n", elemento);
}

// Función para desencolar (eliminar) el primer elemento de la cola
int desencolar(Cola * cola) {
    if (cola -> tamano == 0) {
        fprintf(stderr, "La cola está vacía. No se puede desencolar.\n");
        exit(EXIT_FAILURE);
    }
    int elemento = cola -> datos[0];
    // Desplazar los elementos a la izquierda
    for (int i = 1; i < cola -> tamano; i++) {
        cola -> datos[i - 1] = cola -> datos[i];
    }
    cola -> tamano--;
    return elemento;
}

// Función para mostrar los elementos de la cola
void mostrarCola(Cola * cola) {
    if (cola -> tamano == 0) {
        printf("La cola está vacía.\n");
        return;
    }
    printf("Elementos de la cola: ");
    for (int i = 0; i < cola -> tamano; i++) {
        printf("%d ", cola -> datos[i]);
    }
    printf("\n");
}

// Función para liberar la memoria de la cola
void liberarCola(Cola *cola) {
    free(cola -> datos);
    free(cola);
}

// Función principal para probar la cola
int main() {
    Cola * cola = crearCola(4); // Capacidad inicial de 4

    // Encolar algunos elementos
    encolar(cola, 10);
    encolar(cola, 20);
    encolar(cola, 30);
    encolar(cola, 40);
    mostrarCola(cola);

    // Encolar un elemento más para forzar la redimensión
    encolar(cola, 50);
    mostrarCola(cola);

    // Desencolar algunos elementos
    int e = desencolar(cola);
    printf("Elemento desencolado: %d\n", e);
    e = desencolar(cola);
    printf("Elemento desencolado: %d\n", e);
    mostrarCola(cola);

    // Liberar la memoria
    liberarCola(cola);
    return 0;
}
