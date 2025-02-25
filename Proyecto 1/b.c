#include <stdio.h>
#include <string.h>
#include <stdlib.h>

/*
  Estructura de una persona con datos básicos:
  - turno: se genera con un ID unico.
  - Nombre, Nacionalidad, Motivo: detalles de la persona.
  - Urgencia: nivel de prioridad (1 es urgente, 2 es regular).
 */
typedef struct Persona {
    char turno[10];
    char Nombre[50];
    char Nacionalidad[50];
    char Motivo[100];
    int Urgencia;
} Persona;

/*
  Nodo de la lista ligada de personas:
  - Almacena la info de la persona.
  - Apunta al siguiente nodo, formando una cadena.
 */
typedef struct Nodo {
    Persona persona;
    struct Nodo* siguiente;
} Nodo;

/*
  Estructura para manejar recursos:
  - nombre: nombre del recurso (como: "Alimentos").
  - cantidad: cuántos hay disponibles.
  - siguiente: apunta al siguiente recurso en la lista.
 */
typedef struct Recurso {
    char nombre[50];
    int cantidad;
    struct Recurso* siguiente;
} Recurso;

/*
  Lista doblemente ligada para el historial:
  - Guarda la persona a la que se le asignó un recurso.
  - recurso: nombre del recurso asignado.
  - anterior y siguiente: permiten recorrer el historial en ambos sentidos.
 */
typedef struct Historial {
    Persona persona;
    char recurso[50];
    struct Historial* anterior;
    struct Historial* siguiente;
} Historial;

/* Nodo para la pila (solicitudes urgentes) */
typedef struct NodoPila {
    Persona persona;
    struct NodoPila* siguiente;
} NodoPila;


/*
  Para dar seguimiento a casos:
  - persona: datos de la persona en seguimiento.
  - siguiente: apunta al siguiente caso en seguimiento.
  - resuelto: indica si el caso ya se resolvió (1) o está pendiente (0).
 */
typedef struct Seguimiento {
    Persona persona;
    struct Seguimiento* siguiente;
    int resuelto;
} Seguimiento;

/*
  Crea un nuevo nodo para la lista de personas, copia los datos de la persona al nodo y le pone memoria. Aparte, devuelve el apuntador al nodo recién hecho.
 */
Nodo* crearNodo(Persona persona) {
    Nodo* nuevoNodo = (Nodo*)malloc(sizeof(Nodo));
    if (nuevoNodo == NULL) {
        printf("Error, no se puede asignar memoria");
        return NULL;
    }
    // Copia los campos de la persona al nuevo nodo
    strcpy(nuevoNodo->persona.turno, persona.turno);
    strcpy(nuevoNodo->persona.Nombre, persona.Nombre);
    strcpy(nuevoNodo->persona.Nacionalidad, persona.Nacionalidad);
    strcpy(nuevoNodo->persona.Motivo, persona.Motivo);
    nuevoNodo->persona.Urgencia = persona.Urgencia;
    nuevoNodo->siguiente = NULL;
    return nuevoNodo;
}

/*
  Muestra una lista numerada de las personas registradas y
  permite al usuario elegir a una por su posición en la lista. Aparte, retorna el nodo seleccionado o NULL si no se encuentra.
 */
Nodo* seleccionarPersona(Nodo* lista) {
    if (lista == NULL) {
        printf("No hay personas en la lista.\n");
        return NULL;
    }

    printf("\nSeleccione una persona de la lista:\n");
    int i = 1;
    Nodo* temp = lista;
    
    // Recorre la lista mostrando cada persona
    while (temp != NULL) {
        printf("%d. %s (Turno: %s, Urgencia: %d)\n",
               i, temp->persona.Nombre, temp->persona.turno, temp->persona.Urgencia);
        temp = temp->siguiente;
        i++;
    }

    int opcion;
    printf("Ingrese el numero correspondiente a la persona:\n");
    scanf("%d", &opcion);
    
    temp = lista;
    // Avanza en la lista hasta llegar a la opción elegida
    for (int j = 1; j < opcion && temp != NULL; j++) {
        temp = temp->siguiente;
    }

    if (temp == NULL) {
        printf("Opcion inválida.\n");
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


/*
  Mete un nuevo nodo en la lista de personas, ordenándolas según la urgencia (de mayor a menor).
  Si dos personas tienen la misma urgencia, se mantiene el orden de llegada.
 */
void insertarUrgencia(Nodo** lista, Nodo* nuevoNodo) {
    Nodo* actual = *lista;
    Nodo* anterior = NULL;

    // Busca la posición adecuada comparando urgencias
    while (actual != NULL && actual->persona.Urgencia <= nuevoNodo->persona.Urgencia) {
        anterior = actual;
        actual = actual->siguiente;
    }

    // Si la lista está vacía o el nuevo nodo va al inicio
    if (anterior == NULL) {
        nuevoNodo->siguiente = *lista;
        *lista = nuevoNodo;
    } else {
        // Inserta entre 'anterior' y 'actual'
        nuevoNodo->siguiente = actual;
        anterior->siguiente = nuevoNodo;
    }
}

/*
  Genera un ID de turno a partir de un contador, lo envía como un número de 4 dígitos (ej: "0001", "0002").
 */
void generarTurno(char turno[], int* contadorTurnos) {
    sprintf(turno, "%04d", (*contadorTurnos)++);
}

/*
  Imprime la info de una persona, y se usa para confirmar la captura de datos o para mostrarlos de manera organizada.
 */
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

/*
  Mete una persona a la lista circular de seguimiento.
  Evita duplicados y, si no existe aún, crea un nodo nuevo.
  La lista circular facilita recorrer los casos en seguimiento de forma continua.
 */
void agregarSeguimiento(Seguimiento** cabeza, Persona persona) {
    // Verifica si la persona ya está en seguimiento
    if (*cabeza != NULL) {
        Seguimiento* temp = *cabeza;
        do {
            if (strcmp(temp->persona.Nombre, persona.Nombre) == 0) {
                printf("La persona %s ya esta en seguimiento.\n", persona.Nombre);
                return;  // Evita duplicados
            }
            temp = temp->siguiente;
        } while (temp != *cabeza);
    }

    // Crea un nuevo nodo de seguimiento
    Seguimiento* nuevoSeguimiento = (Seguimiento*)malloc(sizeof(Seguimiento));
    if (nuevoSeguimiento == NULL) {
        printf("Error: No se pudo asignar memoria para seguimiento.\n");
        return;
    }

    nuevoSeguimiento->persona = persona;
    nuevoSeguimiento->resuelto = 0;

    // Si la lista está vacía, el nuevo nodo se autoenlaza
    if (*cabeza == NULL) {
        *cabeza = nuevoSeguimiento;
        nuevoSeguimiento->siguiente = nuevoSeguimiento;
    } else {
        // Busca el último nodo para enlazarlo con el nuevo
        Seguimiento* temp = *cabeza;
        while (temp->siguiente != *cabeza) {
            temp = temp->siguiente;
        }
        temp->siguiente = nuevoSeguimiento;
        nuevoSeguimiento->siguiente = *cabeza;
    }

    printf("Se agrego a seguimiento: %s\n", persona.Nombre);
}

/*
  Pide datos de una persona (nombre, nacionalidad, motivo, urgencia),
  crea su turno y un nodo para insertarlo en la lista con prioridades.
  También añade automáticamente la persona al seguimiento.
 */
void operacionNuevaPersona(int* contadorTurnos, Nodo** listaPersonas, Seguimiento** seguimiento) {
    Persona nuevaPersona;
    generarTurno(nuevaPersona.turno, contadorTurnos);

    printf("\nIngrese su nombre:");
    scanf(" %49[^\n]", nuevaPersona.Nombre);

    printf("\nIngrese su nacionalidad:");
    scanf(" %49[^\n]", nuevaPersona.Nacionalidad);

    printf("\nIngrese su motivo de desplazamiento:");
    scanf(" %49[^\n]", nuevaPersona.Motivo);

    // Valida la urgencia para que sea 1 o 2
    do {
        printf("\nIngrese el grado de urgencia:\n1. Urgente.\n2. Regular.\n");
        scanf("%d", &nuevaPersona.Urgencia);
        
        if (nuevaPersona.Urgencia < 1 || nuevaPersona.Urgencia > 2) {
            printf("\nNumero de urgencia inexistente. Intente de nuevo\n");
        }
    } while (nuevaPersona.Urgencia < 1 || nuevaPersona.Urgencia > 2);

    // Crea e inserta el nodo en la lista de personas
    Nodo* nuevoNodo = crearNodo(nuevaPersona);
    if (nuevoNodo == NULL) {
        printf("Error, no se puede crear el nodo");
        return;
    }

    insertarUrgencia(listaPersonas, nuevoNodo);
    agregarSeguimiento(seguimiento, nuevaPersona);
    datosCliente(nuevaPersona);
}

/*
  Crea un nuevo recurso y lo agrega al inicio de la lista de recursos.
  'nombre' es el tipo de recurso y 'cantidad' cuántos hay disponibles.
 */
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

/*
  Recorre la lista de recursos e imprime la información de cada uno.
  Muestra cuántos están disponibles en ese momento.
 */
void mostrarRecursos(Recurso* lista) {
    printf("\n-Recursos disponibles-\n");
    Recurso* temp = lista;
    while (temp != NULL) {
        printf("- %s: %d disponibles\n", temp->nombre, temp->cantidad);
        temp = temp->siguiente;
    }
}

/*
  Asigna un recurso a una persona seleccionada de la lista de urgencia.
  Luego registra ese evento en el historial y añade/actualiza el seguimiento.
 */
void asignarRecurso(Nodo** listaPersonas, Recurso** listaRecursos,
                    Historial** historial, Seguimiento** seguimiento) {
    if (*listaPersonas == NULL) {
        printf("No hay clientes para asignar recursos.\n");
        return;
    }

    // Selecciona la persona a la que se le va a asignar el recurso
    Nodo* personaSeleccionada = seleccionarPersona(*listaPersonas);
    if (personaSeleccionada == NULL) return;

    // Si no hay recursos, no se puede asignar nada
    if (*listaRecursos == NULL) {
        printf("No hay recursos disponibles.\n");
        return;
    }

    mostrarRecursos(*listaRecursos);

    // Se pide el nombre del recurso que se desea asignar
    printf("\nIngrese el nombre del recurso que desea asignar: ");
    char nombreRecurso[50];
    scanf(" %[^\n]", nombreRecurso);

    // Busca el recurso en la lista
    Recurso* temp = *listaRecursos;
    while (temp != NULL) {
        if (strcmp(temp->nombre, nombreRecurso) == 0 && temp->cantidad > 0) {
            // Disminuye la cantidad disponible de ese recurso
            temp->cantidad--;
            printf("Recurso asignado a %s: %s\n",
                   personaSeleccionada->persona.Nombre, temp->nombre);

            // Crea un nodo para registrar en el historial
            Historial* nuevoHistorial = (Historial*)malloc(sizeof(Historial));
            nuevoHistorial->persona = personaSeleccionada->persona;
            strcpy(nuevoHistorial->recurso, temp->nombre);
            nuevoHistorial->anterior = NULL;
            nuevoHistorial->siguiente = *historial;

            // Enlaza el nuevo nodo al principio de la lista doblemente enlazada
            if (*historial != NULL) {
                (*historial)->anterior = nuevoHistorial;
            }
            *historial = nuevoHistorial;

            // Agrega o actualiza la persona en la lista circular de seguimiento
            agregarSeguimiento(seguimiento, personaSeleccionada->persona);
            return;
        }
        temp = temp->siguiente;
    }

    // Si llega aquí, no se encontró el recurso o no hay suficiente cantidad
    printf("Recurso no encontrado o sin disponibilidad.\n");
    agregarSeguimiento(seguimiento, personaSeleccionada->persona);
}

/*
  Recorre la lista circular de seguimiento, mostrando la información
  de cada caso. Permite marcarlo como resuelto o eliminarlo del seguimiento.
 */
void darSeguimiento(Seguimiento** cabeza) {
    if (*cabeza == NULL) {
        printf("No hay casos en seguimiento.\n");
        return;
    }

    Seguimiento* temp = *cabeza;
    Seguimiento* anterior = NULL;
    do {
        printf("\nSeguimiento de: %s (Turno: %s)\n",
               temp->persona.Nombre, temp->persona.turno);
        printf("Estado: %s\n", temp->resuelto ? "Resuelto" : "Pendiente");

        printf("\nMarcar como resuelto? (1: Si, 0: No): ");
        int opcion;
        scanf("%d", &opcion);

        // Si el usuario elige marcarlo como resuelto
        if (opcion == 1) {
            temp->resuelto = 1;
            printf("Caso de %s marcado como resuelto.\n", temp->persona.Nombre);
        }

        printf("Eliminar de seguimiento? (1: Si, 0: No): ");
        int eliminar;
        scanf("%d", &eliminar);

        // Si se decide eliminar el caso de la lista circular
        if (eliminar == 1) {
            printf("Eliminando caso de %s...\n", temp->persona.Nombre);

            // Caso especial: si solo hay un elemento en la lista
            if (temp == *cabeza && temp->siguiente == *cabeza) {
                free(temp);
                *cabeza = NULL;
                return;
            } else {
                // Busca el último para re-enlazar la lista
                Seguimiento* ultimo = *cabeza;
                while (ultimo->siguiente != *cabeza) {
                    ultimo = ultimo->siguiente;
                }

                // Si el elemento a eliminar es la cabeza
                if (temp == *cabeza) {
                    *cabeza = temp->siguiente;
                    ultimo->siguiente = *cabeza;
                } else {
                    // Elimina el nodo intermedio
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

/*
  Busca en la lista circular de seguimiento un turno específico
  y marca el caso como resuelto si lo encuentra.
 */
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

/*
  Libera la memoria de toda la lista ligada de personas,
  recorriéndola hasta el final.
 */
void liberarLista(Nodo* lista) {
    Nodo* temp;
    while (lista != NULL) {
        temp = lista;
        lista = lista->siguiente;
        free(temp);
    }
}

/*
  Muestra la lista de personas con su turno, urgencia,
  nombre, nacionalidad y motivo. Si la lista está vacía,lo menciona.
 */
void mostrarFila(Nodo* cola) {
    printf("- - - - - - - - - - - - - - - - - - -\n");
    printf("Persona en la cola de prioridades:\n");
    printf("- - - - - - - - - - - - - - - - - - -\n");
    if (cola == NULL) {
        printf("\t* No hay personas en la fila *");
        return;
    }
    
    Nodo* temp = cola;
    while (temp != NULL) {
        printf("--------------------------------------------\n");
        printf("Identificador: %s - Urgencia: %d - Nombre: %s - Nacionalidad: %s - Motivo de desplazamiento: %s\n",
               temp->persona.turno, temp->persona.Urgencia,
               temp->persona.Nombre, temp->persona.Nacionalidad,
               temp->persona.Motivo);
        temp = temp->siguiente;
    }
}

/*
  Recorre la lista doblemente ligada de historial e imprime
  cada asignación realizada: qué cliente recibió qué recurso.
 */
void mostrarHistorial(Historial* historial) {
    printf("Historial de asignaciones:\n");
    Historial* temp = historial;
    while (temp != NULL) {
        printf("Cliente: %s - Recurso: %s\n",
               temp->persona.Nombre, temp->recurso);
        temp = temp->siguiente;
    }
}

/*
 Genera un reporte semanal con:
  - Personas atendidas (en la lista de urgencias).
  - Recursos disponibles.
  - Historial de asignaciones.
  - Casos en seguimiento (pendientes o resueltos).
 */
void generarReporte(Nodo* listaClientes, Recurso* listaRecurso,
                    Historial* historial, Seguimiento* seguimiento) {
    printf("\n--- Reporte semanal ---\n");

    // Personas atendidas
    printf("- Personas Atendidas -\n");
    Nodo* temp = listaClientes;
    while (temp != NULL) {
        printf("- %s (Turno: %s)\n", temp->persona.Nombre, temp->persona.turno);
        temp = temp->siguiente;
    }

    // Recursos disponibles
    printf("\n- Recursos Disponibles -\n");
    Recurso* tempRecursos = listaRecurso;
    while (tempRecursos != NULL) {
        printf("- %s: %d disponibles\n", tempRecursos->nombre, tempRecursos->cantidad);
        tempRecursos = tempRecursos->siguiente;
    }

    // Historial de asignaciones
    printf("\n- Historial de asignaciones -\n");
    Historial* tempHistorial = historial;
    while (tempHistorial != NULL) {
        printf("- %s: %s\n", tempHistorial->persona.Nombre, tempHistorial->recurso);
        tempHistorial = tempHistorial->siguiente;
    }

    // Casos en seguimiento
    printf("\n- Casos de Seguimiento -\n");
    if (seguimiento == NULL) {
        printf("No hay casos en seguimiento\n");
    } else {
        Seguimiento* tempSeguimiento = seguimiento;
        do {
            printf("- %s (Turno: %s) - %s\n",
                   tempSeguimiento->persona.Nombre,
                   tempSeguimiento->persona.turno,
                   tempSeguimiento->resuelto ? "Resuelto" : "Pendiente");
            tempSeguimiento = tempSeguimiento->siguiente;
        } while (tempSeguimiento != seguimiento);
    }
}

/*
  Recorre la lista de recursos y libera la memoria de cada nodo.
 */
void liberarRecursos(Recurso* lista) {
    Recurso* temp;
    while (lista != NULL) {
        temp = lista;
        lista = lista->siguiente;
        free(temp);
    }
}

/*
  Recorre la lista doblemente ligada de historial y libera cada nodo.
 */
void liberarHistorial(Historial* lista) {
    Historial* temp;
    while (lista != NULL) {
        temp = lista;
        lista = lista->siguiente;
        free(temp);
    }
}

/*
  Recorre la lista circular de seguimiento, liberando cada nodo.
  Se detiene cuando vuelve al nodo inicial.
 */
void liberarSeguimiento(Seguimiento* lista) {
    if (lista == NULL) return;
    Seguimiento* temp = lista;
    do {
        Seguimiento* siguiente = temp->siguiente;
        free(temp);
        temp = siguiente;
    } while (temp != lista);
}

/*
 - Controla el ciclo principal de opciones del programa.
 - El usuario puede Registrar un nuevo cliente,  Visualizar la lista de personas,  Asignar un recurso,  Ver historial de asignaciones,  Generar un reporte semanal o Dar seguimiento a casos.
 */
void menuCiclado(int* contadorTurnos, Nodo** listaPersonas,
                 Recurso** listaRecursos, Historial** historial,
                 Seguimiento** seguimiento) {
    int opcion;

    do {
        printf("\n........................\n");
        printf("\n\tMENU\nOpciones disponibles:\n");
        printf("1. Nuevo cliente\n");
        printf("2. Visualizar Lista\n");
        printf("3. Asignar Recurso\n");
        printf("4. Ver Historial\n");
        printf("5. Generar Reporte\n");
        printf("6. Dar Seguimiento\n");
        printf("7. Salir\n");
        printf("\n........................\n");
        printf("\nIngrese el numero de opcion deseado: ");
        scanf("%d", &opcion);

        // Limpia el buffer de entrada
        while (getchar() != '\n');

        // Ejecuta la opción seleccionada
        switch (opcion) {
            case 1:
                operacionNuevaPersona(contadorTurnos, listaPersonas, seguimiento);
                break;
            case 2:
                mostrarFila(*listaPersonas);
                break;
            case 3:
                asignarRecurso(listaPersonas, listaRecursos, historial, seguimiento);
                break;
            case 4:
                mostrarHistorial(*historial);
                break;
            case 5:
                generarReporte(*listaPersonas, *listaRecursos, *historial, *seguimiento);
                break;
            case 6:
                darSeguimiento(seguimiento);
                break;
            case 7:
                break;
            default:
                printf("Opcion no valida\n");
                break;
        }
    } while (opcion != 7);
}

/*
  Función principal que inicializa las estructuras y llama al menú principal.
  Al salir, se liberan todas las listas para evitar fugas de memoria.
 */
int main() {
    Nodo* listaPersonas = NULL;
    Recurso* listaRecursos = NULL;
    Historial* historial = NULL;
    Seguimiento* seguimiento = NULL;
    int contadorTurnos = 0;

    // Se agregan recursos de ejemplo a la lista
    agregarRecurso(&listaRecursos, "Alimentos", 10);
    agregarRecurso(&listaRecursos, "Refugio", 5);
    agregarRecurso(&listaRecursos, "Asesoria Legal", 15);

    // Inicia el menú que controlará las operaciones del programa
    menuCiclado(&contadorTurnos, &listaPersonas, &listaRecursos,
                &historial, &seguimiento);

    // Libera memoria de todas las estructuras al finalizar
    liberarLista(listaPersonas);
    liberarRecursos(listaRecursos);
    liberarHistorial(historial);
    liberarSeguimiento(seguimiento);

    return 0;
}
