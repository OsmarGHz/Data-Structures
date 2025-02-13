#include <stdio.h>
#include <stdlib.h>

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

/**
 * Calcula los coeficientes para el polinomio de direccionamiento.
 * Cada coeficiente corresponde al producto de los tamaños de las dimensiones posteriores.
 */
void calcularCoeficientes(int n, int tDimension[], int limInf[], int coef[]) {
    int factor = 1;
    for (int i = n - 1; i >= 0; i--) {
        coef[i] = factor;
        factor *= tDimension[i];
    }
}

/**
 * Calcula la dirección de un elemento en un arreglo multidimensional usando esta formula:
 *   dirección = baseDir + tElemento * Sumatoria[(k[i] - limInf[i]) * coef[i]]
 */
int calcularDireccion(int n, int coef[], int limInf[], int k[], int baseDir, int tElemento) {
    int direccion = baseDir;
    for (int i = 0; i < n; i++) {
        direccion += (k[i] - limInf[i]) * coef[i] * tElemento;
    }
    return direccion;
}

/**
 * Muestra el polinomio de direccionamiento.
 */
void mostrarPolinomio(int n, int coef[], int limInf[], int tElemento, int baseDir) {
    printf("\nPolinomio de direccionamiento:\n");
    printf("Dirección = %d", baseDir);
    for (int i = 0; i < n; i++) {
        printf(" + %d*(k[%d] - %d)", coef[i] * tElemento, i, limInf[i]);
    }
    printf("\n");
}

/**
 * Función para ingresar las coordenadas del elemento a buscar.
 * Permite elegir si las coordenadas se ingresan en formato "humano" (inician en 1)
 * o en formato "máquina" (inician en 0).
 */
void ingresarCoordenadas(int n, int tDimension[], int k[], int desic) {
    int temp;
    printf("\nIngrese las coordenadas del elemento a buscar, separadas por un espacio");
    printf(" (cada coordenada inicia en %d): \n", desic ? 1 : 0);
    
    for (int i = 0; i < n; ) {
        while (escaneoEntero(&temp) == 0);
        if (desic) 
            temp--;  // Ajusta a índice base 0 si es necesario
        if (temp >= 0 && temp < tDimension[i]) {
            k[i] = temp;
            i++;
        } else {
            printf("Valor invalido para la dimensión %d. Intente nuevamente: ", i + 1);
        }
    }
}

int main() {
    int n, baseDir, tElemento, desic;
    
    printf("Ingrese el número de dimensiones del arreglo: ");
    while (escaneoEntero(&n) == 0);

    // Asignación dinámica de memoria
    int tDimension[n];
    int limInf[n];
    int k[n];
    int coef[n];

    if ( !tDimension || !limInf || !k || !coef ) {
        printf("Error al asignar memoria.\n");
        return 1;
    }

    // Ingreso de tamaños de cada dimensión y asignación de límites inferiores (por defecto 0)
    for (int i = 0; i < n; i++) {
        printf("\nIngrese el tamaño de la dimensión %d: ", i + 1);
        while (escaneoEntero(&tDimension[i]) == 0);
        limInf[i] = 0;  // Se puede tambien ingresar el límite inferior
    }
    
    printf("\nIngrese la dirección base del arreglo: ");
    while (escaneoEntero(&baseDir) == 0);

    printf("¿Desea ingresar coordenadas en lenguaje máquina (0) o en lenguaje humano (1)? ");
    while (escaneoEntero(&desic) == 0);

    // Aqui ingresamos las coordenadas
    ingresarCoordenadas(n, tDimension, k, desic);

    printf("\nIngrese el tamaño en bytes de cada elemento: ");
    while (escaneoEntero(&tElemento) == 0);

    // Calculamos coeficientes para el polinomio de direccionamiento
    calcularCoeficientes(n, tDimension, limInf, coef);

    // Mostramos el polinomio de direccionamiento
    mostrarPolinomio(n, coef, limInf, tElemento, baseDir);

    // Cálculo de la dirección
    int direccion = calcularDireccion(n, coef, limInf, k, baseDir, tElemento);
    printf("\nLa dirección del elemento es: %d\n", direccion);

    return 0;
}
