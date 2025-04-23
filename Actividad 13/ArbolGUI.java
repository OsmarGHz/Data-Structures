import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import javax.imageio.ImageIO;
import javax.swing.*;

class Nodo {
    int valor;
    Nodo izquierdo, derecho;

    public Nodo(int valor) {
        this.valor = valor;
        izquierdo = derecho = null;
    }
}

class ArbolBinarioBusqueda {
    Nodo raiz;
    private List<Nodo> recorridoBusqueda = new ArrayList<>();

    public void insertar(int valor) {
        raiz = insertarRec(raiz, valor);
    }

    private Nodo insertarRec(Nodo nodo, int valor) {
        if (nodo == null) return new Nodo(valor);
        if (valor < nodo.valor) nodo.izquierdo = insertarRec(nodo.izquierdo, valor);
        else if (valor > nodo.valor) nodo.derecho = insertarRec(nodo.derecho, valor);
        return nodo;
    }

    public boolean buscar(int valor) {
        recorridoBusqueda.clear();
        return buscarRec(raiz, valor);
    }

    private boolean buscarRec(Nodo nodo, int valor) {
        if (nodo == null) return false;
        recorridoBusqueda.add(nodo);
        if (valor == nodo.valor) return true;
        return valor < nodo.valor ? buscarRec(nodo.izquierdo, valor) : buscarRec(nodo.derecho, valor);
    }

    public void eliminar(int valor) {
        raiz = eliminarRec(raiz, valor);
    }

    private Nodo eliminarRec(Nodo nodo, int valor) {
        if (nodo == null) return null;
        if (valor < nodo.valor) nodo.izquierdo = eliminarRec(nodo.izquierdo, valor);
        else if (valor > nodo.valor) nodo.derecho = eliminarRec(nodo.derecho, valor);
        else {
            if (nodo.izquierdo == null) return nodo.derecho;
            if (nodo.derecho == null) return nodo.izquierdo;
            nodo.valor = minValor(nodo.derecho);
            nodo.derecho = eliminarRec(nodo.derecho, nodo.valor);
        }
        return nodo;
    }

    private int minValor(Nodo nodo) {
        int min = nodo.valor;
        while (nodo.izquierdo != null) {
            nodo = nodo.izquierdo;
            min = nodo.valor;
        }
        return min;
    }

    public void cargarDesdeArchivo(String archivo) throws IOException {
        BufferedReader br = new BufferedReader(new FileReader(archivo));
        String linea;
        while ((linea = br.readLine()) != null) {
            try {
                insertar(Integer.parseInt(linea.trim()));
            } catch (NumberFormatException e) {
            }
        }
        br.close();
    }

    public List<Integer> enOrden() {
        List<Integer> lista = new ArrayList<>();
        enOrdenRec(raiz, lista);
        return lista;
    }

    private void enOrdenRec(Nodo nodo, List<Integer> lista) {
        if (nodo != null) {
            enOrdenRec(nodo.izquierdo, lista);
            lista.add(nodo.valor);
            enOrdenRec(nodo.derecho, lista);
        }
    }

    public List<Nodo> getRecorridoBusqueda() {
        return recorridoBusqueda;
    }

    // Guardar el árbol en un archivo de texto (recorrido en orden)
    public void guardarEnArchivo(String archivo) throws IOException {
        BufferedWriter bw = new BufferedWriter(new FileWriter(archivo));
        guardarRec(raiz, bw);
        bw.close();
    }

    private void guardarRec(Nodo nodo, BufferedWriter bw) throws IOException {
        if (nodo != null) {
            guardarRec(nodo.izquierdo, bw);
            bw.write(Integer.toString(nodo.valor));
            bw.newLine();
            guardarRec(nodo.derecho, bw);
        }
    }
}

class PanelDibujo extends JPanel {
    private ArbolBinarioBusqueda arbol;
    private final int radio = 20;
    private final int distanciaVertical = 60;
    private List<Nodo> recorridoBusqueda;
    private int pasoActual = 0;
    private Timer timer;

    public PanelDibujo(ArbolBinarioBusqueda arbol) {
        this.arbol = arbol;
        this.recorridoBusqueda = new ArrayList<>();
        setPreferredSize(new Dimension(800, 400));
        setBackground(Color.WHITE);
    }

    public void setArbol(ArbolBinarioBusqueda arbol) {
        this.arbol = arbol;
        this.recorridoBusqueda = arbol.getRecorridoBusqueda();
        repaint();
    }

    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (arbol.raiz != null) {
            dibujarNodo(g, arbol.raiz, getWidth() / 2, 40, getWidth() / 4);
        }
    }

    private void dibujarNodo(Graphics g, Nodo nodo, int x, int y, int offset) {
        g.setColor(Color.BLACK);
        
        // Dibujar líneas de conexión entre nodos
        if (nodo.izquierdo != null) {
            g.drawLine(x, y, x - offset, y + distanciaVertical);
            dibujarNodo(g, nodo.izquierdo, x - offset, y + distanciaVertical, offset / 2);
        }
        if (nodo.derecho != null) {
            g.drawLine(x, y, x + offset, y + distanciaVertical);
            dibujarNodo(g, nodo.derecho, x + offset, y + distanciaVertical, offset / 2);
        }

        // Iluminación de los nodos visitados
        if (recorridoBusqueda.contains(nodo)) {
            if (recorridoBusqueda.indexOf(nodo) <= pasoActual) {
                // Los nodos visitados, iluminados en amarillo claro
                g.setColor(new Color(255, 223, 186)); 
                g.fillOval(x - radio, y - radio, 2 * radio, 2 * radio); 
                g.setColor(Color.RED);  // Bordes rojos
                g.drawOval(x - radio, y - radio, 2 * radio, 2 * radio);
            } else {
                // Nodos no visitados, azul claro
                g.setColor(new Color(135, 206, 250)); 
                g.fillOval(x - radio, y - radio, 2 * radio, 2 * radio);
                g.setColor(Color.BLACK);
                g.drawOval(x - radio, y - radio, 2 * radio, 2 * radio);
            }
        } else {
            // Nodos que no forman parte del recorrido (no visitados)
            g.setColor(new Color(135, 206, 250));  
            g.fillOval(x - radio, y - radio, 2 * radio, 2 * radio);
            g.setColor(Color.BLACK);
            g.drawOval(x - radio, y - radio, 2 * radio, 2 * radio);
        }

        // Escribir el valor del nodo
        g.drawString(Integer.toString(nodo.valor), x - 6, y + 4);
    }

    public void iniciarBusquedaPasoAPaso() {
        if (timer != null && timer.isRunning()) {
            timer.stop();  
        }
        pasoActual = 0;
        timer = new Timer(500, e -> {
            if (pasoActual < recorridoBusqueda.size()) {
                pasoActual++;
                repaint();
            } else {
                timer.stop();
            }
        });
        timer.start();
    }

    public void guardarImagen(String path) {
        BufferedImage image = new BufferedImage(getWidth(), getHeight(), BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = image.createGraphics();
        paint(g2d);
        g2d.dispose();
        try {
            ImageIO.write(image, "PNG", new File(path));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

public class ArbolGUI extends JFrame {
    private ArbolBinarioBusqueda arbol;
    private JTextArea areaSalida;
    private JTextField campoValor;
    private PanelDibujo panelDibujo;

    public ArbolGUI() {
        arbol = new ArbolBinarioBusqueda();

        setTitle("Árbol Binario de Búsqueda");
        setSize(900, 600);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        campoValor = new JTextField(10);
        JButton btnInsertar = new JButton("Insertar");
        JButton btnEliminar = new JButton("Eliminar");
        JButton btnBuscar = new JButton("Buscar");
        JButton btnCargar = new JButton("Cargar desde archivo");
        JButton btnMostrar = new JButton("Mostrar En Orden");
        JButton btnGuardar = new JButton("Guardar Árbol");

        areaSalida = new JTextArea(5, 40);
        areaSalida.setEditable(false);
        JScrollPane scroll = new JScrollPane(areaSalida);

        JPanel panelSuperior = new JPanel();
        panelSuperior.add(new JLabel("Valor:"));
        panelSuperior.add(campoValor);
        panelSuperior.add(btnInsertar);
        panelSuperior.add(btnEliminar);
        panelSuperior.add(btnBuscar);
        panelSuperior.add(btnCargar);
        panelSuperior.add(btnMostrar);
        panelSuperior.add(btnGuardar);

        panelDibujo = new PanelDibujo(arbol);

        add(panelSuperior, BorderLayout.NORTH);
        add(scroll, BorderLayout.CENTER);
        add(panelDibujo, BorderLayout.SOUTH);

        // Acciones
        btnInsertar.addActionListener(e -> {
            try {
                int valor = Integer.parseInt(campoValor.getText());
                arbol.insertar(valor);
                mostrar("Insertado: " + valor);
                panelDibujo.repaint();
            } catch (NumberFormatException ex) {
                mostrar("Número inválido.");
            }
        });

        btnEliminar.addActionListener(e -> {
            try {
                int valor = Integer.parseInt(campoValor.getText());
                arbol.eliminar(valor);
                mostrar("Eliminado (si existía): " + valor);
                panelDibujo.repaint();
            } catch (NumberFormatException ex) {
                mostrar("Número inválido.");
            }
        });

        btnBuscar.addActionListener(e -> {
            try {
                int valor = Integer.parseInt(campoValor.getText());
                boolean encontrado = arbol.buscar(valor);
                if (encontrado) {
                    mostrar("Encontrado: " + valor);
                } else {
                    mostrar("No encontrado: " + valor);
                }
                panelDibujo.setArbol(arbol);
                panelDibujo.iniciarBusquedaPasoAPaso();
            } catch (NumberFormatException ex) {
                mostrar("Número inválido.");
            }
        });

        btnCargar.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser();
            int resultado = fileChooser.showOpenDialog(this);
            if (resultado == JFileChooser.APPROVE_OPTION) {
                try {
                    File archivo = fileChooser.getSelectedFile();
                    arbol.cargarDesdeArchivo(archivo.getAbsolutePath());
                    mostrar("Árbol cargado desde " + archivo.getName());
                    panelDibujo.setArbol(arbol);
                } catch (IOException ex) {
                    mostrar("Error al cargar el archivo.");
                }
            }
        });

        btnMostrar.addActionListener(e -> {
            List<Integer> listaEnOrden = arbol.enOrden();
            StringBuilder sb = new StringBuilder("En Orden: ");
            for (int valor : listaEnOrden) {
                sb.append(valor).append(" ");
            }
            mostrar(sb.toString());
        });

        btnGuardar.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser();
            int resultado = fileChooser.showSaveDialog(this);
            if (resultado == JFileChooser.APPROVE_OPTION) {
                try {
                    File archivo = fileChooser.getSelectedFile();
                    arbol.guardarEnArchivo(archivo.getAbsolutePath());
                    mostrar("Árbol guardado en " + archivo.getName());
                } catch (IOException ex) {
                    mostrar("Error al guardar el archivo.");
                }
            }
        });
    }

    private void mostrar(String mensaje) {
        areaSalida.setText(mensaje);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new ArbolGUI().setVisible(true);
        });
    }
}