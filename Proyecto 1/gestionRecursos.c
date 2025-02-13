#include <stdio.h>
#include <stdlib.h>
#include <string.h>

// Estructura para un nodo de la lista simple (servicios disponibles)
typedef struct Nodo {
    char servicio[20];  // Nombre del servicio (Alimento, Refugio, Asesoría Legal)
    int cantidad;       // Cantidad disponible del servicio
    struct Nodo* siguiente;
} Nodo;

// Estructura para la lista doble (historial de asignaciones)
typedef struct NodoDoble {
    char persona[50];
    char servicio[20];
    int cantidad;
    struct NodoDoble* siguiente;
    struct NodoDoble* anterior;
} NodoDoble;

// Función para crear un nodo de servicio
Nodo* crearNodoServicio(char servicio[], int cantidad) {
    Nodo* nuevo = (Nodo*)malloc(sizeof(Nodo));
    if (nuevo == NULL) return NULL;
    strcpy(nuevo->servicio, servicio);
    nuevo->cantidad = cantidad;
    nuevo->siguiente = NULL;
    return nuevo;
}

// Función para agregar servicios a la lista simple
void agregarServicio(Nodo** listaServicios, char servicio[], int cantidad) {
    Nodo* actual = *listaServicios;

    // Buscar si el servicio ya existe en la lista
    while (actual != NULL) {
        if (strcmp(actual->servicio, servicio) == 0) {
            actual->cantidad += cantidad;  // Aumentar la cantidad disponible
            printf("\n%d unidades de %s fueron agregadas.\n", cantidad, servicio);
            return;
        }
        actual = actual->siguiente;
    }

    // Si no existe, crear un nuevo nodo
    Nodo* nuevo = crearNodoServicio(servicio, cantidad);
    nuevo->siguiente = *listaServicios;
    *listaServicios = nuevo;

    printf("\n%d unidades de %s fueron agregadas.\n", cantidad, servicio);
}

// Función para mostrar los servicios disponibles
void mostrarServicios(Nodo* listaServicios) {
    printf("\nServicios Disponibles:\n");
    if (listaServicios == NULL) {
        printf("No hay servicios disponibles.\n");
        return;
    }

    while (listaServicios != NULL) {
        printf("- %s: %d unidades\n", listaServicios->servicio, listaServicios->cantidad);
        listaServicios = listaServicios->siguiente;
    }
}

// Función para eliminar servicios (asignar a una persona)
int eliminarServicio(Nodo** listaServicios, char servicio[], int cantidad) {
    Nodo* actual = *listaServicios;
    Nodo* anterior = NULL;

    while (actual != NULL) {
        if (strcmp(actual->servicio, servicio) == 0) {
            if (actual->cantidad >= cantidad) {
                actual->cantidad -= cantidad;
                if (actual->cantidad == 0) { // Si ya no queda cantidad, eliminar nodo
                    if (anterior == NULL) {
                        *listaServicios = actual->siguiente;
                    } else {
                        anterior->siguiente = actual->siguiente;
                    }
                    free(actual);
                }
                return 1; // Eliminación exitosa
            } else {
                return 0; // No hay suficiente cantidad disponible
            }
        }
        anterior = actual;
        actual = actual->siguiente;
    }
    return -1; // El servicio no existe
}

// Función para agregar asignaciones al historial (lista doble)
void agregarAHistorial(NodoDoble** historial, char persona[], char servicio[], int cantidad) {
    NodoDoble* nuevo = (NodoDoble*)malloc(sizeof(NodoDoble));
    if (nuevo == NULL) return;

    strcpy(nuevo->persona, persona);
    strcpy(nuevo->servicio, servicio);
    nuevo->cantidad = cantidad;
    nuevo->siguiente = *historial;
    nuevo->anterior = NULL;

    if (*historial != NULL) {
        (*historial)->anterior = nuevo;
    }
    *historial = nuevo;
}

// Función para asignar servicios a una persona
void asignarServicios(Nodo** listaServicios, NodoDoble** historial) {
    char persona[50];
    int opcion, cantidad;
    char* servicios[] = {"Alimento", "Refugio", "Asesoría Legal"};

    printf("\nIngrese el nombre de la persona: ");
    scanf(" %[^\n]", persona);

    do {
        printf("\nSeleccione el servicio a asignar:\n");
        printf("1. Alimento\n2. Refugio\n3. Asesoría Legal\n4. Finalizar asignación\n");
        printf("Opción: ");
        scanf("%d", &opcion);

        if (opcion >= 1 && opcion <= 3) {
            printf("Ingrese la cantidad a asignar: ");
            scanf("%d", &cantidad);

            int resultado = eliminarServicio(listaServicios, servicios[opcion - 1], cantidad);
            if (resultado == 1) {
                agregarAHistorial(historial, persona, servicios[opcion - 1], cantidad);
                printf("Se asignaron %d unidades de %s a %s.\n", cantidad, servicios[opcion - 1], persona);
            } else if (resultado == 0) {
                printf("No hay suficiente cantidad de %s disponible.\n", servicios[opcion - 1]);
            } else {
                printf("El servicio %s no está disponible.\n", servicios[opcion - 1]);
            }
        }
    } while (opcion != 4);
}

// Función para mostrar el historial de asignaciones
void mostrarHistorial(NodoDoble* historial) {
    printf("\nHistorial de Asignaciones:\n");
    if (historial == NULL) {
        printf("No hay asignaciones registradas.\n");
        return;
    }

    while (historial != NULL) {
        printf("Persona: %s - Servicio: %s - Cantidad: %d\n", historial->persona, historial->servicio, historial->cantidad);
        historial = historial->siguiente;
    }
}

// Función para liberar la memoria de la lista simple
void liberarListaSimple(Nodo* lista) {
    Nodo* temp;
    while (lista != NULL) {
        temp = lista;
        lista = lista->siguiente;
        free(temp);
    }
}

// Función para liberar la memoria de la lista doble
void liberarListaDoble(NodoDoble* lista) {
    NodoDoble* temp;
    while (lista != NULL) {
        temp = lista;
        lista = lista->siguiente;
        free(temp);
    }
}

// Menú interactivo
void menu(Nodo** listaServicios, NodoDoble** historial) {
    int opcion;
    do {
        printf("\n========= MENÚ =========\n");
        printf("1. Agregar servicios\n");
        printf("2. Asignar servicios a una persona\n");
        printf("3. Ver historial de asignaciones\n");
        printf("4. Ver servicios disponibles\n");
        printf("5. Salir\n");
        printf("Seleccione una opción: ");
        scanf("%d", &opcion);

        switch (opcion) {
            case 1: {
                int servicio, cantidad;
                printf("\nSeleccione un servicio:\n");
                printf("1. Alimento\n2. Refugio\n3. Asesoría Legal\nOpción: ");
                scanf("%d", &servicio);

                if (servicio >= 1 && servicio <= 3) {
                    printf("Ingrese cantidad a agregar: ");
                    scanf("%d", &cantidad);
                    agregarServicio(listaServicios, servicio == 1 ? "Alimento" : servicio == 2 ? "Refugio" : "Asesoría Legal", cantidad);
                }
                break;
            }
            case 2:
                asignarServicios(listaServicios, historial);
                break;
            case 3:
                mostrarHistorial(*historial);
                break;
            case 4:
                mostrarServicios(*listaServicios);
                break;
        }
    } while (opcion != 5);
}

// Función principal
int main() {
    Nodo* listaServicios = NULL;
    NodoDoble* historial = NULL;

    menu(&listaServicios, &historial);

    liberarListaSimple(listaServicios);
    liberarListaDoble(historial);

    return 0;
}
