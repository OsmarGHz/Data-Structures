/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package visualizacionrecorridosgrafo;

/**
 *
 * @author vanes
 */
public class VerticeBinario {
    int dato;
    VerticeBinario izq;  // Hijo izquierdo
    VerticeBinario der; // Hijo derecho

    public VerticeBinario(int dato) {
        this.dato = dato;
        this.izq = null;
        this.der = null;
    }

    public int getDato() {
        return dato;
    }
}
