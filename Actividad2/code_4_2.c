/*
Implementa un programa que use pilas para verificar si los paréntesis, corchetes y llaves
están balanceados en una expresión dada.
Entrada: Cadena a modo de ejemplo "(a + b) * [c / d]".
Salida: Si o No
*/

#include <stdio.h>
#include <stdlib.h>
#include <string.h>

typedef struct Nodo {
    char dato;
    struct Nodo* siguiente;
} Nodo;

Nodo* pilaExpresiones = NULL;

void push(Nodo** cima, char valor) {
    Nodo* nuevo = (Nodo*)malloc(sizeof(Nodo));
    if (!nuevo) {
        printf("Error de memoria\n");
        exit(1);
    }
    nuevo->dato = valor;
    nuevo->siguiente = *cima;
    *cima = nuevo;
}

char pop(Nodo** cima) {
    if (*cima == NULL) {
        return '\0';
    }
    Nodo* temp = *cima;
    char valor = temp->dato;
    *cima = (*cima)->siguiente;
    free(temp);
    return valor;
}

char cima(Nodo* cima) {
    return (cima) ? cima->dato : '\0';
}

int estaVacia(Nodo* cima) {
    return cima == NULL;
}

void imprimirPila(Nodo* cima) {
    printf("Estado actual de la pila: ");
    while (cima != NULL) {
        printf("%c ", cima->dato);
        cima = cima->siguiente;
    }
    printf("\n");
}

int estaBalanceado(const char* expresion) {
    Nodo* pila = NULL;
    for (int i = 0; expresion[i] != '\0'; i++) {
        char c = expresion[i];
        if (c == '(' || c == '[' || c == '{') {
            push(&pila, c);
        } else if (c == ')' || c == ']' || c == '}') {
            char tope = cima(pila);
            if ((c == ')' && tope == '(') || (c == ']' && tope == '[') || (c == '}' && tope == '{')) {
                pop(&pila);
            } else {
                return 0; // No esta balanceado
            }
        }
    }
    return estaVacia(pila);
}

void guardarExpresion(const char* expresion) {
    for (int i = 0; expresion[i] != '\0'; i++) {
        push(&pilaExpresiones, expresion[i]);
    }
    push(&pilaExpresiones, '\n'); 
}

void imprimirExpresiones() {
    printf("Expresiones balanceadas almacenadas:\n");
    Nodo* temp = pilaExpresiones;
    while (temp != NULL) {
        if (temp->dato == '\n') {
            printf("\n");
        } else {
            printf("%c", temp->dato);
        }
        temp = temp->siguiente;
    }
    printf("\n");
}

int main() {
    char expresion[100];
    while (1) {
        printf("Introduce la expresion (o escribe 'salir' para finalizar): ");
        fgets(expresion, sizeof(expresion), stdin);

        expresion[strcspn(expresion, "\n")] = '\0';

        if (strcmp(expresion, "salir") == 0) {
            printf("Programa finalizado.\n");
            imprimirExpresiones();
            break;
        }

        if (estaBalanceado(expresion)) {
            printf("Si\n");
            guardarExpresion(expresion);
        } else {
            printf("No\n");
        }
    }

    return 0;
}
