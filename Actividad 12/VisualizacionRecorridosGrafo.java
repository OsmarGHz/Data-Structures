/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package visualizacionrecorridosgrafo;

/**
 *
 * @author vanes
 */
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;
import java.util.ArrayList;
import java.util.List;

public class VisualizacionRecorridosGrafo extends JFrame {

    private VerticeBinario raiz;   // Raíz del árbol 
    private PanelGrafo panelGrafo; // Panel gráfico para visualizar el árbol
    private JTextArea txtResumen;  // resumen del recorrido RECORRIDO

    public VisualizacionRecorridosGrafo() {
        super("Recorridos inorden y postorden");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(900, 600);
        setLocationRelativeTo(null);

        raiz = null;
        panelGrafo = new PanelGrafo();

        JPanel panelBotones = new JPanel(new GridLayout(1, 4, 5, 5));
        JButton btnInsertar = new JButton("Insertar Vertice");
        btnInsertar.addActionListener(e -> insertarVe());
        panelBotones.add(btnInsertar);

        JButton btnCargarArchivo = new JButton("Cargar arbol desde archivo (txt)");
        btnCargarArchivo.addActionListener(e -> cargarDesdeArchivo());
        panelBotones.add(btnCargarArchivo);

        JButton btnInorden = new JButton("Recorrido Inorden");
        btnInorden.addActionListener(e -> recorridoInorden());
        panelBotones.add(btnInorden);

        JButton btnPostorden = new JButton("Recorrido Postorden");
        btnPostorden.addActionListener(e -> recorridoPostorden());
        panelBotones.add(btnPostorden);

        txtResumen = new JTextArea(5, 20);
        txtResumen.setEditable(false);
        JScrollPane scrollResumen = new JScrollPane(txtResumen);

        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(panelBotones, BorderLayout.NORTH);
        getContentPane().add(panelGrafo, BorderLayout.CENTER);
        getContentPane().add(scrollResumen, BorderLayout.SOUTH);
    }

    private void insertarVe() {
        try {
            String valorStr = JOptionPane.showInputDialog(this, "Ingrese el vertice (numero entero):");
            if (valorStr == null || valorStr.trim().isEmpty()) return;
            int valor = Integer.parseInt(valorStr.trim());

            if (raiz == null) {
                raiz = new VerticeBinario(valor);
                panelGrafo.setRaiz(raiz);
            } else {
                String padreStr = JOptionPane.showInputDialog(this, "Ingrese el vertice padre:");
                if (padreStr == null || padreStr.trim().isEmpty()) return;
                int valorPadre = Integer.parseInt(padreStr.trim());

                VerticeBinario padre = buscarvertice(raiz, valorPadre);
                if (padre == null) {
                    JOptionPane.showMessageDialog(this, "No existe un vertice padre" + valorPadre);
                    return;
                }

                String lado = JOptionPane.showInputDialog(this,
                        "¿Insertar a la izquierda (I) o a la derecha (D)?").toUpperCase();
                if (lado.equals("I")) {
                    if (padre.izq != null) {
                        JOptionPane.showMessageDialog(this, "El vertice padre "+ valorPadre+ "ya tiene un hijo en el lado izquierdo\nIntenta con el lado derecho o selecciona otro vertice");
                        return;
                    }
                    padre.izq = new VerticeBinario(valor);
                } else if (lado.equals("D")) {
                    if (padre.der != null) {
                        JOptionPane.showMessageDialog(this,  "El vertice padre "+ valorPadre+ "ya tiene un hijo en el lado derecho\nIntenta con el lado izquierdo o selecciona otro vertice");
                        return;
                    }
                    padre.der = new VerticeBinario(valor);
                } else {
                    JOptionPane.showMessageDialog(this, "Opción inválida. Ingrese I o D.");
                    return;
                }
                panelGrafo.setRaiz(raiz);
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Valor inválido. Intente de nuevo.");
        }
    }
     private void cargarDesdeArchivo() {
    JFileChooser fileChooser = new JFileChooser();
    int opcion = fileChooser.showOpenDialog(this);
    if (opcion == JFileChooser.APPROVE_OPTION) {
        File archivo = fileChooser.getSelectedFile();
        try (BufferedReader br = new BufferedReader(new FileReader(archivo))) {
            raiz = null;
            Map<Integer, VerticeBinario> mapaNodos = new HashMap<>();
            List<String[]> relacionesPendientes = new ArrayList<>();
            String linea;

            while ((linea = br.readLine()) != null) {
                linea = linea.trim();
                if (linea.isEmpty()) continue;

                String[] partes = linea.split(",");
                if (partes.length == 1) {
                    int valorRaiz = Integer.parseInt(partes[0].trim());
                    raiz = new VerticeBinario(valorRaiz);
                    mapaNodos.put(valorRaiz, raiz);
                } else if (partes.length == 3) {
                    relacionesPendientes.add(partes);  
                }
            }

            // construir el arbol
            for (String[] partes : relacionesPendientes) {
                int valorHijo = Integer.parseInt(partes[0].trim());
                int valorPadre = Integer.parseInt(partes[1].trim());
                String lado = partes[2].trim().toUpperCase();

                VerticeBinario padre = mapaNodos.get(valorPadre);
                if (padre == null) continue;  

                VerticeBinario hijo = new VerticeBinario(valorHijo);
                mapaNodos.put(valorHijo, hijo);

                if (lado.equals("I")) {
                    padre.izq = hijo;
                } else if (lado.equals("D")) {
                    padre.der = hijo;
                }
            }

            int numVertices = contarVertices(raiz);
            int numAristas = contarAristas(raiz);
            if (numVertices == numAristas + 1) {
                panelGrafo.setRaiz(raiz);
            } else {
                JOptionPane.showMessageDialog(this,
                        "El grafo insertado no es un árbol.\n" +
                        "Vértices: " + numVertices + " | Aristas: " + numAristas +
                        "\nVuelva a intentarlo.");
                raiz = null;
                panelGrafo.setRaiz(null);
            }

        } catch (IOException | NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Error al leer el archivo: " + ex.getMessage());
        }
    }
}
    
    private int contarVertices(VerticeBinario vertice) {
        if (vertice == null)
            return 0;
        return 1 + contarVertices(vertice.izq) + contarVertices(vertice.der);
    }
    
    private int contarAristas(VerticeBinario vertice) {
        if (vertice == null)
            return 0;
        int aristas = 0;
        if (vertice.izq != null)
            aristas++;
        if (vertice.der != null)
            aristas++;
        return aristas + contarAristas(vertice.izq) + contarAristas(vertice.der);
    }
    
private void iniciarAnimacionRecorrido(final List<VerticeBinario> recorridoList, final String resumen) {
        final List<VerticeBinario> recorridoAnimado = new ArrayList<>();
        final Timer timer = new Timer(1500, null);
        timer.addActionListener(new ActionListener() {
            int index = 0;
            @Override
            public void actionPerformed(ActionEvent e) {
                if (index < recorridoList.size()) {
                    recorridoAnimado.add(recorridoList.get(index));
                    panelGrafo.setRecorrido(new ArrayList<>(recorridoAnimado));
                    index++;
                } else {
                    timer.stop();
                    mostrarResumen(resumen);
                }
            }
        });
        timer.start();
    }
     private void mostrarResumen(String resumen) {
        JDialog dialog = new JDialog(this, "Resumen del Recorrido", true);
        dialog.setSize(300, 150);
        dialog.setLocationRelativeTo(this);
        JTextArea areaResumen = new JTextArea(resumen);
        areaResumen.setEditable(false);
        JButton btnCerrar = new JButton("Cerrar");
        btnCerrar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dialog.dispose();
                // limpia el  árbol para poder ver el otro recorrido
                panelGrafo.setRecorrido(new ArrayList<VerticeBinario>());
            }
        });
        JPanel panel = new JPanel(new BorderLayout());
        panel.add(new JScrollPane(areaResumen), BorderLayout.CENTER);
        panel.add(btnCerrar, BorderLayout.SOUTH);
        dialog.setContentPane(panel);
        dialog.setVisible(true);
    }
    private void recorridoInorden() {
        if (raiz == null) {
            txtResumen.setText("No hay árbol para recorrer.");
            return;
        }
        List<VerticeBinario> inordenList = new ArrayList<>();
        inorden(raiz, inordenList);
        StringBuilder sb = new StringBuilder("Recorrido Inorden: ");
        for (VerticeBinario v : inordenList) {
            sb.append(v.getDato()).append(" ");
        }
       iniciarAnimacionRecorrido(inordenList, sb.toString());
    }

    private void inorden(VerticeBinario vertice, List<VerticeBinario> list) {
        if (vertice == null) return;
        inorden(vertice.izq, list);
        list.add(vertice);
        inorden(vertice.der, list);
    }

    private void recorridoPostorden() {
        if (raiz == null) {
            txtResumen.setText("No hay árbol para recorrer.");
            return;
        }
        List<VerticeBinario> postordenList = new ArrayList<>();
        postorden(raiz, postordenList);
        StringBuilder sb = new StringBuilder("Recorrido Postorden: ");
        for (VerticeBinario v : postordenList) {
            sb.append(v.getDato()).append(" ");
        }
       iniciarAnimacionRecorrido(postordenList, sb.toString());
    }

    private void postorden(VerticeBinario vertice, List<VerticeBinario> list) {
        if (vertice == null) return;
        postorden(vertice.izq, list);
        postorden(vertice.der, list);
        list.add(vertice);
    }

    private VerticeBinario buscarvertice(VerticeBinario vertice, int valorBuscado) {
        if (vertice == null) return null;
        if (vertice.dato == valorBuscado) return vertice;
        VerticeBinario temp = buscarvertice(vertice.izq, valorBuscado);
        if (temp != null) return temp;
        return buscarvertice(vertice.der, valorBuscado);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new VisualizacionRecorridosGrafo().setVisible(true);
        });
    }
}
