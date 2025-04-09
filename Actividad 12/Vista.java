import javax.swing.*;
import javax.swing.Timer;
import java.awt.*;
import java.awt.event.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;
import java.util.Queue;

//Panel donde dibujaremos el arbol
class PanelGrafo extends JPanel{
    private final int RADIO_VERTICE = 25;

}

class VerticeBinario{
    int dato;
    VerticeBinario left;
    VerticeBinario right;

    public VerticeBinario (int elemento){
        dato = elemento;
        left = null; right = null;
    }
}

class ArbolGeneral {
    private VerticeBinario root;
    
    public ArbolGeneral(int rootData) {
        // Se crea la raíz del árbol
        root = new VerticeBinario(rootData);
    }

    public VerticeBinario getRoot() {
        return root;
    }

    //Funcion para resetear el arbol
    public void reset() {
        root = null;
    }

    // Recorre el árbol en preorden para verificar si ya existe el dato.
    private boolean exists(VerticeBinario nodo, int dato) {
        if (nodo == null)
            return false;
        if (nodo.dato==dato)
            return true;
        // Primero recorre los hijos y luego los hermanos
        return exists(nodo.left, dato) || exists(nodo.right, dato);
    }

    // Inserta un nuevo nodo como hijo de 'padre' si el dato no existe en ningún nodo.
    public boolean addChild(VerticeBinario padre, int dato) {
        if (exists(root, dato)) {
            return false; // El dato ya existe.
        }
        VerticeBinario nuevo = new VerticeBinario(dato);
        if (padre.left == null) {
            padre.left = nuevo;
        } else {
            // Recorre la lista de hermanos hasta el final y añade el nuevo nodo.
            VerticeBinario current = padre.left;
            while (current.right != null) {
                current = current.right;
            }
            current.right = nuevo;
        }
        return true;
    }

    // Modifica el dato de un nodo (si se cumple que el nuevo dato no existe en el árbol).
    public boolean modifyNode(VerticeBinario nodo, int nuevoDato) {
        if (exists(root, nuevoDato)) {
            return false; // No se puede modificar porque el nuevo dato ya existe.
        }
        nodo.dato = nuevoDato;
        return true;
    }

    // Elimina el nodo que tenga 'dato' dado. Requiere pasar el nodo padre para poder reconectar la lista de hijos.
    // Esta implementación elimina el nodo y todo su subárbol.
    public boolean deleteChild(VerticeBinario padre, int dato) {
        if (padre == null || padre.left == null) {
            return false;
        }
        // Si el primer hijo es el que se desea eliminar:
        if (padre.left.dato==dato) {
            padre.left = padre.left.right; // Se elimina el nodo y se conecta su siguiente hermano (si lo tiene)
            return true;
        } else {
            // Buscar entre los hermanos
            VerticeBinario current = padre.left;
            while (current.right != null) {
                if (current.right.dato==dato) {
                    current.right = current.right.right;
                    return true;
                }
                current = current.right;
            }
        }
        return false;
    }
}

public class Vista extends JFrame{
    public static VerticeBinario grafoBinario = null;
    public static PanelGrafo panelGrafo;

    public Vista(){
        super("Arbol Binario: Preorden, Inorden y Posorden");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(900, 600);
        setLocationRelativeTo(null);
        setResizable(true);
        setLayout(new BorderLayout());

        // Toolbar en la parte lateral izquierda
        JToolBar barraHerramientas = new JToolBar();
        barraHerramientas.setFloatable(false);
        barraHerramientas.setOrientation(SwingConstants.VERTICAL);
        add(barraHerramientas, BorderLayout.WEST);

        JButton btnCrearGrafo = new JButton("Crear Grafo");
        btnCrearGrafo.addActionListener(e -> reset());
        barraHerramientas.add(btnCrearGrafo);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            Vista vista = new Vista();
            vista.setVisible(true);
        });
    }
}