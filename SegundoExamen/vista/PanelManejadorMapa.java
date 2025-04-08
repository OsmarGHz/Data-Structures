import java.awt.*;
import java.io.File;
import java.io.IOException;
import javax.swing.*;

public class PanelManejadorMapa extends JPanel {
    private GuardianesDelBosques guardian;
    private PanelMapa panelMapa;
    private JPanel panelBotones;
    
    // Banderas para rastrear la ejecución de cada algoritmo
    private boolean bfsEjecutado = false, dfsEjecutado = false, dijkstraEjecutado = false,
                    floydEjecutado = false, primEjecutado = false, kruskalEjecutado = false;
                    
    // Referencia al botón Terminar Ejercicio para activarlo
    private JButton btnTerminarEjercicio;

    

    // Constructor por defecto
    public PanelManejadorMapa() {
        setLayout(new BorderLayout());
        panelMapa = new PanelMapa();
        panelBotones = crearPanelBotones();

        // Configurar JSplitPane
        JSplitPane splitPane = new JSplitPane(
            JSplitPane.HORIZONTAL_SPLIT,
            panelMapa,
            panelBotones
        );
        splitPane.setResizeWeight(0.95); // 90% mapa, 10% botones
        splitPane.setDividerLocation(0.95);
        splitPane.setContinuousLayout(true);
        add(splitPane, BorderLayout.CENTER);

        // Asignar action listeners a los botones
        asignarEventosBotones();
    }
    
    // Método que se llama cuando el usuario completa el ejercicio
    public void ejercicioCompletado() {
        if (guardian != null) {
            guardian.evaluarResultadoFinal(1, 1, 4); // Módulo 4 completado
            guardian.recargarPantallaModulos();
        }
    }

    public PanelManejadorMapa(GuardianesDelBosques guardian) {
        this.guardian = guardian;

        setLayout(new BorderLayout());
        panelMapa = new PanelMapa();
        panelBotones = crearPanelBotones();

        // Configurar JSplitPane
        JSplitPane splitPane = new JSplitPane(
            JSplitPane.HORIZONTAL_SPLIT,
            panelMapa,
            panelBotones
        );
        splitPane.setResizeWeight(0.9); // 90% mapa, 10% botones
        splitPane.setDividerLocation(0.9);
        splitPane.setContinuousLayout(true);
        add(splitPane, BorderLayout.CENTER);

        // Asignar action listeners a los botones
        asignarEventosBotones();

        
    }

    
    private JPanel crearPanelBotones() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JButton btnNuevoGrafo = crearBotonEstilizado("Crear nuevo bosque");
        JButton btnBFS = crearBotonEstilizado("Ejecutar BFS");
        JButton btnDFS = crearBotonEstilizado("Ejecutar DFS");
        JButton btnDijkstra = crearBotonEstilizado("Ejecutar Dijkstra");
        JButton btnFloyd = crearBotonEstilizado("Ejecutar Floyd");
        JButton btnPrim = crearBotonEstilizado("Ejecutar Prim");
        JButton btnKruskal = crearBotonEstilizado("Ejecutar Kruskal");
        //JButton btnCentros = crearBotonEstilizado("Poner centros de recolección");
        JToggleButton btnCentros = crearBotonEstilizadoT("Poner centros de recolección");
        btnTerminarEjercicio = crearBotonEstilizado("Terminar ejercicio");
        btnTerminarEjercicio.setEnabled(false); // Inhabilitado hasta que se ejecuten todos los algoritmos

        panel.add(btnNuevoGrafo);
        panel.add(Box.createVerticalStrut(10));
        panel.add(btnBFS);
        panel.add(Box.createVerticalStrut(10));
        panel.add(btnDFS);
        panel.add(Box.createVerticalStrut(10));
        panel.add(btnDijkstra);
        panel.add(Box.createVerticalStrut(10));
        panel.add(btnFloyd);
        panel.add(Box.createVerticalStrut(10));
        panel.add(btnPrim);
        panel.add(Box.createVerticalStrut(10));
        panel.add(btnKruskal);
        panel.add(Box.createVerticalStrut(10));
        panel.add(btnCentros);
        panel.add(Box.createVerticalStrut(10));
        panel.add(btnTerminarEjercicio);

        return panel;
    }

    public void asignarEventosBotones() {
        // Asignar eventos a los botones según su texto
        getBoton("Crear nuevo bosque").addActionListener(e -> {
            panelMapa.ejecutarNuevoGrafo();
        });
        getBoton("Ejecutar BFS").addActionListener(e -> {
            panelMapa.ejecutarBFS();
            bfsEjecutado = true;
            activarTerminarSiCorresponde();
        });
        getBoton("Ejecutar DFS").addActionListener(e -> {
            panelMapa.ejecutarDFS();
            dfsEjecutado = true;
            activarTerminarSiCorresponde();
        });
        getBoton("Ejecutar Dijkstra").addActionListener(e -> {
            panelMapa.ejecutarDijkstra();
            dijkstraEjecutado = true;
            activarTerminarSiCorresponde();
        });
        getBoton("Ejecutar Floyd").addActionListener(e -> {
            panelMapa.ejecutarFloyd();
            floydEjecutado = true;
            activarTerminarSiCorresponde();
        });
        getBoton("Ejecutar Prim").addActionListener(e -> {
            panelMapa.ejecutarPrim();
            primEjecutado = true;
            activarTerminarSiCorresponde();
        });
        getBoton("Ejecutar Kruskal").addActionListener(e -> {
            panelMapa.ejecutarKruskal();
            kruskalEjecutado = true;
            activarTerminarSiCorresponde();
        });
        getBotonT("Poner centros de recolección").addActionListener(e -> {
            panelMapa.toggleCentros();
            //panelMapa.setBoton(btnCentros);  // Aquí pasas el botón a PanelMapa
        });

        if (guardian == null) {
            btnTerminarEjercicio.addActionListener(e -> {
                panelMapa.terminarEjercicio();
            });
        } else {
            btnTerminarEjercicio.addActionListener(e -> {
                JOptionPane.showMessageDialog(this, 
                    "¡Felicidades! Has completado todos los algoritmos requeridos.\n" +
                    "Ejercicio final terminado correctamente.",
                    "Ejercicio Completado", 
                    JOptionPane.INFORMATION_MESSAGE);
                
                ejercicioCompletado(); // Notificar que se completó el módulo 4
            });
        
        }

    }

    private void activarTerminarSiCorresponde() {
        // Si se han ejecutado todos los algoritmos, habilitar el botón Terminar Ejercicio
        if (bfsEjecutado && dfsEjecutado && dijkstraEjecutado &&
            floydEjecutado && primEjecutado && kruskalEjecutado) {
            btnTerminarEjercicio.setEnabled(true);
        }
    }

    private JButton crearBotonEstilizado(String texto) {
        JButton boton = new JButton(texto);
        boton.setBackground(new Color(87, 124, 88));
        boton.setForeground(Color.WHITE);
        //boton.setMaximumSize(new Dimension(ancho, 40));
        boton.setFont(cargarFuente("SegundoExamen\\recursos\\fuenteTitulo.ttf", 16f));
        boton.setFocusPainted(false);
        boton.setBorder(BorderFactory.createEmptyBorder(10, 25, 10, 25));
        boton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        boton.setAlignmentX(Component.CENTER_ALIGNMENT);
        return boton;
    }

    private JButton getBoton(String texto) {
        for (Component comp : panelBotones.getComponents()) {
            if (comp instanceof JButton && ((JButton) comp).getText().equals(texto)) {
                return (JButton) comp;
            }
        }
        return null;
    }

    private JToggleButton crearBotonEstilizadoT(String texto) {
        JToggleButton boton = new JToggleButton(texto);
        boton.setBackground(new Color(87, 124, 88));
        boton.setForeground(Color.WHITE);
        //boton.setMaximumSize(new Dimension(ancho, 40));
        boton.setFont(cargarFuente("SegundoExamen\\recursos\\fuenteTitulo.ttf", 16f));
        boton.setFocusPainted(false);
        boton.setBorder(BorderFactory.createEmptyBorder(10, 25, 10, 25));
        boton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        boton.setAlignmentX(Component.CENTER_ALIGNMENT);
        return boton;
    }

    private JToggleButton getBotonT(String texto) {
        for (Component comp : panelBotones.getComponents()) {
            if (comp instanceof JToggleButton && ((JToggleButton) comp).getText().equals(texto)) {
                return (JToggleButton) comp;
            }
        }
        return null;
    }

        // Cargar fuentes
    public Font cargarFuente(String ruta, float tamaño) {
        try {
            Font fuente = Font.createFont(Font.TRUETYPE_FONT, new File(ruta));
            fuente = fuente.deriveFont(tamaño);
            GraphicsEnvironment.getLocalGraphicsEnvironment().registerFont(fuente);
            return fuente;
        } catch (IOException | FontFormatException e) {
            System.err.println("Error al cargar fuente: " + ruta);
            return new Font("SansSerif", Font.PLAIN, (int)tamaño);
        }
    }


    public static void main(String[] args) {
        JFrame ventana = new JFrame("Grafo Interactivo");
        ventana.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        ventana.setSize(800, 600);
        ventana.setLocationRelativeTo(null);
        ventana.add(new PanelManejadorMapa());
        ventana.setVisible(true);
    }
        
}