#include <stdio.h>
#include <string.h>
#include <stdlib.h>

/* Estructura de una persona con datos básicos */
typedef struct Persona {
    char turno[10];
    char Nombre[50];
    char Nacionalidad[50];
    char Motivo[100];
    int Urgencia;
} Persona;

/* Nodo para la lista ligada (solicitudes regulares) */
typedef struct Nodo {
    Persona persona;
    struct Nodo* siguiente;
} Nodo;

/* Nodo para la pila (solicitudes urgentes) */
typedef struct NodoPila {
    Persona persona;
    struct NodoPila* siguiente;
} NodoPila;

/* Estructura para manejar recursos */
typedef struct Recurso {
    char nombre[50];
    int cantidad;
    struct Recurso* siguiente;
} Recurso;

/* Lista doblemente ligada para el historial */
typedef struct Historial {
    Persona persona;
    char recurso[50];
    struct Historial* anterior;
    struct Historial* siguiente;
} Historial;

/* Lista circular para casos de seguimiento */
typedef struct Seguimiento {
    Persona persona;
    struct Seguimiento* siguiente;
    int resuelto;
} Seguimiento;

/* ***** Funciones para manejo de la lista ligada (solicitudes regulares) ***** */

/* Crea un nuevo nodo para la lista ligada y copia la información de la persona */
Nodo* crearNodo(Persona persona) {
    Nodo* nuevoNodo = (Nodo*)malloc(sizeof(Nodo));
    if (nuevoNodo == NULL) {
        printf("Error, no se puede asignar memoria\n");
        return NULL;
    }
    nuevoNodo->persona = persona;
    nuevoNodo->siguiente = NULL;
    return nuevoNodo;
}

/* Inserta un nodo al final de la lista ligada (para mantener el orden de llegada) */
void insertarRegular(Nodo** lista, Nodo* nuevoNodo) {
    if (*lista == NULL) {
        *lista = nuevoNodo;
    } else {
        Nodo* temp = *lista;
        while (temp->siguiente != NULL) {
            temp = temp->siguiente;
        }
        temp->siguiente = nuevoNodo;
    }
}

/* Muestra la lista ligada de solicitudes regulares */
void mostrarListaRegular(Nodo* lista) {
    if (lista == NULL) {
        printf("\t* No hay solicitudes regulares *\n");
        return;
    }
    while (lista != NULL) {
        printf("  Identificador: %s - Nombre: %s - Urgencia: %d\n",
               lista->persona.turno, lista->persona.Nombre, lista->persona.Urgencia);
        lista = lista->siguiente;
    }
}

/* Permite al usuario seleccionar una persona de la lista ligada */
Nodo* seleccionarPersona(Nodo* lista) {
    if (lista == NULL) {
        printf("No hay solicitudes regulares en la lista.\n");
        return NULL;
    }
    printf("\nSeleccione una solicitud de la lista:\n");
    int i = 1;
    Nodo* temp = lista;
    while (temp != NULL) {
        printf("%d. %s (Turno: %s)\n", i, temp->persona.Nombre, temp->persona.turno);
        temp = temp->siguiente;
        i++;
    }
    int opcion;
    printf("Ingrese el número correspondiente: ");
    scanf("%d", &opcion);
    temp = lista;
    for (int j = 1; j < opcion && temp != NULL; j++) {
        temp = temp->siguiente;
    }
    if (temp == NULL) {
        printf("Opción inválida.\n");
        return NULL;
    }
    return temp;
}

/* ***** Funciones para manejo de la pila (solicitudes urgentes) ***** */

/* Inserta (push) una solicitud urgente en la pila */
void pushUrgente(NodoPila** pila, Persona p) {
    NodoPila* nuevo = (NodoPila*)malloc(sizeof(NodoPila));
    if (nuevo == NULL) {
        printf("Error, no se pudo asignar memoria para la pila.\n");
        return;
    }
    nuevo->persona = p;
    nuevo->siguiente = *pila;
    *pila = nuevo;
}

/* Extrae (pop) la solicitud urgente de la pila */
Persona popUrgente(NodoPila** pila) {
    Persona p;
    // En caso de pila vacía, se retorna una persona con turno vacío
    if (*pila == NULL) {
        strcpy(p.turno, "");
        return p;
    }
    NodoPila* temp = *pila;
    p = temp->persona;
    *pila = temp->siguiente;
    free(temp);
    return p;
}

/* Muestra la pila de solicitudes urgentes */
void mostrarPilaUrgentes(NodoPila* pila) {
    if (pila == NULL) {
        printf("\t* No hay solicitudes urgentes *\n");
        return;
    }
    NodoPila* temp = pila;
    while (temp != NULL) {
        printf("  Identificador: %s - Nombre: %s - Urgencia: %d\n",
               temp->persona.turno, temp->persona.Nombre, temp->persona.Urgencia);
        temp = temp->siguiente;
    }
}

/* ***** Funciones auxiliares comunes ***** */

/* Genera un ID de turno a partir de un contador */
void generarTurno(char turno[], int* contadorTurnos) {
    sprintf(turno, "%04d", (*contadorTurnos)++);
}

/* Muestra la información de una persona */
void datosCliente(Persona persona) {
    printf("-------------------\n");
    printf("Datos del nuevo cliente:\n");
    printf("Turno: %s\n", persona.turno);
    printf("Nombre: %s\n", persona.Nombre);
    printf("Nacionalidad: %s\n", persona.Nacionalidad);
    printf("Motivo: %s\n", persona.Motivo);
    printf("Urgencia: %d\n", persona.Urgencia);
    printf("-------------------\n");
}

/* ***** Funciones de Seguimiento, Recursos, Historial (se mantienen casi sin cambios) ***** */

void agregarSeguimiento(Seguimiento** cabeza, Persona persona) {
    if (*cabeza != NULL) {
        Seguimiento* temp = *cabeza;
        do {
            if (strcmp(temp->persona.Nombre, persona.Nombre) == 0) {
                printf("La persona %s ya está en seguimiento.\n", persona.Nombre);
                return;
            }
            temp = temp->siguiente;
        } while (temp != *cabeza);
    }
    Seguimiento* nuevoSeguimiento = (Seguimiento*)malloc(sizeof(Seguimiento));
    if (nuevoSeguimiento == NULL) {
        printf("Error: No se pudo asignar memoria para seguimiento.\n");
        return;
    }
    nuevoSeguimiento->persona = persona;
    nuevoSeguimiento->resuelto = 0;
    if (*cabeza == NULL) {
        *cabeza = nuevoSeguimiento;
        nuevoSeguimiento->siguiente = nuevoSeguimiento;
    } else {
        Seguimiento* temp = *cabeza;
        while (temp->siguiente != *cabeza) {
            temp = temp->siguiente;
        }
        temp->siguiente = nuevoSeguimiento;
        nuevoSeguimiento->siguiente = *cabeza;
    }
    printf("Se agregó a seguimiento: %s\n", persona.Nombre);
}

void agregarRecurso(Recurso** lista, char nombre[], int cantidad) {
    Recurso* nuevoRecurso = (Recurso*)malloc(sizeof(Recurso));
    if (nuevoRecurso == NULL) {
        printf("Error, no se pudo asignar memoria para recurso.\n");
        return;
    }
    strcpy(nuevoRecurso->nombre, nombre);
    nuevoRecurso->cantidad = cantidad;
    nuevoRecurso->siguiente = *lista;
    *lista = nuevoRecurso;
}

void mostrarRecursos(Recurso* lista) {
    printf("\n- Recursos disponibles -\n");
    while (lista != NULL) {
        printf("  - %s: %d disponibles\n", lista->nombre, lista->cantidad);
        lista = lista->siguiente;
    }
}

void mostrarHistorial(Historial* historial) {
    printf("\n- Historial de asignaciones -\n");
    while (historial != NULL) {
        printf("  Cliente: %s - Recurso: %s\n", historial->persona.Nombre, historial->recurso);
        historial = historial->siguiente;
    }
}

void darSeguimiento(Seguimiento** cabeza) {
    if (*cabeza == NULL) {
        printf("No hay casos en seguimiento.\n");
        return;
    }
    Seguimiento* temp = *cabeza;
    Seguimiento* anterior = NULL;
    do {
        printf("\nSeguimiento de: %s (Turno: %s)\n", temp->persona.Nombre, temp->persona.turno);
        printf("Estado: %s\n", temp->resuelto ? "Resuelto" : "Pendiente");
        printf("\nMarcar como resuelto? (1: Sí, 0: No): ");
        int opcion;
        scanf("%d", &opcion);
        if (opcion == 1) {
            temp->resuelto = 1;
            printf("Caso de %s marcado como resuelto.\n", temp->persona.Nombre);
        }
        printf("Eliminar de seguimiento? (1: Sí, 0: No): ");
        int eliminar;
        scanf("%d", &eliminar);
        if (eliminar == 1) {
            printf("Eliminando caso de %s...\n", temp->persona.Nombre);
            if (temp == *cabeza && temp->siguiente == *cabeza) {
                free(temp);
                *cabeza = NULL;
                return;
            } else {
                Seguimiento* ultimo = *cabeza;
                while (ultimo->siguiente != *cabeza) {
                    ultimo = ultimo->siguiente;
                }
                if (temp == *cabeza) {
                    *cabeza = temp->siguiente;
                    ultimo->siguiente = *cabeza;
                } else {
                    anterior->siguiente = temp->siguiente;
                }
                free(temp);
                return;
            }
        }
        anterior = temp;
        temp = temp->siguiente;
    } while (temp != *cabeza);
}

void marcarCasoResuelto(Seguimiento** cabeza, char turno[]) {
    if (*cabeza == NULL) {
        printf("No hay casos de seguimiento.");
        return;
    }
    Seguimiento* temp = *cabeza;
    do {
        if (strcmp(temp->persona.turno, turno) == 0) {
            temp->resuelto = 1;
            printf("Caso marcado como resuelto: %s\n", temp->persona.Nombre);
            return;
        }
        temp = temp->siguiente;
    } while (temp != *cabeza);
    printf("Caso no encontrado.\n");
}

/* ***** Funciones para liberar memoria ***** */

void liberarLista(Nodo* lista) {
    Nodo* temp;
    while (lista != NULL) {
        temp = lista;
        lista = lista->siguiente;
        free(temp);
    }
}

void liberarPilaUrgentes(NodoPila* pila) {
    NodoPila* temp;
    while (pila != NULL) {
        temp = pila;
        pila = pila->siguiente;
        free(temp);
    }
}

void liberarRecursos(Recurso* lista) {
    Recurso* temp;
    while (lista != NULL) {
        temp = lista;
        lista = lista->siguiente;
        free(temp);
    }
}

void liberarHistorial(Historial* lista) {
    Historial* temp;
    while (lista != NULL) {
        temp = lista;
        lista = lista->siguiente;
        free(temp);
    }
}

void liberarSeguimiento(Seguimiento* lista) {
    if (lista == NULL) return;
    Seguimiento* temp = lista;
    do {
        Seguimiento* siguiente = temp->siguiente;
        free(temp);
        temp = siguiente;
    } while (temp != lista);
}

/* ***** Funciones modificadas para el módulo de registro de solicitudes ***** */

/* Función para registrar un nuevo cliente:
   - Si es urgente se añade a la pila.
   - Si es regular se inserta en la lista ligada. 
   Además, se agrega a la lista de seguimiento. */
void operacionNuevaPersona(int* contadorTurnos, NodoPila** pilaUrgentes, Nodo** listaRegulares, Seguimiento** seguimiento) {
    Persona nuevaPersona;
    generarTurno(nuevaPersona.turno, contadorTurnos);

    printf("\nIngrese su nombre: ");
    scanf(" %49[^\n]", nuevaPersona.Nombre);

    printf("Ingrese su nacionalidad: ");
    scanf(" %49[^\n]", nuevaPersona.Nacionalidad);

    printf("Ingrese su motivo de desplazamiento: ");
    scanf(" %49[^\n]", nuevaPersona.Motivo);

    do {
        printf("Ingrese el grado de urgencia:\n  1. Urgente\n  2. Regular\n");
        scanf("%d", &nuevaPersona.Urgencia);
        if (nuevaPersona.Urgencia < 1 || nuevaPersona.Urgencia > 2)
            printf("Número de urgencia inexistente. Intente de nuevo.\n");
    } while (nuevaPersona.Urgencia < 1 || nuevaPersona.Urgencia > 2);

    if (nuevaPersona.Urgencia == 1) {
        pushUrgente(pilaUrgentes, nuevaPersona);
    } else {
        Nodo* nuevoNodo = crearNodo(nuevaPersona);
        insertarRegular(listaRegulares, nuevoNodo);
    }

    agregarSeguimiento(seguimiento, nuevaPersona);
    datosCliente(nuevaPersona);
}

/* Muestra todas las solicitudes: primero las urgentes y luego las regulares */
void mostrarFila(NodoPila* pilaUrgentes, Nodo* listaRegulares) {
    printf("\n----- Solicitudes de Servicio -----\n");
    printf("\nSolicitudes Urgentes (Pila):\n");
    mostrarPilaUrgentes(pilaUrgentes);
    printf("\nSolicitudes Regulares (Lista):\n");
    mostrarListaRegular(listaRegulares);
}

/* Asigna un recurso a una solicitud:
   - Si hay solicitudes urgentes se procesa la de la cima (pop de la pila).
   - Si no, se permite seleccionar una solicitud regular. */
void asignarRecurso(NodoPila** pilaUrgentes, Nodo** listaRegulares,
                    Recurso** listaRecursos, Historial** historial,
                    Seguimiento** seguimiento) {
    Persona personaSeleccionada;
    // Prioriza solicitudes urgentes
    if (*pilaUrgentes != NULL) {
        personaSeleccionada = popUrgente(pilaUrgentes);
        printf("Asignando recurso a solicitud URGENTE: %s (Turno: %s)\n",
               personaSeleccionada.Nombre, personaSeleccionada.turno);
    } else {
        if (*listaRegulares == NULL) {
            printf("No hay solicitudes para asignar recursos.\n");
            return;
        }
        Nodo* nodoSeleccionado = seleccionarPersona(*listaRegulares);
        if (nodoSeleccionado == NULL)
            return;
        personaSeleccionada = nodoSeleccionado->persona;
    }

    if (*listaRecursos == NULL) {
        printf("No hay recursos disponibles.\n");
        return;
    }

    mostrarRecursos(*listaRecursos);
    printf("\nIngrese el nombre del recurso a asignar: ");
    char nombreRecurso[50];
    scanf(" %[^\n]", nombreRecurso);

    Recurso* temp = *listaRecursos;
    while (temp != NULL) {
        if (strcmp(temp->nombre, nombreRecurso) == 0 && temp->cantidad > 0) {
            temp->cantidad--;
            printf("Recurso asignado a %s: %s\n", personaSeleccionada.Nombre, temp->nombre);

            Historial* nuevoHistorial = (Historial*)malloc(sizeof(Historial));
            nuevoHistorial->persona = personaSeleccionada;
            strcpy(nuevoHistorial->recurso, temp->nombre);
            nuevoHistorial->anterior = NULL;
            nuevoHistorial->siguiente = *historial;
            if (*historial != NULL)
                (*historial)->anterior = nuevoHistorial;
            *historial = nuevoHistorial;

            agregarSeguimiento(seguimiento, personaSeleccionada);
            return;
        }
        temp = temp->siguiente;
    }
    printf("Recurso no encontrado o sin disponibilidad.\n");
    agregarSeguimiento(seguimiento, personaSeleccionada);
}

/* Genera un reporte semanal combinando ambas estructuras de solicitudes */
void generarReporte(NodoPila* pilaUrgentes, Nodo* listaRegulares, Recurso* listaRecursos,
                    Historial* historial, Seguimiento* seguimiento) {
    printf("\n--- Reporte Semanal ---\n");

    printf("\n- Solicitudes Urgentes -\n");
    NodoPila* tempP = pilaUrgentes;
    while (tempP != NULL) {
        printf("  %s (Turno: %s)\n", tempP->persona.Nombre, tempP->persona.turno);
        tempP = tempP->siguiente;
    }

    printf("\n- Solicitudes Regulares -\n");
    Nodo* tempR = listaRegulares;
    while (tempR != NULL) {
        printf("  %s (Turno: %s)\n", tempR->persona.Nombre, tempR->persona.turno);
        tempR = tempR->siguiente;
    }

    printf("\n- Recursos Disponibles -\n");
    Recurso* tempRecursos = listaRecursos;
    while (tempRecursos != NULL) {
        printf("  %s: %d disponibles\n", tempRecursos->nombre, tempRecursos->cantidad);
        tempRecursos = tempRecursos->siguiente;
    }

    mostrarHistorial(historial);

    printf("\n- Casos de Seguimiento -\n");
    if (seguimiento == NULL) {
        printf("  No hay casos en seguimiento\n");
    } else {
        Seguimiento* tempS = seguimiento;
        do {
            printf("  %s (Turno: %s) - %s\n", tempS->persona.Nombre,
                   tempS->persona.turno,
                   tempS->resuelto ? "Resuelto" : "Pendiente");
            tempS = tempS->siguiente;
        } while (tempS != seguimiento);
    }
}

/* ***** Menú principal (actualizado para usar ambas estructuras) ***** */
void menuCiclado(int* contadorTurnos, NodoPila** pilaUrgentes, Nodo** listaRegulares,
                 Recurso** listaRecursos, Historial** historial, Seguimiento** seguimiento) {
    int opcion;
    do {
        printf("\n........................\n");
        printf("\n\tMENU\nOpciones disponibles:\n");
        printf("1. Nuevo cliente\n");
        printf("2. Visualizar Solicitudes\n");
        printf("3. Asignar Recurso\n");
        printf("4. Ver Historial\n");
        printf("5. Generar Reporte\n");
        printf("6. Dar Seguimiento\n");
        printf("7. Salir\n");
        printf("........................\n");
        printf("Ingrese el número de opción deseada: ");
        scanf("%d", &opcion);
        while(getchar() != '\n');  // limpiar buffer

        switch(opcion) {
            case 1:
                operacionNuevaPersona(contadorTurnos, pilaUrgentes, listaRegulares, seguimiento);
                break;
            case 2:
                mostrarFila(*pilaUrgentes, *listaRegulares);
                break;
            case 3:
                asignarRecurso(pilaUrgentes, listaRegulares, listaRecursos, historial, seguimiento);
                break;
            case 4:
                mostrarHistorial(*historial);
                break;
            case 5:
                generarReporte(*pilaUrgentes, *listaRegulares, *listaRecursos, *historial, *seguimiento);
                break;
            case 6:
                darSeguimiento(seguimiento);
                break;
            case 7:
                break;
            default:
                printf("Opción no válida\n");
                break;
        }
    } while (opcion != 7);
}

/* ***** Función principal ***** */
int main() {
    NodoPila* pilaUrgentes = NULL;
    Nodo* listaRegulares = NULL;
    Recurso* listaRecursos = NULL;
    Historial* historial = NULL;
    Seguimiento* seguimiento = NULL;
    int contadorTurnos = 0;

    // Agregar algunos recursos de ejemplo
    agregarRecurso(&listaRecursos, "Alimentos", 10);
    agregarRecurso(&listaRecursos, "Refugio", 5);
    agregarRecurso(&listaRecursos, "Asesoria Legal", 15);

    // Inicia el menú principal
    menuCiclado(&contadorTurnos, &pilaUrgentes, &listaRegulares,
                &listaRecursos, &historial, &seguimiento);

    // Liberar la memoria asignada
    liberarPilaUrgentes(pilaUrgentes);
    liberarLista(listaRegulares);
    liberarRecursos(listaRecursos);
    liberarHistorial(historial);
    liberarSeguimiento(seguimiento);

    return 0;
}
