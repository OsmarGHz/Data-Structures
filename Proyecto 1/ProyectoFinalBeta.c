#include <stdio.h>
#include <string.h>
#include <stdlib.h>

typedef struct Cliente{
    char turno[10];
    char Nombre[50];
    char Nacionalidad[50];
    char Motivo[100];
    int Urgencia;
}Cliente;

typedef struct Nodo{
    Cliente cliente;
    struct Nodo* siguiente;
}Nodo;

Nodo* crearNodo(Cliente cliente) {
    Nodo* nuevoNodo = (Nodo*)malloc(sizeof(Nodo));
    if (nuevoNodo == NULL) {
        printf("Error, no se puede asignar memoria");
        return NULL;
    }
    strcpy(nuevoNodo->cliente.turno, cliente.turno);    
    strcpy(nuevoNodo->cliente.Nombre, cliente.Nombre);   
    strcpy(nuevoNodo->cliente.Nacionalidad, cliente.Nacionalidad);  
    strcpy(nuevoNodo->cliente.Motivo, cliente.Motivo);    
    nuevoNodo->cliente.Urgencia = cliente.Urgencia;       
    nuevoNodo->siguiente = NULL;
    return nuevoNodo;
}


void insertarPorUrgencia(Nodo** lista, Nodo* nuevoNodo) {
    Nodo* actual = *lista;
    Nodo* anterior = NULL;

    while (actual != NULL && actual->cliente.Urgencia <= nuevoNodo->cliente.Urgencia) {
        anterior = actual;
        actual = actual->siguiente;
    }

    if (anterior == NULL) {
        nuevoNodo->siguiente = *lista;
        *lista = nuevoNodo;
    } else {
        nuevoNodo->siguiente = actual;
        anterior->siguiente = nuevoNodo;
    }
}

void generarTurno(char turno[], int* contadorTurnos) {
    sprintf(turno, "T%04d", (*contadorTurnos)++);
}

void datosCliente(Cliente cliente){

    printf("-------------------\n");
    printf("Datos del nuevo cliente:\n");
    printf("Turno: %s\n", cliente.turno);
    printf("Nombre: %s\n", cliente.Nombre);
    printf("Nacionalidad: %s\n", cliente.Nacionalidad);
    printf("Motivo: %s\n", cliente.Motivo);
    printf("Urgencia: %d\n", cliente.Urgencia);
    printf("-------------------\n");
    
}

void operacionNuevoCliente(int *contadorTurnos, Nodo** listaClientes){
    Cliente nuevoCliente;
    generarTurno(nuevoCliente.turno, contadorTurnos);

    printf("\nIngrese su nombre:");
    scanf(" %[^\n]", nuevoCliente.Nombre);

    printf("\nIngrese su nacionalidad:");
    scanf(" %[^\n]", nuevoCliente.Nacionalidad);

    printf("\nIngrese su motivo de desplazamiento:");
    scanf(" %[^\n]", nuevoCliente.Motivo);

    do{
  
        printf("\nIngrese el grado de urgencia:\n1. Urgente.\n2. Regular.");
        scanf("%d", &nuevoCliente.Urgencia);
        
        if (nuevoCliente.Urgencia < 1 || nuevoCliente.Urgencia > 2){
            printf("\n- - - - - - - - - - - - - - - - - - - - - - - -");
            printf("\nNumero de urgencia inexistente. Intente de nuevo");
            printf("\n- - - - - - - - - - - - - - - - - - - - - - - -\n");

        }
    }while (nuevoCliente.Urgencia < 1 || nuevoCliente.Urgencia > 2);

    Nodo* nuevoNodo = crearNodo(nuevoCliente);
    if(nuevoNodo == NULL){
        printf("Error, no se puede crear el nodo");
        return;
    }

    insertarPorUrgencia(listaClientes, crearNodo(nuevoCliente));
    datosCliente(nuevoCliente);
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
    printf("Clientes en la cola de prioridades:\n");
    printf("- - - - - - - - - - - - - - - - - - -\n");
    Nodo* temp = cola;
    if (cola == NULL)
    {
        printf("\t* No hay clientes en la fila *");
    }
    
    while (temp != NULL) {
        printf("Identificador: %s - Urgencia: %d - Nombre: %s - Nacionalidad: %s - Motivo de desplazamiento: %s\n",
               temp->cliente.turno, temp->cliente.Urgencia, temp->cliente.Nombre, temp->cliente.Nacionalidad, temp->cliente.Motivo);
        temp = temp->siguiente;
    }
}

void menuCiclado(int *contadorTurnos, Nodo **listaClientes){
    int opcion;

    do {
        printf("\n........................\n");
        printf("\n\tMENU\nOpciones disponibles:\n");
        printf("1. Nuevo cliente\n2. Visualizar Lista\n3. Salir\n");
        printf("\n........................\n");
        printf("\nIngrese el numero de opcion deseado: ");
        scanf("%d", &opcion);

        while (getchar() != '\n');

        switch (opcion) {
            case 1:
                //Crear Nuevo Cliente
                operacionNuevoCliente(contadorTurnos, listaClientes);
                break;
            case 2:
                // Ver lista 
                    mostrarFila(*listaClientes);                
                break;
            case 3:
                break;
            default:
                printf("Opcion no valida");
                break;
        }
    } while (opcion != 3);
}

int main() {
    Nodo* listaClientes = NULL;
    int contadorTurnos = 0;

    menuCiclado(&contadorTurnos, &listaClientes);

    liberarLista(listaClientes); 
    return 0;
}