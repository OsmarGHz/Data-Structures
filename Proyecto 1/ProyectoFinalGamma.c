#include <stdio.h>
#include <string.h>
#include <stdlib.h>

//estrcuturas, recursos, personas, historial, seguimiento
typedef struct Persona{
    char turno[10];
    char Nombre[50];
    char Nacionalidad[50];
    char Motivo[100];
    int Urgencia;
}Persona;

typedef struct Nodo{
    Persona persona;
    struct Nodo* siguiente;
}Nodo;

typedef struct Recurso{
    char nombre[50];
    int cantidad;
    struct Recurso* siguiente;
}Recurso;

typedef struct Historial{
    Persona persona;
    char recurso[50];
    struct Historial* anterior; 
    struct Historial* siguiente;
}Historial;

typedef struct Seguimiento{
    Persona persona;
    struct Seguimiento* siguiente;  
}Seguimiento;

//Limpiar buffer
void limpiarBuffer(){
    char c;
    while ((c = getchar()) != '\n' && c != EOF);
}

//Escaneo de enteros con validacion
int escaneoEntero(int * variable){
    if (scanf("%d", variable) != 1){
        printf("Entrada invalida. Ingrese un numero: ");
        limpiarBuffer();
        return 0;
    }
    return 1;
}

//Crear un nodo

Nodo* crearNodo(Persona persona){
    Nodo* nuevoNodo = (Nodo*)malloc(sizeof(Nodo));
    if (nuevoNodo == NULL){
        printf("Error, no se puede asignar memoria");
        return NULL;
    }
    strcpy(nuevoNodo->persona.turno, persona.turno);
    strcpy(nuevoNodo->persona.Nombre, persona.Nombre); 
    strcpy(nuevoNodo->persona.Nacionalidad, persona.Nacionalidad);
    strcpy(nuevoNodo->persona.Motivo, persona.Motivo);
    nuevoNodo->persona.Urgencia = persona.Urgencia; 
    nuevoNodo->siguiente = NULL;
    return nuevoNodo;
}

Nodo* seleccionarPersona(Nodo* lista) {
    if (lista == NULL) {
        printf("No hay personas en la lista.\n");
        return NULL;
    }

    printf("\nSeleccione una persona de la lista:\n");
    int i = 1;
    Nodo* temp = lista;
    
    while (temp != NULL) {
        printf("%d. %s (Turno: %s, Urgencia: %d)\n", i, temp->persona.Nombre, temp->persona.turno, temp->persona.Urgencia);
        temp = temp->siguiente;
        i++;
    }

    int opcion;
    printf("Ingrese el numero correspondiente a la persona:\n");
    while (escaneoEntero(&opcion) == 0);
    
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

// Inserta ordenando según la urgencia (1: más urgente, 2: regular)
void insertarUrgencia(Nodo** lista, Nodo* nuevoNodo){
    Nodo* actual = *lista;
    Nodo* anterior = NULL;

    while (actual != NULL && actual->persona.Urgencia <= nuevoNodo->persona.Urgencia){
        anterior = actual;
        actual = actual->siguiente;
    }

    if (anterior == NULL){
        nuevoNodo->siguiente = *lista;
        *lista = nuevoNodo;
    } else {
        nuevoNodo -> siguiente = actual;
        anterior->siguiente = nuevoNodo;
    }
}

void generarTurno(char turno[], int* contadorTurnos){
    sprintf(turno, "%04d", (*contadorTurnos)++);
}

void datosCliente(Persona persona){
    printf("-------------------\n");
    printf("Datos del nuevo cliente:\n");
    printf("Turno: %s\n", persona.turno);
    printf("Nombre: %s\n", persona.Nombre);
    printf("Nacionalidad: %s\n", persona.Nacionalidad);
    printf("Motivo: %s\n", persona.Motivo);
    printf("Urgencia: %d\n", persona.Urgencia);
    printf("-------------------\n");
}

void operacionNuevaPersona(int* contadorTurnos, Nodo** listaPersonas){
    Persona nuevaPersona;
    generarTurno(nuevaPersona.turno, contadorTurnos);

    printf("\nIngrese su nombre:");
    scanf(" %[^\n]", nuevaPersona.Nombre);

    printf("\nIngrese su nacionalidad:");
    scanf(" %[^\n]", nuevaPersona.Nacionalidad);

    printf("\nIngrese su motivo de desplazamiento:");
    scanf(" %[^\n]", nuevaPersona.Motivo);

    do{
        printf("\nIngrese el grado de urgencia:\n1. Urgente.\n2. Regular.\nOpción: ");
        while (escaneoEntero(&nuevaPersona.Urgencia) == 0);
        
        if (nuevaPersona.Urgencia < 1 || nuevaPersona.Urgencia > 2){
            printf("\n- - - - - - - - - - - - - - - - - - - - - - - -");
            printf("\nNumero de urgencia inexistente. Intente de nuevo");
            printf("\n- - - - - - - - - - - - - - - - - - - - - - - -\n");

        }
    } while (nuevaPersona.Urgencia < 1 || nuevaPersona.Urgencia > 2);

    Nodo* nuevoNodo = crearNodo(nuevaPersona);
    if(nuevoNodo == NULL){
        printf("Error, no se puede crear el nodo");
        return;
    }

    // Usar el nodo ya creado (evita duplicados)
    insertarUrgencia(listaPersonas, nuevoNodo);
    datosCliente(nuevaPersona);
}

void liberarLista(Nodo* lista) {
    Nodo* temp;
    while (lista != NULL) {
        temp = lista;
        lista = lista->siguiente;
        free(temp);
    }
}

void mostrarFila(Nodo* cola) {
    printf("- - - - - - - - - - - - - - - - - - -\n");
    printf("Personas en la cola de prioridades:\n");
    printf("- - - - - - - - - - - - - - - - - - -\n");
    Nodo* temp = cola;
    if (cola == NULL) {
        printf("\t* No hay personas en la fila *\n");
        return;
    }
    
    while (temp != NULL) {
        printf("--------------------------------------------\n");
        printf("Identificador: %s - Urgencia: %d - Nombre: %s - Nacionalidad: %s - Motivo: %s\n",
               temp->persona.turno, temp->persona.Urgencia, temp->persona.Nombre, temp->persona.Nacionalidad, temp->persona.Motivo);
        temp = temp->siguiente;
    }
}

void agregarRecurso(Recurso** lista, char nombre[], int cantidad){
    Recurso* nuevoRecurso = (Recurso*)malloc(sizeof(Recurso));
    strcpy(nuevoRecurso->nombre, nombre);
    nuevoRecurso->cantidad = cantidad;
    nuevoRecurso->siguiente = *lista;
    *lista = nuevoRecurso;
}

void mostrarRecursos(Recurso* lista){
    printf("\n-Recursos disponibles-\n");
    Recurso* temp = lista;
    while (temp != NULL){
        printf("- %s: %d disponibles\n", temp->nombre, temp->cantidad);
        temp = temp->siguiente;
    }
}

void asignarRecurso(Nodo** listaPersonas, Recurso** listaRecursos, Historial** historial) {
    if (*listaPersonas == NULL) {
        printf("No hay clientes para asignar recursos.\n");
        return;
    }

    // Seleccionar a la persona
    Nodo* personaSeleccionada = seleccionarPersona(*listaPersonas);
    if (personaSeleccionada == NULL) return;

    // Mostrar recursos
    if (*listaRecursos == NULL) {
        printf("No hay recursos disponibles.\n");
        return;
    }
    mostrarRecursos(*listaRecursos);

    // Seleccionar recurso
    printf("\nIngrese el nombre del recurso que desea asignar: ");
    char nombreRecurso[50];
    scanf(" %[^\n]", nombreRecurso);

    Recurso* temp = *listaRecursos;
    while (temp != NULL) {
        if (strcmp(temp->nombre, nombreRecurso) == 0 && temp->cantidad > 0) {
            temp->cantidad--;
            printf("Recurso asignado a %s: %s\n", personaSeleccionada->persona.Nombre, temp->nombre);

            // Guardar en historial
            Historial* nuevoHistorial = (Historial*)malloc(sizeof(Historial));
            nuevoHistorial->persona = personaSeleccionada->persona;
            strcpy(nuevoHistorial->recurso, temp->nombre);
            nuevoHistorial->anterior = NULL;
            nuevoHistorial->siguiente = *historial;

            if (*historial != NULL) {
                (*historial)->anterior = nuevoHistorial;
            }
            *historial = nuevoHistorial;
            return;
        }
        temp = temp->siguiente;
    }
    printf("Recurso no encontrado o sin disponibilidad.\n");
}

void mostrarHistorial(Historial* historial){
    printf("Historial de asignaciones:\n");
    Historial* temp = historial;
    while(temp != NULL){
        printf("Cliente: %s - Recurso: %s\n", temp->persona.Nombre, temp->recurso);
        temp = temp->siguiente;
    }
}

void agregarSeguimiento(Seguimiento** cabeza, Persona persona){
    Seguimiento* nuevoSeguimiento = (Seguimiento*)malloc(sizeof(Seguimiento));
    nuevoSeguimiento->persona = persona;

    if(*cabeza == NULL){
        nuevoSeguimiento->siguiente = nuevoSeguimiento;
        *cabeza = nuevoSeguimiento;
    } else {
        Seguimiento* temp = *cabeza;
        while (temp->siguiente != *cabeza){
            temp = temp->siguiente;
        }
        temp->siguiente = nuevoSeguimiento;
        nuevoSeguimiento->siguiente = *cabeza;
    }
}

void mostrarSeguimiento(Seguimiento* cabeza){
    if(cabeza == NULL){
        printf("No hay casos de seguimiento\n");
        return;
    }
    Seguimiento* temp = cabeza;
    printf("-- Casos de seguimiento --\n");
    do {
        printf("Identificador: %s - Nombre: %s\n", temp->persona.turno, temp->persona.Nombre);
        temp = temp->siguiente;    
    } while(temp != cabeza);
}

void generarReporte(Nodo* listaClientes, Recurso* listaRecurso, Historial* historial, Seguimiento* seguimiento){
    printf("\n--- Reporte semanal ---\n");

    // Clientes atendidos
    printf("- Personas atendidas -\n");
    Nodo* temp = listaClientes;
    while(temp != NULL){
        printf("- %s (Turno: %s)\n", temp->persona.Nombre, temp->persona.turno);
        temp = temp->siguiente;
    }

    // Recursos disponibles
    printf("\n- Recursos disponibles -\n");
    Recurso* tempRecursos = listaRecurso;
    while (tempRecursos != NULL){
        printf("- %s: %d disponibles\n", tempRecursos->nombre, tempRecursos->cantidad);
        tempRecursos = tempRecursos->siguiente;
    }

    // Historial
    printf("\n- Historial de asignaciones -\n");
    Historial* tempHistorial = historial;
    while(tempHistorial != NULL){
        printf("- %s: %s\n", tempHistorial->persona.Nombre, tempHistorial->recurso);
        tempHistorial = tempHistorial->siguiente;
    }

    // Seguimiento
    printf("\n- Casos en seguimiento -\n");
    if(seguimiento == NULL){
        printf("No hay casos en seguimiento\n");
    } else{
        Seguimiento* tempSeguimiento = seguimiento;
        do {
            printf("- %s (Turno: %s)\n", tempSeguimiento->persona.Nombre, tempSeguimiento->persona.turno);
            tempSeguimiento = tempSeguimiento->siguiente;
        } while(tempSeguimiento != seguimiento);
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
    if (lista == NULL)
        return;
    Seguimiento* temp = lista->siguiente;
    while (temp != lista) {
         Seguimiento* siguiente = temp->siguiente;
         free(temp);
         temp = siguiente;
    }
    free(lista);
}

void menuCiclado(int* contadorTurnos, Nodo** listaPersonas, Recurso** listaRecursos, Historial** historial, Seguimiento** seguimiento){
    int opcion;
    do {
        printf("\n........................\n");
        printf("\n\tMENU\nOpciones disponibles:\n");
        printf("1. Nuevo cliente\n2. Visualizar Lista\n3. Asignar Recurso\n4. Ver Historial\n5. Generar Reporte\n6. Salir\n");
        printf("........................\n");
        printf("\nIngrese el número de opción deseado: ");
        scanf("%d", &opcion);
        while (getchar() != '\n');
        switch (opcion) {
            case 1:
                operacionNuevaPersona(contadorTurnos, listaPersonas);
                break;
            case 2:
                mostrarFila(*listaPersonas);                
                break;
            case 3:
                asignarRecurso(listaPersonas, listaRecursos, historial);
                break;
            case 4:
                mostrarHistorial(*historial);
                break;
            case 5:
                generarReporte(*listaPersonas, *listaRecursos, *historial, *seguimiento);
                break;
            case 6:
                break;
            default:
                printf("Opción no válida\n");
                break;
        }
    } while (opcion != 6);
}

int main(){
    Nodo* listaPersonas = NULL;
    Recurso* listaRecursos = NULL;
    Historial* historial = NULL;
    Seguimiento* seguimiento = NULL;
    int contadorTurnos = 0;

    agregarRecurso(&listaRecursos, "Alimentos", 10);
    agregarRecurso(&listaRecursos, "Refugio", 5);
    agregarRecurso(&listaRecursos, "Asesoria Legal", 15);

    menuCiclado(&contadorTurnos, &listaPersonas, &listaRecursos, &historial, &seguimiento);

    liberarLista(listaPersonas);
    liberarRecursos(listaRecursos);
    liberarHistorial(historial);
    liberarSeguimiento(seguimiento);

    return 0;
}
