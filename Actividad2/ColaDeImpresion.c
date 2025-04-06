/*Implementar un programa que simule el manejo de una cola de impresión en un entorno 
con varias solicitudes de impresión. El programa permite las siguientes funciones: 
a. Cada solicitud de impresión tiene un identificador, un tamaño (número de 
páginas) y una prioridad. 
b. Los trabajos se agregan a la cola en orden de llegada. 
c. El sistema procesa los trabajos de la cola en el orden en que fueron 
solicitados. */
//Cola circular dinamica

#include <stdio.h>
#include <stdlib.h>
#include <string.h>

typedef struct {
    int id;          
    int paginas;     
    int prioridad;   
} Solicitud;

typedef struct {
    Solicitud *array;    
    int capacidad;       
    int frente;          
    int final;           
    int tamano;          
} ColaImpresion;


ColaImpresion* crearCola(int capacidadInicial) {
    ColaImpresion *cola = (ColaImpresion*)malloc(sizeof(ColaImpresion));
    cola->array = (Solicitud*)malloc(capacidadInicial * sizeof(Solicitud));
    cola->capacidad = capacidadInicial;
    cola->frente = 0;
    cola->final = -1;
    cola->tamano = 0;
    return cola;
}


int estaVacia(ColaImpresion *cola) {
    return cola->tamano == 0;
}


int estaLlena(ColaImpresion *cola) {
    return cola->tamano == cola->capacidad;
}


void redimensionarCola(ColaImpresion *cola) {
    int nuevaCapacidad = cola->capacidad * 2;
    Solicitud *nuevoArray = (Solicitud*)malloc(nuevaCapacidad * sizeof(Solicitud));

    for (int i = 0; i < cola->tamano; i++) {
        nuevoArray[i] = cola->array[(cola->frente + i) % cola->capacidad];
    }

    free(cola->array);
    cola->array = nuevoArray;
    cola->capacidad = nuevaCapacidad;
    cola->frente = 0;
    cola->final = cola->tamano - 1;
}

void encolar(ColaImpresion *cola, int id, int paginas, int prioridad) {
    if (estaLlena(cola)) {
        redimensionarCola(cola);
    }
    cola->final = (cola->final + 1) % cola->capacidad;
    cola->array[cola->final].id = id;
    cola->array[cola->final].paginas = paginas;
    cola->array[cola->final].prioridad = prioridad;
    cola->tamano++;
    printf("\nSolicitud %d (%d Paginas, Prioridad: %d) agregada a la cola de impresion.\n", id, paginas, prioridad);
}


Solicitud desencolar(ColaImpresion *cola) {
    if (estaVacia(cola)) {
        printf("\nERROR: La cola esta vacia.\n");
        return (Solicitud){-1, -1, -1}; 
    }
    Solicitud solicitud = cola->array[cola->frente];
    cola->frente = (cola->frente + 1) % cola->capacidad;
    cola->tamano--;
    return solicitud;
}


void mostrarCola(ColaImpresion *cola) {
    if (estaVacia(cola)) {
        printf("\nLa cola de impresion esta vacia.\n");
        return;
    }
    printf("\nCola de impresion: \n");
    printf("%d solicitudes en espera.\n", cola->tamano);
    for (int i = 0; i < cola->tamano; i++) {
        Solicitud solicitud = cola->array[(cola->frente + i) % cola->capacidad];
        printf("\t-ID: %d, %d Paginas, Prioridad: %d\n", solicitud.id, solicitud.paginas, solicitud.prioridad);
    }
}


void liberarCola(ColaImpresion *cola) {
    free(cola->array);
    free(cola);
}

int main() {
    ColaImpresion *cola = crearCola(3); 
    int opcion;

    do {
        printf("\n           MENU\n");
        printf("1. Agregar solicitud de impresion\n");
        printf("2. Imprimir\n");
        printf("3. Mostrar estado de la cola\n");
        printf("4. Salir\n");
        printf("Elija una opcion (Escriba el numero de la opcion): ");
        scanf("%d", &opcion);

        switch (opcion) {
            case 1: {
                int id, paginas, prioridad;
                printf("\n\t-ID : ");
                scanf("%d", &id);
                printf("\tNumero de paginas: ");
                scanf("%d", &paginas);
                printf("\tPrioridad : ");
                scanf("%d", &prioridad);
                encolar(cola, id, paginas, prioridad);
                break;
            }
            case 2: {
                printf("\nImprimiendo: \n");
                Solicitud s = desencolar(cola);
                if (s.id != -1) {
                    printf("ID: %d, %d Paginas, Prioridad: %d\n", s.id, s.paginas, s.prioridad);
                }
                break;
            }
            case 3:
                mostrarCola(cola);
                break;
            case 4:
                printf("\nSaliendo...\n");
                break;
            default:
                printf("\nERROR: Opcion invalida\n");
        }
    } while (opcion != 4);

    liberarCola(cola);
    return 0;
}



