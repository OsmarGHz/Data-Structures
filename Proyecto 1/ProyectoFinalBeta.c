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
    int resuelto;
} Seguimiento;

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
//lista
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

//aquí se usa para la seleccion
Nodo* seleccionarPersona(Nodo* lista) {
    if (lista == NULL) {
        printf("No hay personas en la lista.\n");
        return NULL;
    }
    
    //imprime a las personas que están registradas
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
    
    //empieza a buscar con el nunero y poder dar con ESE nodo.
    temp = lista;
    for (int j = 1; j < opcion && temp != NULL; j++) {
        temp = temp->siguiente;
    }

    if (temp == NULL) {
        printf("Opcion inválida.\n");
        return NULL;
    }

    return temp;
}

//aquí lo que hace es comparar si es 1 o 2, si es 1 que es más urgente, oopondrá al inicio de los anteriores nodos
//si es que ya se tenía. si no es urgente es decir 2, se colocan detrás de los urgentes.
//urgentes y no urgentes llevan una lista, pero urgente siempre será prioridad 
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

//Solo genera turnos de 4 números, desde 00000 hasta 9999
void generarTurno(char turno[], int* contadorTurnos){
    sprintf(turno, "%04d", (*contadorTurnos)++);
}

//impresión de los datos después de ingresarlos.
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

//seguimiento después de asignarles un recurso.
void agregarSeguimiento(Seguimiento** cabeza, Persona persona) {
    // Verificar si la persona ya está en seguimiento
    if (*cabeza != NULL) {
        Seguimiento* temp = *cabeza;
        do {
            if (strcmp(temp->persona.Nombre, persona.Nombre) == 0) {
                printf("La persona %s ya está en seguimiento.\n", persona.Nombre);
                return;  // Evitar duplicados
            }
            temp = temp->siguiente;
        } while (temp != *cabeza);
    }

    // Crear el nuevo nodo para seguimiento
    Seguimiento* nuevo = (Seguimiento*)malloc(sizeof(Seguimiento));
    if (nuevo == NULL) {
        printf("Error: No se pudo asignar memoria para seguimiento.\n");
        return;
    }
    nuevo->persona = persona;
    nuevo->resuelto = 0;  // Caso pendiente

    // Inserción ordenada en la lista circular según la urgencia
    if (*cabeza == NULL) {
        // Si la lista está vacía, el nuevo nodo se vuelve cabeza y se apunta a sí mismo
        *cabeza = nuevo;
        nuevo->siguiente = nuevo;
    } else if (nuevo->persona.Urgencia < (*cabeza)->persona.Urgencia) {
        // Si el nuevo nodo es más urgente que la cabeza, se inserta antes de la cabeza.
        Seguimiento* current = *cabeza;
        // Buscar el último nodo para actualizar su puntero
        while (current->siguiente != *cabeza) {
            current = current->siguiente;
        }
        current->siguiente = nuevo;
        nuevo->siguiente = *cabeza;
        *cabeza = nuevo;  // Actualizar la cabeza
    } else {
        // Insertar en el lugar correcto recorriendo la lista
        Seguimiento* current = *cabeza;
        while (current->siguiente != *cabeza && current->siguiente->persona.Urgencia <= nuevo->persona.Urgencia) {
            current = current->siguiente;
        }
        nuevo->siguiente = current->siguiente;
        current->siguiente = nuevo;
    }
    printf("Se agregó a seguimiento: %s\n", persona.Nombre);
}


//esta función añade a las personas desde teclado, preguntando su nombre, nacionalidad 
//y la razón del desplazamiento  está acepta cadenas (textos largos) y crea un nuevo nodo con
//esta información, lo cual empieza la primera lista.
void operacionNuevaPersona(int* contadorTurnos, Nodo** listaPersonas, Seguimiento** seguimiento){
    Persona nuevaPersona;
    generarTurno(nuevaPersona.turno, contadorTurnos);

    printf("\nIngrese su nombre:");
    scanf(" %49[^\n]", nuevaPersona.Nombre);

    printf("\nIngrese su nacionalidad:");
    scanf(" %49[^\n]", nuevaPersona.Nacionalidad);

    printf("\nIngrese su motivo de desplazamiento:");
    scanf(" %49[^\n]", nuevaPersona.Motivo);

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

    insertarUrgencia(listaPersonas, nuevoNodo);//inserta el nodo dependiendo la urgencia
    agregarSeguimiento(seguimiento, nuevaPersona); // Agregar automáticamente al seguimiento
    datosCliente(nuevaPersona); //imprime los datos
}

//esto sirve para hacer la lista simple de los recursos que se pre-definen en el main
void agregarRecurso(Recurso** lista, char nombre[], int cantidad){
    Recurso* nuevoRecurso = (Recurso*)malloc(sizeof(Recurso));
    strcpy(nuevoRecurso->nombre, nombre);
    nuevoRecurso->cantidad = cantidad;
    nuevoRecurso ->siguiente = *lista;
    *lista = nuevoRecurso;
}

//muestra la lista de los recuesos está es actualizable ya que si se toma algún 4egurso
//wstase reduce y en dado caso se acabe lo hará saber.
void mostrarRecursos(Recurso* lista){
    printf("\n-Recursos disponibles-\n");
    Recurso* temp = lista;
    while (temp != NULL){
        printf("- %s: %d disponibles\n", temp->nombre, temp->cantidad);
        temp = temp->siguiente;
    }
}

//esto se ocupa para asignar algún servicio o producto a la gente
//selecciona a la persona (nodo) y le asigna un recurso.
void asignarRecurso(Nodo** listaPersonas, Recurso** listaRecursos, Historial** historial, Seguimiento** seguimiento) {
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

            agregarSeguimiento(seguimiento, personaSeleccionada->persona);

            return;
        }
        temp = temp->siguiente;
    }

    printf("Recurso no encontrado o sin disponibilidad.\n");

    agregarSeguimiento(seguimiento, personaSeleccionada->persona);//si en dado caso no se asignado el seguimiento z está está opción de añadir
}

//continuar con el serviiciohasta darle la solución a este.
//ya que se cuenta con asesoriamenton fiscal 
void darSeguimiento(Seguimiento** cabeza) {
    if (*cabeza == NULL) {
        printf("No hay casos en seguimiento.\n");
        return;
    }

    Seguimiento* temp = *cabeza;
    Seguimiento* anterior = NULL;
    int opcion;
    int eliminar;
 
    do {
        printf("\nSeguimiento de: %s (Turno: %s)\n", temp->persona.Nombre, temp->persona.turno);
        printf("Estado: %s\n", temp->resuelto ? "Resuelto" : "Pendiente");
        
        // Validar opción para marcar como resuelto
        do {
            printf("\nMarcar como resuelto? (1: Si, 0: No): ");
            scanf("%d", &opcion);
            if (opcion != 1 && opcion != 0) {
                printf("\nNumero erroneo\n");
            }
        } while(opcion != 1 && opcion != 0);

        if (opcion == 1) {
            temp->resuelto = 1;
            printf("Caso de %s marcado como resuelto.\n", temp->persona.Nombre);
        }
        
        // Validar opción para eliminar
        do {
            printf("Eliminar de seguimiento? (1: Si, 0: No): ");
            scanf("%d", &eliminar);
            if (eliminar != 1 && eliminar != 0) {
                printf("\nNumero erroneo\n");
            }
        } while(eliminar != 1 && eliminar != 0);
   
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

//apartir de aquí solo es liberación de memoria antes de que el programa cierre
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
    printf("Persona en la cola de prioridades:\n");
    printf("- - - - - - - - - - - - - - - - - - -\n");
    Nodo* temp = cola;
    if (cola == NULL)
    {
        printf("\t* No hay personas en la fila *");
    }
    
    while (temp != NULL) {
        printf("--------------------------------------------\n");
        printf("Identificador: %s - Urgencia: %d - Nombre: %s - Nacionalidad: %s - Motivo de desplazamiento: %s\n",
               temp->persona.turno, temp->persona.Urgencia, temp->persona.Nombre, temp->persona.Nacionalidad, temp->persona.Motivo);
        temp = temp->siguiente;
    }
}

//procedimientos hechos o asignaciones de reguesos estos se quedan guardados solo
//cómo acciones hechas en el sistema
void mostrarHistorial(Historial* historial){
    printf("Historial de asignaciones:\n");
    Historial* temp = historial;
    while (temp != NULL){
        printf("Cliente: %s - Recurso; %s \n", temp->persona.Nombre, temp->recurso);
        temp = temp->siguiente;
    }
}

//auihace una recopilación de personas atendidas, recursos disponibles y las asignaciones de estos, historial y el seguimiento de estos
void generarReporte(Nodo* listaClientes, Recurso* listaRecurso, Historial* historial, Seguimiento* seguimiento) {
    printf("\n--- Reporte semanal ---\n");

    // Atendidos
    printf("- Personas Atendidas -\n");
    Nodo* temp = listaClientes;
    while (temp != NULL) {
        printf("- %s (Turno: %s)\n", temp->persona.Nombre, temp->persona.turno);
        temp = temp->siguiente;
    }

    // Recursos
    printf("\n- Recursos Disponibles -\n");
    Recurso* tempRecursos = listaRecurso;
    while (tempRecursos != NULL) {
        printf("- %s: %d disponibles\n", tempRecursos->nombre, tempRecursos->cantidad);
        tempRecursos = tempRecursos->siguiente;
    }

    // Historial
    printf("\n- Historial de asignaciones -\n");
    Historial* tempHistorial = historial;
    while (tempHistorial != NULL) {
        printf("- %s: %s\n", tempHistorial->persona.Nombre, tempHistorial->recurso);
        tempHistorial = tempHistorial->siguiente;
    }

    // Seguimiento
    printf("\n- Casos de Seguimiento -\n");
    if (seguimiento == NULL) {
        printf("No hay casos en seguimiento\n");
    } else {
        Seguimiento* tempSeguimiento = seguimiento;
        do {
            printf("- %s (Turno: %s) - %s\n", tempSeguimiento->persona.Nombre, tempSeguimiento->persona.turno,
                   tempSeguimiento->resuelto ? "Resuelto" : "Pendiente");
            tempSeguimiento = tempSeguimiento->siguiente;
        } while (tempSeguimiento != seguimiento);
    }
}

//liberación de memoria
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

//un menú que cicla para evitar saturar el main
void menuCiclado(int* contadorTurnos, Nodo** listaPersonas, Recurso** listaRecursos, Historial** historial, Seguimiento** seguimiento) {
    int opcion;

    do {
        printf("\n........................\n");
        printf("\n\tMENU\nOpciones disponibles:\n");
        printf("1. Nuevo cliente\n2. Visualizar Lista\n3. Asignar Recurso\n4. Ver Historial\n5. Generar Reporte\n6. Dar Seguimiento\n7. Salir\n");
        printf("\n........................\n");
        printf("\nIngrese el numero de opcion deseado: ");
        while(escaneoEntero(&opcion) == 0);
        switch (opcion) {
            case 1:
                operacionNuevaPersona(contadorTurnos, listaPersonas, seguimiento);
                break;
            case 2:
                mostrarFila(*listaPersonas);
                break;
            case 3:
                asignarRecurso(listaPersonas, listaRecursos, historial,seguimiento);
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

int main(){
    Nodo* listaPersonas = NULL; //todo se empieza en null
    Recurso* listaRecursos = NULL;
    Historial* historial = NULL;
    Seguimiento* seguimiento = NULL;
    int contadorTurnos = 0;

    //recursos pre definidos en sistema
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