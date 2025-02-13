#include <stdio.h>
#include <string.h>
#include <stdlib.h>
//Partes a integrar a main

//Variables globales a utilizar -suposcion
//FALTA- Integrar contadores a recursos 
int diaActual = 1;
int semanaActual = 1;
int totalClientes = 0;
int clientesNuevosSem = 0;
int casosResueltos = 0;
int casosPendientes = 0;
int recursosUtilizados = 0;
int recursosDisponibles = 0;

//Estructuras y funciones a integrar 
//Integrar a menu puntos 3. actualizar 4.Mostrar reportes 5.Salir
/* case 3:
                actualizarDiaYSemana();
                break;
            case 4:
                mostrarReportesSemanales();
                break;
            case 5:
                printf("Saliendo del programa.\n");
                break;
            default:
                printf("\nX OPCIÓN NO VÁLIDA X \nIntente de nuevo\n");
                break;
        }
    } while (opcion != 5);*/
    
typedef struct ReporteSemanalNode {
    int semana;
    int clientesAtendidos;
    int casosResueltos;
    int casosPendientes;
    int recursosUtilizados;
    int recursosDisponibles;
    struct ReporteSemanalNode* siguiente;
} ReporteSemanalNode;

ReporteSemanalNode* listaReportes = NULL;

void agregarReporteSemanal() {
    ReporteSemanalNode* nuevo = (ReporteSemanalNode*)malloc(sizeof(ReporteSemanalNode));
    if(nuevo == NULL) {
        printf("Error al asignar memoria para el reporte semanal.\n");
        return;
    }
    nuevo->semana = semanaActual;
    nuevo->clientesAtendidos = clientesNuevosSem;
    nuevo->casosResueltos = casosResueltos;
    nuevo->casosPendientes = casosPendientes;
    nuevo->recursosUtilizados = recursosUtilizados;
    nuevo->recursosDisponibles = recursosDisponibles;
    nuevo->siguiente = NULL;
    
    // Agregar al final de la lista
    if(listaReportes == NULL) {
         listaReportes = nuevo;
    } else {
         ReporteSemanalNode* temp = listaReportes;
         while(temp->siguiente != NULL)
             temp = temp->siguiente;
         temp->siguiente = nuevo;
    }
}

// Función para mostrar todos los reportes semanales guardados
void mostrarReportesSemanales() {
    ReporteSemanalNode* temp = listaReportes;
    if(temp == NULL) {
         printf("No hay reportes semanales guardados.\n");
    } else {
         while(temp != NULL) {
             printf("Semana %d:\n", temp->semana);
             printf("Clientes atendidos: %d\n", temp->clientesAtendidos);
             printf("Casos resueltos: %d\n", temp->casosResueltos);
             printf("Casos pendientes: %d\n", temp->casosPendientes);
             printf("Recursos utilizados: %d\n", temp->recursosUtilizados);
             printf("Recursos disponibles: %d\n", temp->recursosDisponibles);
             printf("--------------------------\n");
             temp = temp->siguiente;
         }
    }
    system("pause");
    system("cls");
}

void liberarListaReportes() {
    ReporteSemanalNode* temp;
    while(listaReportes != NULL) {
         temp = listaReportes;
         listaReportes = listaReportes->siguiente;
         free(temp);
    }
}


// Función para actualizar día y semana
void actualizarDiaYSemana() {
    if (diaActual < 5) {
         diaActual++;
         printf("Semana %d | Dia %d\n", semanaActual, diaActual);
    } else {
         // Finaliza la semana: guardamos el reporte en la lista enlazada
         agregarReporteSemanal();
         printf("Fin de la Semana %d. Reporte guardado.\n", semanaActual);
         // Reiniciar contadores y actualizar a la nueva semana
         diaActual = 1;
         semanaActual++;
         clientesNuevosSem = 0;
         casosResueltos = 0;
         casosPendientes = 0;
         recursosUtilizados = 0;
         recursosDisponibles = 0;
         printf("Inicia la Semana %d | Dia %d\n", semanaActual, diaActual);
    }
    system("pause");
    system("cls");
}