

/*



ADVERTENCIA: Este codigo apenas se va a modificar. Si desea trabajar con el nucleo del programa en si, vea el codigo: moduloCasosLegales.c




*/






#include <stdio.h>
#include <stdlib.h>
#include <string.h>

// Estructura para representar un caso legal
typedef struct CasoLegal {
    int id;                         // Identificador único del caso
    char descripcion[256];          // Descripción o detalles del caso
    int estado;                     // Estado del caso: 0 = No resuelto, 1 = Resuelto
    struct CasoLegal * siguiente;         // Puntero al siguiente nodo (para lista circular)
} CasoLegal;

// Función para crear un nuevo nodo (caso legal)
CasoLegal* crearCasoLegal(int id, char * descripcion, int estado) {
    CasoLegal* nuevoCaso = ( CasoLegal * ) malloc( sizeof(CasoLegal) );
    if(nuevoCaso == NULL) { printf("\nLo sentimos: No se pudo asignar memoria.\n"); exit(1);}
    nuevoCaso -> id = id;

    // Se copia la descripción asegurando no exceder el tamaño
    strncpy(nuevoCaso -> descripcion, descripcion, 255);
    nuevoCaso -> descripcion[255] = '\0'; // Aseguramos la terminación nula
    nuevoCaso -> estado = estado;
    nuevoCaso -> siguiente = NULL;
    return nuevoCaso;
}

// Función para insertar un nuevo caso en la lista circular
void agregarCaso(CasoLegal ** cabeza, int id, char * descripcion, int estado) {
    CasoLegal * nuevoCaso = crearCasoLegal( id, descripcion, estado );
    
    // Si la lista está vacía, el nuevo nodo se convierte en cabeza y su siguiente apunta a sí mismo.
    if( *cabeza == NULL ) {
        *cabeza = nuevoCaso;
        nuevoCaso->siguiente = *cabeza;
    } else {
        // Se recorre la lista para encontrar el último nodo (el que apunta a la cabeza)
        CasoLegal * temp = *cabeza;
        while(temp -> siguiente != *cabeza)
            temp = temp -> siguiente;
        temp -> siguiente = nuevoCaso;
        nuevoCaso -> siguiente = *cabeza; // Se mantiene la circularidad
    }
    printf("\nCaso con ID %d agregado correctamente.\n", id);
}

// Función para mostrar todos los casos en la lista circular
void mostrarCasos(CasoLegal * cabeza) {
    printf("\n--- Lista de Casos Legales ---\n\n");
    if(cabeza == NULL) {
        printf("La lista de casos esta vacia.\n");
        return;
    }
    CasoLegal * temp = cabeza;
    do {
        printf("ID del Caso: %d\n", temp -> id);
        printf("Descripción: %s\n", temp -> descripcion);
        if(temp -> estado == 0) printf("Estado: Sin resolver\n");
        else printf("Estado: Resuelto\n"); //printf("Estado: %s\n", (temp->estado == 0 ? "No resuelto" : "Resuelto"));
        printf("---------------------------\n");
        temp = temp -> siguiente;
    } while( temp != cabeza );
}

// Función para eliminar un caso de la lista circular dado su ID
void quitarCaso(CasoLegal ** cabeza, int id) {
    if( *cabeza == NULL ) {
        printf("\nLo sentimos, la lista ya esta vacia.\n");
        return;
    }
    
    CasoLegal * actual = *cabeza;
    CasoLegal * ant = NULL;
    
    // Se recorre la lista para encontrar el caso con el ID indicado
    do {
        if(actual -> id == id) {
            // Caso especial: si se elimina el nodo cabeza
            if(ant == NULL) {
                // Se busca el último nodo para actualizar su puntero 'siguiente'
                CasoLegal * ultimo = *cabeza;
                while(ultimo -> siguiente != *cabeza)
                    ultimo = ultimo -> siguiente;
                // Si solo hay un nodo en la lista
                if(ultimo == *cabeza) {
                    free(actual);
                    *cabeza = NULL;
                } else {
                    *cabeza = actual -> siguiente;
                    ultimo -> siguiente = *cabeza;
                    free(actual);
                }
            } else {
                ant -> siguiente = actual -> siguiente;
                free(actual);
            }
            printf("\nSe elimino el caso con ID %d.\n", id);
            return;
        }
        ant = actual;
        actual = actual -> siguiente;
    } while(actual != *cabeza);
    printf("\nLo sentimos, No hay casos con ID %d.\n", id);
}

// Función para marcar un caso como resuelto dado su ID
void marcarComoResuelto(CasoLegal * cabeza, int id) {
    if(cabeza == NULL) {
        printf("\nLo sentimos, No hay casos disponibles.\n");
        return;
    }
    
    CasoLegal * temp = cabeza;
    do{ 
        if(temp -> id == id) {
            temp -> estado = 1; // Se marca como resuelto
            printf("\nSe marcó como resuelto el Caso con ID: %d.\n", id);
            return;
        }
        temp = temp -> siguiente;
    } while(temp != cabeza);
    printf("\nLo sentimos, No hay casos con ID %d.\n", id);
}

// Función principal con menú interactivo
int main() {
    CasoLegal * cabeza = NULL;  // Puntero a la cabeza de la lista circular
    int choice, id;
    char descripcion[256];
    
    while(1) {
        printf("\n--- Administración de Casos Legales ---\n");
        printf("\t1. Agregar un nuevo caso\n");
        printf("\t2. Eliminar un caso\n");
        printf("\t3. Marcar caso como resuelto\n");
        printf("\t4. Mostrar todos los casos\n");
        printf("\t5. Salir\n");
        printf("Ingrese su opción: ");
        scanf("%d", &choice);
        getchar(); // Consumir el salto de línea pendiente
        
        switch(choice) {
            case 1:
                printf("Ingrese el ID del caso: ");
                scanf("%d", &id);
                getchar();  // Consumir el salto de línea
                printf("Ingrese la descripción del caso: ");
                fgets(descripcion, 256, stdin);
                // Eliminar el salto de línea que agrega fgets
                descripcion[strcspn(descripcion, "\n")] = '\0';
                agregarCaso(&cabeza, id, descripcion, 0);
                break;
            case 2:
                printf("Ingrese el ID del caso a eliminar: ");
                scanf("%d", &id);
                quitarCaso(&cabeza, id);
                break;
            case 3:
                printf("Ingrese el ID del caso a marcar como resuelto: ");
                scanf("%d", &id);
                marcarComoResuelto(cabeza, id);
                break;
            case 4:
                mostrarCasos(cabeza);
                break;
            case 5:
                printf("Saliendo...\n");
                // Liberar la memoria de todos los nodos antes de salir
                if(cabeza != NULL) {
                    CasoLegal * temp = cabeza -> siguiente;
                    while(temp != cabeza) {
                        CasoLegal * siguiente = temp -> siguiente;
                        free(temp);
                        temp = siguiente;
                    }
                    free(cabeza);
                }
                exit(0);
                break;
            default:
                printf("Opción no válida. Intente de nuevo.\n");
        }
    }
    
    return 0;
}
