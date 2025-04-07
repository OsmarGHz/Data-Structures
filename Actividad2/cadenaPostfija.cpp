#include <stdio.h>
#include <stdbool.h>
#include <string.h>
#define TAM 100 /* Tamaño máximo de la pila*/

typedef struct
{
    int dato[TAM];
    int tope; // índice del elemento en la parte superior de la pila
} Pila;

/* Inicializa la pila*/
void inicializarPila(Pila* pila)
{
    pila->tope = -1; /*Pila vacía*/
}

/*Verifica si la pila está llena*/
bool estaLlena(Pila* pila)
{
    return pila->tope == TAM - 1;
}

/* Verifica si la pila está vacía*/
bool estaVacia(Pila* pila)
{
    return pila->tope == -1;
}

/* Añade un elemento a la pila*/
void push(Pila* pila, int valor)
{
    if (estaLlena(pila))
    {
        printf("Error: Pila llena\n");
        return;
    }
    pila->tope++;
    pila->dato[pila->tope] = valor;
    //printf("Elemento %d agregado a la pila.\n", valor);
}

/* Elimina un elemento de la pila*/
int pop(Pila* pila)
{
    if (estaVacia(pila))
    {
        printf("Error: Pila vacía\n");
        return -1; /* Valor que indica un error*/
    }
    int valor = pila->dato[pila->tope];
    pila->tope--;
    //printf("Elemento %d eliminado de la pila.\n", valor);
    return valor;
}

/*Imprime los elementos de la pila*/
void mostrarPila(Pila* pila)
{
    int i;
    if (estaVacia(pila))
    {
        printf("La pila está vacía.\n");

        return;
    }
    printf("Los elementos en la pila son: ");
    for (i = pila->tope; i >= 0; i--)
    {
        printf("%d ", pila->dato[i]);
    }
    printf("\n");
}

//Funciones para el algoritmo
//Determinar la precedencia de la entrada
int nivelDePrecedencia(char operador){
	int nivel=0;
	switch(operador){
		case '(': nivel =0;
		          break;
		case '+': nivel =1;
		          break;
		case '-': nivel =1;
		          break;
		case '*': nivel =2;
		          break;
		case '/': nivel =2;
		          break;
		case '$': nivel =3;//NOS SERVIRÁ PARA LA POTENCIA
		          break;
	}
	return nivel;
}
//Entre dos operadores, comparar cual tiene la mayor prioridad y cual la menor
//
int tieneMayorOIgualPrioridad(char operador1, char operador2){
	int precedenciaPrimerOperador=nivelDePrecedencia(operador1);
	int precedenciaSegundoOperador=nivelDePrecedencia(operador2);
	if(precedenciaPrimerOperador>=precedenciaSegundoOperador){
		return 1;
	}else{
		return 0;
	}
}
// verifica si la entrada es un operador y lo señala retornando un 1 si es verdad
int esOperador(char elemento){
	if(elemento == '+' ||
	   elemento == '-' ||
	   elemento == '*' ||
	   elemento == '/' ||
	   elemento == '$'
	   ){
		return 1;
	}else{
		return 0;
	}
}
//usada para trabajar con numeros
int esOperando(char elemento){
    if ((elemento >= '0' && elemento <= '9') ||
        (elemento >= 'A' && elemento <= 'Z') ||
        (elemento >= 'a' && elemento <= 'z'))
    {
		return 1;
	} else {
		return 0;
	}
}
//algoritmo de conversion, recibe la cadena infija como parametro
void infijaToPostfija(char *infija, char *postfija) {
	char elemento,operador;
	int j=0;
	int i=0;
	//inicializa la pila
    Pila pila;
    inicializarPila(&pila);

	int longitud= strlen(infija);
	while (i<longitud){
		elemento= infija[i];
		i++;
		//aqui se verifica que la entrada sea un operando (numeros o letras)
		//de ser asi, se ingresa a la expresion postfija
		if(esOperando(elemento)){
			postfija[j]=elemento;
			j++;

		} //en caso contrario se verifica si es un operador (+,-,*,/)
		 else if (esOperador(elemento)){
            //si la pila no esta vacia
			if(!estaVacia(&pila)){
				int seDebeContinuar;
				do{
					//se saca el primer elemento de la pila(que no esta vacia)
					//se asigna a la variable operador
					operador=pop(&pila);
					/*Se compara que operador tiene mayor precedencia entre
					el elemento de la pila y la entrada que se tiene en elemento */
				    if(tieneMayorOIgualPrioridad(operador, elemento)){
						postfija[j]=operador;
						j++;
						seDebeContinuar=1;
					}else{
						seDebeContinuar=0;
						push(&pila,operador);
					}
				} while(!estaVacia(&pila) && seDebeContinuar);
				//operacion se realiza siempre que la pila no este vacia y
				//la bandera "se debe continuar" este marcada.
			}
			/* Cuando la pila ya no esta vacia entonces
			o si desde un principio estaba vacia entonces
			se añade el operador en la varible elemento*/
			push(&pila,elemento);
		}else if (elemento == '('){
            //ingresa directamente el parentesis izquierdo
			push(&pila,elemento);
		}else if (elemento == ')'){
            /*Si el elemento es un parentesis derecho entonces
            se sacará el elemento en el tope de la pila y se guardará
            en operador.
            Se entrará al ciclo while y mientras la pila no esté vacía y
            el operador obtenido de la pila sea diferente de un paréntesis
            derecho:
            Se agregará el operador a la cadena postfija y se asignará a
            operador el siguiente elemento en la pila.
            Así hasta romper el ciclo.*/
			operador=pop(&pila);
			while(!estaVacia(&pila) && operador!='(') {
				postfija[j]=operador;
				j++;
				operador=pop(&pila);
			}
		}
	}
	/*Se vacían todos los elementos de la pila en la cadena postfija
	una vez es analizada por completo la cadena infija */
	while(!estaVacia(&pila)) {
		operador=pop(&pila);
		postfija[j]=operador;
		j++;
	}
	//Se agrega el carácter de finalización.
	postfija[j]='\0';
}

/*Programa principal*/
int main() {
    char infija[100];
    char postfija[100];

    printf("Introduce la expresion: \n");
    scanf("%[^\n]", infija);
    infijaToPostfija(infija, postfija);
    printf("Expresion postfija: %s\n", postfija);

    return 0;
}
