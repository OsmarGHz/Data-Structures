#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <stdbool.h>

#define MAX_NOMBRE 100

typedef struct {
    char * nombre;
    int danada; //1 = danada, 0 = no danada.
} Cancion;

typedef struct {
    Cancion * canciones; // Direccion donde estaran las canciones
    int capacidad;      // Tamano maximo actual de la cola circular
    int frente;
    int final;
    int tamaño;         // Tamano actual de libreria
} ColaCircular;

void leerYLimpiarBuffer(){
    char c;
    while ((c = getchar()) != '\n' && c != EOF);
}

ColaCircular * startCola(int capacidadInicial) {
    ColaCircular * cola = (ColaCircular *) malloc(sizeof(ColaCircular));
    if (cola == NULL) {
        printf("No se pudo asignar memoria para la cola\n");
        exit(EXIT_FAILURE);
    }
    cola->canciones = (Cancion *) malloc(capacidadInicial * sizeof(Cancion));
    if (cola->canciones == NULL) {
        printf("No se pudo asignar memoria para las canciones\n");
        free(cola);
        exit(EXIT_FAILURE);
    }
    cola->capacidad = capacidadInicial;
    cola->frente = 0;
    cola->final = -1;
    cola->tamaño = 0;
    return cola;
}

void cambiarTamanoCola(ColaCircular * cola) {
    int nuevaCapacidad = cola->capacidad * 2;
    Cancion* nuevasCanciones = (Cancion *) malloc(nuevaCapacidad * sizeof(Cancion));
    if (!nuevasCanciones) {
        printf("No se pudo redimensionar la cola\n");
        exit(EXIT_FAILURE);
    }

    // Reorganizar canciones en orden logico
    for (int i = 0; i < cola->tamaño; i++) {
        nuevasCanciones[i] = cola->canciones[(cola->frente + i) % cola->capacidad];
    }

    free(cola->canciones);
    cola->canciones = nuevasCanciones;
    cola->capacidad = nuevaCapacidad;
    cola->frente = 0;
    cola->final = cola->tamaño - 1;
}

// Funcion para agregar una cancion (enqueue)
void agregarCancion(ColaCircular * cola, char * nombre) {
    if (cola->tamaño == cola->capacidad) {
        cambiarTamanoCola(cola);
    }
    cola->final = (cola->final + 1) % cola->capacidad;
    cola->canciones[cola->final].nombre = strdup(nombre);
    cola->canciones[cola->final].danada = 0;
    cola->tamaño++;
    printf("Se agrego la cancion '%s' a la lista de reproduccion\n", nombre);
}

// Funcion para marcar una cancion como dañada
void marcarComoDañada(ColaCircular * cola, int indice) {
    if (indice < 0 || indice >= cola->tamaño) {
        printf("Vuelva a ingresar el indice, el indicado es invalido\n");
        return;
    }
    int posReal = (cola->frente + indice) % cola->capacidad;
    cola->canciones[posReal].danada = 1;
    printf("Se marco la cancion '%s' como dañada\n", cola->canciones[posReal].nombre);
}

// Funcion para reproducir canciones en orden
void reproducir(ColaCircular * cola, int ciclos) {
    printf("\n\nIniciando reproduccion...\n");
    for (int ciclo = 0; ciclo < ciclos; ciclo++) {
        for (int i = 0; i < cola->tamaño; i++) {
            int posReal = (cola->frente + i) % cola->capacidad;
            if (cola->canciones[posReal].danada) {
                printf("\nCancion '%s' dañada, saltando...\n", cola->canciones[posReal].nombre);
                continue;
            }
            printf("\nReproduciendo: '%s'\nIngrese un enter para continuar...\n", cola->canciones[posReal].nombre);
            leerYLimpiarBuffer();
            if (i==0 && ciclo==0) leerYLimpiarBuffer();
        }
        printf("\nCiclo %d completado.\n\n", ciclo + 1);
    }
}

// Funcion para liberar memoria
void liberarCola(ColaCircular * cola) {
    for (int i = 0; i < cola->tamaño; i++) {
        free(cola->canciones[(cola->frente + i) % cola->capacidad].nombre);
    }
    free(cola->canciones);
    free(cola);
}

//Funcion para ver si esta vacia
int estaVacia(ColaCircular * cola){
    if (cola->tamaño==0) return 1;
    else return 0;
    
}

// Funcion principal
int main() {
    ColaCircular * cola = startCola(2);
    int decision, ciclos;

    while (true) {
        printf("\nBienvenido!\n");
        printf("\nOpciones disponibles del reproductor:\n");
        printf("\t1. Agregar cancion\n");
        printf("\t2. Dañar cancion\n");
        printf("\t3. Reproducir libreria\n");
        printf("\t4. Terminar el programa\n");
        printf("Seleccione una opcion: ");
        scanf("%d", &decision);
        getchar(); // Consumir el salto de linea

        switch (decision) {
            case 1: {
                char nombre[MAX_NOMBRE];
                printf("Ingrese el nombre de la cancion: ");
                fgets(nombre, MAX_NOMBRE, stdin);
                nombre[strcspn(nombre, "\n")] = '\0'; // Eliminar el salto de linea
                agregarCancion(cola, nombre);
                break;
            }
            case 2: {
                if (estaVacia(cola)) printf("\nNo hay canciones disponibles para dañar\n");
                else{
                    int indice;
                    printf("Ingrese el indice de la cancion (va de 0 a %d): ", cola->tamaño - 1);
                    scanf("%d", &indice);
                    marcarComoDañada(cola, indice);
                }
                break;
            }
            case 3:
                if (estaVacia(cola)) printf("No hay canciones en la cola\n");
                else{
                    printf("¿Cuantos ciclos de reproduccion desea realizar? ");
                    scanf("%d", &ciclos);
                    reproducir(cola, ciclos);
                }
                break;
            case 4:
                printf("Saliendo del programa...\n");
                liberarCola(cola);
                exit(EXIT_SUCCESS);
            default:
                printf("Opcion no valida.\n");
        }
    }
}